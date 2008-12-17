/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.data.UpdaterAppElements;
import java.util.ArrayList;

/**
 *
 * @author teras
 */
public class ElementExec extends ElementNative {

    private ArrayList<String> arguments;

    public ElementExec(String command, String input, String exectime, UpdaterAppElements elements, ApplicationInfo info) {
        super(command, String.valueOf(Math.random()), input,              // Random hash for this exec
                ExecutionTime.parse(exectime, ExecutionTime.AFTER), elements, info);
        arguments = new ArrayList<String>();
    }

    public void addArgument(String value, ApplicationInfo appinfo) {
        arguments.add(appinfo.updatePath(value));
    }

    protected String[] getExecArguments() {
        String[] args = new String[arguments.size()];
        for (int i = 0; i < args.length; i++)
            args[i] = arguments.get(i);
        return args;
    }
}
