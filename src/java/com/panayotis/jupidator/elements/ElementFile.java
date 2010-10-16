/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.data.UpdaterAppElements;
import com.panayotis.jupidator.launcher.JupidatorDeployer;
import com.panayotis.jupidator.elements.compression.BZip2Compression;
import com.panayotis.jupidator.elements.compression.CompressionMethod;
import com.panayotis.jupidator.elements.compression.GZipCompression;
import com.panayotis.jupidator.elements.compression.NullCompression;
import com.panayotis.jupidator.elements.compression.ZipCompression;
import com.panayotis.jupidator.elements.security.Digester;
import com.panayotis.jupidator.gui.BufferListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * 
 * @author teras
 */
public class ElementFile extends JupidatorElement {

    /** This is actually a URL */
    private final String source;
    private final CompressionMethod compression;
    private final ArrayList<Digester> digesters;
    //
    private final URL source_location;
    private final File download_location;

    public ElementFile(String name, String source, String dest, String size, String compress, UpdaterAppElements elements, ApplicationInfo info) {
        super(name, dest, size, elements, info, ExecutionTime.MID);
        if (source == null)
            source = "";
        this.source = info.applyVariables(elements.getBaseURL() + source);

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
        digesters = new ArrayList<Digester>();

        // Find download URL
        URL url = null;
        try {
            url = new URL(getSourceFile() + compression.getFilenameExtension());
        } catch (MalformedURLException ex) {
        }
        source_location = url;

        // Find download location
        String dl = getDestinationFile();
        if (requiresPrivileges())
            try {
                File temp = File.createTempFile("jupidator.", ".temp");
                dl = temp.getAbsolutePath();
                if (!temp.delete())
                    dl = null;
            } catch (IOException ex) {
                dl = null;
            }
        download_location = (dl == null) ? null : new File(dl + compression.getFilenameExtension() + JupidatorDeployer.EXTENSION);
    }

    public boolean exists() {
        return new File(getDestinationFile()).exists();
    }

    public void addDigester(Digester digester) {
        if (digester != null)
            digesters.add(digester);
    }

    private String getSourceFile() {
        return source + "/" + getFileName();
    }

    @Override
    public String toString() {
        return "+" + getSourceFile() + ">" + getDestinationFile();
    }

    public String getArgument() {
        return "+" + getDestinationFile() + JupidatorDeployer.EXTENSION;
    }

    public String fetch(UpdatedApplication application, BufferListener watcher) {
        if (source_location == null)
            return _("Unable to initialize URL {0}", getSourceFile());
        if (download_location == null)
            return _("Can not initialize download file {0}", getFileName());

        /* Remove old download file, in case it is there.*/
        download_location.delete();
        if (download_location.exists()) {
            application.receiveMessage(_("Could not remove old downloaded file {0}", download_location.getPath()));
            return _("Could not remove old downloaded file {0}", download_location.getPath());
        }

        /* Download file */
        String error = null;
        try {
            error = FileUtils.copyFile(source_location.openConnection().getInputStream(),
                    new FileOutputStream(download_location), watcher);
            if (download_location.length() != getSize())
                error = _("Size of file {0} does not match. Reported {1}, required {2}", download_location.getPath(), download_location.length(), getSize());
            else
                for (Digester d : digesters)
                    if (!d.checkFile(download_location)) {
                        error = _("Checksumming {0} with algorithm {1} failed.", download_location, d.getAlgorithm());
                        break;
                    }
        } catch (IOException ex) {
            error = ex.getMessage();
        }

        if (error == null) {
            /* Successfully downloaded file */
            application.receiveMessage(_("File {0} sucessfully downloaded", getFileName()));
            return null;
        } else {
            /* Error while downloading */
            String msg = _("Unable to download file {0}", getFileName());
            application.receiveMessage(msg + " - " + error);
            return msg;
        }
    }

    public String deploy(UpdatedApplication application) {
        String status = compression.decompress(download_location, new File(getDestinationFile()));
        if (status == null) {
            if (!compression.getFilenameExtension().equals(""))
                if (!download_location.delete())
                    application.receiveMessage(_("Unable to delete downloaded file {0}", download_location.getPath()));
                else
                    application.receiveMessage(_("Successfully deleted downloaded file {0}", download_location.getPath()));
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
}
