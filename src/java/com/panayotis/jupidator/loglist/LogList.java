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

    private String application_info;

    /**
     * @return the release_info
     */
    public String getApplicationInfo() {
        return application_info;
    }

    /**
     * @param release_info the release_info to set
     */
    public void setApplicationInfo(String application_info) {
        if (application_info == null)
            application_info = "";
        this.application_info = application_info;
    }
}
