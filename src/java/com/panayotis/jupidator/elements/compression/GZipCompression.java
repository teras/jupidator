/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.compression;

import com.panayotis.jupidator.elements.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author teras
 */
public class GZipCompression extends SingleFileCompression {

    public String decompress(File compressedfile, File outfile) {
        try {
            return FileUtils.copyFile(new GZIPInputStream(new FileInputStream(compressedfile)), new FileOutputStream(outfile), null);
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }

    public String getFilenameExtension() {
        return ".gz";
    }
}
