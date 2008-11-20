/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.panayotis.jupidator.file.compression;

import com.panayotis.jupidator.deployer.JupidatorDeployer;
import com.panayotis.jupidator.file.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.tools.bzip2.CBZip2InputStream;

/**
 *
 * @author teras
 */
public class BZip2Compression implements CompressionMethod {

    public String decompress(File compressedfile, String outfile) {
        try {
            return FileUtils.copyFile(new CBZip2InputStream(new FileInputStream(compressedfile)), new FileOutputStream(compressedfile.getParent() + FileUtils.FS + outfile + JupidatorDeployer.EXTENSION), null);
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }

    public String getFilenameExtension() {
        return ".bz2";
    }
}
