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
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.Updater;
import com.panayotis.jupidator.UpdaterException;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author teras
 */
public class ChangeLogCreator {

    @SuppressWarnings("CallToThreadDumpStack")
    public static void main(String[] args) {
        {
            FileWriter out = null;
            try {
                if (args.length < 2) {
                    System.err.println("Two argument required:\n  ChangeLog URL\n  ChangeLog output file");
                    return;
                }
                String cl = new Updater(args[0], (ApplicationInfo) null, (UpdatedApplication) null).getChangeLog();
                out = new FileWriter(args[1]);
                out.write(cl);
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (UpdaterException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
