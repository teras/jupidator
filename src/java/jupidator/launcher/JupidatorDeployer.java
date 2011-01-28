/*
 * JupidatorDeployer.java
 *
 * Created on September 29, 2008, 5:10 PM
 */
package jupidator.launcher;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

/**
 *
 * @author teras
 */
public class JupidatorDeployer {

    private static DeployerParameters params;

    public static void main(String[] args) {
        ObjectInputStream in;
        try {
            /* Recreate parameters */
            in = new ObjectInputStream(new FileInputStream(args[0]));
            params = (DeployerParameters) in.readObject();
            Visuals.setHeadless(params.isHeadless());
            Visuals.info("Start of Jupidator Deployer");

            /* Run after visuals have been initialized */
            Thread worker = new Thread() {

                @Override
                public void run() {
                    try {
                        /* Under windows it is important to wait a bit before deleting files */
                        if (System.getProperty("os.name").toLowerCase().contains("windows"))
                            params.getElements().add(0, new XEWait(3000));

                        /* Execute installer commands */
                        for (XElement element : params.getElements())
                            element.perform();

                        /* Relaunch application if applicable */
                        List<String> command = params.getRelaunchCommand();
                        if (command.size() >= 1)
                            new ProcessBuilder(command).start();
                        
                        /* Exit installer */
                        Visuals.finish();
                        System.exit(0);
                    } catch (Exception ex) {
                        finishWithError(ex);
                    }
                }
            };
            worker.start();
        } catch (Exception ex) {
            finishWithError(ex);
        }
    }

    private static void finishWithError(Exception ex) {
        StringBuilder buf = new StringBuilder("Exception found: ");
        buf.append(ex.toString()).append("\n");
        for (StackTraceElement stack : ex.getStackTrace())
            buf.append("       at ").append(stack.toString()).append("\n");
        Visuals.error(buf.toString());
        Visuals.finish();
    }
}
