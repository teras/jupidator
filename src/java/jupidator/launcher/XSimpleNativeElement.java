/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

/**
 *
 * @author teras
 */
public abstract class XSimpleNativeElement extends XNativeElement {

    public XSimpleNativeElement(String target, String input) {
        super(target, input);
    }

    public void perform() {
        exec(getCommand());
    }

    protected abstract XNativeCommand getCommand();
}
