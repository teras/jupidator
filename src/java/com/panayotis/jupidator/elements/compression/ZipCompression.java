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

package com.panayotis.jupidator.elements.compression;

import com.panayotis.jupidator.elements.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.panayotis.jupidator.i18n.I18N._;

/**
 *
 * @author teras
 */
public class ZipCompression implements CompressionMethod {

    private boolean packageBased = false;

    @SuppressWarnings("unchecked")
    public String decompress(File compressedfile, File outfile) {
        ZipFile fin = null;
        try {
            fin = new ZipFile(compressedfile);
            ArrayList<ZipEntry> files = getFileList(fin);
            if (files.size() == 1) {
                /* We have a single file.
                 * Treat it as a regular compressed file */
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(outfile);
                    return FileUtils.copyFile(fin.getInputStream(files.get(0)), fout, null);
                } catch (IOException ex) {
                    return ex.getMessage();
                } finally {
                    if (fout != null)
                        try {
                            fout.close();
                        } catch (Exception ex) {
                        }
                }
            } else if (files.size() > 1) {
                /**
                 * We have a package. Since we are using a lazy installation
                 * scheme, unzip all files in a temporary folder one level deep
                 * than the actual required folder. Thus, if the output file is
                 * a regular file, just perform a rename. If it is a directory
                 * (thus we have a package), move all files one directory up.
                 * With this trick, it is possible to refrain the actual file
                 * manipulation in a latter time.
                 */
                packageBased = true;
                for (ZipEntry entry : files) {
                    File out = new File(outfile, entry.getName().replace("/", File.separator));
                    if (!FileUtils.makeDirectory(out.getParentFile()))
                        return _("Unable to unpack under {0}", out.getParentFile().getPath());
                    FileOutputStream fout = null;
                    try {
                        String status = FileUtils.copyFile(fin.getInputStream(entry), new FileOutputStream(out), null);
                        if (status != null)
                            return status;
                    } catch (IOException ex) {
                        return ex.getMessage();
                    } finally {
                        if (fout != null)
                            try {
                                fout.close();
                            } catch (Exception ex) {
                            }
                    }
                }
                return null;
            } else
                /* Empty ZIP file */
                return null;
        } catch (IOException ex) {
            return ex.getMessage();
        } finally {
            if (fin != null)
                try {
                    fin.close();
                } catch (IOException ex) {
                }
        }
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

    public boolean isPackageBased() {
        return packageBased;
    }
}
