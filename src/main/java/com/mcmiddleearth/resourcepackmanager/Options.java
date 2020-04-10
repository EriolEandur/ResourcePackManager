/*
 * Copyright (C) 2018 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.resourcepackmanager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.ValueConverter;

/**
 *
 * @author Eriol_Eandur
 */
public class Options {
 
    private static final OptionParser parser = new OptionParser();

    public static final OptionSpec<Void> help = parser.acceptsAll(Arrays.asList("?", "h", "help"), "Print this message.").forHelp();

    public static final OptionSpec<Path> blockStateFile = parser.acceptsAll(Arrays.asList("b", "bs", "blockState"), "File to read the available blockstates from")
            .withRequiredArg()
            .withValuesConvertedBy(new PathConverter())
            .defaultsTo(Paths.get("blockList.txt"));
    
    public static final OptionSpec<Path> logFile = parser.acceptsAll(Arrays.asList("l", "log"), "File to write debug output to")
            .withRequiredArg()
            .withValuesConvertedBy(new PathConverter())
            .defaultsTo(Paths.get("log.txt"));
    
    public static final OptionSpec<Path> rpDir = parser.acceptsAll(Arrays.asList("r", "rp", "resourcePack"), "Directory to read the models from")
            .withRequiredArg()
            .withValuesConvertedBy(new PathConverter())
            .defaultsTo(Paths.get("Gondor-2"));

    public static final OptionSpec<Path> defaultRpDir = parser.acceptsAll(Arrays.asList("d", "drp", "defaultResourcePack"), "Directory to read the default models from")
            .withRequiredArg()
            .withValuesConvertedBy(new PathConverter())
            .defaultsTo(Paths.get("1.13"));

    public static final OptionSpec<Path> outFile = parser.acceptsAll(Arrays.asList("o", "out"), "Output file for blockstates")
            .withRequiredArg()
            .withValuesConvertedBy(new PathConverter());

    public static final OptionSpec<Void> minify = parser.accepts("minify", "Minify the json files.");

    private final OptionSet options;
    
    public Options(String args[]) {
        options = parser.parse(args);
    }

    public boolean has(OptionSpec option) {
        return options.has(option);
    }
    
    public void printHelpMsg(OutputStream sink) throws IOException {
        parser.printHelpOn(sink);
    }
    
    public <V> V getValue(OptionSpec<V> option) {
        return options.valueOf(option);
    }
    
    public static class PathConverter implements ValueConverter<Path> {

        @Override
        public Path convert(String s) {
            return Paths.get(s);
        }

        @Override
        public Class<? extends Path> valueType() {
            return Path.class;
        }

        @Override
        public String valuePattern() {
            return "*";
        }
    }

}
