/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.gui;

/**
 *
 * @author teras
 */
public interface BufferListener {

    public void addBytes(long bytes);

    public void setAllBytes(long bytes);

    public void freezeSize();

    public void rollbackSize();
}
