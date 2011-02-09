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

    private boolean useMD5;
    private boolean useSHA1;
    private boolean useSHA2;
    private boolean useZip;
    private boolean useZipRecursively;
    //
    public static final Configuration current = new Configuration(true, false, false, true, false);

    public Configuration(boolean useMD5, boolean useSHA1, boolean useSHA2, boolean useZip, boolean useZipRecursively) {
        this.useMD5 = useMD5;
        this.useSHA1 = useSHA1;
        this.useSHA2 = useSHA2;
        this.useZip = useZip;
        this.useZipRecursively = useZipRecursively;
    }

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

    public boolean useZip() {
        return useZip;
    }

    public void setZip(boolean useZip) {
        this.useZip = useZip;
    }

    public boolean useZipRecursively() {
        return useZipRecursively;
    }

    public void setZipRecursively(boolean useZipRecursively) {
        this.useZipRecursively = useZipRecursively;
    }
}
