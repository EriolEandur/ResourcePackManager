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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.mcmiddleearth.resourcepackmanager.data.ResourcePack;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class Util {
    
    public static JsonObject readJson(Gson gson, File file) {
        try(FileReader fr = new FileReader(file) ) {
            return readJson(gson, fr);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResourcePack.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ResourcePack.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static JsonObject readJson(Gson gson, Reader reader) {
        return gson.fromJson(new JsonReader(reader), JsonObject.class);
    }

    public static String readTextFile(Path path) {
        try(FileInputStream in = new FileInputStream(path.toFile())) {
            return readTextFile(in);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResourcePack.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ResourcePack.class.getName()).log(Level.SEVERE, null, ex);
        }
    /*    byte[] encoded;
        try {
            encoded = Files.readAllBytes(path);
            return new String(encoded, Charset.defaultCharset());
        } catch (IOException ex) {
            Logger.getLogger(ResourcePack.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        return "";
    }
    
    public static String readTextFile(InputStream in) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String         line = null;
            StringBuilder  stringBuilder = new StringBuilder();
            String         ls = System.getProperty("line.separator");

            try {
                while((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(ls);
                }

                return stringBuilder.toString();
            } finally {
                reader.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ResourcePack.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
}
