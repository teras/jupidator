/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.parsables;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.panayotis.jupidator.JupidatorCreatorException;

import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.TreeSet;

/**
 * @author teras
 */
public class HashFolder extends HashItem {

    private final Collection<HashItem> items = new TreeSet<HashItem>() {
        @Override
        public boolean containsAll(Collection<?> col) {
            if (col == null)
                return false;
            //noinspection unchecked
            Collection<HashItem> c = (Collection<HashItem>) col;
            for (HashItem other : c) {
                boolean found = false;
                for (HashItem self : this)
                    if (self.equals(other)) {
                        found = true;
                        break;
                    }
                if (!found)
                    return false;
            }
            return true;
        }
    };

    public HashFolder(File in) {
        this(".", in);
    }

    private HashFolder(String name, File in) {
        super(name);
        if (!in.exists())
            throw new JupidatorCreatorException("Input file '" + in.getPath() + "' should exist");
        File[] children = in.listFiles();
        if (children != null && children.length > 0)
            for (File child : children)
                if (child.isFile())
                    items.add(new HashFile(child));
                else if (child.isDirectory())
                    items.add(new HashFolder(child.getName(), child));
    }

    public HashFolder(JsonObject dir) {
        this(dir.getString("name", ""), dir.get("children").asArray());
    }

    private HashFolder(String name, JsonArray dir) {
        super(name);
        for (int i = 0; i < dir.size(); i++) {
            JsonObject input = dir.get(i).asObject();
            JsonArray children = input.get("children") instanceof JsonArray ? input.get("children").asArray() : null;
            items.add(children == null ? new HashFile(input) : new HashFolder(input.getString("name", ""), children));
        }
    }

    @Override
    public JsonObject toJSON() {
        JsonObject j = super.toJSON();
        JsonArray children = new JsonArray();
        j.add("children", children);
        for (HashItem item : items)
            children.add(item.toJSON());
        return j;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        final HashFolder other = (HashFolder) obj;
        if (!Objects.equals(this.name, other.name))
            return false;
        //noinspection RedundantIfStatement
        if (this.items != other.items && !this.items.equals(other.items))
            return false;
        return true;
    }

    public Collection<String> names() {
        Collection<String> names = new TreeSet<>();
        for (HashItem item : items)
            names.add(item.name);
        return names;
    }

    public HashItem searchFor(String name) {
        for (HashItem item : items)
            if (item.name.equals(name))
                return item;
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + items.toString();
    }

}
