/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.list.UpdaterAppElements;

/**
 *
 * @author teras
 */
public class FileChmod extends FileNativeExec {

    public FileChmod(String file, UpdaterAppElements elements, ApplicationInfo info) {
        super(file, elements, info);
    }

    public String deploy(UpdatedApplication application) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
