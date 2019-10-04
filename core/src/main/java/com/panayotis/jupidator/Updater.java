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
package com.panayotis.jupidator;

import com.panayotis.jupidator.data.SimpleApplication;
import com.panayotis.jupidator.data.TextUtils;
import com.panayotis.jupidator.data.Version;
import com.panayotis.jupidator.elements.ElementSizable;
import com.panayotis.jupidator.elements.FileUtils;
import com.panayotis.jupidator.elements.JupidatorElement;
import com.panayotis.jupidator.elements.security.PermissionManager;
import com.panayotis.jupidator.gui.JupidatorGUI;
import com.panayotis.jupidator.gui.UpdateWatcher;
import com.panayotis.jupidator.gui.console.ConsoleGUI;
import com.panayotis.jupidator.gui.swing.SwingGUI;
import com.panayotis.jupidator.versioning.SystemVersion;
import jupidator.launcher.DeployerParameters;
import jupidator.launcher.XElement;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.panayotis.jupidator.i18n.I18N._t;

/**
 * @author teras
 */
public class Updater {

    // The host information - the application that started the update
    private Version hostVersion;
    private ApplicationInfo hostInfo;
    // The current version - could be either the application that started the update or Jupidator itself
    private Version curVersion;
    private ApplicationInfo curInfo;
    private UpdatedApplication application;
    private Thread download;
    /* Lazy components */
    private JupidatorGUI gui;
    private UpdateWatcher watcher;
    private ProcessBuilder procbuilder;

    public Updater(String xmlurl, String appHome, UpdatedApplication application) throws UpdaterException {
        this(xmlurl, new ApplicationInfo(appHome), application);
    }

    @Deprecated
    public Updater(String xmlurl, String appHome, String appSupportDir, UpdatedApplication application) throws UpdaterException {
        this(xmlurl, new ApplicationInfo(appHome), application);
    }

    @Deprecated
    public Updater(String xmlurl, String appHome, String appSupportDir, int release, String version, UpdatedApplication application) throws UpdaterException {
        this(xmlurl, new ApplicationInfo(appHome, release, version), application);
    }

    public Updater(String xmlurl, String appHome, int release, String version, UpdatedApplication application) throws UpdaterException {
        this(xmlurl, new ApplicationInfo(appHome, release, version), application);
    }

    @Deprecated
    public Updater(String xmlurl, String appHome, String appSupportDir, String release, String version, UpdatedApplication application) throws UpdaterException {
        this(xmlurl, new ApplicationInfo(appHome, TextUtils.getInt(release, 0), version), application);
    }

    public Updater(String xmlurl, ApplicationInfo appinfo, UpdatedApplication application) throws UpdaterException {
        this(xmlurl, appinfo, application, true, false);
    }

    public Updater(String xmlurl, ApplicationInfo appinfo, UpdatedApplication application, boolean tryBzFirst, boolean ignorePostpone) throws UpdaterException {
        curInfo = hostInfo = appinfo;
        hostVersion = curVersion = Version.loadVersion(xmlurl, appinfo, tryBzFirst, ignorePostpone);
        this.application = application == null ? new SimpleApplication() : application;
        if (curVersion.getAppElements().shouldUpdateLibrary()) {
            String oldname = curVersion.getAppElements().getAppName();
            ApplicationInfo selfappinfo = ApplicationInfo.getSelfInfo(FileUtils.getClassHome(Updater.class));
            selfappinfo.setSelfUpdate();

            Version selfvers = Version.loadVersion(SystemVersion.URL, selfappinfo);
            if (!selfvers.isEmpty()) {
                selfvers.setArch(curVersion.getArch());
                curInfo = selfappinfo;
                curVersion = selfvers;
                curVersion.getAppElements().setSelfUpdate(oldname);
                curVersion.getAppElements().setApplicationInfo(_t("This update is required for the smooth updating of {0}", oldname));
            }
        }
    }

    private static ApplicationInfo constructApplicationInfo(String appHome, int release, String version, UpdatedApplication application) {
        try {
            return new ApplicationInfo(appHome, release, version);
        } catch (UpdaterException ex) {
            if (application != null)
                application.receiveMessage(ex.toString());
            return null;
        }
    }

    public static Updater start(String xmlurl, String appHome, UpdatedApplication application) {
        return start(xmlurl, constructApplicationInfo(appHome, 0, null, application), application, null);
    }

    @Deprecated
    public static Updater start(String xmlurl, String appHome, String appSupportDir, UpdatedApplication application) {
        return start(xmlurl, constructApplicationInfo(appHome, 0, null, application), application, null);
    }

    public static Updater start(String xmlurl, String appHome, int release, String version, UpdatedApplication application) {
        return start(xmlurl, appHome, release, version, application, false);
    }

    public static Updater start(String xmlurl, String appHome, int release, String version, UpdatedApplication application, boolean ignorePostpone) {
        return start(xmlurl, appHome, release, version, application, ignorePostpone, true);
    }

    public static Updater start(String xmlurl, String appHome, int release, String version, UpdatedApplication application, boolean ignorePostpone, boolean autoShowDialog) {
        return start(xmlurl, constructApplicationInfo(appHome, release, version, application), application, null, true, ignorePostpone, autoShowDialog);
    }

    @Deprecated
    public static Updater start(String xmlurl, String appHome, String appSupportDir, int release, String version, UpdatedApplication application) {
        return start(xmlurl, constructApplicationInfo(appHome, release, version, application), application, null);
    }

    public static Updater start(String xmlurl, ApplicationInfo appinfo, UpdatedApplication application, JupidatorGUI gui) {
        return start(xmlurl, appinfo, application, gui, true, false);
    }

    public static Updater start(String xmlurl, ApplicationInfo appinfo, UpdatedApplication application, JupidatorGUI gui, boolean tryBzFirst, boolean ignorePostpone) {
        return start(xmlurl, appinfo, application, gui, tryBzFirst, ignorePostpone, true);
    }

    public static Updater start(String xmlurl, ApplicationInfo appinfo, UpdatedApplication application, JupidatorGUI gui, boolean tryBzFirst, boolean ignorePostpone, boolean autoShowDialog) {
        if (appinfo == null)
            return null;
        try {
            Updater up = new Updater(xmlurl, appinfo, application, tryBzFirst, ignorePostpone);
            if (gui != null)
                up.setGUI(gui);
            if (autoShowDialog)
                up.actionDisplay();
            return up;
        } catch (UpdaterException ex) {
            if (application != null)
                application.receiveMessage(ex.toString());
            return null;
        }
    }

    /**
     * Return JupidatorGUI, and create it if it does not exist. This is the
     * official method to create the default GUI GUI is created lazily, when
     * needed
     *
     * @return The requested GUI
     */
    public JupidatorGUI getGUI() {
        if (gui == null)
            if (GraphicsEnvironment.isHeadless())
                gui = new ConsoleGUI();
            else
                gui = new SwingGUI();
        return gui;
    }

    public void setGUI(JupidatorGUI gui) {
        if (gui != null)
            this.gui = gui;
    }

    public Iterable<JupidatorElement> getElements() {
        return curVersion.values2();
    }

    public void actionDisplay() {
        if (!isUpdatable())
            return;
        if (watcher == null) {
            // Lazily initialize visuals
            PermissionManager.manager.estimatePrivileges(new File(curInfo.getApplicationHome() + File.separator + AppVersion.FILETAG));
            watcher = new UpdateWatcher(); // Watcher is also created lazily, when needed
            watcher.setCallBack(getGUI());
            getGUI().setInformation(this, curVersion.getAppElements(), curInfo);
        }
        getGUI().startDialog();
    }

    public void actionCommit() {
        long size = 0;
        for (JupidatorElement element : getElements())
            if (element instanceof ElementSizable)
                size += ((ElementSizable) element).getSize();
        watcher.setAllBytes(size);
        download = new Thread() {
            @Override
            public void run() {
                /* Fetch */
                for (JupidatorElement element : getElements()) {
                    String result = element.fetch(application, watcher);
                    if (result != null) {
                        watcher.stopWatcher();
                        application.receiveMessage(result);
                        gui.errorOnCommit(result);
                        return;
                    }
                }
                /* Prepare */
                watcher.stopWatcher();
                gui.setUndetermined();
                for (JupidatorElement element : getElements()) {
                    String result = element.prepare(application);
                    if (result != null) {
                        application.receiveMessage(result);
                        gui.errorOnCommit(result);
                        return;
                    }
                }

                /* Construct deploy parameters */
                DeployerParameters params = new DeployerParameters(curInfo.getApplicationHome());
                /* Calculate exec elements */
                List<XElement> elements = new ArrayList<XElement>();
                for (JupidatorElement element : getElements())
                    elements.add(element.getExecElement());
                params.setElements(elements);

                /* relaunch should be performed with original arguments, not jupidator update */
                List<String> relaunch = new ArrayList<String>(hostVersion.getArch().getRelaunchCommand(hostInfo));

                if (!curInfo.isSelfUpdate())    // Add self  update information if we do not update jupidator
                    params.addElement(AppVersion.construct(curVersion.getAppElements()).getXElement(curInfo.getApplicationHome()));
                params.setHeadless(gui.isHeadless());
                params.setRelaunchCommand(relaunch);

                /* Construct launcher command */
                try {
                    procbuilder = PermissionManager.manager.getLaunchCommand(application, params);
                    if (procbuilder == null)
                        throw new IOException("Unable to create relauncer");
                } catch (IOException ex) {
                    String message = ex.getMessage();
                    application.receiveMessage(message);
                    gui.errorOnRestart(message);
                    return;
                }

                gui.successOnCommit(!relaunch.isEmpty());
            }
        };
        watcher.startWatcher();
        download.start();
    }

    public void actionCancel() {
        watcher.stopWatcher();
        download.interrupt();
        gui.endDialog();
        try {
            download.join();
        } catch (InterruptedException ex) {
        }
        for (JupidatorElement element : getElements())
            element.cancel(application);
        PermissionManager.manager.cleanUp();
    }

    /* Do nothing - wait for next cycle */
    public void actionDefer() {
        watcher.stopWatcher();
        gui.endDialog();
        curVersion.getUpdaterProperties().defer();
    }

    public void actionIgnore() {
        watcher.stopWatcher();
        gui.endDialog();
        curVersion.getUpdaterProperties().ignore(curVersion.getAppElements().getNewestRelease());
    }

    public void actionRestart() {
        /* Ask application if restart could be performed */
        watcher.stopWatcher();
        if (!application.requestRestart()) {
            gui.errorOnRestart(_t("Application cancelled restart"));
            return;
        }
        try {
            procbuilder.start();
        } catch (IOException ex) {
        }
        gui.endDialog();
        System.exit(0);  // Restarting
    }

    public boolean isUpdatable() {
        return !curVersion.isEmpty();
    }
}
