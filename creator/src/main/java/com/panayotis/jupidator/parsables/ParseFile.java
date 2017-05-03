/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.parsables;

import com.panayotis.jupidator.digester.Digester;
import java.io.File;
import org.json.JSONObject;

/**
 *
 * @author teras
 */
public class ParseFile extends ParseItem {

    public final String md5;
    public final String sha1;
    public final String sha256;

    public ParseFile(File input) {
        this(input.getName(),
                getDigest("MD5", input).toString(),
                getDigest("SHA1", input).toString(),
                getDigest("SHA-256", input).toString());
    }

    ParseFile(JSONObject input) {
        this(input.getString("name"),
                input.getString("md5"),
                input.getString("sha1"),
                input.getString("sha256"));
    }

    public ParseFile(String name, String md5, String sha1, String sha256) {
        super(name);
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
    public JSONObject toJSON() {
        JSONObject j = super.toJSON();
        j.put("md5", md5);
        j.put("sha1", sha1);
        j.put("sha256", sha256);
        return j;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        final ParseFile other = (ParseFile) obj;
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
