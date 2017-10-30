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
package com.panayotis.jupidator.digester;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
        this.hash = new byte[digest.getDigestLength() < (hash.length() / 2) ? digest.getDigestLength() : (hash.length() / 2)];
        for (int i = 0; i < this.hash.length; i++)
            this.hash[i] = (byte) Integer.parseInt(hash.substring(i * 2, i * 2 + 2), 16);
    }

    public Digester setHash(File file) {
        this.hash = getHash(file);
        return this;
    }

    public boolean checkFile(File file) {
        if (hash == null || file == null)
            return false;
        return Arrays.equals(getHash(file), hash);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    @SuppressWarnings("UseSpecificCatch")
    private byte[] getHash(File file) {
        FileInputStream fis = null;
        try {
            byte[] buffer = new byte[1024];
            digest.reset();
            int read;
            fis = new FileInputStream(file);
            while ((read = fis.read(buffer)) >= 0)
                digest.update(buffer, 0, read);
            return digest.digest();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException ex) {
                }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            String part = Integer.toHexString(Byte.toUnsignedInt(hash[i]));
            out.append(part.length() < 2 ? "0" : "").append(part);
        }
        return out.toString();
    }

}
