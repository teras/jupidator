/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import com.panayotis.jupidator.list.*;
import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.ApplicationInfo;

/**
 *
 * @author teras
 */
public abstract class FileElement {

    protected static final String SEP = System.getProperty("file.separator");
    protected String name = "";
    protected String dest;
    protected int release;
    protected ApplicationInfo info;

    public FileElement(String name, String dest, UpdaterAppElements elements, ApplicationInfo appinfo) {
        if (name != null)
            this.name = name;
        release = elements.getLastRelease();
        this.dest = appinfo.updatePath(dest);

        info = appinfo;
        if (info == null) {
            throw new NullPointerException(_("Application info not provided."));
        }
    }

    public String getHash() {
        return dest + SEP + name;
    }

    public String getDestination() {
        return dest;
    }

    /**
     * 
     * @param log
     * @return Error message, or null if everything is fine
     */
    public abstract String action();

    public FileElement getNewestRelease(FileElement fother) {
        if (release > fother.release)
            return this;
        else
            return fother;
    }
}
