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

    private String checkDestFile(String fname, String type, UpdatedApplication application) {
        try {
            FileUtils.fileIsValid(fname, type);
        } catch (IOException ex) {
            String msg = _("File {0} can not be created.", fname);
            if (application != null)
                application.receiveMessage(msg + " - " + ex.getMessage());
            return msg;
        }
        return null;
    }

    public String action(UpdatedApplication application, BufferListener blisten) {
        String fromfile = source + "/" + name;
        String oldtofile = dest + FS + name;
        String newtofile = oldtofile + JupidatorDeployer.EXTENSION;
        String msg;

        if ((msg = checkDestFile(oldtofile, _("Original destination file"), application)) != null)
            return msg;
        if ((msg = checkDestFile(newtofile, _("Downloaded destination file"), application)) != null)
            return msg;

        String error = null;
        try {
            error = FileUtils.copyFile(new URL(fromfile).openConnection().getInputStream(),
                    new FileOutputStream(newtofile), blisten);
        } catch (IOException ex) {
            error = ex.getMessage();
        }

        if (error == null) {
            if (application != null)
                application.receiveMessage(_("File {0} sucessfully downloaded.", name));
            return null;
        }

        msg = _("Unable to download file {0}", name);
        if (application != null)
            application.receiveMessage(msg + " - " + error);
        return msg;
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
