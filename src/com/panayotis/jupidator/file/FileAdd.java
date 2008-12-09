/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.file.compression.CompressionMethod;
import com.panayotis.jupidator.list.*;
import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.deployer.JupidatorDeployer;
import com.panayotis.jupidator.file.compression.BZip2Compression;
import com.panayotis.jupidator.file.compression.GZipCompression;
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
        super(name, dest, size, elements, info, ExecutionTime.MID);
        if (source == null)
            source = "";
        this.source = elements.getBaseURL() + source;
        if (compress == null)
            compress = "none";
        compress = compress.toLowerCase();
        if (compress.equals("zip"))
            compression = new ZipCompression();
        else if (compress.equals("bzip2") || compress.equals("bz2"))
            compression = new BZip2Compression();
        else if (compress.equals("gz") || compress.equals("gzip"))
            compression = new GZipCompression();
        else
            compression = new NullCompression();
    }

    private String getSourceFile() {
        return source + "/" + getFileName();
    }

    public String toString() {
        return "+" + getSourceFile() + ">" + getDestinationFile();
    }

    public String getArgument() {
        return "+" + getDestinationFile() + JupidatorDeployer.EXTENSION;
    }

    public String fetch(UpdatedApplication application, BufferListener blisten) {
        String fromfilename = getSourceFile() + compression.getFilenameExtension();
        String tofilename = getDestinationFile();
        String downloadfilename = tofilename + compression.getFilenameExtension() + JupidatorDeployer.EXTENSION;
        File tofile = new File(tofilename);
        File downloadfile = new File(downloadfilename);
        String msg;

        /* Check if (new) destination file is writable */
        if (!FileUtils.isWritable(tofile)) {
            if (application != null)
                application.receiveMessage(_("Destination file {0} is not writable.", tofilename));
            return _("Destination file {0} is not writable.", getFileName());
        }
        /* Remove old download file. Whether parent directory is writable, was checked with tofile */
        downloadfile.delete();
        if (downloadfile.exists()) {
            if (application != null)
                application.receiveMessage(_("Could not remove old downloaded file {0}", downloadfilename));
            return _("Could not remove old downloaded file {0}", getFileName());
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
                application.receiveMessage(_("File {0} sucessfully downloaded.", downloadfile.getPath()));
            return null;
        }
        /* Error while downloading */
        msg = _("Unable to download file {0}", getFileName());
        if (application != null)
            application.receiveMessage(msg + " - " + error);
        return msg;
    }

    public String deploy(UpdatedApplication application) {
        File downloadfile = new File(getDestinationFile() + compression.getFilenameExtension() + JupidatorDeployer.EXTENSION);
        String status = compression.decompress(downloadfile, getFileName());
        if (status == null) {
            if (!compression.getFilenameExtension().equals("")) {
                if (!downloadfile.delete())
                    application.receiveMessage(_("Unable to delete downloaded file {0}", downloadfile.getPath()));
                else
                    application.receiveMessage(_("Successfully deleted downloaded file {0}", downloadfile.getPath()));
            }
            return null;
        }
        application.receiveMessage(status);
        return status;
    }

    public void cancel(UpdatedApplication application) {
        File del = new File(getDestinationFile() + compression.getFilenameExtension() + JupidatorDeployer.EXTENSION);
        if (!del.delete())
            application.receiveMessage(_("Unable to delete downloaded file {0}", del.getPath()));
        else
            application.receiveMessage(_("Successfully deleted downloaded file {0}", del.getPath()));

        File depfile = new File(getDestinationFile() + JupidatorDeployer.EXTENSION);
        if (!JupidatorDeployer.rmTree(depfile))
            application.receiveMessage(_("Unable to delete file {0}", depfile));
        else
            application.receiveMessage(_("Successfully deleted file {0}", depfile));
    }

    public FileElement updateSystemVariables() {
        super.updateSystemVariables();
        source = info.updatePath(source);
        return this;
    }
}
