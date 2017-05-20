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

import com.panayotis.argparse.StringArg;
import com.panayotis.argparse.Args;
import com.panayotis.argparse.ArgumentException;
import com.panayotis.argparse.BoolArg;
import com.panayotis.jupidator.diff.Diff;
import com.panayotis.jupidator.parsables.ParseFolder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author teras
 */
public class Creator {

    private static final File NULL = new File("");

    /**
     * @param arguments the command line arguments
     */
    public static void main(String... arguments) {
//        arguments = new String[]{"-h"};
//        arguments = new String[]{"parse", "-o", "crossmobile_prev.json", "-a", "osx", "/Users/teras/Desktop/CrossMobile_prev.app/Contents/Java"};
        arguments = new String[]{"create", "-p", "crossmobile_prev.json", "-o", "crossmobile_now.json", "-a", "osx", "/Users/teras/Desktop/CrossMobile.app/Contents/Java"};

        BoolArg parse = new BoolArg();
        BoolArg create = new BoolArg();
        StringArg output = new StringArg("");
        StringArg arch = new StringArg(System.getProperty("os.arch"));
        StringArg prev = new StringArg("");
        StringArg packfile = new StringArg("files");
        StringArg version = new StringArg("1.0-SNAPSHOT");
        Args args = new Args();
        args
                .def("parse", parse)
                .def("create", create)
                .def("-o", output)
                .def("-a", arch)
                .def("-p", prev)
                .def("-f", packfile)
                .def("-v", version)
                .defhelp("-h", "--help")
                .alias("-o", "--output")
                .alias("-a", "--arch")
                .alias("-p", "--prev")
                .alias("-f", "--files")
                .alias("-v", "--version")
                .dep("-p", "create")
                .dep("-f", "create")
                .dep("-v", "create")
                .req("parse", "create")
                .req("-p")
                .uniq("parse", "create")
                .usage("jupidator_creator", "parse", "-o", "-a", "INSTALL_DIR")
                .usage("jupidator_creator", "create", "-p", "-f", "-o", "-a", "INSTALL_DIR")
                .group("Parse existing installation", "parse")
                .group("Create jupidator files", "create", "-p", "-f", "-v")
                .info("parse", "parse INSTALL_DIR and create fingerprints of the directory structure and files.")
                .info("-o", "output result to a file, instead of standard output.", "FILE")
                .info("-a", "the name of the architecture. If missing the default architecture is used.")
                .info("create", "create an installation bundle, based on a previous installation (given by --prev) and the current installation (given by INSTALL_DIR).")
                .info("-p", "the data of the previous installation.")
                .info("-f", "where the compressed package files will be stored; defaults to \"files\".")
                .info("-v", "the version of the produced application. Will be used to locate fiels on server.")
                .error(err -> {
                    System.err.println("Error while executing Jupidator Creator: " + err);
                    System.err.println();
                    System.err.print(args.toString());
                    System.exit(-1);
                });
        List<String> freeArgs = args.parse(arguments);
        if (freeArgs.size() != 1)
            throw new ArgumentException("Exactly one free parameter is required, " + freeArgs.size() + " found");
        File in = new File(freeArgs.get(0));
        File out = output.get().isEmpty() ? null : new File(output.get());

        if (parse.get())
            parse(in, out, arch.get());
        else if (create.get())
            create(new File(prev.get()), in, out, new File(packfile.get()), arch.get(), version.get());
    }

    private static ParseFolder parse(File input, File output, String arch) {
        ParseFolder result = new ParseFolder(input);
        JSONObject obj;
        if (output != null && output.isFile())
            try {
                obj = new JSONObject(new String(Files.readAllBytes(output.toPath()), "UTF-8"));
            } catch (IOException ex) {
                throw new JupidatorCreatorException("Unable to parse output file " + output);
            }
        else
            obj = new JSONObject();

        obj.put(arch, result.toJSON());
        if (output == NULL) {
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

    private static void create(File previous, File input, File output, File packages, String arch, String version) {
        ParseFolder older;
        try {
            JSONObject obj = new JSONObject(new String(Files.readAllBytes(previous.toPath()), "UTF-8"));
            obj = obj.optJSONObject(arch);
            if ((obj == null))
                throw new JupidatorCreatorException("Unable to find architecture " + arch + " in previous configurations at " + previous.getPath());
            older = new ParseFolder(obj);
        } catch (IOException ex) {
            throw new JupidatorCreatorException("Unable to read previous installation file '" + previous + "'");
        }
        ParseFolder current = parse(input, output == null ? NULL : output, arch);
        System.out.println(Diff.diff(older, current, input, packages, version).toString());
    }
}
