/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.diff;

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
public class Diff {

    private final Collection<String> commands = new ArrayList<>();
    private final String version;
    private final File inputRoot;
    private final File output;

    public static Diff diff(ParseFolder oldInstallation, ParseFolder newInstallation, File inputRoot, File output, String version) {
        Diff diff = new Diff(inputRoot, output, version);
        diff.diff(oldInstallation, newInstallation, "");
        return diff;
    }

    private Diff(File inputRoot, File output, String version) {
        this.version = version;
        this.inputRoot = inputRoot;
        this.output = output;
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
        commands.add("        <rm file=\"" + path + item.name + "\"/>");
    }

    private void file(ParseItem item, String path) {
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        path = path.isEmpty() ? "" : "/" + path;

        File infile = new File(inputRoot, path + File.separator + item.name);
        File outfile;

        commands.add("        <file name=\"" + item.name + "\""
                + " compress=\"" + (infile.isDirectory() ? "tar.bz2" : "bzip2") + "\""
                + " destdir=\"${APPHOME}" + path + "\""
                + " sourcedir=\"" + version + path + "\""
                + ">");
//        commands.add("            <md5 value=\"" + Digester.getDigester("MD5").setHash(f).toString() + "\"/>");
//        commands.add("            <sha1 value=\"" + Digester.getDigester("SHA1").setHash(f).toString() + "\"/>");
//        commands.add("            <sha2 type=\"256\" value=\"" + Digester.getDigester("SHA-256").setHash(f).toString() + "\"/>");
        commands.add("        <file/>");
    }

    @Override
    public String toString() {
        return String.join("\n", commands);
    }

}
