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

package com.panayotis.jupidator.elements;

import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.data.TextUtils;
import com.panayotis.jupidator.elements.security.PermissionManager;
import com.panayotis.jupidator.gui.BufferListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.panayotis.jupidator.i18n.I18N._;

/**
 *
 * @author teras
 */
public class FileUtils {

    public final static String JAVAHOME = TextUtils.getProperty("java.home");
    public final static String JAVABIN = getJavaExec();

    private static String getJavaExec() {
        String EXEC = TextUtils.getSystemName().contains("windows") ? "javaw.exe" : "java";
        String file;
        file = JAVAHOME + File.separator + "bin" + File.separator + EXEC;
        if (new File(file).isFile())
            return file;
        file = JAVAHOME + File.separator + "jre" + File.separator + "bin" + File.separator + EXEC;
        if (new File(file).isFile())
            return file;
        return null;
    }

    public static String copyFile(InputStream in, OutputStream out, BufferListener blisten, boolean closeStreams) {
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
            if (closeStreams) {
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
        }
        return message;
    }

    public static String copyJavaPackage(String PACKAGENAME, String FILEHOME) {
        String PACKAGEZIP = PACKAGENAME.replace('.', '/') + '/';
        String PACKAGEDIR = PACKAGEZIP.replace('/', File.separatorChar);
        File depdir = new File(FILEHOME + File.separator + PACKAGEDIR.replace("/", File.separator));
        makeDirectory(depdir);
        if ((!depdir.isDirectory()) || (!PermissionManager.manager.canWrite(depdir)))
            return _("Path {0} is not writable.", depdir.getPath());

        /* Get class paths */
        ArrayList<String> jars = new ArrayList<String>();
        ArrayList<String> dirs = new ArrayList<String>();
        getClassPaths(jars, dirs);

        for (String jar : jars) {
            ZipFile zip = null;
            try {
                zip = new ZipFile(jar);
                for (Enumeration<ZipEntry> e = (Enumeration<ZipEntry>) zip.entries(); e.hasMoreElements();) {
                    ZipEntry entry = e.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(PACKAGEZIP) && (!name.endsWith("/"))) {
                        String FILEOUT = FILEHOME + File.separator + entry.getName().replace("/", File.separator);
                        if (!FILEOUT.endsWith(File.separator)) {
                            FileOutputStream fout = null;
                            try {
                                fout = new FileOutputStream(FILEOUT);
                                String status = copyFile(zip.getInputStream(entry), fout, null, true);
                                if (status != null)
                                    return status;
                            } catch (IOException ex) {
                                return ex.getMessage();
                            } finally {
                                if (fout != null)
                                    fout.close();
                            }
                        }
                    }
                }
            } catch (IOException ex) {
            } finally {
                if (zip != null)
                    try {
                        zip.close();
                    } catch (IOException ex) {
                    }
            }
        }
        for (String path : dirs) {
            //    listener.receiveMessage(_("Checking directory {0} for classes.", path));
            File[] entries = new File(path + File.separator + PACKAGEDIR).listFiles();
            if (entries != null)
                for (int i = 0; i < entries.length; i++) {
                    String status;
                    try {
                        String FILEOUTS = depdir.getPath() + File.separator + entries[i].getName();
                        status = copyFile(new FileInputStream(entries[i]), new FileOutputStream(FILEOUTS), null, true);
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
        String CLASSPATHSYSTEM = CLASSPATH.replace("/", File.separator);

        String FILEDIR = FILEHOME + File.separator + CLASSDIR.replace("/", File.separator);
        String FILEOUT = FILEDIR + File.separator + CLASSFILE;

        /* Create Java path */
        File depdir = new File(FILEDIR);
        makeDirectory(depdir);
        if ((!depdir.isDirectory()) || (!PermissionManager.manager.canWrite(depdir)))
            return _("Deployer path {0} is not writable.", depdir.getPath());

        ArrayList<String> jars = new ArrayList<String>();
        ArrayList<String> dirs = new ArrayList<String>();
        getClassPaths(jars, dirs);

        for (String jar : jars) {
            listener.receiveMessage(_("Checking JAR {0} for Deployer class.", jar));
            try {
                ZipFile zip = new ZipFile(jar);
                ZipEntry entry = zip.getEntry(CLASSPATH);
                if (entry != null && copyFile(zip.getInputStream(entry), new FileOutputStream(FILEOUT), null, true) == null) {
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
                if (copyFile(new FileInputStream(path), new FileOutputStream(FILEOUT), null, true) == null) {
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
        ArrayList<String> jars = new ArrayList<String>();
        getClassPaths(jars, null);
        String jarjar = JarName + ".jar";
        String jarexe = JarName + ".exe";
        for (String jar : jars)
            if (jar.endsWith(jarjar) || jar.endsWith(jarexe))
                return jar;
        return null;
    }

    private static void getClassPaths(ArrayList<String> jarpaths, ArrayList<String> dirpaths) {
        /* Create initial classpath list - will be expanded in classpath inside manifest of JAR files */
        ArrayList<String> classpaths = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(TextUtils.getProperty("java.class.path"), File.pathSeparator);
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
                if (path.length() > 0 && path.endsWith(File.separator))
                    path = path + File.separator;
                if (dirpaths != null)
                    dirpaths.add(path);
            }
        }
    }

    private static void getClassPathFromManifest(ZipFile zip, ArrayList<String> classpaths, String parent) {
        if (parent == null)
            parent = "";
        else
            parent = parent + File.separator;

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
                        StringTokenizer tok = new StringTokenizer(line.substring(11).replace("/", File.separator));
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

    public static boolean makeDirectory(File dirname) {
        if (dirname.exists())
            return dirname.isDirectory();
        return dirname.mkdirs();
    }

    public static String rmTree(File req) {
        if (!req.exists() || req == null)
            return null;
        if (req.isDirectory()) {
            File[] list = req.listFiles();
            if (list != null)
                for (File file : list) {
                    String res = rmTree(file);
                    if (res != null)
                        return res;
                }
        }
        if (req.delete())
            return null;
        return _("Unable to delete file {0}", req.getPath());
    }

    public static boolean setExecute(String path) {
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"chmod", "a+x", path});
            proc.waitFor();
            return true;
        } catch (InterruptedException ex) {
        } catch (IOException ex) {
        }
        return false;
    }
}
