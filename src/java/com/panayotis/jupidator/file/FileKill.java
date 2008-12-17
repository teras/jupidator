/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.data.UpdaterAppElements;

/**
 *
 * @author teras
 */
public class FileKill extends FileNative {

    private String signal = "";

    public FileKill(String application, String signal, UpdaterAppElements elements, ApplicationInfo info) {
        super("kill", application, null, ExecutionTime.BEFORE, elements, info);
        if (signal != null)
            this.signal = signal.toUpperCase();
    }

    protected String[] getExecArguments() {
        String res[] = new String[2];
        res[0] = signal;
        res[1] = getDestinationFile();
        return res;
    }

    protected String getCommandTag() {
        return "k";
    }
}
