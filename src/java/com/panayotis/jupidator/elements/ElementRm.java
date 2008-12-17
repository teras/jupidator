/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.data.*;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.gui.BufferListener;
import java.io.File;

/**
 *
 * @author teras
 */
public class ElementRm extends FileElement {

    public ElementRm(String file, UpdaterAppElements elements, ApplicationInfo info) {
        super(file, elements, info, ExecutionTime.MID);
    }

    public String toString() {
        return "-" + getDestinationFile();
    }

    public String getArgument() {
        return "-" + getDestinationFile();
    }

    /* Nothig to download, but it will be faster if we check files here */
    public String fetch(UpdatedApplication application, BufferListener blisten) {
        File f = new File(getDestinationFile());
        if ((!f.exists()) || (f.getParentFile().canWrite() && FileUtils.isWritable(f))) {
            application.receiveMessage(_("File {0} will be deleted, if exists.", f.getPath()));
            return null;
        }
        String msg = _("File {0} could not be deleted.", f.getPath());
        application.receiveMessage(msg);
        return msg;
    }

    /* Nothing to deploy */
    public String deploy(UpdatedApplication application) {
        return null;
    }

    public void cancel(UpdatedApplication application) {
        application.receiveMessage(_("Cancel updating: Ignoring deleting of file {0}", getDestinationFile()));
    }
}
