/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.panayotis.jupidator.applications;

import com.panayotis.jupidator.UpdatedApplication;

/**
 *
 * @author teras
 */
public class DummyApplication implements UpdatedApplication {

    public boolean requestRestart() {
        return true;
    }

    public void receiveMessage(String message) {
    }

}
