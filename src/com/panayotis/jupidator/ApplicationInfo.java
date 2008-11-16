/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import static com.panayotis.jupidator.i18n.I18N._;
import static com.panayotis.jupidator.file.FileUtils.FS;

import com.panayotis.jupidator.file.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author teras
 */
public class ApplicationInfo {

    private String AppHome;
    private String AppConfigFile;
    private String AppSupportDir;
    private String AppBaseFile = "a.class";
    private int release = -1;
    private String version = "0.0.0";
    /**
    true:  Some files can be ignored, if they are taken care by a distribution
    false: All files should be  updated
     */
    private boolean distributionBased = false;

    public ApplicationInfo(String AppHome, String AppConfigFile, String AppSupportDir, String release, String version) {
        if (AppHome == null)
            throw new NullPointerException(_("Application path can not be null."));
        File f = new File(AppHome);
        if (!f.isDirectory())
            throw new IllegalArgumentException(_("Unable to find Application path {0}.", AppHome));
        this.AppHome = AppHome;

        try {
            FileUtils.fileIsValid(AppConfigFile, "Application configuration");
        } catch (IOException ex) {
            AppConfigFile = AppHome + FS + "config.xml";
        }
        this.AppConfigFile = AppConfigFile;

        try {
            FileUtils.fileIsValid(AppSupportDir, "Application support directory");
        } catch (IOException ex) {
            AppSupportDir = AppHome;
        }
        if (!new File(AppSupportDir).isDirectory())
            AppSupportDir = AppHome;
        if (AppSupportDir.length() > 0 && AppSupportDir.charAt(AppSupportDir.length() - 1) != FS)
            AppSupportDir = AppSupportDir + FS;
        this.AppSupportDir = AppSupportDir;

        try {
            this.release = Integer.parseInt(release);
        } catch (NumberFormatException ex) {
        }

        if (version != null)
            this.version = version;
    }

    public void setBaseFile(String basefile) {
        AppBaseFile = basefile;
    }

    public boolean isDistributionBased() {
        return distributionBased;
    }

    String getUpdaterConfigFile() {
        return AppSupportDir + "updater.xml";
    }

    /* This new release has to do with ignoring a specific version */
    public void updateRelease(String lastrelease) {
        try {
            int lastrel = Integer.parseInt(lastrelease);
            if (lastrel > release)
                release = lastrel;
        } catch (NumberFormatException ex) {
        }
    }

    public int getRelease() {
        return release;
    }

    public String getVersion() {
        return version;
    }

    public void setDistributionBased(boolean distributionBased) {
        this.distributionBased = distributionBased;
    }

    public String updatePath(String path) {
        if (path == null)
            path = "";
        path = path.replaceAll("\\$\\{APPHOME\\}", AppHome);
        path = path.replaceAll("\\$\\{APPCONFIG\\}", AppConfigFile);
        path = path.replaceAll("\\$\\{APPSUPPORT\\}", AppSupportDir);
        path = path.replaceAll("\\$\\{BASEFILE\\}", AppBaseFile);
        path = path.replaceAll("\\$\\{JAVAHOME\\}", AppBaseFile);
        path = path.replaceAll("\\$\\{JAVABIN\\}", AppBaseFile);
        return path;
    }
}
