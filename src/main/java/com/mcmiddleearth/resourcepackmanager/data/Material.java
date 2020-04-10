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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;

/**
 *
 * @author Eriol_Eandur
 */
public class Material implements ResourcePackFile{

    @Getter
    private final List<BlockState> blockStates = new ArrayList<>();
    
    @Getter
    private final String name;
    
    private ResourcePack rp;
    
    public Material(ResourcePack rp, String name) {
        this.rp = rp;
        this.name = name;
    }
    
    public void add(BlockState state) {
        blockStates.add(state);
    }
    
    public boolean isSingleState() {
        return blockStates.size() < 2;
    }
    
    public boolean hasDefaultTexture() {
        for(BlockState state: blockStates) {
            if(state.hasDefaultTexture()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public OutputStream getOutputStream() {
        try {
            Path path = rp.getBaseFolder().resolve(rp.getBlockStatePath(name.toLowerCase()));
            if(!path.toFile().exists()) {
                path.toFile().createNewFile();
            }
            if(path.toFile().exists()) {
                return new FileOutputStream(path.toFile());
            }
        } catch (IOException ex) {
            Logger.getLogger(BlockState.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public boolean isDefault() {
        return blockStates.get(0).isDefault();
    }
    
    public BlockState getBlockState(String attributes) {
        for(BlockState state: blockStates) {
            if(state.getAttributes().equals(attributes)) {
                return state;
            }
        }
        return null;
    }

}
