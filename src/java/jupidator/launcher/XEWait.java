/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

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
        Visuals.info("Waiting for " + waittime + "msecs=");
        try {
            Thread.sleep(waittime);
        } catch (InterruptedException ex) {
        }
    }
}
