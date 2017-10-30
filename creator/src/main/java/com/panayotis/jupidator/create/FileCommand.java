/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.create;

import com.panayotis.jupidator.xml.XMLWalker;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

/**
 *
 * @author teras
 */
public class FileCommand implements Command {

    private final String compress;
    private final String destdir;
    private final String name;
    private final long local_size;
    private final long remote_size;
    private String sourcedir;
    private String local_md5;
    private String local_sha1;
    private String local_sha256;
    private String remote_md5;
    private String remote_sha1;
    private String remote_sha256;

    public FileCommand(XMLWalker w) {
        this(w.attribute("compress"), w.attribute("destdir"), w.attribute("name"),
                Long.parseLong(w.attribute("localsize")), Long.parseLong(w.attribute("remotesize")),
                w.attribute("sourcedir"));
        w.tag();
        if (w.nodeExists("local")) {
            w.toTag();
            w.node("local");
            w.execIf(q -> q.nodeExists("md5"), q -> this.local_md5 = q.node("md5").attribute("value"));
            w.execIf(q -> q.nodeExists("sha1"), q -> this.local_sha1 = q.node("sha1").attribute("value"));
            w.execIf(q -> q.nodeExists("sha2"), q -> this.local_sha256 = q.node("sha2").attribute("value"));
            w.toTag();
        }
        if (w.nodeExists("remote")) {
            w.toTag();
            w.execIf(q -> q.nodeExists("md5"), q -> this.remote_md5 = q.node("md5").attribute("value"));
            w.execIf(q -> q.nodeExists("sha1"), q -> this.remote_sha1 = q.node("sha1").attribute("value"));
            w.execIf(q -> q.nodeExists("sha2"), q -> this.remote_sha256 = q.node("sha2").attribute("value"));
            w.toTag();
        }
    }

    public FileCommand(String compress, String destdir, String name, long localsize, long remotesize, String sourcedir) {
        this.compress = compress;
        this.destdir = destdir;
        this.name = name;
        this.local_size = localsize;
        this.remote_size = remotesize;
        this.sourcedir = sourcedir;
    }

    public void setLocalMD5(String md5) {
        this.local_md5 = md5;
    }

    public void setLocalSHA1(String sha1) {
        this.local_sha1 = sha1;
    }

    public void setLocalSHA256(String sha256) {
        this.local_sha256 = sha256;
    }

    public void setRemoteMD5(String md5) {
        this.remote_md5 = md5;
    }

    public void setRemoteSHA1(String sha1) {
        this.remote_sha1 = sha1;
    }

    public void setRemoteSHA256(String sha256) {
        this.remote_sha256 = sha256;
    }

    @Override
    public void add(XMLWalker w) {
        w.add("file").
                setAttribute("name", name).
                setAttribute("destdir", destdir).
                setAttribute("sourcedir", sourcedir).
                setAttribute("localsize", Long.toString(local_size)).
                setAttribute("remotesize", Long.toString(remote_size));
        if (compress != null && !compress.isEmpty())
            w.setAttribute("compress", compress);
        if (local_md5 != null || local_sha1 != null || local_sha256 != null) {
            w.add("local");
            if (local_md5 != null)
                w.add("md5").setAttribute("value", local_md5).parent();
            if (local_sha1 != null)
                w.add("sha1").setAttribute("value", local_sha1).parent();
            if (local_sha256 != null)
                w.add("sha2").setAttribute("value", local_sha256).parent();
            w.parent();
        }
        if (remote_md5 != null || remote_sha1 != null || remote_sha256 != null) {
            w.add("remote");
            if (remote_md5 != null)
                w.add("md5").setAttribute("value", remote_md5).parent();
            if (remote_sha1 != null)
                w.add("sha1").setAttribute("value", remote_sha1).parent();
            if (remote_sha256 != null)
                w.add("sha2").setAttribute("value", remote_sha256).parent();
            w.parent();
        }
        w.parent();
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
        final FileCommand other = (FileCommand) obj;
        if (!Objects.equals(this.name, other.name))
            return false;
        if (!Objects.equals(this.destdir, other.destdir))
            return false;
        if (this.local_size != other.local_size)
            return false;
        if (this.remote_size != other.remote_size)
            return false;
        if (!Objects.equals(this.compress, other.compress))
            return false;

        if (this.local_md5 != null && other.local_md5 != null && !this.local_md5.equals(other.local_md5))
            return false;
        if (this.local_sha1 != null && other.local_sha1 != null && !this.local_sha1.equals(other.local_sha1))
            return false;
        if (this.local_sha256 != null && other.local_sha256 != null && !this.local_sha256.equals(other.local_sha256))
            return false;

        if (this.remote_md5 != null && other.remote_md5 != null && !this.remote_md5.equals(other.remote_md5))
            return false;
        if (this.remote_sha1 != null && other.remote_sha1 != null && !this.remote_sha1.equals(other.remote_sha1))
            return false;
        if (this.remote_sha256 != null && other.remote_sha256 != null && !this.remote_sha256.equals(other.remote_sha256))
            return false;

        return true;

//        sourcedir might be based on arch        
//        if (!Objects.equals(this.sourcedir, other.sourcedir))
//            return false;
    }

    void moveToAll(File files) {
        String[] split = sourcedir.split("/");
        File old = new File(files, sourcedir + "/" + name + "." + compress);
        if (split.length >= 2) {
            String nsourcedir = split[0] + "/all" + sourcedir.substring(split[0].length() + split[1].length() + 1);
            if (old.exists()) {
                File newf = new File(files, nsourcedir + "/" + name + "." + compress);
                newf.getParentFile().mkdirs();
                try {
                    Files.move(old.toPath(), newf.toPath());
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
            sourcedir = nsourcedir;
        }
    }

}
