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
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author teras
 */
public class ApplicationInfo {

    private HashMap<String, String> vars;
    private int release;
    /**
    true:  Some files can be ignored, if they are taken care by a distribution
    false: All files should be  updated
     */
    private boolean distributionBased = false;

    public ApplicationInfo(String AppHome, String AppConfigFile, String AppSupportDir, String release, String version) {
        vars = new HashMap<String, String>();

        if (AppHome == null)
            throw new NullPointerException(_("Application path can not be null."));
        File f = new File(AppHome);
        if (!f.isDirectory())
            throw new IllegalArgumentException(_("Unable to find Application path {0}.", AppHome));
        vars.put("APPHOME", AppHome);

        try {
            FileUtils.fileIsValid(AppConfigFile, "Application configuration");
        } catch (IOException ex) {
            AppConfigFile = AppHome + FS + "config.xml";
        }
        vars.put("APPCONFIG", AppConfigFile);

        try {
            FileUtils.fileIsValid(AppSupportDir, "Application support directory");
        } catch (IOException ex) {
            AppSupportDir = AppHome;
        }
        if (!new File(AppSupportDir).isDirectory())
            AppSupportDir = AppHome;
        if (AppSupportDir.length() > 0 && AppSupportDir.charAt(AppSupportDir.length() - 1) != FS)
            AppSupportDir = AppSupportDir + FS;
        vars.put("APPSUPPORT", AppSupportDir);

        if (version == null || version.equals(""))
            version = "0.0.0.0";
        vars.put("VERSION", version);

        try {
            this.release = Integer.parseInt(release);
        } catch (NumberFormatException ex) {
        }

        vars.put("JAVABIN", FileUtils.JAVABIN);
    }

    public void setBaseFile(String basefile) {
        vars.put("BASEFILE", basefile);
    }

    public boolean isDistributionBased() {
        return distributionBased;
    }

    String getUpdaterConfigFile() {
        return vars.get("APPSUPPORT") + "updater.xml";
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
        return vars.get("VERSION");
    }

    public void setDistributionBased(boolean distributionBased) {
        this.distributionBased = distributionBased;
    }

    public String updatePath(String path) {
        if (path == null)
            path = "";

        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile("\\$\\{.*?\\}").matcher(path);
        while (m.find()) {
            String group = m.group();
            String name = group.substring(2, group.length() - 1);
            if (name.length() > 0) {
                String value = vars.get(name);
                if (value == null) {
                    value = System.getProperty(name);
                    if (value == null) {
                        value = System.getenv(name);
                    }
                }
                if (value != null)
                    m.appendReplacement(sb, value);
            }
        }
        m.appendTail(sb);
//        System.out.println(path + " -> " + sb.toString());
        return sb.toString();
    }
}
