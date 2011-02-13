/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator;

import java.io.File;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author teras
 */
public class Creator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        for (File f : FileSystemView.getFileSystemView().getRoots() )
            System.out.println(f.getAbsolutePath());
		System.out.println(FileSystemView.getFileSystemView().isFileSystemRoot(new File("/Volumes/FreeSpace")));
        

        CreatorFrame frame = new CreatorFrame();
        frame.setVisible(true);
    }
}
