/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.security;

import com.panayotis.jupidator.UpdatedApplication;
import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.elements.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import jupidator.launcher.DeployerParameters;
import jupidator.launcher.JupidatorDeployer;

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

    public ProcessBuilder getLaunchCommand(UpdatedApplication application, DeployerParameters params) throws IOException {
        /* Copy Jupidator classes */
        String message = FileUtils.copyPackage(JupidatorDeployer.class.getPackage().getName(), downloadLocation.getPath());
        if (message != null)
            throw new IOException(message);
        /* Store requested working elements */
        File paramfile = new File(downloadLocation, "parameters");
        if (!params.storeParameters(paramfile))
            throw new IOException(_("Unable to initialize restart"));

        /* Construct command */
        ArrayList<String> command = new ArrayList<String>();
        command.add(FileUtils.JAVABIN);
        command.add("-cp");
        command.add(downloadLocation.getAbsolutePath());
        command.add(JupidatorDeployer.class.getName());
        command.add(paramfile.getAbsolutePath());

        /* Debug launch command */
        StringBuilder debug = new StringBuilder("Launching command: ");
        for (String cmd : command)
            debug.append(cmd).append(" ");
        application.receiveMessage(debug.toString());

        return new ProcessBuilder(command);
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
