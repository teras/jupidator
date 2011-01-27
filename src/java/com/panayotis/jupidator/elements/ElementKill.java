/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.data.UpdaterAppElements;
import com.panayotis.jupidator.elements.security.PermissionManager;
import jupidator.launcher.XEKill;
import jupidator.launcher.XElement;

/**
 *
 * @author teras
 */
public class ElementKill extends ElementNative {

    private final String signal;

    public ElementKill(String application, String signal, UpdaterAppElements elements, ApplicationInfo info) {
        super("kill", application, null, ExecutionTime.BEFORE, elements, info);
        this.signal = signal == null ? null : signal.toUpperCase();
    }

    @Override
    protected boolean estimatePrivileges(UpdaterAppElements elements) {
        return PermissionManager.manager.forcePrivileges();
    }

    @Override
    public XElement getExecElement() {
        return new XEKill(getDestinationFile(), signal);
    }
}
