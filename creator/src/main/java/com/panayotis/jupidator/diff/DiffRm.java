/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.diff;

import com.panayotis.jupidator.xml.XMLWalker;

/**
 *
 * @author teras
 */
public class DiffRm implements DiffCommand {

    private final String file;

    public DiffRm(XMLWalker node) {
        this(node.attribute("file"));
    }

    public DiffRm(String file) {
        this.file = file;
    }

    @Override
    public void add(XMLWalker parentNode) {
        parentNode.add("rm").setAttribute("file", file).parent();
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return this.file.equals(((DiffRm) obj).file);
    }

}
