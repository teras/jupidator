/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.launcher;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author teras
 */
public class LaunchManager {

    private static final String[] pipedsudo = {
        "sudo",
        "-S",
        "-p",
        ""
    };
    private static JSudo inst;
    private static boolean sudo_is_ok;

    public static boolean execute(String[] command, Closure out, Closure err, Closure begin) {
        String pass = null;
        boolean asAdmin = true;

        String[] cmd = command;
        if (asAdmin) {
            pass = LaunchManager.getPassword();
            if (pass == null)
                return false;
            cmd = combineStrings(pipedsudo, command);
        }

        Commander com = new Commander(cmd);
        com.setOutListener(out);
        com.setErrListener(err);

        begin.exec(com);
        com.exec();
        if (com.isActive()) {
            com.sendLine(pass);
            com.waitFor();
        }
        return true;
    }

    static boolean testSudo(String pass) {
        if (pass == null)
            return false;

        final String SIGNATURE = "_ALL_OK_";
        clearSudo();
        sudo_is_ok = false;
        String[] command = {"sudo", "-S", "echo", SIGNATURE};

        Commander sudo = new Commander(command);
        sudo.setOutListener(new Closure() {

            public void exec(Object line) {
                if (line.equals(SIGNATURE))
                    sudo_is_ok = true;
            }
        });
        sudo.exec();
        sudo.sendLine(pass);
        sudo.terminateInput();
        sudo.waitFor();
        clearSudo();
        return sudo_is_ok;
    }

    private static void clearSudo() {
        Commander clear = new Commander(new String[]{"sudo", "-k"});
        clear.exec();
        clear.terminateInput();
        clear.waitFor();
    }

    private static String getPassword() {
        if (inst == null)
            inst = new JSudo();

        String pass = inst.getUserPass();
        if (testSudo(pass))
            return pass;

        inst.setVisible(true);
        return inst.getUserPass();
    }

    private final static String[] combineStrings(String[] one, String[] two) {
        String[] result = new String[one.length + two.length];
        System.arraycopy(one, 0, result, 0, one.length);
        System.arraycopy(two, 0, result, one.length, two.length);
        return result;
    }

    private final static String[] StringToArray(String array, String delimeter) {
        if (array == null || array.equals(""))
            return emptyString;
        ArrayList<String> res = new ArrayList<String>();
        StringTokenizer tk = new StringTokenizer(array, delimeter);
        while (tk.hasMoreTokens())
            res.add(tk.nextToken());
        return res.toArray(emptyString);
    }
    private final static String[] emptyString = new String[]{};
}
