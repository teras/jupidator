/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.data.UpdaterAppElements;
import com.panayotis.jupidator.gui.BufferListener;

/**
 *
 * @author teras
 */
public class ElementWait extends JupidatorElement {

    private int msecs = 1000;

    public ElementWait(String msecs, String exectime, UpdaterAppElements elements, ApplicationInfo appinfo) {
        super(String.valueOf(Math.random()), elements, appinfo,
                ExecutionTime.valueOf(exectime.toUpperCase()) == null ? ExecutionTime.BEFORE : ExecutionTime.valueOf(exectime.toUpperCase()));
        try {
            this.msecs = Integer.parseInt(msecs);
        } catch (NumberFormatException n) {
        }
    }

    public String fetch(UpdatedApplication application, BufferListener blisten) {
        return null;
    }

    public String deploy(UpdatedApplication application) {
        return null;
    }

    public void cancel(UpdatedApplication application) {
    }

    public String getArgument() {
        return "w" + Integer.toString(msecs);
    }
}
