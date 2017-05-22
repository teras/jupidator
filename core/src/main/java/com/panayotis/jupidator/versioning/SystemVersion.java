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
package com.panayotis.jupidator.versioning;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author teras
 */
public class SystemVersion {

    public static final String URL;
    public static final int RELEASE;
    public static final String VERSION;

    static {
        Properties props = new Properties();
        try {
            props.load(SystemVersion.class.getResourceAsStream("/com/panayotis/jupidator/versioning/current.properties"));
        } catch (IOException ex) {
        }
        VERSION = props.getProperty("version");
        RELEASE = Integer.parseInt(props.getProperty("release"));
        URL = props.getProperty("url");
    }
}
