/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.gui.BufferListener;
import com.panayotis.jupidator.list.UpdaterAppElements;

/**
 *
 * @author teras
 */
public abstract class FileNativeExec extends FileElement {

    public FileNativeExec(String file, UpdaterAppElements elements, ApplicationInfo info) {
        super(file, elements, info);
    }

    /* Nothing to do while fetching.
     * It is impossible to see if a file exists, BEFORE unzipping files
     */
    public String fetch(UpdatedApplication application, BufferListener blisten) {
        return null;
    }

    /* Nothing to do when cancelling */
    public void cancel(UpdatedApplication application) {
    }

    /* No arguments here - execution has been done on deploy time */
    public String getArgument() {
        return "";
    }
}
