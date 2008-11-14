/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import com.panayotis.jupidator.gui.ChangelogFrame;
import com.panayotis.jupidator.list.Version;

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
            System.exit(0);
        }
    }

    public void actionCancel() {
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
