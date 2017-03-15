/*
 *
 * This file is part of Jupidator.
 *
 * Jupidator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 *
 * Jupidator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jupidator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package com.panayotis.jupidator.helpers;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.Updater;
import com.panayotis.jupidator.UpdaterException;
import com.panayotis.jupidator.producer.COutput;
import com.panayotis.jupidator.producer.CPath;
import com.panayotis.jupidator.versioning.SystemVersion;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import jupidator.launcher.OperatingSystem;

/**
 *
 * @author teras
 */
public class Launcher {

    private final static String emphOn, emphOff;

    static {
        if (OperatingSystem.isWindows)
            emphOn = emphOff = "";
        else {
            emphOn = "\033[0m\033[1m";
            emphOff = "\033[0m";
        }
    }

    public static void main(String[] sargs) {
        SimpleArgParser args = new SimpleArgParser(sargs);
        if (args.size() < 1)
            usage();
        if (args.has("record"))
            record(args);
        else if (args.has("diff"))
            diff(args);
        else if (args.has("changelog"))
            changelog(args);
        else
            update(args);
    }

    private static void usage() {
        System.err.println(emphOn + "Jupidator version " + SystemVersion.VERSION + " release " + SystemVersion.RELEASE + emphOff);
        System.err.println("Usage:");
        System.err.println();
        System.err.println(emphOn + "java -jar jupidator.jar URL [APPHOME [RELEASE [VERSION]]]" + emphOff);
        System.err.println("Start the update mechanism from the command line for a specific application. Valid options are:");
        System.err.println("    APPHOME : defaults to .");
        System.err.println("    RELEASE : defaults to 1");
        System.err.println("    VERSION : defaults to null");
        System.err.println();
        System.err.println(emphOn + "java -jar jupidator.jar --record [--arch ARCH] [--in DIR] [--out FILE]" + emphOff);
        System.err.println("Create a signature XML file to store current status of a specific directory, to be used later on with the --diff command. Valid options are:");
        System.err.println("    ARCH : the architecture of the parsed installation, defaults to 'any'. It should match the 'tag' attribute of the 'architect' element, of the build script.");
        System.err.println("    OUT  : the output file, defaults to stdout. If the file exists, it will append new data instead of replace.");
        System.err.println("    IN   : the input directory, defaults to '.'");
        System.err.println();
        System.err.println(emphOn + "java -jar jupidator.jar --diff [--version VERSION] [--old OLDRECS] [--out OUTPREFIX] ARCHPATH..." + emphOff);
        System.err.println("Find differences between a given set of records, as produced by the --record command, and save the upgrade files to disk. Valid options are:");
        System.err.println("    VERSION   : the current version, defaults to 1.0");
        System.err.println("    OLDRECS   : a list of input records, defaults to stdin. This is a file path list seperated by the path separator (currently '" + File.pathSeparator + "') of the records produced with the --record command.");
        System.err.println("    OUTPREFIX : the output directory, defaults to out/");
        System.err.println("    ARCHPATH  : a list of input paths, each one prepanded with the arch argument plus the path separator (currently '" + File.pathSeparator + "'). For example, if we have a folder named 'linux-32' which contains the binaries of the 'linux-i386' tag and a folder named 'linux-64' which contains the binaries of the 'linux-amd64' tag, the ARCHPATHs will be (without quotes) 'linux-i386" + File.pathSeparator + "linux-32 linux-amd64" + File.pathSeparator + "linux-64'");
        System.err.println();
        System.err.println(emphOn + "java -jar jupidator.jar --changelog --url URL [--out OUT]" + emphOff);
        System.err.println("Create a styled changelog file. Valid options are:");
        System.err.println("    URL : the location of the changelog");
        System.err.println("    OUT : the output file, defaults to stdout");
        System.err.println();
        System.exit(-1);
    }

    private static void diff(SimpleArgParser args) {
        String version = args.get("version", "1.0");
        String[] recs = args.get("old", "").split(File.pathSeparator);
        Map<String, CPath> olds = new HashMap<String, CPath>();
        try {
            if (recs.length == 0)
                olds.put("any", CPath.construct(new InputStreamReader(System.in, "UTF=8")));
            else
                for (String rec : recs) {
                    CPath old = CPath.construct(new InputStreamReader(new FileInputStream(rec), "UTF-8"));
                    olds.put(old.getArch(), old);
                }
            for (String archpath : args.getUnnamed()) {
                String[] apath = archpath.split(File.pathSeparator);
                if (apath.length != 2) {
                    displayError("ARCHPATH should have am ARCH part seperated with path separator by a DIR path");
                    return;
                }
                CPath current = CPath.construct(new File(apath[1]), apath[0]);
                CPath old = olds.get(current.getArch());
                if (old == null) {
                    displayError("Not a match for arch '" + apath[0] + "' in the list of provided records");
                    return;
                }
                current.findDiff(old, new COutput(args.get("out"), version), version);
            }
        } catch (IOException ex) {
            displayError(ex);
        }
    }

    private static void record(SimpleArgParser args) {
        String arch = args.get("arch");
        if (arch == null || arch.isEmpty())
            arch = "any";
        String outfile = null;
        Writer out = null;
        try {
            String indir = args.get("in");
            File infile = indir == null || indir.isEmpty() ? new File(System.getProperty("user.dir")) : new File(indir);
            outfile = args.get("out");
            out = new OutputStreamWriter(outfile == null ? System.out : new FileOutputStream(outfile), "UTF-8");
            CPath.construct(infile, arch).dump(out);
        } catch (IOException ex) {
            displayError(ex);
        } finally {
            if (out != null && outfile != null)
                try {
                    out.close();
                } catch (IOException ex) {
                }
        }
    }

    private static void changelog(SimpleArgParser args) {
        String url = args.get("url");
        if (url == null) {
            displayError("The changelog input URL is required.");
            return;
        }
        String outfile = null;
        Writer out = null;
        try {
            outfile = args.get("out");
            out = new OutputStreamWriter(outfile == null ? System.out : new FileOutputStream(outfile), "UTF-8");
            out.write(new Updater(url, ".", null).getChangeLog());
            out.close();
        } catch (UpdaterException ex) {
            displayError(ex);
        } catch (IOException ex) {
            displayError(ex);
        } finally {
            if (out != null && outfile != null)
                try {
                    out.close();
                } catch (IOException ex) {
                }
        }
    }

    private static void update(SimpleArgParser args) {
        args.nameArguments("url", "apphome", "release", "version");
        String URL = args.get("url");
        String APPHOME = args.get("apphome", ".");
        int RELEASE = Integer.parseInt(args.get("release", "1"));
        String VERSION = args.get("version");
        ApplicationInfo ap = new ApplicationInfo(APPHOME, RELEASE, VERSION);
        try {
            Updater upd = new Updater(URL, ap, null);
            upd.actionDisplay();
        } catch (UpdaterException ex) {
            displayError(ex);
        }
    }

    private static void displayError(String error) {
        System.out.println(error);
        System.err.println();
        usage();
        System.exit(-1);
    }

    private static void displayError(Exception ex) {
        StringWriter out = new StringWriter();
        ex.printStackTrace(new PrintWriter(out));
        displayError(out.toString());
    }
}
