/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.gui;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdaterException;
import com.panayotis.jupidator.list.UpdaterAppElements;

/**
 *
 * @author teras
 */
public interface JupidatorGUI {

    public void setInformation(UpdaterAppElements appElements, ApplicationInfo appinfo) throws UpdaterException;

    public void startDialog();
    public void endDialog();

    public void errorOnCommit(String message);
    public void successOnCommit();

    public void setDownloadRatio(long bytes, float percent);
    public void setIndetermined();
}
