/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import com.panayotis.jupidator.changes.DataModel;
import com.panayotis.jupidator.changes.ChangeList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.Icon;
import javax.swing.JFileChooser;

/**
 *
 * @author teras
 */
public class FileItem {

    private static final JFileChooser ICONFACTORY = new JFileChooser();
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
        if (!isDirectory())
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

    public boolean equals(FileItem other, boolean useZip) {
        if (isDirectory() || other.isDirectory())
            return false;
        if (!file.getName().equals(other.file.getName()))
            return false;
        if (file.length() != other.file.length())
            return false;

        if (useZip && isZip() && other.isZip())
            return compareZip(other);
        if (DataModel.current.useMD5() && (!MD5().equals(other.MD5())))
            return false;
        if (DataModel.current.useSHA1() && (!SHA1().equals(other.SHA1())))
            return false;
        if (DataModel.current.useSHA2() && (!SHA2().equals(other.SHA2())))
            return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FileItem))
            return false;
        return equals((FileItem) o, DataModel.current.useZip());
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

    private boolean compareZip(FileItem other) {
        try {
            File out1 = File.createTempFile("creator", null);
            File out2 = File.createTempFile("creator", null);
            out1.delete();
            out2.delete();
            unzip(out1);
            other.unzip(out2);
            ChangeList list = new ChangeList(out1.getAbsolutePath(), out2.getAbsolutePath(), DataModel.current.useZipRecursively());
            rmTree(out1);
            rmTree(out2);
            return list.getSize() == 0;
        } catch (IOException ex) {
            return false;
        }
    }

    public String getRelativePath(FileItem relative) {
        String mine = file.getAbsolutePath();
        String base = relative.isDirectory()
                ? relative.file.getAbsolutePath() + "/"
                : relative.file.getParentFile().getAbsolutePath() + "/";
        int pos = 0;
        int bl = base.length();
        int ml = mine.length();
        int last = 0;
        char ch = 0;
        while (bl > pos && ml > pos && (ch = base.charAt(pos)) == mine.charAt(pos)) {
            if (ch == File.separatorChar)
                last = pos;
            pos++;
        }
        if (pos == 0)
            return mine;
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

    public String digest(String algorithm) {
        if (isDirectory())
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

    private static String bytesToString(byte[] digest) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < digest.length; i++)
            out.append(Integer.toHexString(digest[i]));
        return out.toString();
    }

    private void unzip(File dirout) {
        ZipFile zfile = null;
        try {
            zfile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    File fout = new File(dirout, entry.getName());
                    fout.getParentFile().mkdirs();
                    copyInputStream(zfile.getInputStream(entry), new FileOutputStream(fout));
                }
            }

        } catch (IOException ex) {
        } finally {
            if (zfile != null)
                try {
                    zfile.close();
                } catch (IOException ex) {
                }
        }
    }

    private static void copyInputStream(InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) >= 0)
                out.write(buffer, 0, len);
        } catch (IOException ex) {
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
            }
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }

    private boolean isZip() {
        ZipFile zfile = null;
        try {
            zfile = new ZipFile(file);
            return true;
        } catch (IOException ex) {
            return false;
        } finally {
            if (zfile != null)
                try {
                    zfile.close();
                } catch (IOException ex) {
                }
        }
    }

    private static String rmTree(File req) {
        if (!req.exists() || req == null)
            return null;
        if (req.isDirectory())
            for (File file : req.listFiles()) {
                String res = rmTree(file);
                if (res != null)
                    return res;
            }
        if (req.delete())
            return null;
        return "Unable to delete file " + req.getPath();
    }

    public Icon getIcon() {
        return ICONFACTORY.getIcon(file);
    }
}
