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
import org.apache.tools.bzip2.CBZip2InputStream;

/**
 *
 * @author teras
 */
public class BZip2Compression extends SingleFileCompression {

    public String decompress(File compressedfile, File outfile) {
        FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
            fin = new FileInputStream(compressedfile);
            fout = new FileOutputStream(outfile);
            char b = (char) fin.read();
            char z = (char) fin.read();
            if (b != 'B' && z != 'Z')
                fin.reset();
            return FileUtils.copyFile(new CBZip2InputStream(fin), fout, null);
        } catch (IOException ex) {
            return ex.getMessage();
        } finally {
            if (fin != null)
                try {
                    fin.close();
                } catch (IOException ex) {
                }
            if (fout != null)
                try {
                    fout.close();
                } catch (IOException ex) {
                }
        }
    }

    public String getFilenameExtension() {
        return ".bz2";
    }
}
