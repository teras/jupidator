/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.diff;

import com.panayotis.jupidator.parsables.ParseFile;
import com.panayotis.jupidator.parsables.ParseFolder;
import com.panayotis.jupidator.parsables.ParseItem;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author teras
 */
public class Diff {

    private final Collection<String> commands = new ArrayList<>();

    public static Diff diff(ParseFolder oldInstallation, ParseFolder newInstallation, File inputRoot, File output) {
        Diff diff = new Diff();
        diff.diff(oldInstallation, newInstallation, "");
        return diff;
    }

    private void diff(ParseItem oldItem, ParseItem newItem, String path) {
        if (newItem == null)
            rm(oldItem, path);
        else if (oldItem == null)
            file(newItem, path);
        else if (!oldItem.getClass().equals(newItem.getClass())) {
            rm(oldItem, path);
            file(newItem, path);
        } else if (oldItem instanceof ParseFile) {
            if (!oldItem.equals(newItem))
                file(newItem, path);
        } else if (oldItem instanceof ParseFolder) {
            Collection<String> oldNames = ((ParseFolder) oldItem).names();
            Collection<String> newNames = ((ParseFolder) newItem).names();
            path = oldItem.name.equals(".") ? path : path + oldItem.name + "/";
            for (String name : oldNames)
                if (newNames.contains(name)) {
                    diff(((ParseFolder) oldItem).searchFor(name), ((ParseFolder) newItem).searchFor(name), path);
                    newNames.remove(name);
                } else
                    diff(((ParseFolder) oldItem).searchFor(name), null, path);
            for (String name : newNames)
                diff(null, ((ParseFolder) newItem).searchFor(name), path);
        }
    }

    private void rm(ParseItem item, String path) {
        commands.add("<rm file=\"" + path + item.name + "\"/>");
    }

    private void file(ParseItem item, String path) {
        commands.add("<file file=\"" + path + item.name + "\"/>");
    }

    @Override
    public String toString() {
        return String.join("\n", commands);
    }

}
