/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.gui.console;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.Updater;
import com.panayotis.jupidator.UpdaterException;
import com.panayotis.jupidator.gui.JupidatorGUI;
import com.panayotis.jupidator.list.UpdaterAppElements;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author teras
 */
public class ConsoleGUI implements JupidatorGUI {

    private String info1,  info2;
    private Updater callback;

    public void setInformation(Updater callback, UpdaterAppElements el, ApplicationInfo info) throws UpdaterException {
        info1 = _("A new version of {0} is available!", el.getAppName());
        info2 = _("{0} version {1} is now available - you have {2}.", el.getAppName(), el.getNewVersion(), info.getVersion());
        this.callback = callback;
    }

    public void startDialog() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(info1);
            System.out.println(info2);
            System.out.print("Do you want to (S)kip this version, (R)emind later or (I)nstall? [SRI] ");
            int data;
            while ((data = in.read()) != -1) {
                if (data == 'S' || data == 's') {
                    callback.actionIgnore();
                    break;
                }
                if (data == 'R' || data == 'r') {
                    callback.actionDefer();
                    break;
                }
                if (data == 'I' || data == 'i') {
                    callback.actionCommit();
                    break;
                }
            }

        } catch (IOException ex) {
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException ex1) {
            }
        }

    }

    public void endDialog() {
        System.out.println(_("Thank you for using Jupidator  (http://www.jupidator.com)"));
    }

    public void errorOnCommit(String message) {
        System.out.println(_("Error: {0}", message));
    }

    public void successOnCommit() {
        System.out.println("Upgrade successfull");
    }

    public void setDownloadRatio(String ratio, float percent) {
        System.out.println(_("Downloading {0}, {1} percent completed.", ratio, percent*100));
    }

    public void setIndetermined() {
        System.out.println("Please wait...");
    }
}
