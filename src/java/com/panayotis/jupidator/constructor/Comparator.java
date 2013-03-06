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

package com.panayotis.jupidator.constructor;

import java.io.File;

/**
 *
 * @author teras
 */
public class Comparator {

    private final File in1;
    private final File in2;

    public Comparator(File file1, File file2, File out) {
        this.in1 = file1;
        this.in2 = file2;
    }

    private boolean checkInput(File f) {
        if (!f.exists()) {
            System.err.println("File " + f.getPath() + " does not exist");
            return false;
        }
        if (!f.isFile()) {
            System.err.println("Input file " + f.getPath() + " should be a regular file");
            return false;
        }
        if (!f.canRead()) {
            System.err.println("Unable to read from file " + f.getPath());
            return false;
        }
        return true;
    }

    public boolean compare() {
        if (!checkInput(in1) || !checkInput(in2))
            return false;
        return true;
    }
}
