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
public final class XNativeCommand {

    public final String command;
    public final ArrayList<String> arguments;
    public final String input;

    public XNativeCommand(String command, ArrayList<String> arguments, String input) {
        this.command = command;
        this.arguments = arguments == null && command != null ? new ArrayList<String>() : arguments;
        this.input = input;
    }

    public String[] getArgs() {
        if (command == null)
            return null;
        String[] res = new String[arguments.size() + 1];
        res[0] = command;
        System.arraycopy(arguments.toArray(), 0, res, 1, arguments.size());
        return res;
    }
}
