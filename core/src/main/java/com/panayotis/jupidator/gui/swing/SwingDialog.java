/*
 *
 * This file is part of Jupidator.
 *
 * Jupidator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 *
 * Jupidator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jupidator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

/*
 * SwingDialog.java
 *
 * Created on September 25, 2008, 3:54 AM
 */
package com.panayotis.jupidator.gui.swing;

import com.panayotis.jupidator.Updater;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;

import static com.panayotis.jupidator.i18n.I18N._t;

/**
 * @author teras
 */
class SwingDialog extends JDialog {

    private static final BufferedImage sysicon;

    Updater callback;
    JEditorPane InfoPane;
    Details ChangeLogP;
    BufferedImage icon;
    private boolean infoVisible = true;
    private boolean filelistVisible = true;
    private boolean now_show_versions = true;
    String infotext;
    String filetext;
    String urlText;

    static {
        BufferedImage img = null;
        try {
            img = ImageIO.read(SwingDialog.class.getResource("/com/panayotis/jupidator/icons/package.png"));
        } catch (IOException ex) {
        }
        sysicon = img;
    }

    /**
     * Creates new SwingDialog
     */
    public SwingDialog() {
        super((Frame) null, false);
        initComponents();
        InfoPane = new JEditorPane();
        InfoPane.setContentType("text/html");
        InfoPane.setEditable(false);
        ChangeLogP = new Details();
        ChangeLogP.setViewportView(InfoPane);
        DetailedP.add(ChangeLogP, BorderLayout.CENTER);

        LaterB.requestFocus();
    }

    public void setInfoVisible(boolean infoVisible) {
        this.infoVisible = infoVisible;
    }

    public void setFileListVisible(boolean filelistVisible) {
        this.filelistVisible = filelistVisible;
    }

    void showVersions(boolean showVersions_not_Files) {
        if (!filelistVisible)
            DetailsB.setVisible(false);
        InfoPane.setText(showVersions_not_Files ? infotext : filetext);
        DetailsB.setText(showVersions_not_Files ? _t("Show Actions") : _t("Show Versions"));
        InfoPane.setCaretPosition(0);
        RelNotesL.setText(showVersions_not_Files ? _t("Release Notes") : _t("Actions"));
        now_show_versions = showVersions_not_Files;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ProgressP = new javax.swing.JPanel();
        BarPanel = new javax.swing.JPanel();
        PBar = new javax.swing.JProgressBar();
        ButtonPanel = new javax.swing.JPanel();
        ActionB = new javax.swing.JButton();
        InfoL = new javax.swing.JLabel();
        MainPanel = new javax.swing.JPanel();
        InfoHolderP = new javax.swing.JPanel();
        DetailedP = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        RelNotesL = new javax.swing.JLabel();
        webNotesL = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        VersInfoL = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        InfoB = new javax.swing.JButton();
        NewVerL = new javax.swing.JLabel();
        IconL = new javax.swing.JLabel();
        CommandP = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        DetailsB = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        LaterB = new javax.swing.JButton();
        UpdateB = new javax.swing.JButton();
        SkipB = new javax.swing.JButton();
        PrevL = new javax.swing.JLabel();

        ProgressP.setLayout(new java.awt.BorderLayout());

        BarPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 0));
        BarPanel.setLayout(new java.awt.BorderLayout());

        PBar.setStringPainted(true);
        BarPanel.add(PBar, java.awt.BorderLayout.CENTER);

        ProgressP.add(BarPanel, java.awt.BorderLayout.CENTER);

        ButtonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 24, 8, 8));
        ButtonPanel.setLayout(new java.awt.BorderLayout());

        ActionB.setText(_t("Cancel"));
        ActionB.setActionCommand("cancel");
        ActionB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActionBActionPerformed(evt);
            }
        });
        ButtonPanel.add(ActionB, java.awt.BorderLayout.CENTER);

        ProgressP.add(ButtonPanel, java.awt.BorderLayout.EAST);

        InfoL.setText(_t("Downloading..."));
        ProgressP.add(InfoL, java.awt.BorderLayout.LINE_START);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        MainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        MainPanel.setLayout(new java.awt.BorderLayout());

        InfoHolderP.setLayout(new java.awt.BorderLayout());

        DetailedP.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        RelNotesL.setFont(RelNotesL.getFont().deriveFont(RelNotesL.getFont().getStyle() | java.awt.Font.BOLD));
        RelNotesL.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 0, 4, 0));
        jPanel2.add(RelNotesL, java.awt.BorderLayout.CENTER);

        webNotesL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/panayotis/jupidator/icons/web.png"))); // NOI18N
        webNotesL.setText(_t("On the Web"));
        webNotesL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                webNotesLActionPerformed(evt);
            }
        });
        jPanel2.add(webNotesL, java.awt.BorderLayout.EAST);

        DetailedP.add(jPanel2, java.awt.BorderLayout.NORTH);

        InfoHolderP.add(DetailedP, java.awt.BorderLayout.CENTER);

        jPanel5.setLayout(new java.awt.BorderLayout());

        VersInfoL.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 16));
        jPanel5.add(VersInfoL, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        InfoB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/panayotis/jupidator/icons/info.png"))); // NOI18N
        InfoB.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 4, 0));
        InfoB.setBorderPainted(false);
        InfoB.setContentAreaFilled(false);
        InfoB.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/panayotis/jupidator/icons/info_sel.png"))); // NOI18N
        InfoB.setVisible(false);
        InfoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InfoBActionPerformed(evt);
            }
        });
        jPanel1.add(InfoB, java.awt.BorderLayout.EAST);

        NewVerL.setFont(NewVerL.getFont().deriveFont(NewVerL.getFont().getStyle() | java.awt.Font.BOLD, NewVerL.getFont().getSize() + 1));
        NewVerL.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 4, 0));
        jPanel1.add(NewVerL, java.awt.BorderLayout.CENTER);

        jPanel5.add(jPanel1, java.awt.BorderLayout.NORTH);

        InfoHolderP.add(jPanel5, java.awt.BorderLayout.NORTH);

        MainPanel.add(InfoHolderP, java.awt.BorderLayout.CENTER);

        IconL.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        IconL.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 8, 0, 12));
        MainPanel.add(IconL, java.awt.BorderLayout.WEST);

        CommandP.setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        jPanel3.setLayout(new java.awt.BorderLayout());

        DetailsB.setText(_t("Details"));
        DetailsB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DetailsBActionPerformed(evt);
            }
        });
        jPanel3.add(DetailsB, java.awt.BorderLayout.CENTER);

        CommandP.add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 12));
        jPanel4.setLayout(new java.awt.BorderLayout());

        LaterB.setText(_t("Remind me later"));
        LaterB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LaterBActionPerformed(evt);
            }
        });
        jPanel4.add(LaterB, java.awt.BorderLayout.CENTER);

        UpdateB.setText(_t("Install"));
        UpdateB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdateBActionPerformed(evt);
            }
        });
        jPanel4.add(UpdateB, java.awt.BorderLayout.EAST);

        SkipB.setText(_t("Skip"));
        SkipB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SkipBActionPerformed(evt);
            }
        });
        jPanel4.add(SkipB, java.awt.BorderLayout.WEST);

        CommandP.add(jPanel4, java.awt.BorderLayout.EAST);

        PrevL.setForeground(java.awt.Color.red);
        PrevL.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        PrevL.setText(_t("WARNING! This update requires elevated privileges!"));
        PrevL.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 12, 4, 12));
        CommandP.add(PrevL, java.awt.BorderLayout.NORTH);

        MainPanel.add(CommandP, java.awt.BorderLayout.SOUTH);

        getContentPane().add(MainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void UpdateBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateBActionPerformed
        CommandP.setVisible(false);
        ProgressP.setVisible(true);
        MainPanel.add(ProgressP, BorderLayout.SOUTH);
        callback.actionCommit();
    }//GEN-LAST:event_UpdateBActionPerformed

    private void LaterBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LaterBActionPerformed
        callback.actionDefer();
    }//GEN-LAST:event_LaterBActionPerformed

    private void SkipBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SkipBActionPerformed
        callback.actionIgnore();
    }//GEN-LAST:event_SkipBActionPerformed

    private void ActionBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActionBActionPerformed
        ActionB.setEnabled(false);
        if (ActionB.getActionCommand().startsWith("c"))
            callback.actionCancel();
        else
            callback.actionRestart();
    }//GEN-LAST:event_ActionBActionPerformed

    private void InfoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InfoBActionPerformed
        new AboutDialog(this).setVisible(true);
    }//GEN-LAST:event_InfoBActionPerformed

    private void DetailsBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DetailsBActionPerformed
        if (!DetailedP.isVisible()) {
            DetailedP.setVisible(true);
            InfoB.setVisible(infoVisible && DetailedP.isVisible());
            DetailsB.setText(_t("Show Actions"));
            showVersions(true);
        } else if (filelistVisible)
            showVersions(!now_show_versions);
        pack();
    }//GEN-LAST:event_DetailsBActionPerformed

    private void webNotesLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webNotesLActionPerformed
        try {
            Desktop.getDesktop().browse(new URI(urlText));
        } catch (Exception ignored) {
        }
    }//GEN-LAST:event_webNotesLActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton ActionB;
    javax.swing.JPanel BarPanel;
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JPanel CommandP;
    javax.swing.JPanel DetailedP;
    javax.swing.JButton DetailsB;
    javax.swing.JLabel IconL;
    private javax.swing.JButton InfoB;
    javax.swing.JPanel InfoHolderP;
    javax.swing.JLabel InfoL;
    javax.swing.JButton LaterB;
    private javax.swing.JPanel MainPanel;
    javax.swing.JLabel NewVerL;
    javax.swing.JProgressBar PBar;
    javax.swing.JLabel PrevL;
    javax.swing.JPanel ProgressP;
    private javax.swing.JLabel RelNotesL;
    javax.swing.JButton SkipB;
    javax.swing.JButton UpdateB;
    javax.swing.JLabel VersInfoL;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    javax.swing.JButton webNotesL;
    // End of variables declaration//GEN-END:variables

    @Override
    public void pack() {
        Image current = icon;
        if (icon == null)
            current = sysicon;
        if (!DetailedP.isVisible())
            current = current.getScaledInstance(48, 48, java.awt.Image.SCALE_SMOOTH);
        IconL.setIcon(new ImageIcon(current));
        super.pack();
    }
}
