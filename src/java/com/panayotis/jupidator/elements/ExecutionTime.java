/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

/**
 *
 * @author teras
 */
public enum ExecutionTime {

    BEFORE, MID, AFTER;

    public static ExecutionTime parse(String time, ExecutionTime deflt) {
        if (time == null)
            return deflt;

        ExecutionTime ex = valueOf(time);
        if (ex == null)
            return deflt;

        return ex;
    }
}
