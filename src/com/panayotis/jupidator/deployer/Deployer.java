/*
 * Deployer.java
 *
 * Created on September 29, 2008, 5:10 PM
 */

package com.panayotis.jupidator.deployer;

import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author  teras
 */
public class Deployer extends JFrame {
    private ArrayList<String> movedfiles = new ArrayList<String>();
    private ArrayList<String> deletedfiles = new ArrayList<String>();
    
    
    public void initGUI() {
        initComponents();
        ProgressBar.putClientProperty("JProgressBar.style", "circular");
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        TextL = new javax.swing.JLabel();
        ProgressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        jPanel1.setLayout(new java.awt.BorderLayout(12, 0));

        TextL.setText("Please wait while deploying files");
        jPanel1.add(TextL, java.awt.BorderLayout.WEST);

        ProgressBar.setIndeterminate(true);
        ProgressBar.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanel1.add(ProgressBar, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
        Deployer f = new Deployer();
        f.initGUI();
        
        
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        System.exit(0);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar ProgressBar;
    private javax.swing.JLabel TextL;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
