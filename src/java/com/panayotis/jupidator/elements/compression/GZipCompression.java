/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.compression;

import com.panayotis.jupidator.deployer.JupidatorDeployer;
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
public class GZipCompression implements CompressionMethod {

    public String decompress(File compressedfile, String outfile) {
        try {
            return FileUtils.copyFile(new GZIPInputStream(new FileInputStream(compressedfile)), new FileOutputStream(compressedfile.getParent() + FileUtils.FS + outfile + JupidatorDeployer.EXTENSION), null);
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }

    public String getFilenameExtension() {
        return ".gz";
    }
}
