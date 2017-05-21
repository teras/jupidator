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
public interface DiffCommand {

    public void add(XMLWalker parentNode);

}
