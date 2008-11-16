/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import static com.panayotis.jupidator.i18n.I18N._;
import static com.panayotis.jupidator.file.FileUtils.FS;

import com.panayotis.jupidator.list.*;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdaterListener;

/**
 *
 * @author teras
 */
public abstract class FileElement {

    protected String name = "";
    protected String dest;
    protected int release;
    protected ApplicationInfo info;

    public FileElement(String name, String dest, UpdaterAppElements elements, ApplicationInfo appinfo) {
        if (name != null)
            this.name = name;
        release = elements.getLastRelease();
        this.dest = dest;

        info = appinfo;
        if (info == null) {
            throw new NullPointerException(_("Application info not provided."));
        }
    }

    public String getHash() {
        return dest + FS + name;
    }

    public String getDestination() {
        return dest;
    }

    public FileElement getNewestRelease(FileElement fother) {
        if (release > fother.release)
            return this;
        else
            return fother;
    }

    /**
     * This method performs the commit action for this element.
     * @param log
     * @return Error message, or null if everything is fine
     */
    public abstract String action(UpdaterListener listener);

    public abstract void cancel(UpdaterListener listener);

    public abstract String getArgument();

    /**
     *
     * @return self
     */
    public FileElement updateSystemVariables() {
        name = info.updatePath(name);
        dest = info.updatePath(dest);
        return this;
    }
}
