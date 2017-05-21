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

    public DiffRm(String file) {
        this.file = file;
    }

    @Override
    public void add(XMLWalker parentNode) {
        parentNode.add("rm").setAttribute("file", file).parent();
    }

}
