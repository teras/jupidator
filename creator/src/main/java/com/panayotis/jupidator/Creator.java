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
package com.panayotis.jupidator;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.panayotis.arjs.*;
import com.panayotis.jupidator.create.DiffCreator;
import com.panayotis.jupidator.create.XMLProducer;
import com.panayotis.jupidator.create.XMLSqueezer;
import com.panayotis.jupidator.parsables.HashFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

import com.panayotis.jupidator.create.Command;
import com.panayotis.jupidator.create.SnapshotCreator;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author teras
 */
public class Creator {

    private static final File NOFILE = new File("");

    /**
     * @param arguments the command line arguments
     */
    public static void main(String... arguments) {
        BoolArg snap = new BoolArg();
        BoolArg hash = new BoolArg();
        BoolArg create = new BoolArg();
        BoolArg squeeze = new BoolArg();
        StringArg arch = new StringArg(System.getProperty("os.arch"));
        StringArg version = new StringArg("1.0-SNAPSHOT");
        DecimalArg release = new DecimalArg(0);
        FileArg output = new FileArg();
        MultiStringArg ignore = new MultiStringArg();
        FileArg prev = new FileArg("");
        FileArg packfile = new FileArg("files");
        FileArg jupfile = new FileArg("jupidator.xml");
        BoolExclusiveArg md5 = new BoolExclusiveArg(true);
        BoolExclusiveArg sha1 = new BoolExclusiveArg(false);
        BoolExclusiveArg sha256 = new BoolExclusiveArg(false);
        BoolArg skipfiles = new BoolArg();
        Args args = new Args();
        args
                .def("snap", snap)
                .def("hash", hash)
                .def("create", create)
                .def("squeeze", squeeze)
                .def("-o", output)
                .def("-a", arch)
                .def("-p", prev)
                .def("-f", packfile)
                .def("-v", version)
                .def("-r", release)
                .def("-j", jupfile)
                .def("-i", ignore)
                .def("--no-md5", md5.getInverse())
                .def("--with-md5", md5)
                .def("--no-sha1", sha1.getInverse())
                .def("--with-sha1", sha1)
                .def("--no-sha256", sha256.getInverse())
                .def("--with-sha256", sha256)
                .def("--skip-files", skipfiles)
                .defhelp("-h", "--help")
                .alias("snap", "snapshot")
                .alias("-o", "--output")
                .alias("-a", "--arch")
                .alias("-p", "--prev")
                .alias("-f", "--files")
                .alias("-v", "--version")
                .alias("-r", "--release")
                .alias("-j", "--jupfile")
                .alias("-i", "--ignore")
                .dep("-p", "create")
                .dep("-f", "create", "squeeze")
                .dep("--skip-files", "create")
                .dep("-v", "snap", "create", "squeeze")
                .dep("-j", "snap", "create", "squeeze")
                .dep("--no-md5", "snap", "create")
                .dep("--no-sha1", "snap", "create")
                .dep("--no-sha256", "snap", "create")
                .dep("--with-md5", "snap", "create")
                .dep("--with-sha1", "snap", "create")
                .dep("--with-sha256", "snap", "create")
                .dep("-o", "snap", "hash", "create")
                .dep("-a", "snap", "hash", "create")
                .dep("-i", "snap")
                .req("snap", "hash", "create", "squeeze")
                .req("-p")
                .uniq("snap", "hash", "create", "squeeze")
                .uniq("--no-md5", "--with-md5")
                .uniq("--no-sha1", "--with-sha1")
                .uniq("--no-sha256", "--with-sha256")
                .usage("jupidator_creator", "hash", "-o", "-a", "INSTALL_DIR")
                .usage("jupidator_creator", "snap", "-v", "-r", "-j", "-o", "-a", "-i", "INSTALL_DIR")
                .usage("jupidator_creator", "create", "-p", "-v", "-r", "-j", "-f", "-o", "-a", "INSTALL_DIR")
                .usage("jupidator_creator", "squeeze", "-j", "-v", "-f")
                .group("Read existing installation and create a hash of all files", "hash")
                .group("Create a full snapshot of current installation", "snap", "-i", "-v", "-r", "--with-md5", "--no-md5", "--with-sha1", "--no-sha1", "--with-sha256", "--no-sha256")
                .group("Create jupidator files", "create", "-p", "-f", "--skip-files", "-j", "-v", "-r", "--with-md5", "--no-md5", "--with-sha1", "--no-sha1", "--with-sha256", "--no-sha256")
                .group("Squeeze Jupidator file", "squeeze", "-j", "-v")
                .info("snap", "visit INSTALL_DIR and create a snapshot of the provided directory INSTALL_DIR, to use as a 'snapshot update' with jupidator.")
                .info("hash", "visit INSTALL_DIR and create fingerprints of the directory structure and files.")
                .info("-o", "output resulting hashing information to a file for future use or comparison, instead of standard output.", "FILE")
                .info("-a", "the name of the architecture. If missing the default architecture is used.")
                .info("create", "create an installation bundle, based on a previous installation (given by --prev) and the current installation (given by INSTALL_DIR).")
                .info("-p", "the file with the hashing information of the previous installation.")
                .info("-f", "where the compressed package files will be stored; defaults to \"files\".")
                .info("--skip-files", "skip creation of files, if specific files already exist.")
                .info("-v", "the version of the produced application. Will be used to organize downloaded files.")
                .info("-r", "the release number of the produced application. Will be used to organize downloaded files.")
                .info("-j", "use this jupidator update file to append the update information. Defaults to jupidator.xml.")
                .info("--no-md5", "disable the usage of md5 hashing algorithm.")
                .info("--no-sha1", "disable the usage of sha1 hashing algorithm.")
                .info("--no-sha256", "disable the usage of sha256 hashing algorithm.")
                .info("--with-md5", "enable the usage of md5 hashing algorithm (enabled by default).")
                .info("--with-sha1", "enable the usage of sha1 hashing algorithm (disabled by default).")
                .info("--with-sha256", "enable the usage of sha256 hashing algorithm (disabled by default).")
                .info("squeeze", "squeeze architects of jupidator update file and support the 'all' argument. Note that this is an irreversible procedure.")
                .info("-i", "relative path of items to ignore", "IGNORE_PATH")
                .setCondensed('-')
                .setJoined('=')
                .error(err -> {
                    System.err.println("Error while executing Jupidator Creator: " + err);
                    System.err.println();
                    System.err.print(args.getUsage());
                    System.exit(-1);
                });
        List<String> freeArgs = args.parse(arguments);
        if (squeeze.get()) {
            if (!freeArgs.isEmpty())
                throw new JupidatorCreatorException("No free parameters are expected, " + freeArgs.size() + " found");
            squeeze(jupfile.get(), packfile.get(), version.get());
        } else {
            if (freeArgs.size() != 1)
                throw new JupidatorCreatorException("Exactly one free parameter is required, " + freeArgs.size() + " found");
            File in = new File(freeArgs.get(0));

            if (snap.get())
                snapshot(in, output.get(), arch.get(), version.get(), release.get().longValue(), jupfile.get(), md5.getInverse().get(), sha1.getInverse().get(), sha256.getInverse().get(), ignore.get());
            else if (hash.get())
                hash(in, output.get(), arch.get());
            else if (create.get())
                create(prev.get(), in, output.get(), packfile.get(), arch.get(), version.get(), release.get().longValue(), jupfile.get(), md5.getInverse().get(), sha1.getInverse().get(), sha256.getInverse().get(), skipfiles.get());
        }
    }

    private static void snapshot(File input, File output, String arch, String version, long release, File jupfile, boolean nomd5, boolean nosha1, boolean nosha256, List<String> ignore) {
        HashFolder hashFolder = hash(input, NOFILE, arch);
        Collection<Command> diffs = SnapshotCreator.create(hashFolder, input, output, version, arch, nomd5, nosha1, nosha256, ignore);
        XMLProducer.produce(jupfile, arch, version, release, diffs, true);
    }

    private static HashFolder hash(File input, File output, String arch) {
        HashFolder result = new HashFolder(input);
        JsonObject obj;
        if (output != null && output.isFile())
            try {
                obj = Json.parse(new String(Files.readAllBytes(output.toPath()), "UTF-8")).asObject();
            } catch (IOException ex) {
                throw new JupidatorCreatorException("Unable to parse output file " + output);
            }
        else
            obj = new JsonObject();

        obj.add(arch, result.toJSON());
        if (output == NOFILE) {
        } else if (output != null)
            try {
                Files.write(output.toPath(), obj.toString().getBytes());
            } catch (IOException ex) {
                throw new JupidatorCreatorException("Unable to to write to output file " + output);
            }
        else
            System.out.println(obj);
        return result;
    }

    private static void create(File previous, File input, File output, File packages, String arch, String version, long release, File jupfile, boolean nomd5, boolean nosha1, boolean nosha256, boolean skipfiles) {
        HashFolder older;
        try {
            JsonObject obj = Json.parse(new String(Files.readAllBytes(previous.toPath()), "UTF-8")).asObject();
            if (obj.get(arch) == null)
                throw new JupidatorCreatorException("Unable to find architecture " + arch + " in previous configurations at " + previous.getPath());
            obj = obj.get(arch).asObject();
            older = new HashFolder(obj);
        } catch (IOException ex) {
            throw new JupidatorCreatorException("Unable to read previous installation file '" + previous + "'");
        }
        HashFolder current = hash(input, output == null ? NOFILE : output, arch);
        Collection<Command> diffs = DiffCreator.create(older, current, input, packages, version, arch, nomd5, nosha1, nosha256, skipfiles);
        XMLProducer.produce(jupfile, arch, version, release, diffs, false);
    }

    private static void squeeze(File jupidator, File files, String version) {
        XMLSqueezer.squeeze(jupidator, files, version);
    }
}
