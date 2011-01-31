/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.mirror;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.elements.FileUtils;
import com.panayotis.jupidator.gui.BufferListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author teras
 */
public class MirrorList {

    private ArrayList<Mirror> mirrors = new ArrayList<Mirror>();

    public String downloadFile(MirroredFile file, File download_location, BufferListener watcher, UpdatedApplication app) {
        for (Mirror mirror : mirrors)
            try {
                String status = FileUtils.copyFile(mirror.getURL(file.getElements(), app).openStream(), new FileOutputStream(download_location), watcher);
                if (status == null)
                    return null;
            } catch (IOException ex) {
            }
        return _("Unable to download file " + file.getFile());
    }

    public void addMirror(Mirror mirror) {
        int location = mirrors.size() - 1;
        if (location < 0)
            location = 0;
        mirrors.add(location, mirror);
    }
}
