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
import java.util.ArrayList;
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
                if (args[i].length() > 0) {
                    String data = args[i].substring(1, args[i].length());
                    debug("Working with " + data + "");
                    switch (args[i].charAt(0)) {
                        case '-':
                            debug("  Deleting file " + data);
                            if (!rmTree(new File(data)))
                                debug("*ERROR* Unable to delete file " + data);
                            break;
                        case '+':
                            String oldpath = data.substring(0, data.length() - EXTENSION.length());
                            File oldfile = new File(oldpath);
                            File newfile = new File(data);

                            debug("  Deleting file " + oldfile);
                            if (!rmTree(oldfile))
                                debug("*ERROR* Unable to remove old file " + oldpath);
                            debug("  Renaming " + data + " to " + oldfile);
                            newfile.renameTo(oldfile);
                            break;
                        case 'c':
                            exec(data);
                            break;
                    }
                    debug("End of works with " + data);
                }
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

    private static void exec(String arguments) {
        try {
            String[] cmd = buildArgs(arguments);
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            if (p.exitValue() != 0) {
                debug("  Error while executing " + cmd[0]);
            } else {
                debug("  Successfully executied " + cmd[0]);
            }
        } catch (Exception ex) {
            debug(ex.getMessage());
        }
    }

    private static String[] buildArgs(String arguments) throws NumberFormatException, StringIndexOutOfBoundsException {
        ArrayList<Integer> sizes = new ArrayList<Integer>();
        int pos = 0;
        int lastpos = 0;
        while ((pos = arguments.indexOf("#", pos)) > 0) {
            sizes.add(Integer.valueOf(arguments.substring(lastpos, pos++)));
            lastpos = pos;
            if (arguments.charAt(pos) == '.')
                break;
        }
        pos++;
        String[] args = new String[sizes.size()];
        for (int i = 0; i < sizes.size(); i++) {
            args[i] = arguments.substring(pos, pos + sizes.get(i));
            pos += sizes.get(i);
            debug("  #" + i + ": " + args[i]);
        }
        return args;
    }
}
