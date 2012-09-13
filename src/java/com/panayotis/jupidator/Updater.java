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
import com.panayotis.jupidator.data.Version;
import com.panayotis.jupidator.elements.FileUtils;
import com.panayotis.jupidator.elements.security.PermissionManager;
import com.panayotis.jupidator.gui.JupidatorGUI;
import com.panayotis.jupidator.gui.UpdateWatcher;
import com.panayotis.jupidator.gui.console.ConsoleGUI;
import com.panayotis.jupidator.gui.swing.SwingGUI;
import com.panayotis.jupidator.loglist.creators.HTMLCreator;
import com.panayotis.jupidator.versioning.SystemVersion;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import jupidator.launcher.AppVersion;
import jupidator.launcher.DeployerParameters;
import jupidator.launcher.XElement;

import static com.panayotis.jupidator.i18n.I18N._;

/**
 *
 * @author teras
 */
public class Updater {

    private Version vers;
    private Version orig_vers;
    private ApplicationInfo appinfo;
    private ApplicationInfo orig_info;
    private UpdatedApplication application;
    private Thread download;
    /* Lazy components */
    private JupidatorGUI gui;
    private UpdateWatcher watcher;
    private ProcessBuilder procbuilder;

    public Updater(String xmlurl, String appHome, UpdatedApplication application) throws UpdaterException {
        this(xmlurl, new ApplicationInfo(appHome), application);
    }

    public Updater(String xmlurl, String appHome, String appSupportDir, UpdatedApplication application) throws UpdaterException {
        this(xmlurl, new ApplicationInfo(appHome, appSupportDir), application);
    }

    public Updater(String xmlurl, String appHome, String appSupportDir, String release, String version, UpdatedApplication application) throws UpdaterException {
        this(xmlurl, new ApplicationInfo(appHome, appSupportDir, release, version), application);
    }

    public Updater(String xmlurl, ApplicationInfo appinfo, UpdatedApplication application) throws UpdaterException {
        vers = Version.loadVersion(xmlurl, appinfo);
        orig_vers = vers;
        orig_info = appinfo;
        if (vers.getAppElements().shouldUpdateLibrary()) {
            String oldname = vers.getAppElements().getAppName();
            String CFGDIR = new File(appinfo.getUpdaterConfigFile()).getAbsoluteFile().getParent();

            ApplicationInfo selfappinfo = new ApplicationInfo(FileUtils.getJupidatorHome(), CFGDIR, String.valueOf(SystemVersion.RELEASE), SystemVersion.VERSION);
            selfappinfo.setSelfUpdate();

            Version selfvers = Version.loadVersion(SystemVersion.URL, selfappinfo);
            if (selfvers.isVisible()) {
                selfvers.replaceArch(vers.getArch());
                vers = selfvers;
                appinfo = selfappinfo;
                vers.getAppElements().setSelfUpdate(oldname);
                vers.getAppElements().setApplicationInfo(_("This update is required for the smooth updating of {0}", oldname));
            }
        }

        this.appinfo = appinfo;
        if (application == null)
            application = new SimpleApplication();
        this.application = application;
    }

    /**
     * Return JupidatorGUI, and create it if it does not exist. This is the
     * official method to create the default GUI GUI is created lazily, when
     * needed
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

    public void actionDisplay() throws UpdaterException {
        if (vers.isVisible()) {
            getGUI();  /* GUI is created lazily, when needed (very important) */
            watcher = new UpdateWatcher(); /* Watcher is also created lazily, when needed */
            watcher.setCallBack(gui);
            gui.setInformation(this, vers.getAppElements(), appinfo);
            gui.startDialog();
        }
    }

    public void actionCommit() {
        long size = 0;
        for (String key : vers.keySet())
            size += vers.get(key).getSize();
        watcher.setAllBytes(size);
        download = new Thread() {
            @Override
            public void run() {
                /* Fetch */
                for (String key : vers.keySet()) {
                    String result = vers.get(key).fetch(application, watcher);
                    if (result != null) {
                        watcher.stopWatcher();
                        application.receiveMessage(result);
                        gui.errorOnCommit(result);
                        return;
                    }
                }
                /* Prepare */
                watcher.stopWatcher();
                gui.setIndetermined();
                for (String key : vers.keySet()) {
                    String result = vers.get(key).prepare(application);
                    if (result != null) {
                        application.receiveMessage(result);
                        gui.errorOnCommit(result);
                        return;
                    }
                }

                /* Construct launcher parameters */
                ArrayList<XElement> elements = new ArrayList<XElement>();
                for (String key : vers.keySet())
                    elements.add(vers.get(key).getExecElement());
                ArrayList<String> relaunch = new ArrayList<String>();
                /* relaunch should be performed with original arguments, not jupidator update */
                for (int i = 0; i < orig_vers.getArch().countArguments(); i++)
                    relaunch.add(orig_vers.getArch().getArgument(i, orig_info));
                DeployerParameters params = new DeployerParameters();
                params.setElements(elements);
                params.addElement(AppVersion.construct(vers.getAppElements()).getXElement(appinfo.getApplicationHome()));
                params.setHeadless(gui.isHeadless());
                params.setRelaunchCommand(relaunch);
                params.setLogLocation(appinfo.getApplicationSupportDir());

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

                gui.successOnCommit();
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
        for (String key : vers.keySet())
            vers.get(key).cancel(application);
        PermissionManager.manager.cleanUp();
    }

    /* Do nothing - wait for next cycle */
    public void actionDefer() {
        watcher.stopWatcher();
        gui.endDialog();
        vers.getUpdaterProperties().defer();
    }

    public void actionIgnore() {
        watcher.stopWatcher();
        gui.endDialog();
        vers.getUpdaterProperties().ignore(vers.getAppElements().getNewRelease());
    }

    public void actionRestart() {
        /* Ask application if restart could be performed */
        watcher.stopWatcher();
        if (!application.requestRestart()) {
            gui.errorOnRestart(_("Application cancelled restart"));
            return;
        }
        try {
            procbuilder.start();
        } catch (IOException ex) {
        }
        gui.endDialog();
        System.exit(0);  // Restarting
    }

    public String getChangeLog() {
        return HTMLCreator.getList(vers.getAppElements().getLogList());
    }
}
