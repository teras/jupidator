/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.gui;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.Updater;
import com.panayotis.jupidator.UpdaterException;
import com.panayotis.jupidator.data.UpdaterAppElements;

/**
 *
 * @author teras
 */
public interface JupidatorGUI {

    public void setInformation(Updater callback, UpdaterAppElements appElements, ApplicationInfo appinfo) throws UpdaterException;

    public void startDialog();
    public void endDialog();

    public void errorOnCommit(String message);
    public void successOnCommit();
    public void errorOnRestart(String message);

    public void setDownloadRatio(String ratio, float percent);
    public void setIndetermined();

    public void setProperty(String key, String value);

    public boolean isHeadless();
}
