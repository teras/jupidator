/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.compression;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author teras
 */
public interface CompressionMethod extends Serializable {

    /**
     * 
     * @param f
     * @return error message
     */
    public String decompress(File compressedfile, File outfile);
    /**
     * 
     * @return source filename extension
     */
    public String getFilenameExtension();
}
