/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import com.panayotis.jupidator.file.FileUtils;
import com.panayotis.jupidator.gui.ChangelogFrame;
import com.panayotis.jupidator.list.Arch;
import com.panayotis.jupidator.list.Version;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author teras
 */
public class Updater {

    private Version vers;
    private ChangelogFrame frame;
    private UpdatedApplication application;
    private ApplicationInfo appinfo;
    private Thread download;

    public Updater(String xmlurl, ApplicationInfo appinfo, UpdatedApplication application) throws UpdaterException {
        this.appinfo = appinfo;
        vers = Version.loadVersion(xmlurl, appinfo);
        this.application = application;
    }

    public void actionDisplay() throws UpdaterException {
        if (vers.size() > 0) {
            frame = new ChangelogFrame(this);
            frame.setInformation(vers.getAppElements(), appinfo);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }

    public void actionCommit() {
        long size = 0;
        for (String key : vers.keySet()) {
            size += vers.get(key).getSize();
        }
        frame.setAllBytes(size);
        download = new Thread() {

            public void run() {
                /* Download */
                for (String key : vers.keySet()) {
                    String result = vers.get(key).updateSystemVariables().fetch(application, frame); // Lazy update of arguments
                    if (result != null) {
                        frame.errorOnCommit(result);
                        return;
                    }
                }
                /* Deploy */
                frame.setIndetermined();
                for (String key : vers.keySet()) {
                    String result = vers.get(key).deploy(application);
                    if (result != null) {
                        frame.errorOnCommit(result);
                        return;
                    }
                }
                frame.successOnCommit();
            }
        };
        download.start();
        frame.startDownloadTimer();
    }

    public void actionCancel() {
        download.interrupt();
        frame.setVisible(false);
        frame.dispose();
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
        frame.setVisible(false);
        frame.dispose();
        vers.getUpdaterProperties().defer();
    }

    public void actionIgnore() {
        frame.setVisible(false);
        frame.dispose();
        vers.getUpdaterProperties().ignore(vers.getAppElements().getNewRelease());
    }

    public void actionRestart() {
        frame.setVisible(false);
        frame.dispose();
        if (application == null || application.requestRestart()) {
            String classname = "com.panayotis.jupidator.deployer.JupidatorDeployer";
            String temppath = System.getProperty("java.io.tmpdir");
            Arch arch = vers.getArch();

            String message = FileUtils.copyClass(classname, temppath);
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
