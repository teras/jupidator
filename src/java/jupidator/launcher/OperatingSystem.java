/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

/**
 *
 * @author teras
 */
public class OperatingSystem {

    public static final boolean isWindows, isMac, isLinux;

    static {
        String OS = System.getProperty("os.name").toLowerCase();
        isWindows = OS.startsWith("windows");
        isMac = OS.startsWith("mac");
        isLinux = OS.startsWith("linux");
    }
}
