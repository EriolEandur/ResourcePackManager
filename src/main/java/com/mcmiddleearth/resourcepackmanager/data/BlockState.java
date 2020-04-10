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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Eriol_Eandur
 */
public class BlockState implements ResourcePackFile{
    
    @Setter
    private boolean defaultBlockState = false;
    
    @Getter
    @Setter
    private boolean urpsBlockState = false;
    
    @Setter
    @Getter
    private BlockModel[] models;
    
    @Getter
    private final String material;
    
    @Getter
    @Setter
    private String comment = "";
    
    private final Map<String,String> attributes = new LinkedHashMap<>();
    
    private ResourcePack rp;
    
    public BlockState(ResourcePack rp, String material, String attributeData) {
        this.rp = rp;
        this.material = material;
        //if(!attributeData.equals("")) {
            String[] rawData = attributeData.split(Formatting.attributeDelimiter);
            //material = rawData[0];
            for (String rawData1 : rawData) {
                String[] entry = rawData1.split(Formatting.valueDelimiter);
                if(entry.length>1) {
                    attributes.put(entry[0], entry[1]);
                } else if(entry.length == 1) {
                    if(entry[0].equalsIgnoreCase("urps")) {
                        urpsBlockState = true;
                    } else {
                        comment = comment + (comment.equals("")?"":"; ") +entry[0];
                    }
                }
            }
        //}
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
    
    public String getAttribute(String key) {
        return attributes.get(key);
    }
    
    public int countAttributes() {
        return attributes.size();
    }

    public String getAttributes() {
        String result = "";
        for(Entry entry: attributes.entrySet()) {
            result = result + (result.equals("")?"":Formatting.attributeDelimiter) 
                            + entry.getKey() + Formatting.valueDelimiter + entry.getValue();
        }
        return result;
    }
    
    public synchronized String toString(int maxAttributes, boolean onlyUrpsOutput) {
        String result = material;
        int attributesLeft = maxAttributes;
        for(Entry entry: attributes.entrySet()) {
            result = result + (result.equals("")?"":Formatting.attributeDelimiter) 
                            + entry.getKey() + Formatting.valueDelimiter + entry.getValue();
            attributesLeft--;
        }
        for(int i=0; i<attributesLeft; i++) {
            result = result + Formatting.attributeDelimiter;
        }
        if(urpsBlockState) {
            result = result + Formatting.attributeDelimiter + "URPS";
        }
        if(!onlyUrpsOutput || urpsBlockState) {
            result = result + Formatting.attributeDelimiter + comment;
        }
        
        /*for(BlockModel model: models) {
            result = result + Formatting.attributeDelimiter + model.toString();
            break;
        }
        if(models.length>1) {
            result = result + Formatting.attributeDelimiter + "alternate models!";
        }*/
        /*Texture[] textures = models[0].getTextures();
        if(textures == null || textures.length==0) {
            result = result + Formatting.attributeDelimiter + "No Texture found: "+models[0].toString();
        } else {
            if(!textures[0].isDefaultTexture()) {
                result = result + Formatting.attributeDelimiter + textures[0].toString();
            } else {
                result = result + Formatting.attributeDelimiter + "Default or Missing!!!";
            }
        }*/
        return result;
    }
    
    public boolean isSingleModel() {
        return models.length < 2;
    }
    
    public boolean hasDefaultTexture() {
        for(BlockModel model: models) {
            if(model.hasDefaultTexture()) {
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
            Path path = rp.getBaseFolder().resolve(rp.getBlockStatePath(material.toLowerCase()));
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
        return defaultBlockState;
    }

}
