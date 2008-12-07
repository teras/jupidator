/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
