/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.gui.console;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.Updater;
import com.panayotis.jupidator.UpdaterException;
import com.panayotis.jupidator.data.TextUtils;
import com.panayotis.jupidator.gui.JupidatorGUI;
import com.panayotis.jupidator.data.UpdaterAppElements;
import com.panayotis.jupidator.loglist.creators.TextCreator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author teras
 */
public class ConsoleGUI implements JupidatorGUI {

    private String info1,  info2,  loglist;
    private Updater callback;
    private boolean is_loglist_enabled = true;
    private BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));

    public void setInformation(Updater callback, UpdaterAppElements el, ApplicationInfo info) throws UpdaterException {
        info1 = _("A new version of {0} is available!", el.getAppName());
        info2 = _("{0} version {1} is now available - you have {2}.", el.getAppName(), el.getNewVersion(), info.getVersion());
        if (is_loglist_enabled)
            loglist = TextCreator.getList(el.getLogList());
        this.callback = callback;
    }

    public void startDialog() {
        System.out.println(info1);
        System.out.println(info2);
        if (is_loglist_enabled && getAnswer(_("Do you want to see the detailed changelog? [Y/n] "), "n") != 'n') {
            System.out.println();
            System.out.println(loglist);
        }
        boolean valid = false;
        while (!valid) {
            valid = true;   // Be optimistic; will handle this in default section
            switch (getAnswer(_("Do you want to (S)kip this version, (R)emind later or (I)nstall? [s/r/i] "), "sri")) {
                case 's':
                    callback.actionIgnore();
                    break;
                case 'r':
                    callback.actionDefer();
                    break;
                case 'i':
                    callback.actionCommit();
                    break;
                default:
                    System.out.println(_("Wrong answer."));
                    valid = false;
            }
        }
    }

    public void endDialog() {
        System.out.println(_("Thank you for using Jupidator  (http://www.jupidator.com)"));
        System.out.println();
    }

    public void errorOnCommit(String message) {
        System.out.println(_("Error: {0}", message));
    }

    public void successOnCommit() {
        System.out.println(_("Downloading successfull"));
        getAnswer(_("Press [RETURN] to restart the application "), null);
        callback.actionRestart();
    }

    public void setDownloadRatio(String ratio, float percent) {
        System.out.println(_("Downloading {0}, {1} percent completed.", ratio, percent * 100));
    }

    public void setIndetermined() {
        System.out.println(_("Please wait..."));
    }

    public void setProperty(String key, String value) {
        if (key.toLowerCase().equals("loglist")) {
            is_loglist_enabled = TextUtils.isTrue(value);
        }
    }

    private char getAnswer(String message, String list) {
        try {
            if (sysin == null)
                return 0;
            System.out.print(message);
            String input = sysin.readLine();
            if (input == null || list == null)
                return 0;

            input = input.toLowerCase().trim();
            list = list.toLowerCase();
            if (list.length() == 0 || input.length() == 0)
                return 0;
            for (int i = 0; i < list.length(); i++) {
                if (input.charAt(0) == list.charAt(i))
                    return input.charAt(0);
            }
        } catch (IOException ex) {
        }
        return 0;
    }
}
