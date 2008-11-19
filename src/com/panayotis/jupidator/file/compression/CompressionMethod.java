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
public interface CompressionMethod {

    /**
     * 
     * @param f
     * @return error message
     */
    public String decompress(File compressedfile, String outfile);
    /**
     * 
     * @return source filename extension
     */
    public String getFilenameExtension();
}
