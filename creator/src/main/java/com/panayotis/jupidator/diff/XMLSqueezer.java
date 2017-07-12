/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.diff;

import com.panayotis.jupidator.JupidatorCreatorException;
import com.panayotis.jupidator.xml.XMLWalker;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author teras
 */
public class XMLSqueezer {

    public static void squeeze(File jupidator, File files, String version) {
        XMLWalker w = XMLWalker.load(jupidator);
        if (w == null)
            throw new JupidatorCreatorException("Unable to locate file " + jupidator.getPath());

        w.node("updatelist");
        w.filterNodes("version", q -> q.tag("version"), q -> version.equals(q.attribute("version")));
        if (!w.hasTag("version"))
            throw new JupidatorCreatorException("Unable to find version " + version);

        Map<String, Collection<DiffCommand>> arch_rm = new HashMap<>();
        Collection<DiffCommand> all_rm = new LinkedHashSet<>();
        Map<String, Collection<DiffCommand>> arch_file = new HashMap<>();
        Collection<DiffCommand> all_file = new LinkedHashSet<>();

        // Gather all rm commands from the packed files
        getCommands(w, arch_rm, all_rm, "rm", q -> new DiffRm(q));
        getCommands(w, arch_file, all_file, "file", q -> new DiffFile(q));

        if (arch_rm.size() != arch_file.size())
            throw new RuntimeException("Implementation error: both rm and file commands should have the same size");

        // Important!!! The findCommon methods are destructive and will change the values of the parameters
        Collection<DiffCommand> filesInAll;
        //Create all arch, based on common files
        reconstructArch(w, "all", findCommon(all_rm, arch_rm), filesInAll = findCommon(all_file, arch_file));
        for (DiffCommand c : filesInAll)
            ((DiffFile) c).moveToAll(files);
        for (String arch : arch_rm.keySet())
            reconstructArch(w, arch, arch_rm.get(arch), arch_file.get(arch));

        w.store(new File(jupidator.getParentFile(), "new_" + jupidator.getName()), true);

        removeEmptyDirs(new File(files, version));
    }

    private static void reconstructArch(XMLWalker w, String arch, Collection<DiffCommand> rmc, Collection<DiffCommand> filec) {
        w.toTag("version");
        w.filterNodes("arch", q -> q.remove(), q -> arch.equals(q.attribute("name")));
        w.add("arch").setAttribute("name", arch);
        for (DiffCommand c : rmc)
            c.add(w);
        for (DiffCommand c : filec)
            c.add(w);
    }

    private static void getCommands(XMLWalker w, Map<String, Collection<DiffCommand>> arch, Collection<DiffCommand> all, String nodeName, Function<XMLWalker, DiffCommand> constructor) {
        w.toTag("version").nodes("arch", a -> {
            Collection<DiffCommand> current = new ArrayList<>();
            arch.put(a.attribute("name"), current);
            a.nodes(nodeName, q -> current.add(constructor.apply(q)));
            all.addAll(current);
        });
    }

    private static Collection<DiffCommand> findCommon(Collection<DiffCommand> all, Map<String, Collection<DiffCommand>> archCmd) {
        Collection<DiffCommand> rmc = new ArrayList<>();
        for (DiffCommand cmd : all)
            if (foundInAll(cmd, archCmd.values())) {
                rmc.add(cmd);
                for (String arch : archCmd.keySet())
                    archCmd.get(arch).remove(cmd);
            }
        return rmc;
    }

    private static boolean foundInAll(DiffCommand cmd, Collection<Collection<DiffCommand>> col) {
        for (Collection<DiffCommand> d : col)
            if (!d.contains(cmd))
                return false;
        return true;
    }

    private static void removeEmptyDirs(File root) {
        if (root.isDirectory()) {
            File[] children = root.listFiles();
            if (children != null)
                for (File child : children)
                    removeEmptyDirs(child);
            children = root.listFiles();
            if (children == null || children.length == 0) {
                System.out.println("will delete " + root);
                root.delete();
            }
        }
    }
}
