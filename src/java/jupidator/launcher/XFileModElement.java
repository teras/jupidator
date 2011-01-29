/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author teras
 */
public abstract class XFileModElement extends XTargetElement {

    public XFileModElement(String target) {
        super(target);
    }

    protected static boolean safeDelete(File f) {
        if (!f.exists())
            return true;
        if (f.isDirectory()) {
            File dir[] = f.listFiles();
            for (int i = 0; i < dir.length; i++)
                if (!safeDelete(dir[i]))
                    return false;
        }
        return f.delete();
    }

    protected static boolean safeMkDir(File dir) {
        if (dir.exists())
            return dir.isDirectory();
        return dir.mkdirs();
    }

    protected static boolean safeMv(File from, File to) {
        if (from.isDirectory()) {
            /* The target is a directory, copy it recursively */

            if (!safeMkDir(to)) // Make sure that the destination file is a directory and it exists
                return false;
            to = new File(to, from.getName()); // Define actual destination directory
            if (!safeMkDir(to)) // Fail if destination directory could not be created
                return false;

            for (File entry : from.listFiles())
                if (!safeMv(entry, to))
                    return false;
            return from.delete();
        } else {
            /* if it is not a directory, copy file */

            if (to.isDirectory())   // If we copy to a directory, copy this file inside the directory
                to = new File(to, from.getName());
            if (!safeMkDir(to.getParentFile())) // Fail if parent destination directory could not be created
                return false;

            boolean status = true;
            BufferedInputStream in = null;
            BufferedOutputStream out = null;
            byte[] buffer = new byte[4000];
            int hm;
            try {
                in = new BufferedInputStream(new FileInputStream(from));
                out = new BufferedOutputStream(new FileOutputStream(to));
                while ((hm = in.read(buffer)) > 0)
                    out.write(buffer, 0, hm);
            } catch (IOException ex) {
                status = false;
            } finally {
                try {
                    if (in != null)
                        in.close();
                } catch (IOException ex) {
                    status = false;
                }
                try {
                    if (out != null)
                        out.close();
                } catch (IOException ex) {
                    status = false;
                }
            }
            status &= from.delete();
            return status;
        }
    }
}