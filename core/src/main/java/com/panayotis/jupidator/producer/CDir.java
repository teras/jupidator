/*
 *
 * This file is part of Jupidator.
 *
 * Jupidator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 *
 * Jupidator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jupidator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package com.panayotis.jupidator.producer;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class CDir extends CPath {

    private final List<CPath> paths = new ArrayList<CPath>();

    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    protected CDir(File file, String arch) throws IOException {
        this(file, null, arch);
    }

    protected CDir(File file, CDir parent) throws IOException {
        this(file, parent, parent == null ? null : parent.arch);
    }

    // From XML
    protected CDir(String dirname, CDir parent, String arch) {
        super(dirname, parent, arch);
    }

    private CDir(File file, CDir parent, String arch) throws IOException {
        super(file.getName(), parent, arch);

        File[] children = file.listFiles();
        if (children != null && children.length > 0)
            for (File child : children)
                if (child.isDirectory())
                    paths.add(new CDir(child, this));
                else if (child.isFile())
                    paths.add(new CFile(child, this));
        Collections.sort(paths);
    }

    @Override
    protected void dump(Writer out, int depth) throws IOException {
        tabs(out, depth).append("<dir name=\"").append(getName()).append("\"");
        if (paths.isEmpty())
            out.append("/>\n");
        else {
            out.append(">\n");
            for (CPath el : paths)
                el.dump(out, depth + 1);
            tabs(out, depth).append("</dir>\n");
        }
    }

    public void add(CPath cpath) {
        paths.add(cpath);
    }

    @Override
    protected void compare(CPath original, COutput out) throws IOException {
        if (original instanceof CDir) {
            CDir ordir = (CDir) original;
            Set<CPath> orpaths = new TreeSet<CPath>(ordir.paths);
            for (CPath mysub : paths) {
                CPath orsub = ordir.find(mysub.getName());
                if (orsub == null)
                    mysub.store(out);
                else {
                    mysub.compare(orsub, out);
                    orpaths.remove(orsub);
                }
            }
            for (CPath othersubpath : orpaths)
                othersubpath.delete(out);
        } else {
            original.delete(out);
            store(out);
        }
    }

    private CPath find(String name) {
        for (CPath path : paths)
            if (path.getName().equals(name))
                return path;
        return null;
    }

    @Override
    protected void store(COutput out) throws IOException {
        for (CPath path : paths)
            path.store(out);
        if (paths.isEmpty()) {
            // mkdir does not exist in jupidator yet
        }
    }
}
