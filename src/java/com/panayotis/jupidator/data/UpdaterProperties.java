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
package com.panayotis.jupidator.data;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdaterException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Properties;

/**
 *
 * @author teras
 */
public class UpdaterProperties implements Serializable  {

    private final static String TIMEIGNORE = "Updater.Version.NextCheck";
    private final static String VERSIONIGNORE = "Updater.Version.Ignore";
    private final Properties opts;
    private ApplicationInfo appinfo;

    public UpdaterProperties(ApplicationInfo appinfo) throws UpdaterException {
        opts = new Properties();
        if (appinfo == null)
            return;
        this.appinfo = appinfo;
        try {
            opts.loadFromXML(new FileInputStream(appinfo.getUpdaterConfigFile()));
            opts.remove("Updater.Version.Release");
            opts.remove("Updater.Version.LastCheck");
            storeOptions();
        } catch (IOException ex) {
        }
        appinfo.updateIgnoreRelease(opts.getProperty(VERSIONIGNORE, "0"));
    }

    public boolean isTooSoon() {
        long now = Calendar.getInstance().getTimeInMillis();
        try {
            long last = Long.parseLong(opts.getProperty(TIMEIGNORE, "-1"));
            long next = last + 1000 * 60 * 60 * 24;
            if (now < next)
                return true;
        // It's too soon - We don't need to check it, yet
        } catch (NumberFormatException e) { // if something went wrong, just check web version  
        }
        return false;
    }

    public void defer() {
        opts.put(TIMEIGNORE, Long.toString(Calendar.getInstance().getTimeInMillis()));
        storeOptions();
    }

    public void ignore(int newrelease) {
        opts.put(VERSIONIGNORE, Integer.toString(newrelease));
        storeOptions();
    }

    private void storeOptions() {
        try {
            opts.storeToXML(new FileOutputStream(appinfo.getUpdaterConfigFile()), "Jupidator Java Updater http://sourceforge.net/projects/jupidator/");
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to store config file : " + ex.getMessage());
        }
    }
}
