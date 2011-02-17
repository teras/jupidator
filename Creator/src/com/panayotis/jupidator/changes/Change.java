/*
 *
 * This file is part of Jupidator.
 *
 * Jupidator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 *
 * Jupidator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jupidator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package com.panayotis.jupidator.changes;

import com.panayotis.jupidator.FileItem;
import javax.swing.Icon;

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

    public Icon getIcon() {
        return item.getIcon();
    }

    public abstract boolean willRemove();
}
