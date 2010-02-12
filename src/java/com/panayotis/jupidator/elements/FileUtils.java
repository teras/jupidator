/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

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
import java.util.Enumeration;
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
                if (Thread.interrupted())
                    throw new IOException("User asked to cancel update");
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

    public static String copyPackage(String PACKAGENAME, String FILEHOME, UpdatedApplication listener) {
        String PACKAGEDIR = PACKAGENAME.replace('.', '/') + FS;
        File depdir = new File(FILEHOME + FS + PACKAGEDIR.replace('/', FS));
        depdir.mkdirs();
        if ((!depdir.isDirectory()) || (!depdir.canWrite()))
            return _("Path {0} is not writable.", depdir.getPath());

        /* Get class paths */
        Vector<String> jars = new Vector<String>();
        Vector<String> dirs = new Vector<String>();
        getClassPaths(jars, dirs);

        for (String jar : jars) {
            listener.receiveMessage(_("Checking JAR {0} for classes.", jar));
            try {
                ZipFile zip = new ZipFile(jar);
                for (Enumeration<ZipEntry> e = (Enumeration<ZipEntry>) zip.entries(); e.hasMoreElements();) {
                    ZipEntry entry = e.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(PACKAGEDIR) && (!name.endsWith("/"))) {
                        String FILEOUT = FILEHOME + FS + entry.getName().replace('/', FS);
                        String status = copyFile(zip.getInputStream(entry), new FileOutputStream(FILEOUT), null);
                        if (status != null)
                            return status;
                    }
                }
            } catch (IOException ex) {
                return _("Unable to extract files from JAR {0}", jar);
            }
        }
        for (String path : dirs) {
            listener.receiveMessage(_("Checking directory {0} for classes.", path));
            File[] entries = new File(path + FS + PACKAGEDIR).listFiles();
            for (int i = 0; i < entries.length; i++) {
                String status;
                try {
                    String FILEOUTS = depdir.getPath() + FS + entries[i].getName();
                    status = copyFile(new FileInputStream(entries[i]), new FileOutputStream(FILEOUTS), null);
                    if (status != null)
                        return status;
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    public static String copyClass(String CLASSNAME, String FILEHOME, UpdatedApplication listener) {
        String CLASS = CLASSNAME.substring(CLASSNAME.lastIndexOf('.') + 1);
        String CLASSDIR = CLASSNAME.substring(0, CLASSNAME.length() - CLASS.length() - 1).replace('.', '/');

        String CLASSFILE = CLASS + ".class";
        String CLASSPATH = CLASSDIR + "/" + CLASSFILE;
        String CLASSPATHSYSTEM = CLASSPATH.replace('/', FS);

        String FILEDIR = FILEHOME + FS + CLASSDIR.replace('/', FS);
        String FILEOUT = FILEDIR + FS + CLASSFILE;

        /* Create Java path */
        File depdir = new File(FILEDIR);
        depdir.mkdirs();
        if ((!depdir.isDirectory()) || (!depdir.canWrite()))
            return _("Deployer path {0} is not writable.", depdir.getPath());

        Vector<String> jars = new Vector<String>();
        Vector<String> dirs = new Vector<String>();
        getClassPaths(jars, dirs);

        for (String jar : jars) {
            listener.receiveMessage(_("Checking JAR {0} for Deployer class.", jar));
            try {
                ZipFile zip = new ZipFile(jar);
                ZipEntry entry = zip.getEntry(CLASSPATH);
                if (entry != null && copyFile(zip.getInputStream(entry), new FileOutputStream(FILEOUT), null) == null) {
                    listener.receiveMessage(_("Deployer stored in {0}", FILEOUT));
                    return null;
                }
            } catch (IOException ex) {
            }
        }
        for (String path : dirs) {
            listener.receiveMessage(_("Checking Directory {0} for Deployer class.", path));
            path = path + CLASSPATHSYSTEM;
            try {
                if (copyFile(new FileInputStream(path), new FileOutputStream(FILEOUT), null) == null) {
                    listener.receiveMessage(_("Deployer stored in {0}", FILEOUT));
                    return null;
                }
            } catch (FileNotFoundException ex) {
            }
        }
        return _("Unable to create Deployer");
    }

    public static String getJupidatorHome() {
        String JUPIDATORHOME = ".";
        String jupidatorpath = getJarPath("jupidator");
        if (jupidatorpath != null)
            JUPIDATORHOME = new File(jupidatorpath).getAbsoluteFile().getParent();
        return JUPIDATORHOME;
    }

    private static String getJarPath(String JarName) {
        Vector<String> jars = new Vector<String>();
        getClassPaths(jars, null);
        String jarjar = JarName + ".jar";
        String jarexe = JarName + ".exe";
        for (String jar : jars)
            if (jar.endsWith(jarjar) || jar.endsWith(jarexe))
                return jar;
        return null;
    }

    private static void getClassPaths(Vector<String> jarpaths, Vector<String> dirpaths) {
        /* Create initial classpath list - will be expanded in classpath inside manifest of JAR files */
        Vector<String> classpaths = new Vector<String>();
        StringTokenizer tok = new StringTokenizer(System.getProperty("java.class.path"),
                System.getProperty("path.separator"));
        while (tok.hasMoreElements())
            classpaths.add(tok.nextToken());

        String path;
        while (classpaths.size() > 0) {
            path = classpaths.get(0);
            classpaths.remove(0);
            if (path.length() > 4 && (path.toLowerCase().endsWith(".jar") || path.toLowerCase().endsWith(".exe"))) {
                if (jarpaths != null)
                    jarpaths.add(path);
                try {
                    /* make sure that in this zip entry there is no classpath definition */
                    getClassPathFromManifest(new ZipFile(path), classpaths, new File(path).getParent());
                } catch (IOException ex) {
                }
            } else {
                if (path.length() > 0 && path.charAt(path.length() - 1) != FS)
                    path = path + FS;
                if (dirpaths != null)
                    dirpaths.add(path);
            }
        }
    }

    private static void getClassPathFromManifest(ZipFile zip, Vector<String> classpaths, String parent) {
        if (parent == null)
            parent = "";
        else
            parent = parent + FS;

        ZipEntry manifest = zip.getEntry("META-INF/MANIFEST.MF");
        if (manifest != null) {
            BufferedReader cpin = null;
            try {
                String line;
                cpin = new BufferedReader(new InputStreamReader(zip.getInputStream(manifest)));
                while ((line = cpin.readLine()) != null)
                    if (line.toLowerCase().startsWith("class-path:")) {
                        String nextline;
                        while ((nextline = cpin.readLine()) != null && nextline.startsWith(" "))
                            line = line + nextline.substring(1);
                        StringTokenizer tok = new StringTokenizer(line.substring(11).replace('/', FS));
                        while (tok.hasMoreElements())
                            classpaths.add(parent + tok.nextToken());
                        return;
                    }
            } catch (IOException ex) {
            } finally {
                try {
                    cpin.close();
                } catch (IOException ex) {
                }
            }
        }

    }
}
