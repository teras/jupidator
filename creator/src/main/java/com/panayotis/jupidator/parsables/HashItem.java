/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.parsables;

import com.eclipsesource.json.JsonObject;

import java.util.Objects;

/**
 * @author teras
 */
public abstract class HashItem implements Comparable<HashItem> {

    public final String name;

    public HashItem(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return (this.name != null ? this.name.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HashItem other = (HashItem) obj;
        return Objects.equals(this.name, other.name);
    }

    public JsonObject toJSON() {
        JsonObject item = new JsonObject();
        item.add("name", name);
        return item;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(HashItem o) {
        return this.name.compareTo(o.name);
    }
}
