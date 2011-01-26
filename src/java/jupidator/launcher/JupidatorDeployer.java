/*
 * JupidatorDeployer.java
 *
 * Created on September 29, 2008, 5:10 PM
 */
package jupidator.launcher;

import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

/**
 *
 * @author teras
 */
public class JupidatorDeployer {

    @SuppressWarnings("SleepWhileHoldingLock")
    public static void main(String[] args) {
        try {
            Debug.info("Start log of Jupidator Deployer with arguments:");
            for (int i = 0; i < args.length; i++)
                Debug.info("  #" + i + ": " + args[i]);

            int pos = 0;

            if (Character.toLowerCase(args[pos++].charAt(0)) == 'g')
                showGUI();

            int files = Integer.valueOf(args[pos++]);
            Debug.info("Number of affected files: " + files);

            /* Under windows it is important to wait a bit before deleting files */
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                Thread.sleep(3000);
                Debug.info("Waiting 3 seconds before starting updating");
            }

            int initpos = pos;
            String exec[] = new String[args.length - pos];
            Debug.info("Restarting application with following arguments:");
            for (; pos < args.length; pos++) {
                exec[pos - initpos] = args[pos];
                Debug.info("  #" + (pos - initpos) + ": " + exec[pos - initpos]);
            }
            try {
                Runtime.getRuntime().exec(exec);
            } catch (IOException ex) {
            }

        } catch (Exception ex) {
            Debug.info("Exception found: " + ex.toString());
        } finally {
            Debug.finish();
            System.exit(0);
        }
    }

    private static void showGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }

        JFrame frame = new JFrame();
        JPanel jPanel1 = new javax.swing.JPanel();
        JLabel TextL = new javax.swing.JLabel();
        JProgressBar ProgressBar = new javax.swing.JProgressBar();

        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        jPanel1.setLayout(new java.awt.BorderLayout(12, 0));

        TextL.setText("Please wait while deploying files");
        jPanel1.add(TextL, java.awt.BorderLayout.WEST);

        ProgressBar.setIndeterminate(true);
        ProgressBar.putClientProperty("JProgressBar.style", "circular");
        ProgressBar.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanel1.add(ProgressBar, java.awt.BorderLayout.CENTER);

        frame.setUndecorated(true);
        frame.getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
