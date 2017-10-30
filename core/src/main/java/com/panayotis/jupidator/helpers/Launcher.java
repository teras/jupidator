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
import com.panayotis.jupidator.versioning.SystemVersion;
import java.io.PrintWriter;
import java.io.StringWriter;
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

    public static void main(String[] args) {
        if (args.length < 1)
            usage();
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
        System.exit(-1);
    }

    private static void update(String[] args) {
        String URL = args[0];
        String APPHOME = args.length > 1 ? args[1] : null;
        int RELEASE = args.length > 2 ? Integer.parseInt(args[2]) : 1;
        String VERSION = args.length > 3 ? args[3] : null;
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
