/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import static com.panayotis.jupidator.i18n.I18N._;
import static com.panayotis.jupidator.elements.FileUtils.FS;

import com.panayotis.jupidator.data.*;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.gui.BufferListener;
import java.io.File;

/**
 *
 * @author teras
 */
public abstract class JupidatorElement {

    private String filename = "";
    private String destdir = "";
    private long size = 0;
    private int release;
    private ExecutionTime exectime;

    public ExecutionTime getExectime() {
        return exectime;
    }
    protected ApplicationInfo info;

    public JupidatorElement(String file, UpdaterAppElements elements, ApplicationInfo appinfo, ExecutionTime exectime) {
        this(new File(file).getName(), new File(file).getParent(), elements, appinfo, exectime);
    }

    public JupidatorElement(String name, String dest, UpdaterAppElements elements, ApplicationInfo appinfo, ExecutionTime exectime) {
        this(name, dest, "0", elements, appinfo, exectime);
    }

    public JupidatorElement(String name, String dest, String size, UpdaterAppElements elements, ApplicationInfo appinfo, ExecutionTime exectime) {
        if (name != null)
            this.filename = appinfo.updatePath(name);
        if (destdir != null)
            this.destdir = appinfo.updatePath(dest);
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
        if (exectime == null)
            exectime = ExecutionTime.MID;
        this.exectime = exectime;
    }

    public String getHash() {
        return getDestinationFile();
    }

    public String getDestinationFile() {
        if (destdir.equals(""))
            return filename;
        return destdir + FS + filename;
    }

    public String getFileName() {
        return filename;
    }

    public JupidatorElement getNewestRelease(JupidatorElement fother) {
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
}
