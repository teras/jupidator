/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jubler.plugins;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.Updater;
import com.panayotis.jupidator.UpdaterException;

/**
 *
 * @author teras
 */
public class AutoUpdatePlugin implements Plugin {

    public AutoUpdatePlugin() {
    }

    public String[] getAffectionList() {
        return new String[]{"com.panayotis.jupidator.AutoUpdater"};
    }

    public void postInit(Object o) {
        try {
            Object[] p = (Object[]) o;
            ApplicationInfo info = new ApplicationInfo((String) p[0], (String) p[1], (String) p[2], (String) p[3]);
            info.setDistributionBased((Boolean) p[4]);
            Updater upd = new Updater((String) p[5], info, (UpdatedApplication) p[6]);
            upd.actionDisplay();
        } catch (UpdaterException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
