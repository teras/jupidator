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
package com.panayotis.jupidator.gui.console;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.Updater;
import com.panayotis.jupidator.UpdaterException;
import com.panayotis.jupidator.data.TextUtils;
import com.panayotis.jupidator.data.UpdaterAppElements;
import com.panayotis.jupidator.gui.JupidatorGUI;
import com.panayotis.jupidator.loglist.creators.TextCreator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.panayotis.jupidator.i18n.I18N._t;

/**
 *
 * @author teras
 */
public class ConsoleGUI implements JupidatorGUI {

    private String info1, info2, loglist, filelist;
    private Updater callback;
    private boolean is_loglist_enabled = true;
    private boolean can_not_ignore = false;
    private boolean should_show_jupidator_about = true;
    private boolean can_show_filelist = true;
    private String appname;
    private final BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));

    public void setInformation(Updater callback, UpdaterAppElements el, ApplicationInfo info) throws UpdaterException {
        appname = el.getAppName();
        info1 = _t("A new version of {0} is available!", appname);
        info2 = _t("{0} version {1} is now available", el.getAppName(), el.getNewestVersion())
                + (info.getVersion() == null ? "" : " - " + _t("you have {0}", info.getVersion())) + ".";
        loglist = TextCreator.getList(el.getLogList(), true);
        filelist = TextCreator.getFileList(callback.getElements());
        this.callback = callback;
        can_not_ignore = info.isSelfUpdate();
    }

    public void startDialog() {
        System.out.println(_t("Welcome to the installation of {0}", appname));
        if (should_show_jupidator_about)
            System.out.println(_t("Installation library") + ": Jupidator (C) 2012 Panayotis Katsaloulis, panayotis" + ('?' + 1) + "panayotis.com");
        System.out.println();
        System.out.println(info1);
        System.out.println(info2);
        if (is_loglist_enabled && getAnswer(_t("Do you want to see the detailed changelog? [Y/n] "), "n") != 'n') {
            System.out.println();
            System.out.println(loglist);
        }
        if (is_loglist_enabled && getAnswer(_t("Do you want to see the full file list? [Y/n] "), "n") != 'n') {
            System.out.println();
            System.out.println(filelist);
        }
        String question = _t("Do you want to (S)kip this version, (R)emind later or (I)nstall? [s/r/i] ");
        String valid_ans = "sri";
        if (can_not_ignore) {
            question = _t("Do you want to (R)emind later or (I)nstall? [r/i] ");
            valid_ans = "ri";
        }

        boolean valid = false;
        while (!valid) {
            valid = true;   // Be optimistic; will handle this in default section
            switch (getAnswer(question, valid_ans)) {
                case 'r':
                    callback.actionDefer();
                    break;
                case 'i':
                    callback.actionCommit();
                    break;
                case 's':
                    if (!can_not_ignore) {
                        callback.actionIgnore();
                        break;
                    }
                default:
                    System.out.println(_t("Wrong answer."));
                    valid = false;
            }
        }
    }

    public void endDialog() {
        System.out.println(_t("Thank you for using Jupidator") + "  (http://sourceforge.net/projects/jupidator/)");
        System.out.println();
    }

    public void errorOnCommit(String message) {
        System.out.println(_t("Error: {0}", message));
    }

    public void successOnCommit(boolean restartableApp) {
        System.out.println(_t("Downloading successfull"));
        getAnswer(_t("Press [RETURN] to restart the application") + " ", null);
        getAnswer(_t("Press [RETURN] to finalize the update") + " ", null);
        callback.actionRestart();
    }

    public void errorOnRestart(String message) {
        System.out.println(_t(message));
        System.out.println(_t("Cancel installation"));
        callback.actionCancel();
    }

    public void setDownloadRatio(String ratio, float percent) {
        System.out.println(_t("Downloading {0}, {1} percent completed.", ratio, percent * 100));
    }

    public void setIndetermined() {
        System.out.println(_t("Please wait..."));
    }

    public void setProperty(String key, String value) {
        key = key.toLowerCase();
        if (key.equals(LOGLIST))
            is_loglist_enabled = TextUtils.isTrue(value);
        else if (key.equals(ABOUT))
            should_show_jupidator_about = TextUtils.isTrue(value);
        else if (key.endsWith(ACTIONLIST))
            can_show_filelist = TextUtils.isTrue(value);
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
            for (int i = 0; i < list.length(); i++)
                if (input.charAt(0) == list.charAt(i))
                    return input.charAt(0);
        } catch (IOException ex) {
        }
        return 0;
    }

    public boolean isHeadless() {
        return true;
    }
}
