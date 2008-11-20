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
public class FileChstatus extends FileNativeExec {

    private String attr = "";
    private boolean recursive = false;

    public FileChstatus(String command, String file, String attr, String recursive, UpdaterAppElements elements, ApplicationInfo info) {
        super(command, file, elements, info);
        if (attr != null)
            this.attr = attr;
        if (recursive != null && (!recursive.equals(""))) {
            recursive = recursive.toLowerCase();
            this.recursive = "true".startsWith(recursive) || "yes".startsWith(recursive) || recursive.equals("1");
        }
    }

    protected String[] getExecArguments() {
        String res[] = new String[recursive ? 3 : 2];
        int which = 0;
        if (recursive)
            res[which++] = "-R";
        res[which++] = attr;
        res[which++] = getDestinationFile();
        return res;
    }
}
