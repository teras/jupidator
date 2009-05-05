/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.elements.security;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 *
 * @author teras
 */
public class Digester {

    private MessageDigest digest;
    private byte[] hash;
    private final String algorithm;

    public static Digester getDigester(String algorithm) {
        if (algorithm == null)
            return null;
        try {
            return new Digester(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }

    private Digester(String algorithm) throws NoSuchAlgorithmException {
        digest = MessageDigest.getInstance(algorithm);
        this.algorithm = algorithm;
    }

    public void setHash(String hash) {
        if (hash.length() != (digest.getDigestLength() * 2)) {
            this.hash = null;
            return;
        }
        this.hash = new byte[digest.getDigestLength()];
        for (int i = 0; i < digest.getDigestLength(); i += 2)
            this.hash[i] = Byte.decode("0x" + hash.substring(i * 2, i * 2 + 2));
    }

    public boolean checkFile(File file) {
        if (hash == null || file == null)
            return false;
        try {
            byte[] buffer = new byte[1024];
            digest.reset();
            int read;
            FileInputStream fis = new FileInputStream(file);
            while ((read = fis.read(buffer)) >= 0)
                digest.update(buffer, 0, read);
            return Arrays.equals(digest.digest(), hash);
        } catch (Exception ex) {
        }
        return false;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
