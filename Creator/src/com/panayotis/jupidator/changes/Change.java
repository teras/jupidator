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
public abstract class Change {

    private final String entry;
    private final FileItem item;
    private boolean accepted = true;

    public Change(FileItem item, FileItem base) {
        entry = item.getRelativePath(base);
        this.item = item;
    }

    @Override
    public String toString() {
        return entry;
    }

    public FileItem toFileItem() {
        return item;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public abstract boolean willRemove();
}
