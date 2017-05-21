/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.parsables;

import com.panayotis.jupidator.JupidatorCreatorException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author teras
 */
public class ParseFolder extends ParseItem {

    private final Collection<ParseItem> items = new TreeSet<ParseItem>((o1, o2) -> o1.name.compareTo(o2.name)) {
        @Override
        public boolean containsAll(Collection<?> col) {
            Collection<ParseItem> c = (Collection<ParseItem>) col;
            for (ParseItem otheritem : c) {
                boolean found = false;
                for (ParseItem selfitem : this)
                    if (selfitem.equals(otheritem)) {
                        found = true;
                        break;
                    }
                if (!found)
                    return false;
            }
            return true;
        }
    };

    public ParseFolder(File in) {
        this(".", in);
    }

    private ParseFolder(String name, File in) {
        super(name);
        if (!in.exists())
            throw new JupidatorCreatorException("Input file '" + in.getPath() + "' should exist");
        File[] children = in.listFiles();
        if (children != null && children.length > 0)
            for (File child : children)
                if (child.isFile())
                    items.add(new ParseFile(child));
                else if (child.isDirectory())
                    items.add(new ParseFolder(child.getName(), child));
    }

    public ParseFolder(JSONObject dir) {
        this(dir.getString("name"), dir.getJSONArray("children"));
    }

    private ParseFolder(String name, JSONArray dir) {
        super(name);
        for (int i = 0; i < dir.length(); i++) {
            JSONObject input = dir.getJSONObject(i);
            JSONArray children = (JSONArray) input.opt("children");
            items.add(children == null ? new ParseFile(input) : new ParseFolder(input.getString("name"), children));
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject j = super.toJSON();
        JSONArray children = new JSONArray();
        j.put("children", children);
        for (ParseItem item : items)
            children.put(item.toJSON());
        return j;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        final ParseFolder other = (ParseFolder) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        if (this.items != other.items && (this.items == null || !this.items.equals(other.items)))
            return false;
        return true;
    }

    public Collection<String> names() {
        Collection<String> names = new TreeSet<>();
        for (ParseItem item : items)
            names.add(item.name);
        return names;
    }

    public ParseItem searchFor(String name) {
        for (ParseItem item : items)
            if (item.name.equals(name))
                return item;
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + items.toString();
    }

}
