/*
 * To change this template, choose Tools | Templates
 * and open the template bufferin the editor.
 */
package com.panayotis.jupidator.launcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 *
 * @author teras
 */
public class Commander {

    /** If synchronization with streams is required, waitFor first waits for
     * streams to finish and then exits. It is expected that this feature is
     * safe, but in anycase this oculd be disabled by setting this variable to
     * "false" */
    private final static boolean SYNC_WITH_STREAMS = true;
    /* */
    private static final int INVALID_EXIT_VALUE = Integer.MIN_VALUE;
    private final String[] command;
    /* */
    private Closure out, err, finish;
    private Process proc;
    private BufferedWriter bufferin = null;
    private OutputProxy procout = null;
    private OutputProxy procerr = null;
    private int exit_value = INVALID_EXIT_VALUE;
    private boolean output_is_terminated = true;

    public Commander(ArrayList<String> command) {
        this(command.toArray(new String[]{}));
    }

    public Commander(String[] command) {
        this.command = command;
        if (command == null)
            throw new NullPointerException("Command should not be null");
    }

    public void setOutListener(Closure out) {
        this.out = out;
    }

    public void setErrListener(Closure err) {
        this.err = err;
    }

    public void setEndListener(Closure finish) {
        this.finish = finish;
    }

    public synchronized void exec() {
        if (proc != null)
            throw new RuntimeException("Command already running");
        exit_value = INVALID_EXIT_VALUE;
        proc = null;
        waitThread = null;
        try {
            proc = Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            if (err != null)
                err.exec("Process can not start: " + ex.getMessage());
            if (finish != null)
                finish.exec("");
            return;
        }
        bufferin = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
        procout = new OutputProxy(out, proc.getInputStream(), this);
        procerr = new OutputProxy(err, proc.getErrorStream(), this);
        output_is_terminated = false;
        procout.worker.start();
        procerr.worker.start();
    }

    public boolean isActive() {
        return proc != null;
    }

    public void sendLine(String line) {
        if (bufferin == null) {
            if (err != null)
                err.exec("Request to send data inappropriate: process not active");
            return;
        }
        if (line == null)
            return;
        try {
            bufferin.write(line);
            bufferin.newLine();
            bufferin.flush();
        } catch (IOException ex) {
        }
    }

    public void terminateInput() {
        if (bufferin != null)
            try {
                bufferin.close();
            } catch (IOException ex) {
            }
        bufferin = null;
    }

    private synchronized void doWaitFor() {
        if (proc == null)
            return;
        try {
            proc.waitFor();
        } catch (InterruptedException ex) {
        }
        try {
            exit_value = proc.exitValue();
        } catch (Exception e) {
            exit_value = 0;
        }
        proc = null;
    }
    private Thread waitThread;

    public void waitFor() {
        /* Use this thread trick, to suspend this thread until the streams
         * have finished executing. Thus, first is "finish" Closure being
         * informed and then waitFor exits. A new thread is required since we
         * want to catch InterruptedExceptions only for the waitThread, not the
         * current thread.
         * This feature could be disabled by setting SYNC_WITH_STREAMS to false
         */
        waitThread = new Thread() {

            public void run() {
                doWaitFor();
                if (SYNC_WITH_STREAMS)
                    try {
                        /* do not perform this loop, if we don't want to synchronize with out/error streams */
                        while ((!output_is_terminated || waitThread.isInterrupted()))
                            Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                    }
            }
        };
        waitThread.start();
        try {
            waitThread.join();
        } catch (InterruptedException ex) {
        }
    }

    private void doKill() {
        try {
            proc.destroy();
        } catch (Exception e) {
        }
    }

    public void kill() {
        doKill();
        waitFor();
    }

    public int exitValue() {
        if (exit_value == INVALID_EXIT_VALUE)
            throw new IllegalThreadStateException("Unable to retrieve exit value");
        return exit_value;
    }

    private synchronized void doInterrupt() {
        if (waitThread != null)
            waitThread.interrupt();
    }

    private synchronized void outputTerminated(OutputProxy proxy) {
        /* Do not care if no call back is defined */
        if (finish == null) {
            doInterrupt();
            output_is_terminated = true;
            return;
        }
        /* Do not care if it is already nulled */
        if (procout == null && procerr == null)
            return;

        if (proxy == procout)
            procout = null;
        if (proxy == procerr)
            procerr = null;
        if (procerr != null || procout != null)
            return;

        /* Kill after both streams have ended */
        output_is_terminated = true;
        doKill();
        doWaitFor();
        finish.exec(exit_value);
        doInterrupt();
    }

    private class OutputProxy {

        private final BufferedReader in;
        private final Thread worker;
        private final Closure listen;
        private final OutputProxy self;
        private Commander cmdr;

        private OutputProxy(Closure listener, InputStream inputStream, Commander commander) {
            in = new BufferedReader(new InputStreamReader(inputStream));
            this.listen = listener;
            this.cmdr = commander;
            this.self = this;
            worker = new Thread() {

                public void run() {
                    String line;
                    try {
                        while (in != null && (line = in.readLine()) != null && (!isInterrupted()))
                            if (listen != null)
                                listen.exec(line);
                    } catch (IOException ex) {
                    } finally {
                        try {
                            in.close();
                        } catch (IOException ex) {
                        }
                    }
                    if (cmdr != null)
                        cmdr.outputTerminated(self);
                }
            };
        }
    }
}
