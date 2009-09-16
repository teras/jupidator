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
        if (args.length < 2) {
            System.err.println("Two argument required:\n  New directory\n  Old directory");
            System.exit(1);
        }

        File newer = new File(args[0]);
        File older = new File(args[1]);

        if (!newer.exists()) {
            System.out.println(newer.getPath() + " does not exist!");
            System.exit(2);
        }
        if (!older.exists()) {
            System.out.println(older.getPath() + " does not exist!");
            System.exit(2);
        }

        System.out.println("Finding changes between older " + older.getPath() + " and newer " + newer.getPath());
        parseDir(newer, older);
    }

    private static void parseDir(File newer, File older) {

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
                    checkFileDiff(nfile, ofile);
                else if (nfile.isDirectory() && ofile.isDirectory())    // Both are files, dive in
                    parseDir(nfile, ofile);
                else {  // Unsupported
                    System.out.println("Not supported change from directory to file");
                    System.exit(1);
                }

            } else    // old set does not have this file, only new set has it
                addFileEntry(cfile);
        }

        for (String ofilename : oset) // new set does not have this file, only new set has it
            addRmEntry(ofilename);
    }

    private static void checkFileDiff(File nfile, File ofile) {
        byte[] nmd5 = Digester.getMD5Sum(nfile);
        byte[] omd5 = Digester.getMD5Sum(ofile);
        if (!Arrays.equals(nmd5, omd5))
            addFileEntry(nfile.getName());
        else
            System.out.println("  "+nfile.getName());
    }

    private static void addFileEntry(String fname) {
        System.out.println("+ " + fname);
    }

    private static void addRmEntry(String fname) {
        System.out.println("- " + fname);
    }
}
