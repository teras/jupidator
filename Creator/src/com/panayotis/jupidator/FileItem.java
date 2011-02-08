/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author teras
 */
public class FileItem {

    private final File file;

    public FileItem(String file) {
        this(file, null);
    }

    public FileItem(String file, String base) {
        try {
            this.file = (base == null ? new File(file) : new File(file, base)).getCanonicalFile();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        if (!this.file.canRead())
            throw new RuntimeException("Unable to read form file " + this.file.getPath());
    }

    public Map<String, FileItem> getChildren() {
        if (!file.isDirectory())
            return null;
        HashMap<String, FileItem> children = new HashMap<String, FileItem>();
        for (File f : file.listFiles())
            children.put(f.getName(), new FileItem(f.getPath()));
        return children;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public String toString() {
        return file.getPath();
    }

    public URI toURI() {
        return file.toURI();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FileItem))
            return false;
        FileItem other = (FileItem) o;
        if (file.isDirectory() || other.file.isDirectory())
            return false;
        if (!file.getName().equals(other.file.getName()))
            return false;
        if (file.length() != other.file.length())
            return false;

        if (Configuration.current.useMD5() && (!MD5().equals(other.MD5())))
            return false;
        if (Configuration.current.useSHA1() && (!SHA1().equals(other.SHA1())))
            return false;
        if (Configuration.current.useSHA2() && (!SHA2().equals(other.SHA2())))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return file.getName().hashCode();
    }

    public String MD5() {
        return digest("md5");
    }

    public String SHA1() {
        return digest("sha1");
    }

    public String SHA2() {
        return digest("sha-256");
    }

    private String digest(String algorithm) {
        if (file.isDirectory())
            return "";
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.reset();

            byte[] buffer = new byte[1024];
            int read;
            FileInputStream fis = new FileInputStream(file);
            while ((read = fis.read(buffer)) >= 0)
                digest.update(buffer, 0, read);
            return bytesToString(digest.digest());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String bytesToString(byte[] digest) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < digest.length; i++)
            out.append(Integer.toHexString(digest[i]));
        return out.toString();
    }

    public String getRelative(FileItem relative) {
        String mine = file.getAbsolutePath();
        String base = relative.file.isDirectory()
                ? relative.file.getAbsolutePath() + "/"
                : relative.file.getParentFile().getAbsolutePath() + "/";
        System.out.println("    M=" + mine);
        System.out.println("    B=" + base);

        int pos = 0;
        int bl = base.length();
        int ml = mine.length();
        int last = 0;
        char ch = 'a';
        while (bl > pos && ml > pos && (ch = base.charAt(pos)) == mine.charAt(pos)) {
            System.out.print(ch);
            if (ch == File.separatorChar)
                last = pos;
            pos++;
        }
        System.out.println(" * " + ch);
        if (pos == 0)
            return mine;
        System.out.println(">>" + mine.substring(last + 1));

        int countslash = 0;
        pos = last + 1;
        while (bl > pos) {
            if (base.charAt(pos) == File.separatorChar)
                countslash++;
            pos++;
        }

        StringBuilder out = new StringBuilder();
        while ((countslash--) > 0)
            out.append("../");
        out.append(mine.substring(last + 1));
        return out.toString();
    }
}
