/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author teras
 */
public class FileItem {

    private final File file;

    public FileItem(File file) {
        this.file = file;
        if (file == null)
            throw new NullPointerException("Unable to initialize FileItem with null pointer");
        if (!file.canRead())
            throw new RuntimeException("Unable to read form file " + file.getPath());
    }

    public Map<String, FileItem> getChildren() {
        if (!file.isDirectory())
            return null;
        HashMap<String, FileItem> children = new HashMap<String, FileItem>();
        for (File f : file.listFiles())
            children.put(f.getName(), new FileItem(f));
        return children;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public String toString() {
        return file.getPath();
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
}
