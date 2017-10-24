/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.compress;

import com.panayotis.jupidator.JupidatorCreatorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author teras
 */
public class NoCompression {

    public static Exception compress(File input, File output) {
        if (!input.isFile())
            throw new JupidatorCreatorException("No compression is selected for files only");
        try {
            Files.copy(input.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return null;
        } catch (IOException ex) {
            return ex;
        }
    }
}
