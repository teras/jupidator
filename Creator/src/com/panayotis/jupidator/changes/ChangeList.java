/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.changes;

import com.panayotis.jupidator.FileItem;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author teras
 */
public class ChangeList {

    private ArrayList<Change> list = new ArrayList<Change>();

    public ChangeList(String f1, String f2) {
        getChanges(new FileItem(new File(f1)), new FileItem(new File(f2)));
    }

    private void getChanges(FileItem one, FileItem two) {
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
                        getChanges(f1, f2);
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
        } else if (!one.equals(two))
            addItem(two);
    }

    private void removeItem(FileItem item) {
        list.add(new FileRemove(item));
    }

    private void addItem(FileItem item) {
        if (item.isDirectory()) {
            Map<String, FileItem> childs = item.getChildren();
            if (childs.isEmpty())
                list.add(new DirAdd(item));
            else
                for (String key : childs.keySet())
                    addItem(childs.get(key));
        } else
            list.add(new FileAdd(item));
    }

    public int getSize() {
        return list.size();
    }

    public Object getItem(int index) {
        return list.get(index);
    }
}
