/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.data.TextUtils;
import com.panayotis.jupidator.data.UpdaterAppElements;
import jupidator.launcher.XEChstatus;
import jupidator.launcher.XElement;

/**
 *
 * @author teras
 */
public abstract class ElementChstatus extends ElementNative {

    private final String attr;
    private final boolean recursive;

    public ElementChstatus(String command, String file, String attr, String recursive, UpdaterAppElements elements, ApplicationInfo info) {
        super(command, file, null, ExecutionTime.AFTER, elements, info);
        this.attr = attr == null ? "" : attr;
        this.recursive = TextUtils.isTrue(recursive);
    }

    @Override
    public String prepare(UpdatedApplication application) {
        return attr.equals("") ? _("Unable to provide empty attribute for file {0}", getFileName()) : null;
    }

    @Override
    public XElement getExecElement() {
        return new XEChstatus(command, getDestinationFile(), attr, recursive);
    }
}
