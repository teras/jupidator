/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.gui.BufferListener;
import com.panayotis.jupidator.data.UpdaterAppElements;

/**
 *
 * @author teras
 */
public abstract class FileNative extends FileElement {

    private static final boolean isWindows,  isLinux,  isMac;
    private String command = "";
    private String input = "";


    static {
        String OS = System.getProperty("os.name").toLowerCase();
        isWindows = OS.startsWith("windows");
        isMac = OS.startsWith("mac");
        isLinux = OS.startsWith("linux");
    }

    protected final static boolean isWindows() {
        return isWindows;
    }

    protected final static boolean isMac() {
        return isMac;
    }

    protected final static boolean isLinux() {
        return isLinux;
    }

    public FileNative(String command, String file, String input, ExecutionTime time, UpdaterAppElements elements, ApplicationInfo appinfo) {
        super(file, elements, appinfo, time);
        if (command != null)
            this.command = appinfo.updatePath(command);
        if (input != null)
            this.input = appinfo.updatePath(input);
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

    public String getArgument() {
        String[] args = getExecArguments();
        StringBuffer b = new StringBuffer();

        b.append('c');
        b.append(command.length()).append('#');
        for (int i = 0; i < args.length; i++) {
            b.append(args[i].length()).append('#');
        }
        b.append(input.length()).append('#');
        b.append(".");

        b.append(command);
        for (int i = 0; i < args.length; i++) {
            b.append(args[i]);
        }
        b.append(input);
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
