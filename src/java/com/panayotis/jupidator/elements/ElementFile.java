/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import static com.panayotis.jupidator.i18n.I18N._;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.data.UpdaterAppElements;
import com.panayotis.jupidator.elements.compression.BZip2Compression;
import com.panayotis.jupidator.elements.compression.CompressionMethod;
import com.panayotis.jupidator.elements.compression.GZipCompression;
import com.panayotis.jupidator.elements.compression.NullCompression;
import com.panayotis.jupidator.elements.compression.ZipCompression;
import com.panayotis.jupidator.elements.mirror.MirrorList;
import com.panayotis.jupidator.elements.security.Digester;
import com.panayotis.jupidator.elements.mirror.MirroredFile;
import com.panayotis.jupidator.elements.security.PermissionManager;
import com.panayotis.jupidator.gui.BufferListener;
import jupidator.launcher.XEFile;
import jupidator.launcher.XElement;
import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author teras
 */
public class ElementFile extends JupidatorElement {

    private static final String EXTENSION = ".jupidator";
    private final CompressionMethod compression;
    private final ArrayList<Digester> digesters;
    private final MirroredFile source_location;
    private final File download_location;
    private final File uncompress_location;
    private final MirrorList mirrors;

    public ElementFile(String name, String source, String dest, String size, String compress, UpdaterAppElements elements, ApplicationInfo info) {
        super(name, dest, size, elements, info, ExecutionTime.MID);

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

        // Calculate source location
        source_location = new MirroredFile(source, getFileName(), info);
        source_location.setExtension(compression.getFilenameExtension());
        mirrors = elements.getMirrors();

        // Find download location
        if (requiresPrivileges())
            download_location = new File(PermissionManager.manager.requestSlot(), getFileName() + compression.getFilenameExtension() + EXTENSION);
        else
            download_location = new File(getDestinationFile() + compression.getFilenameExtension() + EXTENSION);
        uncompress_location = new File(download_location.getParent(), getFileName() + EXTENSION);
    }

    public boolean exists() {
        return new File(getDestinationFile()).exists();
    }

    public void addDigester(Digester digester) {
        if (digester != null)
            digesters.add(digester);
    }

    @Override
    public String toString() {
        return "+" + source_location.toString() + ">" + getDestinationFile();
    }

    public String fetch(UpdatedApplication application, BufferListener watcher) {
        if (download_location == null)
            return _("Can not initialize download file {0}", getFileName());

        /* Create destination directory, if it does not exist */
        if (!FileUtils.makeDirectory(download_location.getParentFile()))
            return _("Unable to create directory structure under {0}", download_location.getParentFile().getPath());

        /* Remove old download/uncompressed file, in case it exists */
        if (FileUtils.rmTree(download_location) != null)
            return _("Could not remove old download file {0}", download_location.getPath());
        if (FileUtils.rmTree(uncompress_location) != null)
            return _("Could not remove old temporary file {0}", uncompress_location.getPath());

        /* Download file */
        String error = mirrors.downloadFile(source_location, download_location, watcher, application);
        if (error != null)
            return error;

        /* Check file size */
        if (download_location.length() != getSize())
            return _("Size of file {0} does not match. Reported {1}, required {2}", download_location.getPath(), download_location.length(), getSize());

        /* Check sums */
        for (Digester d : digesters)
            if (!d.checkFile(download_location))
                return _("Checksumming {0} with algorithm {1} failed.", download_location, d.getAlgorithm());

        /* Successfully downloaded file */
        application.receiveMessage(_("File {0} sucessfully downloaded", getFileName()));
        return null;
    }

    public String prepare(UpdatedApplication application) {
        String status = compression.decompress(download_location, uncompress_location);
        if (status == null) {
            if (!compression.getFilenameExtension().equals(""))
                if (FileUtils.rmTree(download_location) != null)
                    application.receiveMessage(_("Unable to delete downloaded file {0}", download_location.getPath()));
            return null;
        }
        application.receiveMessage(status);
        return status;
    }

    public void cancel(UpdatedApplication application) {
        String res = FileUtils.rmTree(download_location);
        if (res != null)
            application.receiveMessage(res);
        res = FileUtils.rmTree(uncompress_location);
        if (res != null)
            application.receiveMessage(res);
    }

    @Override
    public XElement getExecElement() {
        File destination = new File(getDestinationFile());
        if (compression.isPackageBased())
            destination = destination.getParentFile();
        return new XEFile(uncompress_location.getPath(), destination.getAbsolutePath());
    }
}
