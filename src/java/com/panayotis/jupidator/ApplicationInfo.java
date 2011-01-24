/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.elements.FileUtils;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This information is given to the library from the runtime environment
 * @author teras
 */
public class ApplicationInfo implements Serializable {

    private HashMap<String, String> vars;
    /**
    true:  Some files can be ignored, if they are taken care by a distribution
    false: All files should be  updated
     */
    private boolean distributionBased = false;
    private boolean selfupdate;

    public ApplicationInfo(String appHome, String appSupportDir, String release, String version) {
        vars = new HashMap<String, String>();

        appHome = fixDir(appHome, "Application");
        if (appSupportDir == null || (!new File(appSupportDir).isDirectory()))
            appSupportDir = appHome;        
        vars.put("APPHOME", appHome);        
        vars.put("APPSUPPORTDIR", appSupportDir);

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
        return vars.get("APPSUPPORTDIR") + File.separator + "updater.xml";
    }

    /* This new release has to do with ignoring a specific version */
    public final void updateIgnoreRelease(String release) {
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

    public String applyVariables(String path) {
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
        return sb.toString().replace("/./", "/");
    }

    public boolean isSelfUpdate() {
        return selfupdate;
    }

    public void setSelfUpdate() {
        this.selfupdate = true;
    }

    private String fixDir(String dir, String title) {
        if (dir == null)
            throw new NullPointerException(title + "directory can not be null.");
        if (dir.equals("") || dir.equals("."))
            dir = System.getProperty("user.dir");
        dir = dir.replace("/./", "/");
        if (dir.endsWith(File.separator))
            dir = dir.substring(0, dir.length() - 1);
        if (!new File(dir).isDirectory())
            throw new IllegalArgumentException("Unable to find " + title + " directory " + dir);
        return dir;
    }
}
