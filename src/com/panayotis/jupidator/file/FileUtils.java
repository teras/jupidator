/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.file;

import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.gui.BufferListener;
import java.io.BufferedReader;
import static com.panayotis.jupidator.i18n.I18N._;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author teras
 */
public class FileUtils {

    public final static char FS = System.getProperty("file.separator").charAt(0);
    public final static String JAVAHOME = System.getProperty("java.home");
    public final static String JAVABIN = getJavaExec();

    private static String getJavaExec() {
        String EXEC = System.getProperty("os.name").toLowerCase().contains("windows") ? "java.exe" : "java";
        String file;
        file = JAVAHOME + FS + "bin" + FS + EXEC;
        if (new File(file).isFile())
            return file;
        file = JAVAHOME + FS + "jre" + FS + "bin" + FS + EXEC;
        if (new File(file).isFile())
            return file;
        return null;
    }

    public static String copyFile(InputStream in, OutputStream out, BufferListener blisten) {
        String message = null;
        byte[] buffer = new byte[1024];
        int count;

        try {
            while ((count = in.read(buffer)) != -1) {
                if (Thread.interrupted()) {
                    throw new IOException("User asked to cancel update");
                }
                out.write(buffer, 0, count);
                if (blisten != null)
                    blisten.addBytes(count);
            }
        } catch (IOException ex) {
            message = ex.getMessage();
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException ex) {
                if (message != null)
                    message = ex.getMessage();
            }
            try {
                if (out != null)
                    out.close();
            } catch (IOException ex) {
                if (message != null)
                    message = ex.getMessage();
            }
        }
        return message;
    }

    public static String copyClass(String CLASSNAME, String FILEHOME, UpdatedApplication listener) {
        String CLASS = CLASSNAME.substring(CLASSNAME.lastIndexOf('.') + 1);
        String CLASSDIR = CLASSNAME.substring(0, CLASSNAME.length() - CLASS.length() - 1).replace('.', '/');

        String CLASSFILE = CLASS + ".class";
        String CLASSPATH = CLASSDIR + "/" + CLASSFILE;
        String CLASSPATHSYSTEM = CLASSPATH.replace('/', FS);

        String FILEDIR = FILEHOME + FS + CLASSDIR.replace('/', FS);
        String FILEOUT = FILEDIR + FS + CLASSFILE;

        /* Create initial classpath list - will be expanded in classpath inside manifest of JAR files */
        Vector<String> classpaths = new Vector<String>();
        StringTokenizer tok = new StringTokenizer(System.getProperty("java.class.path"),
                System.getProperty("path.separator"));
        while (tok.hasMoreElements())
            classpaths.add(tok.nextToken());

        /* Create Java path */
        File dir = new File(FILEDIR);
        dir.mkdirs();
        if ((!dir.isDirectory()) || (!dir.canWrite()))
            return _("Deployer path {0} is not writable.", dir.getPath());

        /* Find JAR/EXE with the desired .class file */
        String path;
        while (classpaths.size() > 0) {
            path = classpaths.get(0);
            classpaths.remove(0);
            listener.receiveMessage(_("Checking path {0} for Deployer class.", path));
            if (path.length() > 4 && (path.toLowerCase().endsWith(".jar") || path.toLowerCase().endsWith(".exe"))) {
                try {
                    ZipFile zip = new ZipFile(path);
                    ZipEntry entry = zip.getEntry(CLASSPATH);
                    if (entry != null && copyFile(zip.getInputStream(entry), new FileOutputStream(FILEOUT), null) == null)
                        return null;
                    /* make sure that in this zip entry there is no classpath definition */
                    getClassPathFromManifest(zip, classpaths, new File(path).getParent());
                } catch (IOException ex) {
                }
            } else {
                if (path.length() > 0 && path.charAt(path.length() - 1) != FS)
                    path = path + FS;
                path = path + CLASSPATHSYSTEM;
                try {
                    if (copyFile(new FileInputStream(path), new FileOutputStream(FILEOUT), null) == null) {
                        listener.receiveMessage(_("Deployer stored in {0}", FILEOUT));
                        return null;
                    }
                } catch (FileNotFoundException ex) {
                }
            }
        }
        return _("Unable to create Deployer");
    }

    private static void getClassPathFromManifest(ZipFile zip, Vector<String> classpaths, String parent) {
        if (parent==null)
            parent = "";
        else
            parent = parent + FS;

        ZipEntry manifest = zip.getEntry("META-INF/MANIFEST.MF");
        if (manifest != null) {
            BufferedReader cpin = null;
            try {
                String line;
                cpin = new BufferedReader(new InputStreamReader(zip.getInputStream(manifest)));
                while ((line = cpin.readLine()) != null) {
                    if (line.toLowerCase().startsWith("class-path:")) {
                        String nextline;
                        while ((nextline = cpin.readLine()) != null && nextline.startsWith(" "))
                            line = line + nextline.substring(1);
                        StringTokenizer tok = new StringTokenizer(line.substring(11).replace('/', FS));
                        while (tok.hasMoreElements())
                            classpaths.add(parent + tok.nextToken());
                        return;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    cpin.close();
                } catch (IOException ex) {
                }
            }
        }

    }

    static boolean isWritable(File f) {
        if (f == null)
            throw new NullPointerException(_("Updated file could not be null."));
        if (!isParentWritable(f))
            return false;
        if (!f.exists())
            return true;
        return isWritableLoop(f);
    }

    private static boolean isWritableLoop(File f) {
        if (f.isDirectory()) {
            File dir[] = f.listFiles();
            for (int i = 0; i < dir.length; i++) {
                if (!isWritable(dir[i]))
                    return false;
            }
            return true;
        } else {
            return f.canWrite();
        }
    }

    private static boolean isParentWritable(File f) {
        File p = f.getParentFile();
        if (p == null)  // No parent file - can't work on root files
            return false;
        if (f.exists()) {
            /* we are sure that a parent exists for this file */
            return p.canWrite();
        } else {
            if (p.exists()) {
                /* Check if parent file is directory AND can write in it */
                if (p.isDirectory() && p.canWrite())
                    return true;
                return false;
            } else {
                /* directories created (?) */
                return p.mkdirs();
            }
        }
    }
}
