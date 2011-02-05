/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.data;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author teras
 */
public class TextUtils {

    public static final String NL = System.getProperty("line.separator");

    public static String getProperty(String key) {
        return System.getProperty(key).replace("%20", " ");
    }

    public static String getSystemName() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static String getSystemArch() {
        return System.getProperty("os.arch").toLowerCase();
    }

    public static boolean isTrue(String value) {
        if (value == null)
            return false;
        value = value.trim().toLowerCase();
        return value.equals("true") || value.equals("yes") || value.equals("1") || value.equals("on") || value.startsWith("enable");
    }

    public static String applyVariables(Map<String, String> set, String source) {
        if (source == null)
            source = "";

        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile("\\$\\{.*?\\}").matcher(source);
        while (m.find()) {
            String group = m.group();
            String name = group.substring(2, group.length() - 1);
            if (name.length() > 0) {
                String value = set.get(name);
                if (value == null) {
                    value = getProperty(name);
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
}
