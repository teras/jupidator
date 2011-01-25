/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import static jupidator.launcher.JupidatorDeployer.debug;

import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 *
 * @author teras
 */
public class XEKill extends XNativeElement {

    private final String signal;

    public XEKill(String target, String signal) {
        super(target, null);
        this.signal = signal;
    }

    @Override
    public void perform() {
        debug("Killing process with pattern " + target);

        XNativeCommand ps;
        int ps_column;
        if (isWindows) {
            ArrayList args = new ArrayList();
            args.add("/FO");
            args.add("CSV");
            ps = new XNativeCommand("tasklist.exe", args, null);
            ps_column = 3;
        } else {
            ArrayList args = new ArrayList();
            args.add("aux");
            ps = new XNativeCommand("ps", args, null);
            ps_column = 2;
        }

        ArrayList<String> proclist = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(exec(ps), "\n\r");
        while (tok.hasMoreTokens()) {
            String line = tok.nextToken();
            if (line.indexOf(target) >= 0 && line.indexOf(JupidatorDeployer.class.getName()) < 0) {
                debug("  Killing " + line);
                StringTokenizer col = new StringTokenizer(line, isWindows ? "\"" : " ");
                for (int i = 0; i < ps_column - 1; i++)
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
