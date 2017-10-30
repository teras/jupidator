/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.jupidator.create;

import com.panayotis.jupidator.JupidatorCreatorException;
import com.panayotis.jupidator.compress.BZip2FileCompression;
import com.panayotis.jupidator.compress.NoCompression;
import com.panayotis.jupidator.compress.TarBz2FolderCompression;
import com.panayotis.jupidator.digester.Digester;
import com.panayotis.jupidator.digester.fileperms.FindPermissions;
import com.panayotis.jupidator.parsables.ParseItem;
import java.io.File;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Collection;
import static java.nio.file.attribute.PosixFilePermission.*;
import java.util.Arrays;

/**
 *
 * @author teras
 */
public class CommandCreator {

    private static final Collection<PosixFilePermission> defaultPerms = Arrays.asList(OWNER_READ, OWNER_WRITE, GROUP_READ, OTHERS_READ);

    private final Collection<Command> rmCommands = new ArrayList<>();
    private final Collection<Command> fileCommands = new ArrayList<>();
    private final String version;
    private final String arch;
    private final File inputRoot;
    private final File output;
    private final boolean nomd5;
    private final boolean nosha1;
    private final boolean nosha256;
    private final boolean skipfiles;

    protected CommandCreator(File inputRoot, File output, String version, String arch, boolean nomd5, boolean nosha1, boolean nosha256, boolean skipfiles) {
        this.version = version;
        this.inputRoot = inputRoot;
        this.output = output;
        this.nomd5 = nomd5;
        this.nosha1 = nosha1;
        this.nosha256 = nosha256;
        this.arch = arch;
        this.skipfiles = skipfiles;
    }

    public Collection<Command> getCommands() {
        Collection<Command> commands = new ArrayList<>(rmCommands);
        commands.addAll(fileCommands);
        return fileCommands;
    }

    protected void rm(ParseItem item, String path) {
        rmCommands.add(new RmCommand(path + item.name));
    }

    protected void file(ParseItem item, String path) {
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        path = path.isEmpty() ? "" : "/" + path;
        String srcprefix = version + "/" + arch;
        String destprefix = "${APPHOME}";

        File infile = new File(inputRoot, path + File.separator + item.name).getAbsoluteFile();
        boolean smallFile = infile.length() < 20;
        String ext = infile.isDirectory() ? "tar.bz2" : smallFile ? "" : "bz2";
        File outfile = new File(output, srcprefix + path + "/" + item.name + (smallFile ? "" : "." + ext)).getAbsoluteFile();
        outfile.getParentFile().mkdirs();
        if (!(skipfiles && outfile.exists())) {
            Exception ex;
            if (infile.isDirectory())
                ex = TarBz2FolderCompression.compress(infile, outfile);
            else if (smallFile)
                ex = NoCompression.compress(infile, outfile);
            else
                ex = BZip2FileCompression.compress(infile, outfile);
            if (ex != null)
                throw new JupidatorCreatorException("Unable to create output file " + outfile.getAbsolutePath(), ex);
        }

        FileCommand file = new FileCommand(ext, destprefix + path, item.name, infile.length(), outfile.length(), srcprefix + path);
        if (!nomd5) {
            file.setLocalMD5(Digester.getDigester("MD5").setHash(infile).toString());
            file.setRemoteMD5(Digester.getDigester("MD5").setHash(outfile).toString());
        }
        if (!nosha1) {
            file.setLocalSHA1(Digester.getDigester("SHA1").setHash(infile).toString());
            file.setRemoteSHA1(Digester.getDigester("SHA1").setHash(outfile).toString());
        }
        if (!nosha256) {
            file.setLocalSHA256(Digester.getDigester("SHA-256").setHash(infile).toString());
            file.setRemoteSHA256(Digester.getDigester("SHA-256").setHash(outfile).toString());
        }
        fileCommands.add(file);

        String perms = FindPermissions.getPerms(infile);
        if (perms != null && !perms.equals("644"))
            fileCommands.add(new ChmodCommand(path, perms));
    }
}
