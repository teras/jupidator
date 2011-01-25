/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.data.UpdaterAppElements;
import com.panayotis.jupidator.gui.BufferListener;
import jupidator.launcher.XERm;
import jupidator.launcher.XElement;

/**
 *
 * @author teras
 */
public class ElementRm extends JupidatorElement {

    public ElementRm(String file, UpdaterAppElements elements, ApplicationInfo info) {
        super(file, elements, info, ExecutionTime.MID);
    }

    @Override
    public String toString() {
        return "-" + getDestinationFile();
    }

    /* Nothig to download */
    public String fetch(UpdatedApplication application, BufferListener blisten) {
        return null;
    }

    /* Nothing to deploy */
    public String prepare(UpdatedApplication application) {
        application.receiveMessage(_("File {0} will be deleted, if exists.", getDestinationFile()));
        return null;
    }

    public void cancel(UpdatedApplication application) {
    }

    @Override
    public XElement getExecElement() {
        return new XERm(getDestinationFile());
    }
}
