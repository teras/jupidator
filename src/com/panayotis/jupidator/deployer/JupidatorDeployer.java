/*
 * JupidatorDeployer.java
 *
 * Created on September 29, 2008, 5:10 PM
 */

package com.panayotis.jupidator.deployer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author teras
 */
public class JupidatorDeployer {
    public static final String EXTENSION = ".jupidator";
    private static BufferedWriter out;
    

    static {
        try {
            String filename = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "jupidator." + new SimpleDateFormat("yyyyMMdd_hhmmss").format(Calendar.getInstance().getTime()) + ".log";
            out = new BufferedWriter(new FileWriter(filename));
        } catch (IOException ex) {
        }
    }

    private static void debug(String message) {
        try {
            if (out != null) {
                out.write(message);
                out.newLine();
                out.flush();
            }
        } catch (IOException ex) {
        }
    }

    private static void endDebug() {
        if (out != null) {
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }

    public static void main(String[] args) {
        try {
            new JupidatorDeployer();
            debug("Start log of Jupidator Deployer with arguments:");
            for (int i = 0; i < args.length; i++) {
                debug("  #" + i + ": " + args[i]);
            }

            int files = Integer.valueOf(args[0]);
            debug("Number of affected files: " + files);

            for (int i = 1; i <= files; i++) {
                boolean rm = args[i].charAt(0) == '-';
                String path = args[i].substring(1, args[i].length());
                debug("Working with " + path + "");
                if (rm) {
                    debug("  Deleting file " + path);
                    if (!rmTree(new File(path)))
                        debug("*ERROR* Unable to delete file "+path);
                } else {
                    String oldpath = path.substring(0, path.length() - EXTENSION.length());
                    File oldfile = new File(oldpath);
                    File newfile = new File(path);

                    debug("  Deleting file " + oldfile);
                    if (!rmTree(oldfile))
                        debug("*ERROR* Unable to remove old file "+oldpath);
                    debug("  Renaming " + path + " to " + oldfile);
                    newfile.renameTo(oldfile);
                }
                debug("End of works with " + path);
            }

            files++;
            String exec[] = new String[args.length - files];
            debug("Restarting application with following arguments:");
            for (int i = files; i < args.length; i++) {
                exec[i - files] = args[i];
                debug("  #" + (i - files) + ": " + exec[i - files]);
            }
            try {
                Runtime.getRuntime().exec(exec);
            } catch (IOException ex) {
            }

        } catch (Exception ex) {
            debug("Exception found: " + ex.toString());
        } finally {
            endDebug();
            System.exit(0);
        }
    }

    public static boolean rmTree(File f) {
        if (!f.exists())
            return true;
        if (f.isDirectory()) {
            File dir[] = f.listFiles();
            for (int i = 0; i < dir.length; i++) {
                if (!rmTree(dir[i]))
                    return false;
            }
        }
        return f.delete();
    }

}
