/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.compress;

import com.panayotis.jupidator.JupidatorCreatorException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.tools.bzip2.CBZip2OutputStream;

/**
 *
 * @author teras
 */
public class BZip2FileCompression {

    public static Exception compress(File input, File output) {
        if (!input.isFile())
            throw new JupidatorCreatorException("BZip2 compression is selected for files only");

        try (FileInputStream in = new FileInputStream(input);
                BufferedOutputStream outb = new BufferedOutputStream(new FileOutputStream(output));
                CBZip2OutputStream out = new CBZip2OutputStream(outb)) {
            byte buffer[] = new byte[0x1000];
            int size = 0;
            outb.write('B');
            outb.write('Z');
            while ((size = in.read(buffer)) >= 0)
                out.write(buffer, 0, size);
            out.flush();
            return null;
        } catch (Exception ex) {
            return ex;
        }
    }
}
