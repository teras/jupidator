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
import java.io.Reader;
import java.io.Writer;
import java.util.Stack;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class CPath implements Comparable<CPath> {

    private final String pathname;
    private final CDir parent;
    protected final String PS = "/";
    protected final String DEFAULTROOT = "${APPHOME}";
    protected final String arch;

    public static CPath construct(File file, String arch) throws IOException {
        if (!file.exists())
            throw new IOException("File " + file.getPath() + " does not exist");
        else if (!file.canRead())
            throw new IOException("Unable to read from file " + file.getPath());
        if (file.isDirectory())
            return new CDir(file, arch);
        else if (file.isFile())
            return new CFile(file, arch);
        else
            throw new IOException("Unknwon file type for file " + file.getPath());
    }

    private final static class Handler extends DefaultHandler {

        CPath result;
        Stack<CDir> dirs = new Stack<CDir>();
        String arch;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("dir")) {
                CDir cdir = new CDir(attributes.getValue("name"), dirs.isEmpty() ? null : dirs.peek(), arch);
                if (result == null)
                    result = cdir;
                dirs.push(cdir);
            } else if (qName.equals("file")) {
                CFile cfile = new CFile(attributes.getValue("name"), Long.parseLong(attributes.getValue("size")), attributes.getValue("md5"), attributes.getValue("sha256"), dirs.isEmpty() ? null : dirs.peek(), arch);
                if (result == null)
                    result = cfile;
            } else if (qName.equals("arch"))
                arch = attributes.getValue("name");
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equals("dir"))
                dirs.pop();
        }
    }

    public static CPath construct(Reader in) throws IOException {
        Handler handler = new Handler();
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(in), handler);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
        return handler.result;
    }

    public CPath(String pathname, CDir parent, String arch) {
        this.pathname = pathname;
        this.parent = parent;
        if (parent != null)
            parent.add(this);
        this.arch = arch;
    }

    @Override
    public String toString() {
        return pathname;
    }

    @Override
    public int compareTo(CPath t) {
        if (pathname == null)
            return -1;
        return pathname.compareTo(t.pathname);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;
        final CPath other = (CPath) obj;
        if ((this.pathname == null) ? (other.pathname != null) : !this.pathname.equals(other.pathname))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return 314 + (this.pathname != null ? this.pathname.hashCode() : 0);
    }

    public String getName() {
        return pathname;
    }

    public String getArch() {
        return arch;
    }

    protected CDir getParent() {
        return parent;
    }

    protected String getPath(String root) {
        if (parent == null)
            return root;
        else
            return parent.getPath(root) + PS + pathname;
    }

    public void dump(Writer out) throws IOException {
        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.append("<jupidatordump>\n");
        tabs(out, 1);
        out.append("<arch name=\"").append(arch).append("\">\n");
        dump(out, 2);
        tabs(out, 1);
        out.append("</arch>\n");
        out.append("</jupidatordump>\n");
        out.flush();
    }

    protected Writer tabs(Writer out, int depth) throws IOException {
        for (int i = 0; i < depth; i++)
            out.write("  ");
        return out;
    }

    public void findDiff(CPath original, COutput output, String version) throws IOException {
        output.writer.append("<updatelist application=\"\" baseurl=\"\" jupidator=\"600\">\n");
        tabs(output.writer, 1).append("<version release=\"\" version=\"" + version + "\">\n");
        tabs(output.writer, 2).append("<arch name=\"all\">\n");
        tabs(output.writer, 3).append("<description></description>\n");
        compare(original, output);
        tabs(output.writer, 2).append("</arch>\n");
        tabs(output.writer, 1).append("</version>\n");
        output.writer.append("</updatelist>\n");
        output.writer.close();
    }

    protected abstract void dump(Writer out, int depth) throws IOException;

    protected abstract void compare(CPath original, COutput out) throws IOException;

    protected abstract void store(COutput out) throws IOException;

    protected void delete(COutput out) throws IOException {
        tabs(out.writer, 3).append("<rm file=\"").append(getPath(DEFAULTROOT)).append("\"/>\n");
    }
}
