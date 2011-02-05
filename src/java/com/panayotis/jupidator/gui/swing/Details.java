/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.gui.swing;

import java.awt.Dimension;
import javax.swing.JScrollPane;

/**
 *
 * @author teras
 */
public class Details extends JScrollPane {

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.width = 400;
        if (d.height < 120)
            d.height = 120;
        if (d.height > 240)
            d.height = 240;
        System.out.println(d);
        return d;
    }
}
