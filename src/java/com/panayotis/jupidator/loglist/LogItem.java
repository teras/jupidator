/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.loglist;

import java.io.Serializable;

/**
 *
 * @author teras
 */
public class LogItem implements Serializable {

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
