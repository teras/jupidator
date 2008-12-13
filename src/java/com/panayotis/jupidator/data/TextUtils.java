/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.panayotis.jupidator.data;

/**
 *
 * @author teras
 */
public class TextUtils {
    public static final String NL = System.getProperty("line.separator");


    public static boolean isTrue(String value) {
        value = value.trim().toLowerCase();
        return value.equals("true") || value.equals("yes") || value.equals("1") || value.equals("on") || value.startsWith("enable");
    }
}
