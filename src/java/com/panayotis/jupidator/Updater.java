/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.elements.FileUtils;
import com.panayotis.jupidator.gui.JupidatorGUI;
import com.panayotis.jupidator.gui.UpdateWatcher;
import com.panayotis.jupidator.data.Arch;
import com.panayotis.jupidator.data.SimpleApplication;
import com.panayotis.jupidator.gui.console.ConsoleGUI;
import com.panayotis.jupidator.gui.swing.SwingGUI;
import com.panayotis.jupidator.loglist.creators.HTMLCreator;
import com.panayotis.jupidator.data.Version;
import com.panayotis.jupidator.launcher.Closure;
import com.panayotis.jupidator.launcher.JupidatorDeployer;
import com.panayotis.jupidator.launcher.LaunchManager;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author teras
 */
public class Updater {

    private Version vers;
    private UpdatedApplication application;
    private ApplicationInfo appinfo;
    private Thread download;
    /* Lazy components */
    private JupidatorGUI gui;
    private UpdateWatcher watcher;

    public Updater(String xmlurl, ApplicationInfo appinfo, UpdatedApplication application) throws UpdaterException {
        vers = Version.loadVersion(xmlurl, appinfo);
        if (vers.getAppElements().shouldUpdateLibrary()) {
            String oldname = vers.getAppElements().getAppName();
            String CFGDIR = new File(appinfo.getUpdaterConfigFile()).getAbsoluteFile().getParent();

            ApplicationInfo selfappinfo = new ApplicationInfo(FileUtils.getJupidatorHome(), CFGDIR, String.valueOf(SystemVersion.RELEASE), SystemVersion.VERSION);
            selfappinfo.setSelfUpdate();

            Version selfvers = Version.loadVersion("http://www.panayotis.com/versions/jupidator/jupidator.xml", selfappinfo);
            if (selfvers.isVisible()) {
                selfvers.replaceArch(vers.getArch());
                vers = selfvers;
                appinfo = selfappinfo;
                vers.getAppElements().setSelfUpdate(oldname);
                vers.getAppElements().setApplicationInfo(_("This update is required for the smooth updating of {0}", oldname));
            }
        }

        this.appinfo = appinfo;
        if (application == null)
            application = new SimpleApplication();
        this.application = application;
    }

    /** Return JupidatorGUI, and create it if it does not exist.
     *  This is the official method to create the default GUI
     *  GUI is created lazily, when needed
     */
    public JupidatorGUI getGUI() {
        if (gui == null)
            if (GraphicsEnvironment.isHeadless())
                gui = new ConsoleGUI();
            else
                gui = new SwingGUI();
        return gui;
    }

    public void setGUI(JupidatorGUI gui) {
        if (gui != null)
            this.gui = gui;
    }

    public void actionDisplay() throws UpdaterException {
        if (vers.isVisible()) {
            getGUI();  /* GUI is created lazily, when needed */
            watcher = new UpdateWatcher(); /* Watcher is also created lazily, when needed */
            watcher.setCallBack(gui);
            gui.setInformation(this, vers.getAppElements(), appinfo);
            gui.startDialog();
        }
    }

    public void actionCommit() {
        long size = 0;
        for (String key : vers.keySet())
            size += vers.get(key).getSize();
        watcher.setAllBytes(size);
        download = new Thread() {

            public void run() {
                /* Download */
                for (String key : vers.keySet()) {
                    String result = vers.get(key).fetch(application, watcher);
                    if (result != null) {
                        watcher.stopWatcher();
                        gui.errorOnCommit(result);
                        return;
                    }
                }
                /* Deploy */
                watcher.stopWatcher();
                gui.setIndetermined();
                for (String key : vers.keySet()) {
                    String result = vers.get(key).deploy(application);
                    if (result != null) {
                        gui.errorOnCommit(result);
                        return;
                    }
                }
                gui.successOnCommit();
            }
        };
        download.start();
        watcher.startWatcher();
    }

    public void actionCancel() {
        watcher.stopWatcher();
        download.interrupt();
        gui.endDialog();
        try {
            download.join();
        } catch (InterruptedException ex) {
        }
        for (String key : vers.keySet())
            vers.get(key).cancel(application);
    }

    /* Do nothing - wait for next cycle */
    public void actionDefer() {
        watcher.stopWatcher();
        gui.endDialog();
        vers.getUpdaterProperties().defer();
    }

    public void actionIgnore() {
        watcher.stopWatcher();
        gui.endDialog();
        vers.getUpdaterProperties().ignore(vers.getAppElements().getNewRelease());
    }

    public void actionRestart() {
        watcher.stopWatcher();
        gui.endDialog();
        if (application.requestRestart()) {
            String temppath = System.getProperty("java.io.tmpdir");
            Arch arch = vers.getArch();

            String message = FileUtils.copyPackage(LaunchManager.class.getPackage().getName(), temppath, application);
            if (message != null) {
                application.receiveMessage(message);
                JOptionPane.showMessageDialog(null, message, message, JOptionPane.ERROR_MESSAGE);
                return;
            }

            int header = 6;
            String args[] = new String[header + vers.size() + arch.countArguments()];
            args[0] = FileUtils.JAVABIN;
            args[1] = "-cp";
            args[2] = temppath;
            args[3] = LaunchManager.class.getName();
            args[4] = vers.isGraphicalDeployer() ? "g" : "t";
            args[5] = String.valueOf(vers.size());

            for (String key : vers.keySet())
                args[header++] = vers.get(key).getArgument();

            for (int i = 0; i < arch.countArguments(); i++)
                args[header++] = arch.getArgument(i, appinfo);

            Closure callback = new Closure() {

                public void exec(Object data) {
                    application.receiveMessage(data.toString());
                }
            };
            application.receiveMessage(_("Executing {0}", LaunchManager.ArrayToString(args, " ")));
            try {
                Runtime.getRuntime().exec(args);
            } catch (IOException ex) {
                application.receiveMessage(ex.getMessage());
            }
            System.exit(0);
        }
    }

    public String getChangeLog() {
        return HTMLCreator.getList(vers.getAppElements().getLogList());
    }
}
