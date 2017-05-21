/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.diff;

import com.panayotis.jupidator.xml.XMLWalker;
import java.util.Objects;

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

    public DiffFile(XMLWalker w) {
        this(w.attribute("compress"), w.attribute("destdir"), w.attribute("name"), Long.parseLong(w.attribute("size")), w.attribute("sourcedir"));
        w.execIf(q -> q.nodeExists("md5"), q -> this.md5 = q.node("md5").attribute("value"));
        w.execIf(q -> q.nodeExists("sha1"), q -> this.sha1 = q.node("sha1").attribute("value"));
        w.execIf(q -> q.nodeExists("sha2"), q -> this.sha256 = q.node("sha2").attribute("value"));
    }

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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.destdir);
        hash = 59 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DiffFile other = (DiffFile) obj;
        if (!Objects.equals(this.name, other.name))
            return false;
        if (!Objects.equals(this.destdir, other.destdir))
            return false;
        if (this.size != other.size)
            return false;
        if (!Objects.equals(this.compress, other.compress))
            return false;

        if (this.md5 != null && other.md5 != null && !this.md5.equals(other.md5))
            return false;
        if (this.sha1 != null && other.sha1 != null && !this.sha1.equals(other.sha1))
            return false;
        if (this.sha256 != null && other.sha256 != null && !this.sha256.equals(other.sha256))
            return false;
        return true;

//        sourcedir might be based on arch        
//        if (!Objects.equals(this.sourcedir, other.sourcedir))
//            return false;
    }

}
