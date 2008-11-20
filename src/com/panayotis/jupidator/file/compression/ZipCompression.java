/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file.compression;

import com.panayotis.jupidator.deployer.JupidatorDeployer;
import com.panayotis.jupidator.file.FileUtils;
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
    public String decompress(File file, String outfile) {
        try {
            ZipFile zip = new ZipFile(file);
            ArrayList<ZipEntry> files = getFileList(zip);
            if (files.size() == 1) {
                return FileUtils.copyFile(zip.getInputStream(files.get(0)), new FileOutputStream(file.getParent()+FileUtils.FS+outfile+JupidatorDeployer.EXTENSION), null);
            } else {
                String status;
                File out;
                int fsize = outfile.length() + 1;
                String parent = file.getParent() + FileUtils.FS;
                File parentfile = new File(parent);
                boolean embedded = isOutFileEmbedded(files, outfile);
                
                parentfile.mkdirs();
                for (ZipEntry entry : files) {
                    if (embedded)
                        out = new File(parent + outfile + JupidatorDeployer.EXTENSION + FileUtils.FS + entry.getName().substring(fsize).replace('/', FileUtils.FS));
                    else
                        out = new File(parent + outfile + JupidatorDeployer.EXTENSION + FileUtils.FS + entry.getName().replace('/', FileUtils.FS));
                    out.getParentFile().mkdirs();
                    status = FileUtils.copyFile(zip.getInputStream(entry), new FileOutputStream(out), null);
                    if (status != null)
                        return status;
                }
            }
        } catch (ZipException ex) {
            return ex.getMessage();
        } catch (IOException ex) {
            return ex.getMessage();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
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

    private boolean isOutFileEmbedded(ArrayList<ZipEntry> files, String outfile) {
        String outdir = outfile + "/";
        for (ZipEntry entry : files) {
            if (!entry.getName().startsWith(outdir))
                return false;
        }
        return true;
    }

    public String getFilenameExtension() {
        return ".zip";
    }
}
