/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import com.panayotis.jupidator.statics.SystemVersion;
import com.panayotis.jupidator.statics.SelfUpdate;
import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.elements.FileUtils;
import com.panayotis.jupidator.gui.JupidatorGUI;
import com.panayotis.jupidator.gui.UpdateWatcher;
import com.panayotis.jupidator.data.SimpleApplication;
import com.panayotis.jupidator.gui.console.ConsoleGUI;
import com.panayotis.jupidator.gui.swing.SwingGUI;
import com.panayotis.jupidator.data.Version;
import com.panayotis.jupidator.elements.security.PermissionManager;
import com.panayotis.jupidator.loglist.creators.HTMLCreator;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import jupidator.launcher.DeployerParameters;
import jupidator.launcher.XElement;

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

    public Updater(String xmlurl, String appHome, String appSupportDir, String release, String version, UpdatedApplication application) throws UpdaterException {
        this(xmlurl, new ApplicationInfo(appHome, appSupportDir, release, version), application);
    }

    public Updater(String xmlurl, ApplicationInfo appinfo, UpdatedApplication application) throws UpdaterException {
        vers = Version.loadVersion(xmlurl, appinfo);
        if (vers.getAppElements().shouldUpdateLibrary()) {
            String oldname = vers.getAppElements().getAppName();
            String CFGDIR = new File(appinfo.getUpdaterConfigFile()).getAbsoluteFile().getParent();

            ApplicationInfo selfappinfo = new ApplicationInfo(FileUtils.getJupidatorHome(), CFGDIR, String.valueOf(SystemVersion.RELEASE), SystemVersion.VERSION);
            selfappinfo.setSelfUpdate();

            Version selfvers = Version.loadVersion(SelfUpdate.URL, selfappinfo);
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
        download = new Thread()  {

            @Override
            public void run() {
                /* Fetch */
                for (String key : vers.keySet()) {
                    String result = vers.get(key).fetch(application, watcher);
                    if (result != null) {
                        watcher.stopWatcher();
                        gui.errorOnCommit(result);
                        return;
                    }
                }
                /* Prepare */
                watcher.stopWatcher();
                gui.setIndetermined();
                for (String key : vers.keySet()) {
                    String result = vers.get(key).prepare(application);
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
        PermissionManager.manager.cleanUp();
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
        /* Ask application if restart could be performed */
        watcher.stopWatcher();
        if (!application.requestRestart()) {
            gui.errorOnRestart(_("Application cancelled restart"));
            return;
        }

        /* Construct parameters */
        ArrayList<XElement> elements = new ArrayList<XElement>();
        for (String key : vers.keySet())
            elements.add(vers.get(key).getExecElement());
        ArrayList<String> relaunch = new ArrayList<String>();
        for (int i = 0; i < vers.getArch().countArguments(); i++)
            relaunch.add(vers.getArch().getArgument(i, appinfo));
        DeployerParameters params = new DeployerParameters();
        params.setElements(elements);
        params.setRelaunchCommand(relaunch);
        params.setHeadless(gui.isHeadless());
        params.setRelaunchCommand(null);

        /* Construct launcher command */
        try {
            PermissionManager.manager.getLaunchCommand(application, params).start();
        } catch (IOException ex) {
            String message = ex.getMessage();
            application.receiveMessage(message);
            gui.errorOnRestart(message);
            return;
        }
        gui.endDialog();
        System.exit(0);  // Restarting
    }

    public String getChangeLog() {
        return HTMLCreator.getList(vers.getAppElements().getLogList());
    }
}
