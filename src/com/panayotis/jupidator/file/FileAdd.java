/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import com.panayotis.jupidator.list.*;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.download.Downloader;
import com.panayotis.jupidator.file.FileUtils;
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
            Downloader.download(fromfile, tofile + ".updated");
        } catch (IOException ex) {
            return "Unable to download file " + fromfile;
        }
        return null;
    }
}
