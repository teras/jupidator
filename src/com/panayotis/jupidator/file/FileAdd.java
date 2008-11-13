/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.list.*;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.download.Downloader;
import java.io.IOException;

/**
 * 
 * @author teras
 */
public class FileAdd extends FileElement {

    /** This is actually a URL */
    private String source;

    public FileAdd(String name, String source, String dest, UpdaterAppElements elements, ApplicationInfo info) {
        super(name, dest, elements, info);
        if (source == null)
            source = "";
        this.source = elements.getBaseURL() + source;
    }

    public String toString() {
        return "+" + source + SEP + name + ">" + getDestination();
    }

    public String action() {
        String fromfile = source+"/"+name;
        String tofile = dest+SEP+name;
        try {
            FileUtils.fileIsValid(tofile, "Destination file");
        } catch (IOException ex) {
            return "File " + tofile + " can not be created.";
        }
        try {
            Downloader.download(fromfile+"d", tofile + ".updated");
        } catch (IOException ex) {
            return _("Unable to download file: {0}" , name);
        }
        return null;
    }
}
