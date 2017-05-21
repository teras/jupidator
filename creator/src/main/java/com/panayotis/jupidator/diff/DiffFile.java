/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.diff;

import com.panayotis.jupidator.xml.XMLWalker;

/**
 *
 * @author teras
 */
public class DiffFile implements DiffCommand {

    private final String compress;
    private final String destdir;
    private final String name;
    private final long size;
    private final String sourcedir;
    private String md5;
    private String sha1;
    private String sha256;

    public DiffFile(String compress, String destdir, String name, long size, String sourcedir) {
        this.compress = compress;
        this.destdir = destdir;
        this.name = name;
        this.size = size;
        this.sourcedir = sourcedir;
    }

    public void setMD5(String md5) {
        this.md5 = md5;
    }

    public void setSHA1(String sha1) {
        this.sha1 = sha1;
    }

    public void setSHA256(String sha256) {
        this.sha256 = sha256;
    }

    @Override
    public void add(XMLWalker parentNode) {
        parentNode.add("file").
                setAttribute("name", name).
                setAttribute("destdir", destdir).
                setAttribute("sourcedir", sourcedir).
                setAttribute("compress", compress).
                setAttribute("size", Long.toString(size));
        if (md5 != null)
            parentNode.add("md5").setAttribute("value", md5).parent();
        if (sha1 != null)
            parentNode.add("sha1").setAttribute("value", sha1).parent();
        if (sha256 != null)
            parentNode.add("sha2").setAttribute("value", sha256).parent();
        parentNode.parent();
    }

}
