/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.data.TextUtils;
import com.panayotis.jupidator.data.UpdaterAppElements;

/**
 *
 * @author teras
 */
public abstract class ElementChstatus extends ElementNative {

    private String attr = "";
    private boolean recursive = false;

    public ElementChstatus(String command, String file, String attr, String recursive, UpdaterAppElements elements, ApplicationInfo info) {
        super(command, file, null, ExecutionTime.AFTER, elements, info);
        if (attr != null)
            this.attr = attr;
        this.recursive = TextUtils.isTrue(recursive);
    }

    protected String[] getExecArguments() {
        if (isWindows())
            return new String[] {""};
        
        String res[] = new String[recursive ? 3 : 2];
        int which = 0;
        if (recursive)
            res[which++] = "-R";
        res[which++] = attr;
        res[which++] = getDestinationFile();
        return res;
    }
}
