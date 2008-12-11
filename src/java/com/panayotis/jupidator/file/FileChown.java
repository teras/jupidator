/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.list.UpdaterAppElements;

/**
 *
 * @author teras
 */
public class FileChown extends FileChstatus {

    public FileChown(String file, String attr, String recursive, UpdaterAppElements elements, ApplicationInfo info) {
        super("chown", file, attr, recursive, elements, info);
    }
}
