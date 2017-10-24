/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.create;

import com.panayotis.jupidator.parsables.ParseFile;
import com.panayotis.jupidator.parsables.ParseFolder;
import com.panayotis.jupidator.parsables.ParseItem;
import java.io.File;
import java.util.Collection;

/**
 *
 * @author teras
 */
public class SnapshotCreator extends CommandCreator {

    public static Collection<Command> create(ParseFolder installation, File inputRoot, File output, String version, String arch, boolean nomd5, boolean nosha1, boolean nosha256, Iterable<String> ignore) {
        SnapshotCreator snap = new SnapshotCreator(inputRoot, output, version, arch, nomd5, nosha1, nosha256);
        snap.parse(installation, "");
        Collection<Command> commands = snap.getCommands();
        for (String ign : ignore)
            commands.add(new IgnoreCommand(ign));
        return commands;
    }

    public SnapshotCreator(File inputRoot, File output, String version, String arch, boolean nomd5, boolean nosha1, boolean nosha256) {
        super(inputRoot, output, version, arch, nomd5, nosha1, nosha256, false);
    }

    private void parse(ParseItem item, String path) {
        if (item instanceof ParseFile)
            file(item, path);
        else if (item instanceof ParseFolder) {
            path = item.name.equals(".") ? path : path + item.name + "/";
            for (String name : ((ParseFolder) item).names())
                parse(((ParseFolder) item).searchFor(name), path);
        }
    }

}
