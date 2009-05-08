/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.loglist;

import java.util.ArrayList;

/**
 *
 * @author teras
 */
public class LogList extends ArrayList<LogItem> {

    private String release_info;

    /**
     * @return the release_info
     */
    public String getReleaseInfo() {
        return release_info;
    }

    /**
     * @param release_info the release_info to set
     */
    public void setReleaseInfo(String release_info) {
        if (release_info == null)
            release_info = "";
        this.release_info = release_info;
    }
}
