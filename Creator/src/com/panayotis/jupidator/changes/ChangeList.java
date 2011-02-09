/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.changes;

import com.panayotis.jupidator.FileItem;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author teras
 */
public class ChangeList {

    private ArrayList<Change> list = new ArrayList<Change>();
    private final FileItem base;

    public ChangeList(String f1, String f2) {
        this(f1, f2, null, DataModel.current.useZip());
    }

    public ChangeList(String f1, String f2, String base) {
        this(f1, f2, base, DataModel.current.useZip());
    }

    public ChangeList(String f1, String f2, boolean useZip) {
        this(f1, f2, null, useZip);
    }

    private ChangeList(String f1, String f2, String base, boolean useZip) {
        this.base = new FileItem(f2, base);
        getChanges(new FileItem(f1), new FileItem(f2), useZip);
    }

    private void getChanges(FileItem one, FileItem two, boolean useZip) {
        if (one.isDirectory())
            if (two.isDirectory()) {
                FileItem f1, f2;
                Map<String, FileItem> set1 = one.getChildren();
                Map<String, FileItem> set2 = two.getChildren();
                for (String key : set1.keySet()) {
                    f1 = set1.get(key);
                    f2 = set2.get(key);
                    if (f2 == null)
                        removeItem(f1);
                    else {
                        set2.remove(key);
                        getChanges(f1, f2, useZip);
                    }
                }
                for (String key : set2.keySet())
                    addItem(set2.get(key));
            } else {
                removeItem(one);
                addItem(two);
            }
        else if (two.isDirectory()) {
            removeItem(one);
            addItem(two);
        } else if (!one.equals(two, useZip))
            addItem(two);
    }

    private void removeItem(FileItem item) {
        list.add(new FileRemove(item, base));
    }

    private void addItem(FileItem item) {
        if (item.isDirectory()) {
            Map<String, FileItem> childs = item.getChildren();
            if (childs.isEmpty())
                list.add(new DirAdd(item, base));
            else
                for (String key : childs.keySet())
                    addItem(childs.get(key));
        } else
            list.add(new FileAdd(item, base));
    }

    public int getSize() {
        return list.size();
    }

    public Change getChange(int index) {
        return list.get(index);
    }

    public boolean isAcceptable(int index) {
        return list.get(index).isAccepted();
    }

    public void setAcceptable(int index, boolean value) {
        list.get(index).setAccepted(value);
    }

    @Override
    public String toString() {
        return "[size=" + list.size() + " " + list.toString() + "]";
    }
}
