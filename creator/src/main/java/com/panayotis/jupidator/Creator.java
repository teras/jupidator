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

    /**
     * @param arguments the command line arguments
     */
    public static void main(String... arguments) {
//        arguments = new String[]{"-h"};
        arguments = new String[]{"parse", "/Applications/CrossMobile.app", "-o", "koko"};

        BoolArg parse = new BoolArg();
        StringArg output = new StringArg("");
        StringArg arch = new StringArg(System.getProperty("os.arch"));
        Args args = new Args();
        args
                .def("parse", parse)
                .def("-o", output)
                .def("-a", arch)
                .defhelp("-h", "--help")
                .alias("-o", "--output")
                .alias("-a", "--arch")
                .req("parse")
                .info("parse", "parse INSTALL_DIR and create fingerprints of the directory structure and files")
                .info("-o", "output result to a file, instead of standard output", "FILE")
                .info("-a", "the name of the architecture. If missing the default architecture is used")
                .usage("jupidator_creator", "parse", "-o", "-a", "INSTALL_DIR")
                .error(err -> {
                    System.err.println("Error while executing Jupidator Creator: " + err);
                    System.err.println();
                    System.err.print(args.toString());
                    System.exit(-1);
                });
        List<String> freeArgs = args.parse(arguments);
        if (parse.get()) {
            if (freeArgs.size() != 1)
                throw new ArgumentException("Exactly one free parameter is required, " + freeArgs.size() + " found");
            parse(new File(freeArgs.get(0)), output.get().isEmpty() ? null : new File(output.get()), arch.get());
        }
    }

    private static void parse(File input, File output, String arch) {
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
        if (output != null)
            try {
                Files.write(output.toPath(), obj.toString().getBytes());
            } catch (IOException ex) {
                throw new JupidatorCreatorException("Unable to to write to output file " + output);
            }
        else
            System.out.println(obj);
    }
}
