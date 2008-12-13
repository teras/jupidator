/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.loglist;

/**
 *
 * @author teras
 */
public class LogItem {

    private String version;
    private String info;

    public LogItem(String version, String info) {
        this.version = version;
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public String getVersion() {
        return version;
    }
}
