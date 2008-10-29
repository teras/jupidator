/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import com.panayotis.jupidator.list.*;
import com.panayotis.jupidator.ApplicationInfo;
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

    public String action() {
        String tofile = dest+SEP+name;
        File f = new File(tofile);
        if (f.exists()) {
            if (!f.delete()) {
                return "File " + tofile + " can not be deleted.";
            }
        }
        return null;
    }
}