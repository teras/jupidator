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
public class XEChstatus extends XSimpleNativeElement {

    private final String command;
    private final String mode;
    private final boolean recursive;

    public XEChstatus(String command, String target, String mode, boolean recursive) {
        super(target, null);
        this.command = command;
        this.mode = mode;
        this.recursive = recursive;
    }

    @Override
    protected XNativeCommand getCommand() {
        ArrayList<String> args = new ArrayList<String>();
        if (recursive)
            args.add("-R");
        args.add(mode);
        args.add(target);
        return new XNativeCommand(command, args, input);
    }
}
