/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.diff;

import com.panayotis.jupidator.compress.BZip2FileCompression;
import com.panayotis.jupidator.compress.TarBz2FolderCompression;
import com.panayotis.jupidator.digester.Digester;
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
public class DiffCreator {

    private final Collection<DiffCommand> commands = new ArrayList<>();
    private final String version;
    private final File inputRoot;
    private final File output;
    private final boolean nomd5;
    private final boolean nosha1;
    private final boolean nosha256;

    public static Collection<DiffCommand> create(ParseFolder oldInstallation, ParseFolder newInstallation, File inputRoot, File output, String version, boolean nomd5, boolean nosha1, boolean nosha256) {
        DiffCreator diff = new DiffCreator(inputRoot, output, version, nomd5, nosha1, nosha256);
        diff.diff(oldInstallation, newInstallation, "");
        return diff.commands;
    }

    private DiffCreator(File inputRoot, File output, String version, boolean nomd5, boolean nosha1, boolean nosha256) {
        this.version = version;
        this.inputRoot = inputRoot;
        this.output = output;
        this.nomd5 = nomd5;
        this.nosha1 = nosha1;
        this.nosha256 = nosha256;
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
        commands.add(new DiffRm(path + item.name));
    }

    private void file(ParseItem item, String path) {
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        path = path.isEmpty() ? "" : "/" + path;

        System.out.println("Parsing file " + (path + File.separator + item.name).substring(1));
        File infile = new File(inputRoot, path + File.separator + item.name).getAbsoluteFile();
        String ext = infile.isDirectory() ? "tar.bz2" : "bz2";
        File outfile = new File(output, version + path + "/" + item.name + "." + ext).getAbsoluteFile();
        outfile.getParentFile().mkdirs();
        if (infile.isDirectory())
            TarBz2FolderCompression.compress(infile, outfile);
        else
            BZip2FileCompression.compress(infile, outfile);

        DiffFile file = new DiffFile(ext, "${APPHOME}" + path, item.name, outfile.length(), version + path);
        if (!nomd5)
            file.setMD5(Digester.getDigester("MD5").setHash(outfile).toString());
        if (!nosha1)
            file.setSHA1(Digester.getDigester("SHA1").setHash(outfile).toString());
        if (!nosha256)
            file.setSHA256(Digester.getDigester("SHA-256").setHash(outfile).toString());
        commands.add(file);
    }
}
