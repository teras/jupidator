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
public class RmCommand implements Command {

    private final String file;

    public RmCommand(XMLWalker node) {
        this(node.attribute("file"));
    }

    public RmCommand(String file) {
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
        return this.file.equals(((RmCommand) obj).file);
    }

}
