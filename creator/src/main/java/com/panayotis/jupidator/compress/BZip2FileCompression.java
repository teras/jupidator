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
import java.io.IOException;
import org.apache.tools.bzip2.CBZip2OutputStream;

/**
 *
 * @author teras
 */
public class BZip2FileCompression {

    public static Exception compress(File input, File output) {
        if (!input.isFile())
            throw new JupidatorCreatorException("BZip2 compression is selected for files only");

        BufferedOutputStream outb = null;
        CBZip2OutputStream out = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(input);
            byte buffer[] = new byte[0x1000];
            int size = 0;

            outb = new BufferedOutputStream(new FileOutputStream(output));
            outb.write('B');
            outb.write('Z');
            out = new CBZip2OutputStream(outb);
            while ((size = in.read(buffer)) >= 0)
                out.write(buffer, 0, size);
            out.flush();
            return null;
        } catch (Exception ex) {
            return ex;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException ex) {
                }
            if (out != null)
                try {
                    out.close();
                } catch (IOException ex) {
                }
            else if (outb != null)
                try {
                    outb.close();
                } catch (IOException ex2) {
                }
        }
    }
}
