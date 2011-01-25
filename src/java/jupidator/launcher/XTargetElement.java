/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

/**
 *
 * @author teras
 */
public abstract class XTargetElement implements XElement {

    public final String target;

    public XTargetElement(String target) {
        this.target = target;
    }
}
