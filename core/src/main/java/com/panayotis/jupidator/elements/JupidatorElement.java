/*
 *
 * This file is part of Jupidator.
 *
 * Jupidator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 *
 * Jupidator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jupidator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package com.panayotis.jupidator.elements;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.data.UpdaterAppElements;
import com.panayotis.jupidator.elements.security.PermissionManager;
import com.panayotis.jupidator.gui.BufferListener;
import java.io.File;
import java.io.Serializable;
import jupidator.launcher.XElement;

/**
 *
 * @author teras
 */
public abstract class JupidatorElement implements Serializable {

    private final String filename;
    private final long release;
    private final ExecutionTime exectime;
    private final boolean requiresPrivileges;
    private String destdir;

    public JupidatorElement(String file, UpdaterAppElements elements, ApplicationInfo appinfo, ExecutionTime exectime) {
        this((file == null) ? null : new File(file).getName(), (file == null) ? null : new File(file).getParent(), elements, appinfo, exectime);
    }

    public JupidatorElement(String name, String dest, UpdaterAppElements elements, ApplicationInfo appinfo, ExecutionTime exectime) {
        this(appinfo.applyVariables(name),
                elements.getLastRelease(),
                exectime == null ? ExecutionTime.MID : exectime,
                appinfo.applyVariables(dest),
                elements
        );
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public JupidatorElement(String filename, long release, ExecutionTime exectime, String destdir, UpdaterAppElements elements) {
        this.filename = filename;
        this.release = release;
        this.exectime = exectime;
        this.destdir = destdir;
        requiresPrivileges = estimatePrivileges(elements);
    }

    public void setDestDir(ApplicationInfo appinfo, String destination) {
        this.destdir = appinfo.applyVariables(destination);
    }

    protected boolean estimatePrivileges(UpdaterAppElements elements) {
        return PermissionManager.manager.estimatePrivileges(new File(getDestinationFile()));
    }

    public ExecutionTime getExectime() {
        return exectime;
    }

    public String getHash() {
        return FileUtils.getAbsolute(getDestinationFile()).getAbsolutePath();
    }

    public final String getDestinationFile() {
        if (destdir.equals(""))
            return filename;
        return destdir + File.separator + filename;
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

    public boolean requiresPrivileges() {
        return requiresPrivileges;
    }

    /**
     * This method downloads files for this element.
     *
     * @param application The requested application
     * @param blisten Where data are stored
     * @return Error message, or null if everything is fine
     */
    public abstract String fetch(UpdatedApplication application, BufferListener blisten);

    /**
     * This method performs housekeeping work for this element (i.e. unzip
     * files).
     *
     * @param application The application to update
     * @return Error message, or null if everything is fine
     */
    public abstract String prepare(UpdatedApplication application);

    /**
     * This method cancels action and rolls back everything
     *
     * @param application The application to update
     */
    public abstract void cancel(UpdatedApplication application);

    /**
     * Use this method to provide the actual work to be done, when updating the
     * application
     *
     * @return the object which will perform this work. It should be under
     * package com.panayotis.jupidator.launcher
     */
    public abstract XElement getExecElement();
}
