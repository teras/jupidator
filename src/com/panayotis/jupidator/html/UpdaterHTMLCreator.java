/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.html;

/**
 *
 * @author teras
 */
public interface UpdaterHTMLCreator {

    public abstract void addInfo(String lastrelease, String information);

    public abstract String getHTML();
}
