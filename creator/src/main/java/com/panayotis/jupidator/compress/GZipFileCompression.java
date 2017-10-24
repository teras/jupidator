/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.compress;

import com.panayotis.jupidator.JupidatorCreatorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author teras
 */
public class GZipFileCompression {

    public static Exception compress(File input, File output) {
        if (!input.isFile())
            throw new JupidatorCreatorException("GZip compression is selected for files only");

        try (FileInputStream fis = new FileInputStream(input);
                GZIPInputStream gis = new GZIPInputStream(fis);
                FileOutputStream fos = new FileOutputStream(output)) {
            byte[] buffer = new byte[0x1000];
            int len;
            while ((len = gis.read(buffer)) != -1)
                fos.write(buffer, 0, len);
            return null;
        } catch (Exception e) {
            return e;
        }
    }
}
