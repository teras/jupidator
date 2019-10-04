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
package com.panayotis.jupidator.gui.swing;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.Updater;
import com.panayotis.jupidator.data.TextUtils;
import com.panayotis.jupidator.data.UpdaterAppElements;
import com.panayotis.jupidator.elements.security.PermissionManager;
import com.panayotis.jupidator.gui.JupidatorGUI;
import com.panayotis.jupidator.loglist.creators.HTMLCreator;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.UIManager;

import static com.panayotis.jupidator.i18n.I18N._t;

/**
 * @author teras
 */
public class SwingGUI implements JupidatorGUI {

    private SwingDialog gui;
    private Updater callback;
    private String newver, versinfo, title;
    private BufferedImage icon;
    private boolean skipvisible, prevvisible, detailedvisible = false, infovisible = true, systemlook = true, can_show_filelist = true;
    private String infotext, filetext, urlText;

    public boolean isHeadless() {
        return false;
    }

    public void setInformation(Updater callback, UpdaterAppElements el, ApplicationInfo info, String urlInfo) {
        this.callback = callback;
        newver = _t("A new version of {0} is available!", el.getAppName());
        versinfo = _t("{0} version {1} is now available", el.getAppName(), el.getNewestVersion())
                + (info.getVersion() == null ? "" : " - " + _t("you have {0}", info.getVersion())) + ".";
        title = _t("New version of {0} found!", el.getAppName());
        infotext = HTMLCreator.getList(el.getLogList(), true);
        filetext = HTMLCreator.getFileList(callback.getElements());
        try {
            String iconPath = el.getIconpath();
            if (iconPath != null && (!iconPath.equals("")))
                icon = ImageIO.read(new URL(iconPath));
        } catch (IOException ignored) {
        }
        skipvisible = !info.isSelfUpdate();
        prevvisible = PermissionManager.manager.isRequiredPrivileges();
        urlText = urlInfo;
    }

    public void setProperty(String key, String value) {
        key = key.toLowerCase();
        if (key.equals(ABOUT))
            infovisible = TextUtils.isTrue(value);
        else if (key.equals(SYSTEMLOOK))
            systemlook = TextUtils.isTrue(value);
        else if (key.equals(LOGLIST))
            detailedvisible = TextUtils.isTrue(value);
        else if (key.endsWith(ACTIONLIST))
            can_show_filelist = TextUtils.isTrue(value);
    }

    public void startDialog() {
        if (gui == null) {
            try {
                if (systemlook)
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            gui = new SwingDialog();
            gui.infotext = infotext;
            gui.filetext = filetext;
            gui.callback = callback;
            gui.NewVerL.setText(newver);
            gui.VersInfoL.setText(versinfo);
            gui.setTitle(title);
            gui.icon = icon;
            gui.SkipB.setVisible(skipvisible);
            gui.PrevL.setVisible(prevvisible);
            gui.setInfoVisible(infovisible);
            gui.setFileListVisible(can_show_filelist);
            gui.DetailedP.setVisible(detailedvisible);
            gui.webNotesL.setVisible(urlText != null);
            gui.urlText = urlText;
            gui.pack();
            gui.setLocationRelativeTo(null);
        }
        gui.setVisible(true);
    }

    public void endDialog() {
        gui.setVisible(false);
        gui.dispose();
    }

    @Override
    public void setUndetermined() {
        gui.ActionB.setEnabled(false);
        gui.PBar.setIndeterminate(true);
        gui.PBar.setToolTipText(_t("Processing update"));
        gui.PBar.setString("");
        gui.InfoL.setText(_t("Deploying files..."));
    }

    public void errorOnCommit(String message) {
        setInfoArea(message);
        gui.InfoL.setForeground(Color.RED);
        gui.ProgressP.revalidate();
    }

    public void successOnCommit(boolean restartableApp) {
        setInfoArea(_t("Successfully downloaded updates"));
        gui.ActionB.setText(restartableApp ? _t("Restart application") : _t("Finalize update"));
        gui.ActionB.setActionCommand("restart");
        gui.ProgressP.revalidate();
    }

    public void errorOnRestart(String message) {
        if (message == null)
            setInfoArea(_t("Application cancelled restart"));
        else
            setInfoArea(message);
        gui.ActionB.setText(_t("Cancel"));
        gui.ActionB.setActionCommand("cancel");
    }

    public void setDownloadRatio(String ratio, float percent) {
        gui.PBar.setValue(Math.round(percent * 100));
        gui.PBar.setToolTipText(_t("Download speed: {0}", ratio));
        gui.PBar.setString(ratio);
    }

    private void setInfoArea(String message) {
        gui.ActionB.setEnabled(true);
        gui.BarPanel.remove(gui.PBar);
        gui.ProgressP.remove(gui.InfoL);
        gui.BarPanel.add(gui.InfoL);
        gui.InfoL.setText(message);
        gui.InfoL.setToolTipText(message);
    }
}
