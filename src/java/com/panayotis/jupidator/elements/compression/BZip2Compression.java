/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.compression;

import com.panayotis.jupidator.launcher.JupidatorDeployer;
import com.panayotis.jupidator.elements.FileUtils;
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
            FileInputStream fin = new FileInputStream(compressedfile);
            char b = (char) fin.read();
            char z = (char) fin.read();
            if (b != 'B' && z != 'Z')
                fin.reset();
            return FileUtils.copyFile(new CBZip2InputStream(fin), new FileOutputStream(compressedfile.getParent() + FileUtils.FS + outfile + JupidatorDeployer.EXTENSION), null);
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }

    public String getFilenameExtension() {
        return ".bz2";
    }
}
