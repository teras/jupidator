/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
