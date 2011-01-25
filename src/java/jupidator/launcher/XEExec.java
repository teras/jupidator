/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import java.util.ArrayList;

/**
 *
 * @author teras
 */
public class XEExec extends XSimpleNativeElement {

    private final ArrayList<String> args;

    public XEExec(String target, String input, ArrayList<String> args) {
        super(target, input);
        this.args = args;
    }

    @Override
    protected XNativeCommand getCommand() {
        return new XNativeCommand(target, args, input);
    }
}
