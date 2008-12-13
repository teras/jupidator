/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.panayotis.jupidator.data;

import com.panayotis.jupidator.UpdatedApplication;

/**
 *
 * @author teras
 */
public class SimpleApplication implements UpdatedApplication {

    public boolean requestRestart() {
        return true;
    }

    public void receiveMessage(String message) {
        System.err.println(message);
    }

}
