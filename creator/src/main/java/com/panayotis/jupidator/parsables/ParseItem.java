/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.parsables;

import org.json.JSONObject;

/**
 *
 * @author teras
 */
public abstract class ParseItem {

    public final String name;

    public ParseItem(String name) {
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
        ParseItem other = (ParseItem) obj;
        return !((this.name == null) ? (other.name != null) : !this.name.equals(other.name));
    }

    public JSONObject toJSON() {
        JSONObject item = new JSONObject();
        item.put("name", name);
        return item;
    }

    @Override
    public String toString() {
        return name;
    }

}
