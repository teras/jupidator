/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.file.FileUtils;
import com.panayotis.jupidator.gui.JupidatorGUI;
import com.panayotis.jupidator.gui.UpdateWatcher;
import com.panayotis.jupidator.list.Arch;
import com.panayotis.jupidator.applications.SimpleApplication;
import com.panayotis.jupidator.gui.console.ConsoleGUI;
import com.panayotis.jupidator.gui.swing.SwingGUI;
import com.panayotis.jupidator.list.Version;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author teras
 */
public class Updater {

    private Version vers;
    private JupidatorGUI gui;
    private UpdateWatcher watcher;
    private UpdatedApplication application;
    private ApplicationInfo appinfo;
    private Thread download;

    public Updater(String xmlurl, ApplicationInfo appinfo, UpdatedApplication application) throws UpdaterException {
        this.appinfo = appinfo;
        vers = Version.loadVersion(xmlurl, appinfo);
        if (application == null)
            application = new SimpleApplication();
        this.application = application;
        watcher = new UpdateWatcher();
    }

    public void actionDisplay() throws UpdaterException {
        if (vers.size() > 0) {
            if (GraphicsEnvironment.isHeadless())
                gui = new ConsoleGUI();
            else
                gui = new SwingGUI();
            watcher.setCallBack(gui);
            gui.setInformation(this, vers.getAppElements(), appinfo);
            gui.startDialog();
        }
    }

    public void actionCommit() {
        long size = 0;
        for (String key : vers.keySet()) {
            size += vers.get(key).getSize();
        }
        watcher.setAllBytes(size);
        download = new Thread() {

            public void run() {
                /* Download */
                for (String key : vers.keySet()) {
                    String result = vers.get(key).updateSystemVariables().fetch(application, watcher); // Lazy update of arguments
                    if (result != null) {
                        watcher.stopWatcher();
                        gui.errorOnCommit(result);
                        return;
                    }
                }
                /* Deploy */
                gui.setIndetermined();
                for (String key : vers.keySet()) {
                    String result = vers.get(key).deploy(application);
                    if (result != null) {
                        watcher.stopWatcher();
                        gui.errorOnCommit(result);
                        return;
                    }
                }
                watcher.stopWatcher();
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
        for (String key : vers.keySet()) {
            vers.get(key).cancel(application);
        }
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
            String classname = "com.panayotis.jupidator.deployer.JupidatorDeployer";
            String temppath = System.getProperty("java.io.tmpdir");
            Arch arch = vers.getArch();

            String message = FileUtils.copyClass(classname, temppath, application);
            if (message != null) {
                application.receiveMessage(message);
                JOptionPane.showMessageDialog(null, message, message, JOptionPane.ERROR_MESSAGE);
                return;
            }

            String args[] = new String[5 + vers.size() + arch.countArguments()];
            args[0] = FileUtils.JAVABIN;
            args[1] = "-cp";
            args[2] = temppath;
            args[3] = classname;

            args[4] = String.valueOf(vers.size());
            int counter = 5;
            for (String key : vers.keySet()) {
                args[counter++] = vers.get(key).getArgument();
            }

            for (int i = 0; i < arch.countArguments(); i++) {
                args[counter++] = appinfo.updatePath(arch.getArgument(i));  // Lazy update of arguments
            }

            try {
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < args.length; i++)
                    buf.append(args[i]).append(' ');
                application.receiveMessage(_("Executing {0}", buf.toString()));
                Runtime.getRuntime().exec(args);
            } catch (IOException ex) {
                application.receiveMessage(ex.getMessage());
            }
            System.exit(0);
        }
    }

    public String getChangeLog() {
        return vers.getAppElements().getHTML();
    }
}
