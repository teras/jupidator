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

    private final FileItem item;
    private boolean accepted = true;

    public Change(FileItem item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return item.toString();
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
