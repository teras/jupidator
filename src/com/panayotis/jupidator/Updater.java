/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import com.panayotis.jupidator.file.FileUtils;
import com.panayotis.jupidator.gui.ChangelogFrame;
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
    private UpdaterListener listener;

    public Updater(String xmlurl, ApplicationInfo apinfo, UpdaterListener listener) throws UpdaterException {
        vers = Version.loadVersion(xmlurl, apinfo);
        this.listener = listener;
        if (vers.size() > 0) {
            frame = new ChangelogFrame(this);
            frame.setInformation(vers.getAppElements(), apinfo);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }

    public void actionCommit() {
        for (String key : vers.keySet()) {
            String result = vers.get(key).action(listener);
            if (result != null) {
                frame.errorOnCommit(result);
                return;
            }
        }
        frame.successOnCommit();
    }

    public void actionRestart() {
        frame.setVisible(false);
        frame.dispose();
        if (listener == null || listener.requestRestart()) {
            String classname = "com.panayotis.jupidator.deployer.JupidatorDeployer";
            String temppath = System.getProperty("java.io.tmpdir");
            
            String message = FileUtils.copyClass(classname, temppath);
            if (message != null) {
                listener.receiveMessage(message);
                JOptionPane.showMessageDialog(null, message, message, JOptionPane.ERROR_MESSAGE);
                return;
            }

            String args[] = new String[4 + vers.size()];
            args[0] = FileUtils.getJavaExec();
            args[1] = "-cp";
            args[2] = temppath;
            args[3] = classname;

            int counter = 4;
            for (String key : vers.keySet()) {
                args[counter++] = vers.get(key).getDestinationAction();
            }

            try {
                Process proc = Runtime.getRuntime().exec(args);
            } catch (IOException ex) {
                listener.receiveMessage(ex.getMessage());
            }
            System.exit(0);
        }
    }

    public void actionCancel() {
        for (String key : vers.keySet()) {
            vers.get(key).cancel(listener);
        }
        frame.setVisible(false);
        frame.dispose();
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
}
