/*
 *
 * This file is part of Jupidator.
 *
 * Jupidator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 *
 * Jupidator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jupidator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
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
