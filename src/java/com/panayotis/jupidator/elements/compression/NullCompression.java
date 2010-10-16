/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.compression;

import com.panayotis.jupidator.elements.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 *
 * @author teras
 */
public class NullCompression implements CompressionMethod {

    public String getFilenameExtension() {
        return "";
    }

    public String decompress(File compressedfile, File outfile) {
        if (compressedfile.equals(outfile))
            return null;

        if (!outfile.getParentFile().mkdirs())
            return "Unable to create directory structure under " + outfile.getParentFile().getPath();
        try {
            FileUtils.copyFile(new FileInputStream(compressedfile), new FileOutputStream(outfile), null);
        } catch (FileNotFoundException ex) {
            return ex.getMessage();
        }
        return null;
    }
}
