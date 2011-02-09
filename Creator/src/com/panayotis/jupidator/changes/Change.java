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
public class Change {

    private final String entry;
    private boolean accepted = true;

    public Change(FileItem item, FileItem base) {
        entry = item.getRelativePath(base);
    }

    @Override
    public String toString() {
        return entry;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
