/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.gui;

import java.util.Formatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author teras
 */
public class UpdateWatcher extends TimerTask implements BufferListener {

    private Timer timer = new Timer(true);
    private long bytes;
    private long lastbytes;
    private long allbytes;
    private long freezedbytes;
    private JupidatorGUI callback;

    public void run() {
        long diffbytes = bytes - lastbytes;
        lastbytes = bytes;

        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        if (diffbytes < 1e3)
            formatter.format("%db/sec", diffbytes);
        else if (diffbytes < 1e6)
            formatter.format("%2.1fKb/sec", diffbytes / 1e3);
        else if (diffbytes < 1e9)
            formatter.format("%2.1fMb/sec", diffbytes / 1e6);
        else if (diffbytes < 1e12)
            formatter.format("%2.1fGb/sec", diffbytes / 1e9);
        callback.setDownloadRatio(sb.toString().trim(), ((float) lastbytes) / allbytes);
    }

    public void setCallBack(JupidatorGUI callback) {
        this.callback = callback;
    }

    public void startWatcher() {
        timer.schedule(this, 0, 1000);
    }

    public void stopWatcher() {
        timer.cancel();
    }

    public void addBytes(long bytes) {
        this.bytes += bytes;
    }

    public void setAllBytes(long bytes) {
        allbytes = bytes;
    }

    public void freezeSize() {
        freezedbytes = bytes;
    }

    public void rollbackSize() {
        bytes = freezedbytes;
    }
}
