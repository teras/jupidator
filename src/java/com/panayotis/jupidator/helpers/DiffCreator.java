/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.helpers;

import com.panayotis.jupidator.elements.security.Digester;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author teras
 */
public class DiffCreator {

    private File newer_dir;
    private File older_dir;
    private String arch;
    private String server_dir;
    private String local_dir_name;
    //
    private File local_dir;
    private BufferedWriter xmlout;

    public static void main(String[] args) {
        DiffCreator diff = new DiffCreator("/Users/teras/Desktop/Jubler.app", "/Users/teras/Desktop/Jubler-old.app");
        diff.setSeverDir("4.2");
        StringWriter out = new StringWriter();
        try {
            diff.produce();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public DiffCreator(String newdirname, String olddirname) {
        this.newer_dir = new File(newdirname);
        this.older_dir = new File(olddirname);
        this.arch = System.getProperty("os.name").toLowerCase().replace(" ", "");
        this.server_dir = "update";
        this.local_dir_name = this.server_dir;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public void setSeverDir(String server_dir) {
        this.server_dir = server_dir;
    }

    public void setLocalDir(String local_dir) {
        this.local_dir_name = local_dir;
    }

    public void produce() throws IOException {
        if (!newer_dir.exists())
            exitOnError(newer_dir.getPath() + " does not exist!");
        if (!older_dir.exists())
            exitOnError(older_dir.getPath() + " does not exist!");

        local_dir = new File(local_dir_name);
        local_dir.mkdirs();
        if ((!local_dir.exists()) || (!local_dir.isDirectory()))
            exitOnError("Local_directory is not a directory");

        xmlout = new BufferedWriter(new FileWriter(new File(local_dir, "update.xml")));

        xmlout.write("<arch name=\"" + arch + "\">\n");
        parseDir(newer_dir, older_dir, "");
        xmlout.write("</arch>\n");
        debug("Closing XML file");
        xmlout.close();
    }

    private void parseDir(File newer, File older, String history) throws IOException {

        File[] narray = newer.listFiles();
        File[] oarray = older.listFiles();

        HashSet<String> oset = new HashSet<String>();
        for (int i = 0; i < oarray.length; i++)
            oset.add(oarray[i].getName());

        String cfile;
        File nfile;
        File ofile;
        for (int i = 0; i < narray.length; i++) {   // First chech existing files
            cfile = narray[i].getName();
            if (oset.contains(cfile)) { // both sets have the same filename
                oset.remove(cfile); // remove from list since it exists in both sets

                nfile = new File(newer, cfile);
                ofile = new File(older, cfile);

                if (nfile.isFile() && ofile.isFile())   // Bot are files, thus can be checked
                    checkFileDiff(nfile, ofile, history);
                else if (nfile.isDirectory() && ofile.isDirectory()) {   // Both are files, dive in
                    if (!history.equals(""))
                        history += "/";
                    history += cfile;
                    parseDir(nfile, ofile, history);
                } else  // Unsupported
                    exitOnError("Not supported change from directory to file");

            } else    // old set does not have this file, only new set has it
                addFileEntry(narray[i], history);
        }

        for (String ofilename : oset) // new set does not have this file, only new set has it
            addRmEntry(ofilename, history);
    }

    private void checkFileDiff(File nfile, File ofile, String history) throws IOException {
        byte[] nmd5 = Digester.getMD5Sum(nfile);
        byte[] omd5 = Digester.getMD5Sum(ofile);
        if (!Arrays.equals(nmd5, omd5))
            addFileEntry(nfile, history);
    }

    private void addFileEntry(File file, String history) throws IOException {
        File gz = new File(local_dir, file.getName() + ".gz");
        compressFile(file, gz);
        xmlout.append("  <file name=\"").append(file.getName());
        xmlout.append("\" sourcedir=\"").append(server_dir);
        xmlout.append("\" destdir=\"").append(history);
        xmlout.append("\" size=\"").append(String.valueOf(gz.length()));
        xmlout.append("\" compress=\"gz\"/>\n");
    }

    private void addRmEntry(String fname, String history) throws IOException {
        xmlout.write("  <rm file=\"" + history + "/" + fname + "\">\n");
    }

    private void exitOnError(String error) {
        System.err.println(error);
        System.exit(1);
    }

    private void compressFile(File source, File dest) throws IOException {
        if (source.equals(dest))
            throw new IOException("Source and destination file should not be the same");
        if (dest.exists())
            throw new IOException("File "+dest.getName()+" already exists!");
        
        debug("Compressing " + source.getPath() + " to " + dest.getPath());
        BufferedInputStream i = new BufferedInputStream(new FileInputStream(source));
        BufferedOutputStream o = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(dest)));

        byte[] buffer = new byte[1024];
        int hm;
        while ((hm = i.read(buffer)) > 0)
            o.write(buffer, 0, hm);
        i.close();
        o.close();
    }

    private static void debug(String info) {
        System.out.println(info);
    }
}
