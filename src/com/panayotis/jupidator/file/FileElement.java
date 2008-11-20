/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import static com.panayotis.jupidator.i18n.I18N._;
import static com.panayotis.jupidator.file.FileUtils.FS;

import com.panayotis.jupidator.list.*;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.gui.BufferListener;
import java.io.File;

/**
 *
 * @author teras
 */
public abstract class FileElement {

    private String filename = "";
    private String destdir = "";
    private long size = 0;
    private int release;
    
    protected ApplicationInfo info;

    public FileElement(String file, UpdaterAppElements elements, ApplicationInfo appinfo) {
        this(new File(file).getParent(), new File(file).getName(), elements, appinfo);
    }

    public FileElement(String name, String dest, UpdaterAppElements elements, ApplicationInfo appinfo) {
        this(name, dest, "0", elements, appinfo);
    }

    public FileElement(String name, String dest, String size, UpdaterAppElements elements, ApplicationInfo appinfo) {
        if (name != null)
            this.filename = name;
        if (destdir != null)
            this.destdir = dest;
        release = elements.getLastRelease();

        info = appinfo;
        if (info == null) {
            throw new NullPointerException(_("Application info not provided."));
        }
        try {
            this.size = Long.parseLong(size);
            if (this.size < 0)
                this.size = 0;
        } catch (NumberFormatException ex) {
        }
    }

    public String getHash() {
        return getDestinationFile();
    }

    public String getDestinationFile() {
        return destdir + FS + filename;
    }
    
    public String getFileName() {
        return filename;
    }

    public FileElement getNewestRelease(FileElement fother) {
        if (release > fother.release)
            return this;
        else
            return fother;
    }

    public long getSize() {
        return size;
    }

    /**
     * This method downloads files for this element.
     * @return Error message, or null if everything is fine
     */
    public abstract String fetch(UpdatedApplication application, BufferListener blisten);

    /**
     * This method performs housekeeping (check permissions, unzip files etc.) work for this element.
     * @return Error message, or null if everything is fine
     */
    public abstract String deploy(UpdatedApplication application);

    /**
     * This mehtod cancels action and rollbacks everything
     * @param application
     */
    public abstract void cancel(UpdatedApplication application);

    public abstract String getArgument();

    /**
     *
     * @return self
     */
    public FileElement updateSystemVariables() {
        filename = info.updatePath(filename);
        destdir = info.updatePath(destdir);
        return this;
    }
}
