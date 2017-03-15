/*
 *
 * This file is part of Jupidator.
 *
 * Jupidator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 *
 * Jupidator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jupidator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package com.panayotis.jupidator.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author teras
 */
public class SimpleArgParser {

    private final Map<String, String> values = new HashMap<String, String>();
    private final List<String> unnamedvalues = new ArrayList<String>();

    public SimpleArgParser(String[] args) {
        String last_key = null;
        for (String arg : args)
            if (arg.startsWith("--")) {
                arg = arg.substring(2);
                if (last_key != null)
                    put(last_key, null);
                last_key = arg;
            } else if (last_key == null)
                unnamedvalues.add(arg);
            else {
                put(last_key, arg);
                last_key = null;
            }
    }

    private void put(String key, String value) {
        if (value == null) {
            if (!values.containsKey(key))
                values.put(key, null);
        } else
            values.put(key, value);
    }

    public List<String> getUnnamed() {
        return unnamedvalues;
    }

    public void nameArguments(String... names) {
        for (int i = 0; i < names.length && !unnamedvalues.isEmpty(); i++)
            put(names[i], unnamedvalues.remove(0));
    }

    public int size() {
        return values.size() + unnamedvalues.size();
    }

    public String get(String name) {
        return get(name, null);
    }

    public String get(String name, String deflt) {
        String res = values.get(name);
        return res == null || res.isEmpty() ? deflt : res;
    }

    public boolean has(String name) {
        return values.containsKey(name);
    }

}
