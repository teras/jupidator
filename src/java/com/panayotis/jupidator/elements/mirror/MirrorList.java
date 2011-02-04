/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.mirror;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.elements.FileUtils;
import com.panayotis.jupidator.elements.security.Digester;
import com.panayotis.jupidator.gui.BufferListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author teras
 */
public class MirrorList {

    private ArrayList<Mirror> mirrors = new ArrayList<Mirror>();

    public String downloadFile(MirroredFile file, File download_location, BufferListener watcher, UpdatedApplication app) {
        String reason = "";
        for (Mirror mirror : mirrors) {
            watcher.freezeSize();
            try {
                /* Create URL */
                URL url = mirror.getURL(file.getElements());
                app.receiveMessage(_("Request URL {0}", url.toString()));
                /* Download file */
                String status = FileUtils.copyFile(url.openStream(), new FileOutputStream(download_location), watcher);
                /* Check download status */
                if (status != null)
                    reason = status;
                else if (download_location.length() != file.getSize())
                    reason = "Wrong size, required " + file.getSize() + ", found " + download_location.length();
                else if (!isProperlyDigested(file, download_location))
                    reason = "Not properly digested";
                else
                    return null;
            } catch (IOException ex) {
                reason = ex.getMessage();
            }
            watcher.rollbackSize();
        }
        return _("Unable to download file " + file.getFile() + " : " + reason);
    }

    private boolean isProperlyDigested(MirroredFile file, File download_location) {
        for (Digester d : file.getDigesters())
            if (!d.checkFile(download_location))
                return false;
        return true;
    }

    public void addMirror(Mirror mirror) {
        int location = mirrors.size() - 1;
        if (location < 0)
            location = 0;
        mirrors.add(location, mirror);
    }
}
