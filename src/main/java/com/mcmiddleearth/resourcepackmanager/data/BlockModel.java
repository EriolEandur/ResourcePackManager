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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Eriol_Eandur
 */
public class BlockModel implements ResourcePackFile{
    
    @Setter
    private String name = "";
    
    @Getter
    private final String modelFile;
    
    @Setter
    private boolean defaultModel=true;
    
    @Setter
    @Getter
    private Texture[] textures;
    
    private final ResourcePack rp;
    
    public BlockModel(ResourcePack rp, String modelFile) {
        this.rp = rp;
        this.modelFile = modelFile;
        
    }
    
    public boolean isSingleTexture() {
        return textures.length < 2;
    }
            
    @Override
    public String toString() {
        return name + Formatting.attributeDelimiter + modelFile.substring(modelFile.lastIndexOf("/")+1);
    }
    
    public boolean hasDefaultTexture() {
        if(textures==null) {
            return true;
        } else {
            for(Texture texture: textures) {
                if(texture.isDefaultTexture()) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public boolean equals(Object other) {
        if(other instanceof BlockModel) {
            return modelFile.equals(((BlockModel)other).getModelFile());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.modelFile);
        return hash;
    }

    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public OutputStream getOutputStream() {
        try {
            Path path = rp.getBaseFolder().resolve(rp.getBlockModelPath(modelFile));
            System.out.println("searching: "+path.toString());
            if(!path.toFile().exists()) {
                path.toFile().createNewFile();
                System.out.println("Create new file: "+path.toString());
            }
            if(path.toFile().exists()) {
                System.out.println("creating output stream: "+path.toString());
                return new FileOutputStream(path.toFile());
            } else {
                System.out.println("creating output stream: "+path.toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(BlockState.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
            
    }
    
    @Override
    public boolean isDefault() {
        return defaultModel;
    }
}
