/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.compression;

import com.panayotis.jupidator.elements.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 *
 * @author teras
 */
public class ZipCompression implements CompressionMethod {

    @SuppressWarnings("unchecked")
    public String decompress(File compressedfile, File outfile) {
        try {
            ZipFile zip = new ZipFile(compressedfile);
            ArrayList<ZipEntry> files = getFileList(zip);
            if (files.size() < 1)
                return null;
            else if (files.size() == 1)
                return FileUtils.copyFile(zip.getInputStream(files.get(0)), new FileOutputStream(outfile), null);
            else
                /**
                 * We have a package.
                 * Since we are using a lazy installation scheme, unzip all files in a temporary folder one level deep than the actual required folder.
                 * Thus, if the output file is a regular file, just perform a rename. If it is a directory (thus we have a package), move all files one directory up.
                 * With this trick, it is possible to refrain the actual file manipulation in a latter time.
                 */
                for (ZipEntry entry : files) {
                    File out = new File(outfile, entry.getName().replace("/", File.separator));
                    if (!FileUtils.makeDirectory(out.getParentFile()))
                        return "Unable to create directory structure under " + out.getParentFile().getPath();
                    String status = FileUtils.copyFile(zip.getInputStream(entry), new FileOutputStream(out), null);
                    if (status != null)
                        return status;
                }
        } catch (ZipException ex) {
            return ex.getMessage();
        } catch (IOException ex) {
            return ex.getMessage();
        }
        return null;
    }

    private ArrayList<ZipEntry> getFileList(ZipFile zip) {
        ArrayList<ZipEntry> files = new ArrayList<ZipEntry>();
        ZipEntry entry;
        Enumeration<ZipEntry> num = (Enumeration<ZipEntry>) zip.entries();
        while (num.hasMoreElements()) {
            entry = num.nextElement();
            if (!entry.isDirectory())
                files.add(entry);
        }
        return files;
    }

    public String getFilenameExtension() {
        return ".zip";
    }
}
