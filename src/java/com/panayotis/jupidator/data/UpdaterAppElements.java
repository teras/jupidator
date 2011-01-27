/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.data;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.statics.SystemVersion;
import com.panayotis.jupidator.loglist.LogItem;
import com.panayotis.jupidator.loglist.LogList;
import java.io.Serializable;

/**
 * This information is gathered for the library from the XML file
 * @author teras
 */
public class UpdaterAppElements implements Serializable {

    private String AppName = "Unknown";
    private String baseURL = "";
    private String iconpath = "";
    private int newrelease = -1;    // Latest release overall, read form XML
    private int lastrelease = -1;   // Last value we read from XML
    private String newversion = "0.0.0";    // Latest version overall, read form XML
    private String lastversion = "0.0.0.0"; // Last value we read from XML
    private LogList loglist = new LogList();
    private boolean needs_update = false;

    public String getAppName() {
        return AppName;
    }

    public int getLastRelease() {
        return lastrelease;
    }

    public String getLastVersion() {
        return lastversion;
    }

    public void setSelfUpdate(String appname) {
        setAppName(_("Jupidator for {0}", appname));
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

    public void setApplicationInfo(String release_info) {
        loglist.setApplicationInfo(release_info);
    }

    public String getIconpath() {
        return iconpath;
    }

    void setIconpath(String iconpath) {
        if (iconpath != null)
            this.iconpath = baseURL + iconpath;
    }

    void setJupidatorVersion(String jupidator_version) {
        if (jupidator_version != null)
            try {
                needs_update = Integer.parseInt(jupidator_version) > SystemVersion.RELEASE;
            } catch (NumberFormatException ex) {
            }
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

    public boolean shouldUpdateLibrary() {
        return needs_update;
    }
}
