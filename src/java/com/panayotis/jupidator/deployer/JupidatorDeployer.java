/*
 * JupidatorDeployer.java
 *
 * Created on September 29, 2008, 5:10 PM
 */
package com.panayotis.jupidator.deployer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
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

    public static final String EXTENSION = ".jupidator";
    private static BufferedWriter out;


    static {
        try {
            String filename = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "jupidator." + new SimpleDateFormat("yyyyMMdd_hhmmss").format(Calendar.getInstance().getTime()) + ".log";
            out = new BufferedWriter(new FileWriter(filename));
        } catch (IOException ex) {
        }
    }

    private static void debug(String message) {
        try {
            if (out != null) {
                out.write(message);
                out.newLine();
                out.flush();
            }
        } catch (IOException ex) {
        }
    }

    private static void endDebug() {
        if (out != null) {
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }

    public static void main(String[] args) {
        try {
            new JupidatorDeployer();
            debug("Start log of Jupidator Deployer with arguments:");
            for (int i = 0; i < args.length; i++) {
                debug("  #" + i + ": " + args[i]);
            }

            int pos = 0;

            if (Character.toLowerCase(args[pos++].charAt(0)) == 'g')
                showGUI();

            int files = Integer.valueOf(args[pos++]);
            debug("Number of affected files: " + files);

            /* Under windows it is important to wait a bit before deleting files */
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                Thread.sleep(3000);
                debug("Waiting 3 seconds before starting updating");
            }

            files += pos;
            for (; pos < files; pos++) {
                if (args[pos].length() > 0) {
                    String data = args[pos].substring(1, args[pos].length());
                    switch (args[pos].charAt(0)) {
                        case '-':
                            debug("Removing file " + data);
                            debug("  Deleting file " + data);
                            if (!rmTree(new File(data)))
                                debug("*ERROR* Unable to delete file " + data);
                            break;
                        case '+':
                            debug("Installing file " + data);
                            String oldpath = data.substring(0, data.length() - EXTENSION.length());
                            File oldfile = new File(oldpath);
                            File newfile = new File(data);

                            debug("  Deleting file " + oldfile);
                            if (!rmTree(oldfile))
                                debug("  *ERROR* Unable to remove old file " + oldpath);
                            debug("  Renaming " + data + " to " + oldfile);
                            newfile.renameTo(oldfile);
                            break;
                        case 'c':
                            debug("Executing command");
                            execCmd(data);
                            break;
                        case 'w':
                            debug("Waiting msecs=" + data);
                            Thread.sleep(Integer.parseInt(data));
                            break;
                        case 'k':
                            debug("Killing process");
                            killProc(data);
                            break;
                        default:
                            debug("Unknown command " + args[pos]);
                    }
                    debug("End of works");
                }
            }

            int initpos = pos;
            String exec[] = new String[args.length - pos];
            debug("Restarting application with following arguments:");
            for (; pos < args.length; pos++) {
                exec[pos - initpos] = args[pos];
                debug("  #" + (pos - initpos) + ": " + exec[pos - initpos]);
            }
            try {
                Runtime.getRuntime().exec(exec);
            } catch (IOException ex) {
            }

        } catch (Exception ex) {
            debug("Exception found: " + ex.toString());
        } finally {
            endDebug();
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

    /* This method is also called from cancel action by FileAdd */
    public static boolean rmTree(File f) {
        if (!f.exists())
            return true;
        if (f.isDirectory()) {
            File dir[] = f.listFiles();
            for (int i = 0; i < dir.length; i++) {
                if (!rmTree(dir[i]))
                    return false;
            }
        }
        return f.delete();
    }


    private static void killProc(String data) {
        ArrayList<String> args = buildArgs(data);
        args.remove(args.size() - 1);
        String pattern = args.get(args.size() - 1);

        ArrayList<String> ids = new ArrayList<String>();

        ArrayList<String> list = new ArrayList<String>();
        list.add("ps");
        list.add("aux");
        StringTokenizer tok = new StringTokenizer(exec(list, null), "\n");
        String token;
        while (tok.hasMoreTokens()) {
            token = tok.nextToken();
            if (token.indexOf(pattern) >= 0 && token.indexOf("com.panayotis.jupidator.deployer.JupidatorDeployer") < 0) {
                debug("  Killing " + token);
                StringTokenizer tok2 = new StringTokenizer(token);
                tok2.nextToken();
                ids.add(tok2.nextToken());
            }
        }
        ArrayList<String> kill = new ArrayList<String>();
        kill.add("kill");
        kill.add("ID");
        for (String id : ids) {
            kill.set(1, id);

            exec(kill, null);
        }
    }

    private static void execCmd(String arguments) {
        ArrayList<String> list = buildArgs(arguments);
        String input = list.get(list.size() - 1);
        list.remove(list.size() - 1);
        exec(list, input);
    }

    private static String exec(ArrayList<String> list, String input) {
        String[] cmd = list.toArray(new String[]{});
        StringBuffer output = new StringBuffer();
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            if (input != null && input.length() > 0) {
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                w.write(input);
                w.close();
            }
            String line;
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = r.readLine()) != null) {
                output.append(line).append('\n');
            }

            p.waitFor();
            if (p.exitValue() == 0) {
                debug("  Successfully executed " + cmd[0]);
                return output.toString();
            }
        } catch (Exception ex) {
            debug(ex.getMessage());
        }
        debug("  Error while executing " + cmd[0]);
        return output.toString();
    }

    private static ArrayList<String> buildArgs(String arguments) throws NumberFormatException, StringIndexOutOfBoundsException {
        ArrayList<Integer> sizes = new ArrayList<Integer>();
        int pos = 0;
        int lastpos = 0;
        while ((pos = arguments.indexOf("#", pos)) > 0) {
            sizes.add(Integer.valueOf(arguments.substring(lastpos, pos++)));
            lastpos = pos;
            if (arguments.charAt(pos) == '.')
                break;
        }
        pos++;
        ArrayList<String> args = new ArrayList<String>();
        String a;
        for (int i = 0; i < sizes.size(); i++) {
            a = arguments.substring(pos, pos + sizes.get(i));
            args.add(a);
            pos += sizes.get(i);
            debug("  #" + i + ": " + a);
        }
        return args;
    }
}
