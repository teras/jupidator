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
import com.panayotis.jupidator.deployer.JupidatorDeployer;
import com.panayotis.jupidator.gui.BufferListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * 
 * @author teras
 */
public class FileAdd extends FileElement {

    /** This is actually a URL */
    private String source;

    public FileAdd(String name, String source, String dest, String size, UpdaterAppElements elements, ApplicationInfo info) {
        super(name, dest, size, elements, info);
        if (source == null)
            source = "";
        this.source = elements.getBaseURL() + source;
    }

    public String toString() {
        return "+" + source + FS + name + ">" + getDestination();
    }

    public String getArgument() {
        return "+" + getHash() + JupidatorDeployer.EXTENSION;
    }

    public String fetch(UpdatedApplication application, BufferListener blisten) {
        String fromfilename = source + "/" + name;
        String oldtofilename = dest + FS + name;
        String newtofilename = oldtofilename + JupidatorDeployer.EXTENSION;
        File oldtofile = new File(oldtofilename);
        File newtofile = new File(newtofilename);
        String msg;

        /* Check if (new) destination file is writable */
        if (!FileUtils.isWritable(oldtofile)) {
            if (application != null)
                application.receiveMessage(_("Old destination file {0} is not writable.", oldtofilename));
            return _("Old destination file {0} is not writable.", name);
        }
        /* Remove old download file. Whether parent directory is writable, was checked with oldtofile */
        if (!FileUtils.rmTree(newtofile)) {
            if (application != null)
                application.receiveMessage(_("Could not remove old downloaded file {0}", newtofilename));
            return _("Could not remove old downloaded file {0}", name);
        }

        /* Download file */
        String error = null;
        try {
            error = FileUtils.copyFile(new URL(fromfilename).openConnection().getInputStream(),
                    new FileOutputStream(newtofilename), blisten);
        } catch (IOException ex) {
            error = ex.getMessage();
        }
        /* Successfully downloaded file */
        if (error == null) {
            if (application != null)
                application.receiveMessage(_("File {0} sucessfully downloaded.", name));
            return null;
        }
        /* Error while downloading */
        msg = _("Unable to download file {0}", name);
        if (application != null)
            application.receiveMessage(msg + " - " + error);
        return msg;
    }

    public String deploy(UpdatedApplication application) {
        return null;
    }

    public void cancel(UpdatedApplication application) {
        File del = new File(dest + FS + name + JupidatorDeployer.EXTENSION);
        if (!del.delete()) {
            application.receiveMessage(_("Cancel updating: Unable to delete downloaded file {0}", del));
        } else {
            application.receiveMessage(_("Cancel updating: Successfully deleted downloaded file {0}", del));
        }
    }

    public FileElement updateSystemVariables() {
        super.updateSystemVariables();
        source = info.updatePath(source);
        return this;
    }
}
