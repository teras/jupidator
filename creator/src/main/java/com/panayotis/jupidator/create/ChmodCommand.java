/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.create;

import com.panayotis.jupidator.xml.XMLWalker;

/**
 *
 * @author teras
 */
public class ChmodCommand implements Command {

    private final String file;
    private final String attr;

    public ChmodCommand(XMLWalker node) {
        this(node.attribute("file"), node.attribute("attr"));
    }

    public ChmodCommand(String file, String attr) {
        this.file = file;
        this.attr = attr;
    }

    @Override
    public void add(XMLWalker parentNode) {
        parentNode.add("chmod").setAttribute("file", file).setAttribute("attr", attr).parent();
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
        return this.file.equals(((ChmodCommand) obj).file);
    }
}
