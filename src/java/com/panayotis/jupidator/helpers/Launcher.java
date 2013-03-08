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
import com.panayotis.jupidator.constructor.CPath;
import com.panayotis.jupidator.versioning.SystemVersion;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author teras
 */
public class Launcher {

    @SuppressWarnings("CallToThreadDumpStack")
    public static void main(String[] args) {
        if (args.length < 1)
            usage();
        if (args[0].equals("-l") || args[0].equals("--list"))
            list(args);
        else if (args[0].equals("-c") || args[0].equals("--compare"))
            compare(args);
        else
            update(args);
    }

    private static void usage() {
        System.err.println("Jupidator version " + SystemVersion.VERSION + " release " + SystemVersion.RELEASE);
        System.err.println("Usage:");
        System.err.println("java -jar jupidator.jar [-u|--update] URL [APPHOME [RELEASE [VERSION [APPSUPPORTDIR]]]]");
        System.err.println("     APPHOME defaults to .");
        System.err.println("     RELEASE defaults to 1");
        System.err.println("     VERSION defaults to null");
        System.err.println("     APPSUPPORTDIR defaults to APPHOME");
        System.err.println("java -jar jupidator.jar -l|--list [PATH]");
        System.err.println("     PATH defaults to .");
        System.err.println("java -jar jupidator.jar -c|--compare [PATH [INFILE [OUTDIR]]]");
        System.err.println("     PATH defaults to .");
        System.err.println("     INFILE defaults to stdin");
        System.err.println("     OUTDIR defaults to output_DATE");
        System.err.println();
        System.exit(-1);
    }

    private static void compare(String[] args) {
        try {
            CPath current = CPath.construct(new File(args.length < 2 ? "." : args[1]));
            CPath old = CPath.construct(new InputStreamReader(args.length < 3 ? System.in : new FileInputStream(args[2]), "UTF-8"));
            Writer out = new OutputStreamWriter(System.out, "UTF-8");
            old.dump(out);
            current.dump(out);

            String filename = args.length < 4 ? "output_" + new SimpleDateFormat("yMMdd_HHmmss").format(new Date()) : args[3];
            current.findDiff(old, new File(filename));
        } catch (IOException ex) {
            displayError(ex);
        }
    }

    private static void list(String[] args) {
        try {
            CPath path = CPath.construct(new File(args.length < 2 ? "." : args[1]));
            path.dump(new OutputStreamWriter(System.out, "UTF-8"));
        } catch (Exception ex) {
            displayError(ex);
        }
    }

    private static void update(String[] args) {
        String URL;
        String APPHOME = ".";
        int RELEASE = 1;
        String VERSION = null;
        String APPSUPPORTDIR = null;

        /* 
         * Ignore -u/--update parameter. 
         * This is useful when the pathname is indeed one of the option shortcuts, like "-c" or "--update"
         */
        if (args[0].equals("-u") || args[0].equals("--update")) {
            String[] nargs = new String[args.length - 1];
            System.arraycopy(args, 1, nargs, 0, nargs.length);
            args = nargs;
        }

        URL = args[0];
        if (args.length > 1)
            APPHOME = args[1];
        if (args.length > 2)
            try {
                RELEASE = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
            }
        if (args.length > 3)
            VERSION = args[3];
        if (args.length > 4)
            APPSUPPORTDIR = args[4];

        ApplicationInfo ap = new ApplicationInfo(APPHOME, APPSUPPORTDIR, RELEASE, VERSION);

        try {
            Updater upd = new Updater(URL, ap, null);
            upd.actionDisplay();
        } catch (UpdaterException ex) {
            displayError(ex);
        }
    }

    private static void displayError(Exception ex) {
        try {
            ex.printStackTrace(new PrintWriter(new OutputStreamWriter(System.err, "UTF-8"), true));
        } catch (UnsupportedEncodingException ex1) {
        }
        System.err.println();
        usage();
    }
}
