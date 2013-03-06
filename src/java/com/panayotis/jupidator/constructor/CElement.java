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

public abstract class CElement {

    public static CElement construct(File file) throws IOException {
        if (file.isDirectory())
            return new CDir(file);
        else if (file.isFile())
            return new CFile(file);
        else
            throw new IOException("Unknwon file type for file " + file.getPath());
    }
    private final File file;

    public CElement(File file) throws IOException {
        this.file = file;
        if (!file.exists())
            throw new IOException("File " + file + " does not exist");
        if (!file.canRead())
            throw new IOException("File " + file + " can not be read");
    }

    public String getName() {
        return file.getName();
    }

    @Override
    public String toString() {
        return file.toString();
    }

    public void dump(Writer out) throws IOException {
        out.append("<jupidatordump>\n");
        dump(out, 1);
        out.append("</jupidatordump>\n");
        out.flush();
    }

    protected abstract void dump(Writer out, int depth) throws IOException;

    protected void dumpTabs(Writer out, int depth) throws IOException {
        for (int i = 0; i < depth; i++)
            out.write("  ");
    }
}
