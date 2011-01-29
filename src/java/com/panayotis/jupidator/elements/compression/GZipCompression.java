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
        FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
            fin = new FileInputStream(compressedfile);
            fout = new FileOutputStream(outfile);
            return FileUtils.copyFile(new GZIPInputStream(fin), fout, null);
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
        return ".gz";
    }
}
