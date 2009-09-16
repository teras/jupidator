/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.helpers;

import com.panayotis.jupidator.elements.security.Digester;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author teras
 */
public class DiffCreator {

    private String server_dir;
    private String arch;
    private File newer_dir;
    private File older_dir;
    private BufferedWriter out;

    public static void main(String[] args) {
        DiffCreator diff = new DiffCreator("/Users/teras/Desktop/Jubler.app", "/Users/teras/Desktop/Jubler-old.app");
        diff.setArch("macosx");
        diff.setSeverDir("4.2");
        StringWriter out = new StringWriter();
        try {
            diff.produce(out);
        } catch (IOException ex) {
        }
        System.out.print(out);
    }

    public DiffCreator(String newdirname, String olddirname) {
        this.newer_dir = new File(newdirname);
        this.older_dir = new File(olddirname);
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public void setSeverDir(String server_dir) {
        this.server_dir = server_dir;
    }

    public void produce(Writer output) throws IOException {
        if (!newer_dir.exists())
            exitOnError(newer_dir.getPath() + " does not exist!");
        if (!older_dir.exists())
            exitOnError(older_dir.getPath() + " does not exist!");

        if (out instanceof BufferedWriter)
            out = (BufferedWriter) output;
        else
            out = new BufferedWriter(output);

        out.write("<arch name=\"" + arch + "\">\n");
        parseDir(newer_dir, older_dir, "");
        out.write("</arch>\n");
        out.flush();
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
                addFileEntry(cfile, history);
        }

        for (String ofilename : oset) // new set does not have this file, only new set has it
            addRmEntry(ofilename, history);
    }

    private void checkFileDiff(File nfile, File ofile, String history) throws IOException {
        byte[] nmd5 = Digester.getMD5Sum(nfile);
        byte[] omd5 = Digester.getMD5Sum(ofile);
        if (!Arrays.equals(nmd5, omd5))
            addFileEntry(nfile.getName(), history);
    }

    private void addFileEntry(String fname, String history) throws IOException {
        out.append("  <file name=\"").append(fname);
        out.append("\" sourcedir=\"").append(server_dir);
        out.append("\" destdir=\"").append(history);
        out.append("\" size=\"").append("0");
        out.append("\" compress=\"gz\"/>\n");
    }

    private void addRmEntry(String fname, String history) throws IOException {
        out.write("  <rm file=\"" + history + "/" + fname + "\">\n");
    }

    private void exitOnError(String error) {
        System.err.println(error);
        System.exit(1);
    }
}
