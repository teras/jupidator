/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 *
 * @author teras
 */
public class XEKill extends XNativeElement {

    private static final XNativeCommand PS_CMD;
    private static final int PS_COLUMN;

    static {
        if (isWindows) {
            ArrayList args = new ArrayList();
            args.add("/FO");
            args.add("CSV");
            PS_CMD = new XNativeCommand("tasklist.exe", args, null);
            PS_COLUMN = 3;
        } else {
            ArrayList args = new ArrayList();
            args.add("aux");
            PS_CMD = new XNativeCommand("ps", args, null);
            PS_COLUMN = 2;
        }

    }
    private final String signal;

    public XEKill(String target, String signal) {
        super(target, null);
        this.signal = signal;
    }

    @Override
    public void perform() {
        Debug.info("Killing process with pattern " + target);
        ArrayList<String> proclist = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(exec(PS_CMD), "\n\r");
        while (tok.hasMoreTokens()) {
            String line = tok.nextToken();
            if (line.indexOf(target) >= 0 && line.indexOf(JupidatorDeployer.class.getName()) < 0) {
                Debug.info("  Killing " + line);
                StringTokenizer col = new StringTokenizer(line, isWindows ? "\"" : " ");
                for (int i = 0; i < PS_COLUMN - 1; i++)
                    col.nextToken();
                String next_pid = col.nextToken();
                if (next_pid.startsWith("\""))
                    next_pid = next_pid.substring(1, next_pid.length() - 1);
                proclist.add(next_pid);
            }
        }
        for (String id : proclist)
            exec(getKillCmd(id));
    }

    private XNativeCommand getKillCmd(String proc) {
        XNativeCommand kill;
        if (isWindows) {
            ArrayList args = new ArrayList();
            args.add("/PID");
            args.add(proc);
            args.add(target);
            if (!signal.equals(""))
                args.add(signal);
            kill = new XNativeCommand("taskkill.exe", args, null);
        } else {
            ArrayList args = new ArrayList();
            if (!signal.equals(""))
                args.add("-" + signal);
            args.add(proc);
            kill = new XNativeCommand("kill", args, null);
        }
        return kill;
    }
}
