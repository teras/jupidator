/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.helpers;

import com.panayotis.jupidator.elements.security.Digester;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author teras
 */
public class DiffCreator {

    public static void main(String[] args) {
        if (args.length < 2)
            exitOnError("Two argument required:\n  New directory\n  Old directory");

        File newer = new File(args[0]);
        File older = new File(args[1]);

        if (!newer.exists())
            exitOnError(newer.getPath() + " does not exist!");
        if (!older.exists())
            exitOnError(older.getPath() + " does not exist!");

        parseDir(newer, older, "");
    }

    private static void parseDir(File newer, File older, String history) {

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

    private static void checkFileDiff(File nfile, File ofile, String history) {
        byte[] nmd5 = Digester.getMD5Sum(nfile);
        byte[] omd5 = Digester.getMD5Sum(ofile);
        if (!Arrays.equals(nmd5, omd5))
            addFileEntry(nfile.getName(), history);
        else
            System.out.println("  " + history + "/" + nfile.getName());
    }

    private static void addFileEntry(String fname, String history) {
        System.out.println("+ " + history + "/" + fname);
    }

    private static void addRmEntry(String fname, String history) {
        System.out.println("- " + history + "/" + fname);
    }

    public static void exitOnError(String error) {
        System.err.println(error);
        System.exit(1);
    }
}
