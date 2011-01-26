/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.security;

import com.panayotis.jupidator.elements.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author teras
 */
public class PermissionManager implements Serializable {

    private boolean reqprev = false;
    private int slots = 0;
    private final File downloadLocation;

    public PermissionManager() {
        File dl = null;
        try {
            dl = File.createTempFile("jupidator_download_", "");
            dl.delete();
        } catch (IOException ex) {
        }
        downloadLocation = dl;
        System.out.println("Permission location is "+downloadLocation.getPath());
    }

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

    public File requestSlot() {
        return new File(downloadLocation, "slot" + (++slots));
    }

    public void cleanUp() {
        FileUtils.rmRecursive(downloadLocation);
    }

    public File getRestartObject() {
        return new File(downloadLocation, "parameters");
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
            return FileUtils.makeDirectory(p);
    }

    public static boolean canWrite(File f) {
        return f.canWrite();
    }
}
