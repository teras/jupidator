/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.parsables;

import com.eclipsesource.json.JsonObject;
import com.panayotis.jupidator.digester.Digester;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Objects;

/**
 * @author teras
 */
public class HashFile extends HashItem {

    public final long size;
    public final String md5;
    public final String sha1;
    public final String sha256;
    public final boolean exec;

    public HashFile(File input) {
        this(input.length(),
                input.getName(),
                getDigest("MD5", input).toString(),
                getDigest("SHA1", input).toString(),
                getDigest("SHA-256", input).toString(),
                FindPermissions.isExec(input));
    }


    HashFile(JsonObject input) {
        this(input.getLong("size", 0),
                input.getString("name", ""),
                input.getString("md5", ""),
                input.getString("sha1", ""),
                input.getString("sha256", ""),
                input.getBoolean("exec", false));
    }

    public HashFile(long size, String name, String md5, String sha1, String sha256, boolean isExec) {
        super(name);
        this.size = size;
        this.md5 = md5;
        this.sha1 = sha1;
        this.sha256 = sha256;
        this.exec = isExec;
    }

    private static Digester getDigest(String name, File input) {
        Digester digester = Digester.getDigester(name);
        digester.setHash(input);
        return digester;
    }

    @Override
    public JsonObject toJSON() {
        JsonObject j = super.toJSON();
        j.add("size", size);
        j.add("md5", md5);
        j.add("sha1", sha1);
        j.add("sha256", sha256);
        if (exec)
            j.add("exec", true);
        return j;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        final HashFile other = (HashFile) obj;
        if (this.size != other.size)
            return false;
        if (!Objects.equals(this.name, other.name))
            return false;
        if (!Objects.equals(this.md5, other.md5))
            return false;
        if (!Objects.equals(this.sha1, other.sha1))
            return false;
        if (!Objects.equals(this.sha256, other.sha256))
            return false;
        return true;
    }
}
