/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import static com.panayotis.jupidator.i18n.I18N._;

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
        super(name, dest, elements, info);
    }

    public String toString() {
        return "-" + getHash();
    }

    public String action(UpdaterListener listener) {
        String tofile = dest + SEP + name;
        File f = new File(tofile);
        if (f.exists()) {
            if (!f.delete()) {
                String msg = _("File {0} can not be deleted.", tofile);
                if (listener != null)
                    listener.receiveMessage(msg);
                return msg;
            }
        }
        if (listener != null)
            listener.receiveMessage(_("File {0} successfully deleted."));
        return null;
    }
}
