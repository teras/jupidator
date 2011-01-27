/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 *
 * @author teras
 */
public abstract class XNativeElement extends XTargetElement {

    public static final boolean isWindows, isMac, isLinux;
    protected final String input;

    static {
        String OS = System.getProperty("os.name").toLowerCase();
        isWindows = OS.startsWith("windows");
        isMac = OS.startsWith("mac");
        isLinux = OS.startsWith("linux");
    }

    public XNativeElement(String target, String input) {
        super(target);
        this.input = input;
    }

    protected static String exec(XNativeCommand command) {
        StringBuilder output = new StringBuilder();
        Visuals.info("Executing " + command.command);
        try {
            Process p = Runtime.getRuntime().exec(command.getArgs());
            if (command.input != null && command.input.length() > 0) {
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                w.write(command.input);
                w.close();
            }
            String line;
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = r.readLine()) != null)
                output.append(line).append('\n');
            p.waitFor();
            if (p.exitValue() == 0)
                return output.toString();
        } catch (Exception ex) {
            Visuals.error("Exception while executing " + command.command + ": " + ex.toString());
        }
        return output.toString();
    }
}
