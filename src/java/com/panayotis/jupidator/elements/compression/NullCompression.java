/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.compression;

import java.io.File;

/**
 *
 * @author teras
 */
public class NullCompression implements CompressionMethod {

    public String getFilenameExtension() {
        return "";
    }

    public String decompress(File compressedfile, File outfile) {
        return null;
    }
}
