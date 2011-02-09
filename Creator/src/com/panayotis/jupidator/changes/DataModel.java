/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.changes;

import com.panayotis.jupidator.FileItem;

/**
 *
 * @author teras
 */
public class DataModel {

    private boolean useMD5;
    private boolean useSHA1;
    private boolean useSHA2;
    private boolean useZip;
    private boolean useZipRecursively;
    private String origin, target, offset;
    //
    public static final DataModel current = new DataModel(true, false, false, true, false);

    private DataModel(boolean useMD5, boolean useSHA1, boolean useSHA2, boolean useZip, boolean useZipRecursively) {
        this.useMD5 = useMD5;
        this.useSHA1 = useSHA1;
        this.useSHA2 = useSHA2;
        this.useZip = useZip;
        this.useZipRecursively = useZipRecursively;
        origin = "/Users/teras/Works/Development/Java/__jubler/Jubler4.6.1.app";
        target = "/Users/teras/Works/Development/Java/__jubler/Jubler.app/";
        offset = "";
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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = new FileItem(target).getRelativePath(new FileItem(offset));
    }

    public ChangeList getChangeList() {
        return new ChangeList(origin, target, offset);
    }
}
