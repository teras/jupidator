/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.data;

import com.panayotis.jupidator.ApplicationInfo;
import java.util.ArrayList;

/**
 *
 * @author teras
 */
public class Arch {

    private String tag;
    private String os;
    private String arch;
    private String exec;
    private ArrayList<String> arguments;

    public Arch(String tag, String os, String arch) {
        this.tag = tag.toLowerCase();
        this.os = os.toLowerCase();
        this.arch = arch.toLowerCase();
        arguments = new ArrayList<String>();
    }

    boolean isTag(String tag) {
        return this.tag.toLowerCase().equals(tag);
    }

    boolean isCurrent() {
        if (tag.equals("any"))  // Never match "any" architecture
            return false;
        String c_os = System.getProperty("os.name");
        String c_arch = System.getProperty("os.arch");
        return (c_os.toLowerCase().startsWith(os) && c_arch.toLowerCase().startsWith(arch));
    }

    void setExec(String exec) {
        this.exec = exec;
    }

    void addArgument(String argument, ApplicationInfo appinfo) {
        arguments.add(appinfo.updatePath(argument));
    }

    public int countArguments() {
        return arguments.size() + 1;
    }

    public String getArgument(int index) {
        if (index == 0)
            return exec;
        if (index > 0 && index <= arguments.size())
            return arguments.get(index - 1);
        throw new ArrayIndexOutOfBoundsException("Not valid index for architecture arguments: " + index);
    }
}
