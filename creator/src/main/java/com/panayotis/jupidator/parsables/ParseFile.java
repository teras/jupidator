/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.parsables;

import com.eclipsesource.json.JsonObject;
import com.panayotis.jupidator.digester.Digester;
import java.io.File;

/**
 *
 * @author teras
 */
public class ParseFile extends ParseItem {

    public final long size;
    public final String md5;
    public final String sha1;
    public final String sha256;

    public ParseFile(File input) {
        this(input.length(),
                input.getName(),
                getDigest("MD5", input).toString(),
                getDigest("SHA1", input).toString(),
                getDigest("SHA-256", input).toString());
    }

    ParseFile(JsonObject input) {
        this(input.getLong("size", 0),
                input.getString("name", ""),
                input.getString("md5", ""),
                input.getString("sha1", ""),
                input.getString("sha256", ""));
    }

    public ParseFile(long size, String name, String md5, String sha1, String sha256) {
        super(name);
        this.size = size;
        this.md5 = md5;
        this.sha1 = sha1;
        this.sha256 = sha256;
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
        return j;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        final ParseFile other = (ParseFile) obj;
        if (this.size != other.size)
            return false;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        if ((this.md5 == null) ? (other.md5 != null) : !this.md5.equals(other.md5))
            return false;
        if ((this.sha1 == null) ? (other.sha1 != null) : !this.sha1.equals(other.sha1))
            return false;
        if ((this.sha256 == null) ? (other.sha256 != null) : !this.sha256.equals(other.sha256))
            return false;
        return true;
    }

}
