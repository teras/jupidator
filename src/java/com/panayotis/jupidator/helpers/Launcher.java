/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.helpers;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.Updater;
import com.panayotis.jupidator.UpdaterException;

/**
 *
 * @author teras
 */
public class Launcher {

    public static void usage() {
        System.err.println("Usage:");
        System.err.println("java -jar jupidator.jar URL APPHOME [RELEASE [VERSION [APPSUPPORTDIR]]]");
        System.err.println();
    }

    public static void main(String[] args) {
        String URL = null;
        String APPHOME = null;
        String RELEASE = null;
        String VERSION = null;
        String APPSUPPORTDIR = null;

        if (args.length < 2) {
            usage();
            System.exit(1);
        }

        URL = args[0];
        APPHOME = args[1];
        if (args.length > 2)
            RELEASE = args[2];
        if (args.length > 3)
            VERSION = args[3];
        if (args.length > 4)
            APPSUPPORTDIR = args[4];


        ApplicationInfo ap = new ApplicationInfo(APPHOME, APPSUPPORTDIR, RELEASE, VERSION);

        try {
            new Updater(URL, ap, null).actionDisplay();
        } catch (UpdaterException ex) {
            ex.printStackTrace();
        }
    }
}
