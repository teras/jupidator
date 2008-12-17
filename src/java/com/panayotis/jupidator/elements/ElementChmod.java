/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.data.UpdaterAppElements;

/**
 *
 * @author teras
 */
public class ElementChmod extends ElementChstatus {

    public ElementChmod(String file, String attr, String recursive, UpdaterAppElements elements, ApplicationInfo info) {
        super("chmod", file, attr, recursive, elements, info);
    }
}
