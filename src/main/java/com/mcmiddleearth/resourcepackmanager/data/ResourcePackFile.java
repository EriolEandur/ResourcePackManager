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

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Eriol_Eandur
 */
public interface ResourcePackFile {
    
    /**
     * First tries to return the File from the current rp, then from the external default rp.
     * Fails for files from internal default pack or non existent files.
     * of if the file doesn't exist
     * @return File object or null if the file is internal default or doesn't exist
     */
    //public File getFile();
    
    /**
     * Get an input stream for this file either from current rp, external default rp or internal 
     * default rp.
     * @return input stream or null if file doesn't exist
     */
    public InputStream getInputStream();
    
    /**
     * Get and output stream for this file in current rp.
     * @return 
     */
    public OutputStream getOutputStream();
    
    public boolean isDefault();
    
}
