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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author teras
 */
public class PathDumper {

    private final File start;
    private Writer out;

    public PathDumper(File start) {
        this.start = start;
        try {
            out = new OutputStreamWriter(System.out, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
    }

    public void setOut(Writer out) {
        this.out = out;
    }

    public boolean dump() {
        if (!start.exists()) {
            System.err.println("Unknown path " + start.getPath());
            return false;
        }

        try {
            out.append("<jupidatordump>\n");
            dumpItem(start, out, 1);
            out.append("</jupidatordump>\n");
            out.flush();
        } catch (IOException ex) {
            System.err.println("Unable to write to output stream");
        }
        return true;
    }

    private void dumpItem(File in, Writer out, int depth) throws IOException {
        dumpTabs(out, depth);
        if (in.isFile())
            out.append("<file name=\"").append(in.getName())
                    .append("\" size=\"").append(Long.toString(in.length()))
                    .append("\" md5=\"").append(getDigest(in, "MD5"))
                    .append("\" sha256=\"").append(getDigest(in, "SHA-256"))
                    .append("\"/>\n");
        else if (in.isDirectory()) {
            out.append("<dir name=\"").append(in.getName()).append("\"");
            File[] children = in.listFiles();
            if (children != null && children.length > 0) {
                out.append(">\n");
                for (File child : children)
                    dumpItem(child, out, depth + 1);
                dumpTabs(out, depth);
                out.append("</dir>\n");
            } else
                out.append("/>\n");
        }
    }

    private void dumpTabs(Writer out, int depth) throws IOException {
        for (int i = 0; i < depth; i++)
            out.write("  ");
    }

    private String getDigest(File in, String algorithm) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
            FileInputStream fis = null;
            try {
                byte[] buffer = new byte[1024];
                digest.reset();
                int read;
                fis = new FileInputStream(in);
                while ((read = fis.read(buffer)) >= 0)
                    digest.update(buffer, 0, read);

                byte[] outb = digest.digest();
                StringBuilder sbout = new StringBuilder();
                for (byte b : outb)
                    sbout.append(String.format("%02x", b));
                return sbout.toString();
            } catch (Exception ex) {
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException ex) {
                    }
            }
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException("Algorithm error while digesting");
        }
        return null;
    }
}
