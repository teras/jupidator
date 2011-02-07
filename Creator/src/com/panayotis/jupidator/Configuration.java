/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

/**
 *
 * @author teras
 */
public class Configuration {

    public static final Configuration current = new Configuration();
    private boolean useMD5 = false;
    private boolean useSHA1 = false;
    private boolean useSHA2 = false;

    public boolean useMD5() {
        return useMD5;
    }

    public void setMD5(boolean useMD5) {
        this.useMD5 = useMD5;
    }

    public boolean useSHA1() {
        return useSHA1;
    }

    public void setSHA1(boolean useSHA1) {
        this.useSHA1 = useSHA1;
    }

    public boolean useSHA2() {
        return useSHA2;
    }

    public void setSHA2(boolean useSHA2) {
        this.useSHA2 = useSHA2;
    }
}
