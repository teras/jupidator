/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import static com.panayotis.jupidator.i18n.I18N._;
import static com.panayotis.jupidator.file.FileUtils.FS;

import com.panayotis.jupidator.list.*;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.gui.BufferListener;
import java.io.File;

/**
 *
 * @author teras
 */
public class FileRm extends FileElement {

    public FileRm(String name, String dest, UpdaterAppElements elements, ApplicationInfo info) {
        super(name, dest, "0", elements, info);
    }

    public String toString() {
        return "-" + getHash();
    }

    public String getArgument() {
        return dest + FS + name;
    }

    /* Nothig to download, but it will be faster if we check files here */
    public String fetch(UpdatedApplication application, BufferListener blisten) {
        File f = new File(dest + FS + name);
        if ((!f.exists()) || (f.getParentFile().canWrite() && FileUtils.isWritable(f))) {
            if (application != null)
                application.receiveMessage(_("File {0} will be deleted, if exists.", f.getPath()));
            return null;
        }
        String msg = _("File {0} could not be deleted.", f.getPath());
        if (application != null)
            application.receiveMessage(msg);
        return msg;
    }

    /* Nothing to deploy */
    public String deploy(UpdatedApplication application) {
        return null;
    }

    public void cancel(UpdatedApplication application) {
        if (application != null)
            application.receiveMessage(_("Cancel updating: Ignoring deleting of file {0}", dest + FS + name));
    }
}
