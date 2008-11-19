/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file.compression;


import java.io.File;

/**
 *
 * @author teras
 */
public class NullCompression implements CompressionMethod {

    public String decompress(File f, String name) {
       return null;
    }

    public String getFilenameExtension() {
        return "";
    }
}
