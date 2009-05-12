/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import static com.panayotis.jupidator.i18n.I18N._;
import static com.panayotis.jupidator.elements.FileUtils.FS;

import com.panayotis.jupidator.elements.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This information is given to the library from the runtime environment
 * @author teras
 */
public class ApplicationInfo {

    private HashMap<String, String> vars;
    /**
    true:  Some files can be ignored, if they are taken care by a distribution
    false: All files should be  updated
     */
    private boolean distributionBased = false;
    private boolean selfupdate;

    public ApplicationInfo(String AppHome, String AppSupportDir, String release, String version) {
        vars = new HashMap<String, String>();

        if (AppHome == null)
            throw new NullPointerException(_("Application path can not be null."));
        if (!new File(AppHome).isDirectory())
            throw new IllegalArgumentException(_("Unable to find Application path {0}.", AppHome));
        if (!AppHome.endsWith("/"))
            AppHome += "/";
        vars.put("APPHOME", AppHome);

        if (AppSupportDir == null || (!new File(AppSupportDir).isDirectory()))
            AppSupportDir = AppHome;
        if (AppSupportDir.length() > 0 && AppSupportDir.charAt(AppSupportDir.length() - 1) != FS)
            AppSupportDir = AppSupportDir + FS;
        if (!AppSupportDir.endsWith("/"))
            AppSupportDir += "/";
        vars.put("APPSUPPORTDIR", AppSupportDir);

        if (version == null || version.equals(""))
            version = "0.0.0.0";
        vars.put("VERSION", version);

        int currelease = 0;
        try {
            currelease = Integer.parseInt(release);
        } catch (NumberFormatException ex) {
        }
        vars.put("RELEASE", Integer.toString(currelease));

        updateIgnoreRelease("0");

        vars.put("JAVABIN", FileUtils.JAVABIN);
    }

    public void setProperty(String name, String value) {
        if (name == null || name.equals(""))
            throw new NullPointerException(_("Property name could not be null"));
        if (value == null)
            value = "";
        vars.put(name, value);
    }

    public void setDistributionBased(boolean distributionBased) {
        this.distributionBased = distributionBased;
    }

    public boolean isDistributionBased() {
        return distributionBased;
    }

    public String getUpdaterConfigFile() {
        return vars.get("APPSUPPORTDIR") + "updater.xml";
    }

    /* This new release has to do with ignoring a specific version */
    public void updateIgnoreRelease(String release) {
        int ignorerelease = 0;
        try {
            ignorerelease = Integer.parseInt(release);
        } catch (NumberFormatException ex) {
        }
        vars.put("IGNORERELEASE", Integer.toString(ignorerelease));
    }

    public int getRelease() {
        return Integer.parseInt(vars.get("RELEASE"));
    }

    public int getIgnoreRelease() {
        return Integer.parseInt(vars.get("IGNORERELEASE"));
    }

    public String getVersion() {
        return vars.get("VERSION");
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
                    if (value == null)
                        value = System.getenv(name);
                }
                if (value != null) {
                    value = value.replace("\\", "\\\\").replace("$", "\\$");
                    m.appendReplacement(sb, value);
                }
            }
        }
        m.appendTail(sb);
//        System.out.println(path + " -> " + sb.toString());
        return sb.toString();
    }

    public boolean isSelfUpdate() {
        return selfupdate;
    }

    public void setSelfUpdate() {
        this.selfupdate = true;
    }
}
