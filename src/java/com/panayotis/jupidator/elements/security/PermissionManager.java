/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.security;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author teras
 */
public class PermissionManager implements Serializable {

    private boolean reqprev = false;

    public boolean isRequiredPrivileges() {
        return reqprev;
    }

    public boolean estimatePrivileges(File file) {
        boolean estimation = !isWritable(file);
        reqprev |= estimation;
        return estimation;
    }

    public boolean forcePrivileges() {
        reqprev = true;
        return true;
    }

    private static boolean isWritable(File f) {
        if (f == null)
            throw new NullPointerException("Updated file could not be null.");
        if (!isParentWritable(f))
            return false;
        if (!f.exists())
            return true;
        return isWritableLoop(f);
    }

    private static boolean isWritableLoop(File f) {
        if (f.isDirectory()) {
            File dir[] = f.listFiles();
            for (int i = 0; i < dir.length; i++)
                if (!isWritable(dir[i]))
                    return false;
            return true;
        } else
            return canWrite(f);
    }

    private static boolean isParentWritable(File f) {
        File p = f.getParentFile();
        if (p == null)  // No parent file - can't work on root files
            return false;
        if (f.exists())
            /* we are sure that a parent exists for this file */
            return canWrite(p);
        else if (p.exists()) {
            /* Check if parent file is directory AND can write in it */
            if (p.isDirectory() && canWrite(p))
                return true;
            return false;
        } else
            /* directories created (?) */
            return p.mkdirs();
    }

    public static boolean canWrite(File f) {
        return f.canWrite();
    }
}
