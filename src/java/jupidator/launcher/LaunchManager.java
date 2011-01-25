/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author teras
 */
public class LaunchManager {

    private static final String[] sudocmd = {
        "sudo",
        "-S",
        "-p",
        ""
    };
    private static final int FILESIZE_LOCATION = 2;
    private static JSudo inst;
    private static boolean sudo_is_ok;
    private final static Closure status = new Closure() {

        public void exec(Object data) {
        }
    };

    public static void main(String[] command) {
        String pass = null;
        boolean asAdmin = needsSudo(splitArray(command, FILESIZE_LOCATION, FILESIZE_LOCATION + Integer.parseInt(command[1])));

        String[] cmd = command;
        if (asAdmin) {
            pass = LaunchManager.getPassword();
            if (pass == null)
                return;
            cmd = combineStrings(sudocmd, command);
        }
        if (true)
            return;

        Commander com = new Commander(cmd);
        com.setOutListener(status);
        com.setErrListener(status);

        com.exec();
        if (com.isActive()) {
            com.sendLine(pass);
            com.waitFor();
        }
    }

    private static boolean needsSudo(String[] files) {
        for (int i = 0; i < files.length; i++)
            if (files[i].startsWith("-") || files[i].startsWith("+")) {
                String fname = files[i].substring(1);
                System.out.println("check " + fname);
            }
        return false;
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

    private static String[] combineStrings(String[] one, String[] two) {
        String[] result = new String[one.length + two.length];
        System.arraycopy(one, 0, result, 0, one.length);
        System.arraycopy(two, 0, result, one.length, two.length);
        return result;
    }

    private static String[] splitArray(String[] pool, int from, int to) {
        if (from < 0 || to > pool.length || from >= to)
            return emptyString;
        String[] result = new String[to - from];
        for (int i = from; i < to; i++)
            result[i - from] = pool[i];
        return result;
    }

    private static String[] StringToArray(String array, String delimeter) {
        if (array == null || array.equals(""))
            return emptyString;
        ArrayList<String> res = new ArrayList<String>();
        StringTokenizer tk = new StringTokenizer(array, delimeter);
        while (tk.hasMoreTokens())
            res.add(tk.nextToken());
        return res.toArray(emptyString);
    }

    public static String ArrayToString(String[] array, String delimeter) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < array.length; i++)
            buf.append(array[i]).append(delimeter);
        return buf.toString();
    }
    private final static String[] emptyString = new String[]{};
}
