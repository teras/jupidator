/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import com.panayotis.jupidator.changes.Change;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author teras
 */
public class TableRenderers {

    private static final Color RED = new Color(255, 240, 240);
    private static final Color GREEN = new Color(240, 255, 240);

    public static class InfoRenderer extends DefaultTableCellRenderer {

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

    public static class UseRenderer extends JCheckBox implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Change item = (Change) value;
            if (isSelected)
                setBackground(table.getSelectionBackground());
            else
                setBackground(!item.willRemove() ? RED : GREEN);
            setSelected(item.isAccepted());
            return this;
        }
    }
}
