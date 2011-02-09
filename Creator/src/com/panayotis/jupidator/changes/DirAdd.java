/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.changes;

import com.panayotis.jupidator.FileItem;

/**
 *
 * @author teras
 */
public class DirAdd extends Change {

    public DirAdd(FileItem item, FileItem base) {
        super(item, base);
    }

    @Override
    public boolean willRemove() {
        return false;
    }
}
