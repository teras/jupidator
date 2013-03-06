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

package com.panayotis.jupidator.constructor;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CDir extends CPath {

    private final List<CPath> paths;

    public CDir(File file) throws IOException {
        super(file);

        paths = new ArrayList<CPath>();
        File[] children = file.listFiles();
        if (children != null && children.length > 0)
            for (File child : children)
                if (child.isDirectory())
                    paths.add(new CDir(child));
                else if (child.isFile())
                    paths.add(new CFile(child));
    }

    public CPath find(String name) {
        for (CPath path : paths)
            if (path.getName().equals(name))
                return path;
        return null;
    }

    @Override
    protected void dump(Writer out, int depth) throws IOException {
        dumpTabs(out, depth);
        out.append("<dir name=\"").append(getName()).append("\"");
        if (paths.isEmpty())
            out.append("/>\n");
        else {
            out.append(">\n");
            for (CPath el : paths)
                el.dump(out, depth + 1);
            dumpTabs(out, depth);
            out.append("</dir>\n");
        }
    }
}
