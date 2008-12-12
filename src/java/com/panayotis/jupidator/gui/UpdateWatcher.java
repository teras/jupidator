/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.gui;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author teras
 */
public class UpdateWatcher extends TimerTask implements BufferListener {

    private Timer timer = new Timer();
    private long bytes;
    private long lastbytes;
    private long allbytes;
    private JupidatorGUI callback;

    public void run() {
        long diffbytes = bytes - lastbytes;
        lastbytes = bytes;
        callback.setDownloadRatio(diffbytes, ((float) lastbytes) / allbytes);
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
}
