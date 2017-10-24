/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.compress;

import com.panayotis.jupidator.JupidatorCreatorException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.tools.bzip2.CBZip2OutputStream;
import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarOutputStream;

/**
 *
 * @author teras
 */
public class TarBz2FolderCompression {

    private static void appendFile(File f, TarOutputStream out, String path) throws IOException {
        path = path + (path.isEmpty() ? "" : "/") + f.getName();
        out.putNextEntry(new TarEntry(f, path));
        if (f.isFile()) {
            int size;
            byte buffer[] = new byte[0x1000];

            // Safely copy stream
            BufferedInputStream in = null;
            try {
                in = new BufferedInputStream(new FileInputStream(f));
                while ((size = in.read(buffer)) != -1)
                    out.write(buffer, 0, size);
            } catch (IOException ex) {
                throw ex;
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException ex) {
                    }
            }
        }
        out.flush();
        if (f.isDirectory()) {
            File[] subfiles = f.listFiles();
            if (subfiles != null && subfiles.length > 0)
                for (File child : subfiles)
                    appendFile(child, out, path);
        }
    }

    public static Exception compress(File input, File output) {
        if (!input.isDirectory())
            throw new JupidatorCreatorException("TarBz2 compression is selected for folders only");
        try (BufferedOutputStream outb = new BufferedOutputStream(new FileOutputStream(output));
                TarOutputStream out = new TarOutputStream(new CBZip2OutputStream(outb))) {
//            outfile.write('B');
//            outfile.write('Z');
            outb.write('B');
            outb.write('Z');
            appendFile(input, out, "");
            out.close();
            return null;
        } catch (Exception ex) {
            return ex;
        }
    }
}
