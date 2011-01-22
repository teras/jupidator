/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.data.UpdaterAppElements;

/**
 *
 * @author teras
 */
public class ElementKill extends ElementNative {

    private String signal = "";

    public ElementKill(String application, String signal, UpdaterAppElements elements, ApplicationInfo info) {
        super("kill", application, null, ExecutionTime.BEFORE, elements, info);
        if (signal != null)
            this.signal = signal.toUpperCase();
    }

    @Override
    protected boolean estimatePrivileges(UpdaterAppElements elements) {
        return elements.permissionManager.forcePrivileges();
    }

    protected String[] getExecArguments() {
        String res[] = new String[2];
        res[0] = signal;
        res[1] = getDestinationFile();
        return res;
    }

    @Override
    protected String getCommandTag() {
        return "k";
    }
}
