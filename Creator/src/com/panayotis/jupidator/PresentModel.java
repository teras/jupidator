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
import com.panayotis.jupidator.changes.ChangeList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author teras
 */
public class PresentModel extends AbstractTableModel {

    private final ChangeList list;

    public PresentModel(ChangeList list) {
        this.list = list;
    }

    public int getRowCount() {
        return list.getSize();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return list.isAcceptable(rowIndex);
            default:
                return list.getChange(rowIndex);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Boolean.class;
            default:
                return Change.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0)
            list.setAcceptable(rowIndex, ((Boolean) aValue));
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Use";
            default:
                return "File";
        }
    }
}
