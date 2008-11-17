/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import static com.panayotis.jupidator.i18n.I18N._;
import static com.panayotis.jupidator.file.FileUtils.FS;

import com.panayotis.jupidator.list.*;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdaterListener;
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
        return toString();
    }

    public String action(UpdaterListener listener) {
        String tofile = dest + FS + name;  // replace system variables
        File f = new File(tofile);
        if (f.exists()) {
            if (f.getParentFile().canWrite() && f.canWrite()) {
                if (listener != null)
                    listener.receiveMessage(_("File {0} will be deleted."));
                return null;
            }
            String msg = _("File {0} could not be deleted.", tofile);
            if (listener != null)
                listener.receiveMessage(msg);
            return msg;
        }
        return null;
    }

    public void cancel(UpdaterListener listener) {
        if (listener != null)
            listener.receiveMessage(_("Cancel updating: Ignoring deleting of file {0}", dest + FS + name));
    }
}
