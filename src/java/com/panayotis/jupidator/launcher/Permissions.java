/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.launcher;

import java.io.File;

/**
 *
 * @author teras
 */
public class Permissions {

    static boolean isWritable(File f) {
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
            return f.canWrite();
    }

    private static boolean isParentWritable(File f) {
        File p = f.getParentFile();
        if (p == null)  // No parent file - can't work on root files
            return false;
        if (f.exists())
            /* we are sure that a parent exists for this file */
            return p.canWrite();
        else if (p.exists()) {
            /* Check if parent file is directory AND can write in it */
            if (p.isDirectory() && p.canWrite())
                return true;
            return false;
        } else
            /* directories created (?) */
            return p.mkdirs();
    }
}
