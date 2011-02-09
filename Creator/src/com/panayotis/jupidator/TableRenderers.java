/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        setIcon(item.toFileItem().getIcon());
        setText(item.toString());
        return this;
    }
}
