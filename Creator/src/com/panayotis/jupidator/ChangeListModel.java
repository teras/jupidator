/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import com.panayotis.jupidator.changes.ChangeList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author teras
 */
public class ChangeListModel implements ListModel {

    private final ChangeList list;

    public ChangeListModel(ChangeList list) {
        this.list = list;
    }

    public int getSize() {
        return list.getSize();
    }

    public Object getElementAt(int index) {
        return list.getItem(index);
    }

    public void addListDataListener(ListDataListener l) {
    }

    public void removeListDataListener(ListDataListener l) {
    }
}
