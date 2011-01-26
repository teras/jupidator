/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jupidator.launcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author teras
 */
public class Debug {

    private static BufferedWriter outd;

    static {
        try {
            String filename = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "jupidator." + new SimpleDateFormat("yyyyMMdd_hhmmss").format(Calendar.getInstance().getTime()) + ".log";
            outd = new BufferedWriter(new FileWriter(filename));
        } catch (IOException ex) {
        }
    }

    public static void info(String message) {
        try {
            if (outd != null) {
                outd.write(message);
                outd.newLine();
                outd.flush();
            }
        } catch (IOException ex) {
        }
    }

    public static void error(String message) {
        info("*ERROR* " + message);
    }

    public static void finish() {
        if (outd != null)
            try {
                outd.close();
            } catch (IOException ex) {
            }
    }
}
