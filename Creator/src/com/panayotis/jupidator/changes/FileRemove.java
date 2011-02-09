/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.changes;

import com.panayotis.jupidator.FileItem;
import javax.swing.Icon;

/**
 *
 * @author teras
 */
public class FileRemove extends Change {

    private final Icon fileicon;

    public FileRemove(FileItem item, FileItem olditem, FileItem base) {
        super(item, base);
        fileicon = olditem.getIcon();
    }

    @Override
    public boolean willRemove() {
        return true;
    }

    @Override
    public Icon getIcon() {
        return fileicon;
    }
}
