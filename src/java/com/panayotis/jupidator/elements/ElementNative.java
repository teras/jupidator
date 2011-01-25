/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.gui.BufferListener;
import com.panayotis.jupidator.data.UpdaterAppElements;

/**
 *
 * @author teras
 */
public abstract class ElementNative extends JupidatorElement {

    protected final String command;
    protected final String input;

    public ElementNative(String command, String file, String input, ExecutionTime time, UpdaterAppElements elements, ApplicationInfo appinfo) {
        super(file, elements, appinfo, time);
        this.command = command == null ? "" : appinfo.applyVariables(command);
        this.input = input == null ? null : appinfo.applyVariables(input);
    }

    public String fetch(UpdatedApplication application, BufferListener blisten) {
        return null;
    }

    public void cancel(UpdatedApplication application) {
    }

    public String prepare(UpdatedApplication application) {
        return null;
    }

    @Override
    public String getHash() {
        return command + ":" + super.getHash();
    }
}
