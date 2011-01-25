/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import static jupidator.launcher.JupidatorDeployer.debug;

/**
 *
 * @author teras
 */
public class XEWait implements XElement {

    private final int waittime;

    public XEWait(int waittime) {
        this.waittime = waittime;
    }

    public void perform() {
        debug("Waiting msecs=" + waittime);
        try {
            Thread.sleep(waittime);
        } catch (InterruptedException ex) {
        }
    }
}
