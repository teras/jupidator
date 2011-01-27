/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.compression;

/**
 *
 * @author teras
 */
public abstract class SingleFileCompression implements CompressionMethod {

    public boolean isPackageBased() {
        return false;
    }
}
