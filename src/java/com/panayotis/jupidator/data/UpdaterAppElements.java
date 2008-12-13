/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.data;

import com.panayotis.jupidator.loglist.LogItem;
import com.panayotis.jupidator.loglist.LogList;

/**
 *
 * @author teras
 */
public class UpdaterAppElements {

    private String AppName = "Unknown";
    private String baseURL = "";
    private String iconpath = "";
    private int newrelease = -1;
    private int lastrelease = -1;   // Last known release, read from XML
    private String newversion = "0.0.0";
    private String lastversion = "0.0.0.0"; // Last known version, read from XML
    private LogList loglist = new LogList();

    public String getAppName() {
        return AppName;
    }

    public int getLastRelease() {
        return lastrelease;
    }

    public String getLastVersion() {
        return lastversion;
    }

    void setAppName(String AppName) {
        if (AppName != null && (!AppName.equals("")))
            this.AppName = AppName;
    }

    public LogList getLogList() {
        return loglist;
    }

    public String getBaseURL() {
        return baseURL;
    }

    void setBaseURL(String base) {
        if (base == null)
            throw new IllegalArgumentException("Base URL should be defined in XML.");
        baseURL = base + "/";
    }

    void addLogItem(String version, String information) {
        loglist.add(new LogItem(version, information));
    }

    public String getIconpath() {
        return iconpath;
    }

    void setIconpath(String iconpath) {
        if (iconpath != null)
            this.iconpath = baseURL + iconpath;
    }

    void updateVersion(int lastrelease, String lastversion) {
        this.lastrelease = lastrelease;
        this.lastversion = lastversion;
        if (lastrelease > newrelease) {
            newrelease = lastrelease;
            newversion = lastversion;
        }
        if (newversion == null)
            newversion = "0.0.0";
    }

    public String getNewVersion() {
        return newversion;
    }
    
    public int getNewRelease() {
        return newrelease;
    }
}
