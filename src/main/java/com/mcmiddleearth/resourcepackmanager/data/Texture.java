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

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Eriol_Eandur
 */
public class Texture {
    
    @Setter
    @Getter
    private boolean defaultTexture;
    
    @Getter
    private final String textureFile;
    
    public Texture(String textureFile) {
        this.textureFile = textureFile;
    }
    
    @Override
    public String toString() {
        return textureFile;
    }
    
    @Override
    public boolean equals(Object other) {
        if(other instanceof Texture) {
            return textureFile.equals(((Texture)other).getTextureFile());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.textureFile);
        return hash;
    }

    
}
