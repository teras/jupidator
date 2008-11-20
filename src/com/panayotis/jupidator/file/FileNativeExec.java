/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.gui.BufferListener;
import com.panayotis.jupidator.list.UpdaterAppElements;
import java.io.IOException;

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

    public String deploy(UpdatedApplication application) {
        try {
            String[] args = getExecArguments();
            String[] cmd = new String[args.length + 1];
            cmd[0] = command;
            System.arraycopy(args, 0, cmd, 1, args.length);
            StringBuffer b = new StringBuffer();
            for (int i = 0; i < cmd.length; i++)
                b.append(cmd[i]).append(" ");

            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            if (p.exitValue() != 0) {
                application.receiveMessage(_("Error while executing {0}", b.toString()));
                return "Error while executing " + command;
            } else {
                application.receiveMessage(_("Successfully executed {0}", b.toString()));
            }
        } catch (InterruptedException ex) {
            return ex.getMessage();
        } catch (IOException ex) {
            return ex.getMessage();
        }
        return null;
    }

    public String getHash() {
        return command + ":" + super.getHash();
    }

    protected abstract String[] getExecArguments();
}
