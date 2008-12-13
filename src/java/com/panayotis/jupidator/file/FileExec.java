/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.data.UpdaterAppElements;
import java.util.ArrayList;

/**
 *
 * @author teras
 */
public class FileExec extends FileNative {

    private ArrayList<String> arguments;

    public FileExec(String command, UpdaterAppElements elements, ApplicationInfo info) {
        super(command, String.valueOf(Math.random()), elements, info);  // No hash for this exec
        arguments = new ArrayList<String>();
    }

    public void addArgument(String value) {
        arguments.add(value);
    }

    protected String[] getExecArguments() {
        String[] args = new String[arguments.size()];
        for (int i = 0; i < args.length; i++)
            args[i] = arguments.get(i);
        return args;
    }
}
