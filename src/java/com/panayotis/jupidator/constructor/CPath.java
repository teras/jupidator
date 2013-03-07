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
import java.io.Reader;
import java.io.Writer;
import java.util.Stack;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class CPath {

    private final String pathname;

    public static CPath construct(File file) throws IOException {
        if (file.isDirectory())
            return new CDir(file);
        else if (file.isFile())
            return new CFile(file);
        else
            throw new IOException("Unknwon file type for file " + file.getPath());
    }

    private final static class Handler extends DefaultHandler {

        CPath result;
        Stack<CDir> dirs = new Stack<CDir>();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("dir")) {
                CDir cdir = new CDir(attributes.getValue("name"));
                attachPath(cdir);
                dirs.push(cdir);
            } else if (qName.equals("file")) {
                CFile cfile = new CFile(attributes.getValue("name"), Long.parseLong(attributes.getValue("size")), attributes.getValue("md5"), attributes.getValue("sha256"));
                attachPath(cfile);
            }
        }

        private void attachPath(CPath path) {
            if (result == null)
                result = path;
            CDir parent = dirs.isEmpty() ? null : dirs.peek();
            if (parent != null)
                parent.add(path);
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

    public CPath(String pathname) {
        this.pathname = pathname;
    }

    public String getName() {
        return pathname;
    }

    @Override
    public String toString() {
        return pathname;
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
