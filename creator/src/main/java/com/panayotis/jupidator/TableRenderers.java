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
package com.panayotis.jupidator;

import com.panayotis.jupidator.changes.Change;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author teras
 */
public class TableRenderers extends DefaultTableCellRenderer {

    private static final Color RED = new Color(255, 230, 230);
    private static final Color GREEN = new Color(230, 255, 240);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Change item = (Change) value;
        if (isSelected)
            setBackground(table.getSelectionBackground());
        else
            setBackground(item.willRemove() ? RED : GREEN);
        setIcon(item.getIcon());
        setText(item.toString());
        return this;
    }
}
