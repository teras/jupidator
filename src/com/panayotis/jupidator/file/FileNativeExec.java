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

    private String command = "";

    public FileNativeExec(String command, String file, UpdaterAppElements elements, ApplicationInfo info) {
        super(file, elements, info, ExecutionTime.AFTER);
        if (command != null)
            this.command = command;
    }

    /* Nothing to do while fetching.
     * It is impossible to see if a file exists, BEFORE unzipping files, so work 
     * is done in JupidatorDeployer class
     */
    public String fetch(UpdatedApplication application, BufferListener blisten) {
        return null;
    }

    /* Nothing to do when cancelling */
    public void cancel(UpdatedApplication application) {
    }

    /* No arguments here - execution has been done on deploy time */
    public String getArgument() {
        String[] args = getExecArguments();
        StringBuffer b = new StringBuffer();

        b.append('c');
        b.append(command.length()).append("#");
        for (int i = 0; i < args.length; i++) {
            b.append(args[i].length()).append('#');
        }
        b.append(".");

        b.append(command);
        for (int i = 0; i < args.length; i++) {
            b.append(args[i]);
        }
        return b.toString();
    }

    /* Nothing to do when deploying */
    public String deploy(UpdatedApplication application) {
        return null;
    }

    public String getHash() {
        return command + ":" + super.getHash();
    }

    protected abstract String[] getExecArguments();
}
