/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import static com.panayotis.jupidator.i18n.I18N._;
import static com.panayotis.jupidator.file.FileUtils.FS;

import com.panayotis.jupidator.file.compression.CompressionMethod;
import com.panayotis.jupidator.list.*;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.deployer.JupidatorDeployer;
import com.panayotis.jupidator.file.compression.NullCompression;
import com.panayotis.jupidator.file.compression.ZipCompression;
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
    private CompressionMethod compression = null;

    public FileAdd(String name, String source, String dest, String size, String compress, UpdaterAppElements elements, ApplicationInfo info) {
        super(name, dest, size, elements, info);
        if (source == null)
            source = "";
        this.source = elements.getBaseURL() + source;
        if (compress == null)
            compress = "none";
        compress = compress.toLowerCase();
        if (compress.startsWith("zip"))
            compression = new ZipCompression();
        else
            compression = new NullCompression();
    }

    public String toString() {
        return "+" + source + FS + name + ">" + getDestination();
    }

    public String getArgument() {
        return "+" + dest + FS + name + JupidatorDeployer.EXTENSION;
    }

    public String fetch(UpdatedApplication application, BufferListener blisten) {
        String fromfilename = source + "/" + name + compression.getFilenameExtension();
        String tofilename = dest + FS + name;
        String downloadfilename = tofilename + compression.getFilenameExtension() + JupidatorDeployer.EXTENSION;
        File tofile = new File(tofilename);
        File downloadfile = new File(downloadfilename);
        String msg;

        /* Check if (new) destination file is writable */
        if (!FileUtils.isWritable(tofile)) {
            if (application != null)
                application.receiveMessage(_("Destination file {0} is not writable.", tofilename));
            return _("Destination file {0} is not writable.", name);
        }
        /* Remove old download file. Whether parent directory is writable, was checked with tofile */
        downloadfile.delete();
        if (downloadfile.exists()) {
            if (application != null)
                application.receiveMessage(_("Could not remove old downloaded file {0}", downloadfilename));
            return _("Could not remove old downloaded file {0}", name);
        }

        /* Download file */
        String error = null;
        try {
            error = FileUtils.copyFile(new URL(fromfilename).openConnection().getInputStream(),
                    new FileOutputStream(downloadfilename), blisten);
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
        File downloadfile = new File(dest + FS + name + compression.getFilenameExtension() + JupidatorDeployer.EXTENSION);
        String status = compression.decompress(downloadfile, name);
        if (status == null) {
            if (!compression.getFilenameExtension().equals(""))
                downloadfile.delete();
            return null;
        }
        application.receiveMessage(status);
        JupidatorDeployer.rmTree(new File(dest + FS + name + JupidatorDeployer.EXTENSION));
        return status;
    }

    public void cancel(UpdatedApplication application) {
        File del = new File(dest + FS + name + compression.getFilenameExtension() + JupidatorDeployer.EXTENSION);
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
