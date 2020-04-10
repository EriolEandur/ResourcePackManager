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
package com.mcmiddleearth.resourcepackmanager.data;

import com.mcmiddleearth.resourcepackmanager.Main;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;

/**
 *
 * @author Eriol_Eandur
 */
public class MaterialList {
    
    @Getter
    private final List<Material> materials = new ArrayList<>();
    
    @Getter
    private final ResourcePack rp;
    
    public MaterialList(Scanner internalBlockListScanner, ResourcePack rp) throws IOException {
        this.rp = rp;
        String lastMaterial = "";
        Material currentMaterial = null;
        internalBlockListScanner.useDelimiter(",");
        while(internalBlockListScanner.hasNext()) {
            String material = internalBlockListScanner.next();
            if(currentMaterial==null || !material.equals(lastMaterial)) {
                currentMaterial = new Material(rp, material);
                materials.add(currentMaterial);
                lastMaterial = material;
                Main.log("Reading default models: "+currentMaterial.getName());
            }
            BlockState newState = new BlockState(rp,currentMaterial.getName(),
                                                 internalBlockListScanner.nextLine().toLowerCase());
            newState.setModels(rp.getModels(currentMaterial.getName(),newState));
            for(BlockModel model: newState.getModels()) {
                if(!model.getModelFile().contains("!")) {
                    model.setTextures(rp.getTextures(model));
                }
            }
            currentMaterial.add(newState);
        }
        File file = new File("blockList.txt");
        if(file.exists()) {
            try(Scanner urpsBlockListScanner = new Scanner(file)) {
                urpsBlockListScanner.useDelimiter(",");
                while(urpsBlockListScanner.hasNext()) {
                    String material = urpsBlockListScanner.next();
                    if(currentMaterial==null || !material.equals(currentMaterial.getName())) {
                        currentMaterial = getMaterial(material);
                        Main.log("Reading URPS models: "+currentMaterial.getName());
                    }
                    if(currentMaterial!=null) {
                        BlockState newState = new BlockState(rp,currentMaterial.getName(),
                                                             urpsBlockListScanner.nextLine().toLowerCase());
                        if(newState.isUrpsBlockState()) {
                            BlockState existingState = currentMaterial.getBlockState(newState.getAttributes());
                            existingState.setUrpsBlockState(true);
                            existingState.setComment(newState.getComment());
                        }
                    }
                }
            }
        }
        file = rp.getBaseFolder().resolve("blockList.txt").toFile();
        if(file.exists()) {
            try(Scanner rpBlockListScanner = new Scanner(file)) {
                rpBlockListScanner.useDelimiter(",");
                while(rpBlockListScanner.hasNext()) {
                    String material = rpBlockListScanner.next();
                    if(currentMaterial==null || !material.equals(currentMaterial.getName())) {
                        currentMaterial = getMaterial(material);
                        Main.log("Reading RP models: "+currentMaterial.getName());
                    }
                    if(currentMaterial!=null) {
                        BlockState newState = new BlockState(rp,currentMaterial.getName(),
                                                             rpBlockListScanner.nextLine().toLowerCase());
                        BlockState existingState = currentMaterial.getBlockState(newState.getAttributes());
                        if(!existingState.isUrpsBlockState()) {
                            existingState.setComment(newState.getComment());
                        }
                    }
                }
            }
        }
    }
    
    public boolean saveToFile(Path output) {
        return saveToFile(output, true)
               && saveToFile(rp.getBaseFolder().resolve("blockList.txt"),false);
    }
    
    private boolean saveToFile(Path output, boolean onlyUrpsOutput) {
        if(!output.toFile().exists()) {
            try {
                if(!output.toFile().createNewFile()) {
                    return false;
                }
            } catch (IOException ex) {
                Logger.getLogger(MaterialList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try(Writer writer = new BufferedWriter(new FileWriter(output.toFile()))) {
            int maxAttrib = countMaxAttributes();
            for(Material material: materials) {
                for(BlockState state: material.getBlockStates()) {
                    writer.write(state.toString(maxAttrib,onlyUrpsOutput)+"\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MaterialList.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
        
    private int countMaxAttributes() {
        int maxAttributes = 0;
        for(Material material: materials) {
            for(BlockState state: material.getBlockStates()) {
                maxAttributes = Math.max(state.countAttributes(),maxAttributes);
            }
        }
        return maxAttributes;
    }
    
    private Material getMaterial(String name) {
        for(Material material: materials) {
            if(material.getName().equals(name)) {
                return material;
            }
        }
        return null;
    }
}
