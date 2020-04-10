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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mcmiddleearth.resourcepackmanager.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;

/**
 *
 * @author Eriol_Eandur
 */
public class ResourcePack {
    
    @Getter
    private final Path baseFolder;
    
    private final Path defaultBaseFolder;
    private final String internalBaseFolder = "/1.13.2";
    
    private boolean minifyJson;
    
    private boolean cleanUpBlockStates;
    private boolean cleanUpModels;
    private boolean cleanUpTextures;
    
    private final Gson gson;
    
    public ResourcePack(Path baseFolder, Path defaultBaseFolder) {
        this.defaultBaseFolder = defaultBaseFolder;
        this.baseFolder = baseFolder;
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (false) gsonBuilder.setPrettyPrinting();
        this.gson = gsonBuilder.create();
        //System.out.println("RP internal: "+getClass().getResource(internalBaseFolder).toString());
        //System.out.println("stop");
    }
    
    public BlockModel[] getModels(String material, BlockState blockState) {
        //String material = blockState.getMaterial();
        Path file = baseFolder.resolve(getBlockStatePath(material.toLowerCase()));
        //System.out.println(file.toAbsolutePath().toString());
        if(file.toFile().exists()) {
            //custom blockstate file
            return readModels(blockState, file);
        } else {
            //default blockstate file
            blockState.setDefaultBlockState(true);
            if(defaultBaseFolder.toFile().exists()) {
                file = defaultBaseFolder.resolve(getBlockStatePath(material.toLowerCase()));
                if(file.toFile().exists()) {
                    return readModels(blockState, file);
                } else {
                    return new BlockModel[]{new BlockModel(null, "ERROR: Missing blockstate file in exteral default pack!!!")};
                }
            } else {
                InputStream in = getClass().getResourceAsStream(internalBaseFolder
                                                                 +"/assets/minecraft/blockstates/"
                                                                 +material.toLowerCase()+".json");
                if(in==null) {
                    return new BlockModel[]{new BlockModel(null,"ERROR: Missing blockstate file in internal default pack!!!")};
                }
                return readModels(blockState, new BufferedReader(new InputStreamReader(in)));
            }
        }
    }
    
    private BlockModel[] readModels(BlockState blockState, Path file) {
        JsonObject json = Util.readJson(gson, file.toFile());
        return readModels(blockState, json);
    }
    
    private BlockModel[] readModels(BlockState blockState, Reader reader) {
        JsonObject json = Util.readJson(gson, reader);
        return readModels(blockState, json);
    }
    
    private BlockModel[] readModels(BlockState blockState, JsonObject json) {
        JsonObject variantsObject = json.getAsJsonObject("variants");
        if (variantsObject != null) {
            search:
                for(Entry<String,JsonElement> entry: variantsObject.entrySet()) {
                    if(!entry.getKey().equals("")) {
                        String[] attributes = entry.getKey().split(",");
                        for(String attribute: attributes) {
                            String[] data = attribute.split("=");
                            String blockStateValue = blockState.getAttribute(data[0]);
                            if(blockStateValue==null || !blockStateValue.equalsIgnoreCase(data[1])) {
                                continue search;
                            }
                        }
                    }
                    return getModels(entry.getValue());
                }
            return new BlockModel[]{new BlockModel(null,"No model defined!!!")};
        } else {
            //multipart blockstate file
            JsonArray multipartArray = json.getAsJsonArray("multipart");
            if(multipartArray!=null) {
                Set<BlockModel> models = new HashSet<>();
                multipart:
                    for(JsonElement multipartElement: multipartArray) {
                        JsonObject condition = multipartElement.getAsJsonObject().getAsJsonObject("when");
                        JsonElement model = multipartElement.getAsJsonObject().get("apply");
                        if(condition==null) {
                            models.addAll(Arrays.asList(getModels(model)));
                        } else {
                            JsonArray orArray = condition.getAsJsonObject().getAsJsonArray("OR");
                            if(orArray!=null) {
                                for(JsonElement orElement: orArray) {
                                    if(matches(orElement.getAsJsonObject(),blockState)) {
                                        models.addAll(Arrays.asList(getModels(model)));
                                        continue multipart;
                                    }
                                }
                            } else {
                                if(matches(condition, blockState)) {
                                    models.addAll(Arrays.asList(getModels(model)));
                                }
                            }
                        }
                    }
                if(models.size()<1) {
                    return new BlockModel[]{new BlockModel(null,"No multipart model defined!!!")};
                } else {
                    return models.toArray(new BlockModel[0]);
                }
            }else {
                return new BlockModel[]{new BlockModel(null,"Invalid blockstate file!!!")};
            }
        }
    }

    private boolean matches(JsonObject condition, BlockState blockState) {
        for(Entry<String,JsonElement> entry: condition.entrySet()) {
            String blockStateValue = blockState.getAttribute(entry.getKey());
            //System.out.println(entry.getKey());
            if(!blockStateValue.equalsIgnoreCase(entry.getValue().getAsString())) {
                return false;
            }
        }
        return true;
    }
    
    private BlockModel[] getModels(JsonElement element) {
        if(element instanceof JsonObject) {
            return new BlockModel[]{new BlockModel(this,
                    element.getAsJsonObject()
                         .getAsJsonPrimitive("model").getAsString())};
        } else {
            JsonArray alternateModels = element.getAsJsonArray();
            BlockModel[] models = new BlockModel[alternateModels.size()];
            for(int i=0; i<models.length;i++) {
                JsonObject model=alternateModels.get(i).getAsJsonObject();
                models[i] = new BlockModel(this,
                        model.getAsJsonPrimitive("model").getAsString());
            }
            return models;
        }
    }

    public Texture[] getTextures(BlockModel model) {
        Path file = baseFolder.resolve(getBlockModelPath(model.getModelFile()));
        //System.out.println(file.toAbsolutePath().toString());
        if(file.toFile().exists()) {
            //custom model
            model.setDefaultModel(false);
            return readTextures(file);
        } else {
            //default model
            if(defaultBaseFolder.toFile().exists()) {
                file = defaultBaseFolder.resolve(getBlockModelPath(model.getModelFile()));
                if(file.toFile().exists()) {
                    return readTextures(file);
                } else {
                    return new Texture[]{new Texture("ERROR: Missing model file in external default pack!!!")};
                }
            } else {
                InputStream in = getClass().getResourceAsStream(internalBaseFolder
                                                                 +"/assets/minecraft/models/"
                                                                 +model.getModelFile()+".json");
                if(in==null) {
                    return new Texture[]{new Texture("ERROR: Missing blockstate file in internal default pack!!!")};
                }
                return readTextures(new BufferedReader(new InputStreamReader(in)));
            }
        }
    }
    
    private Texture[] readTextures(Path file) {
        JsonObject json = Util.readJson(gson, file.toFile());
        return readTextures(json);
    }
    
    private Texture[] readTextures(Reader reader) {
        JsonObject json = Util.readJson(gson, reader);
        return readTextures(json);
    }
    
    private Texture[] readTextures(JsonObject json) {
        JsonObject texturesObject = json.getAsJsonObject("textures");
        if (texturesObject != null) {
            Texture[] textures = new Texture[texturesObject.size()];
            //TODO getting textures from parent models
            int i=0;
            for(Entry<String,JsonElement> entry: texturesObject.entrySet()) {
                textures[i] = new Texture(entry.getValue().getAsString());
                textures[i].setDefaultTexture(checkForDefaultTexture(textures[i]));
                i++;
            }
            return removeDublicateTextures(textures);
        }
        return new Texture[]{new Texture("No textures!!")};
    }
    
    private boolean checkForDefaultTexture(Texture texture) {
        Path file = baseFolder.resolve(Paths.get("assets","minecraft","textures",texture.getTextureFile()+".png"));
        return !file.toFile().exists();
    }
    
    public URL getTextureURL(String fileName) {
        try {
            if(fileName.contains("!")) {
                //return Paths.get("");
                return new URL("file","","");
            }
            Path file = baseFolder.resolve(getTexturePath(fileName.toLowerCase()));
            //System.out.println(file.toAbsolutePath().toString());
            if(file.toFile().exists()) {
                //custom blockstate file
                return new URL("file","",file.toString());
            } else {
                //default blockstate file
                if(defaultBaseFolder.toFile().exists()) {
                    return new URL("file","",defaultBaseFolder.resolve(getTexturePath(fileName.toLowerCase())).toString());
                } else {
                        URL url = getClass().getResource(internalBaseFolder+"/assets/minecraft/textures/"+fileName.toLowerCase()+".png");
                        if(url!=null) {
                            return url;//new File(url.getFile());//Paths.get(url.toURI());
                        }
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(ResourcePack.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void saveTo(File newFolder) {
        
    }
    
    public ResourcePack withMinifyJson(boolean minifyJson) {
        this.minifyJson = minifyJson;
        return this;
    }
           
    public ResourcePack withCleanUpBlockStates(boolean cleanUpBlockStates) {
        this.cleanUpBlockStates = cleanUpBlockStates;
        return this;
    }
           
    public ResourcePack witthCleanUpModels(boolean cleanUpModels) {
        this.cleanUpModels = cleanUpModels;
        return this;
    }
           
    public ResourcePack withCleanUpTextures(boolean cleanUpTextures) {
        this.cleanUpTextures = cleanUpTextures;
        return this;
    }
    
    private Texture[] removeDublicateTextures(Texture[] textures) {
        List<Texture> temp = new ArrayList<>();
        for(Texture texture : textures) {
            if(!temp.contains(texture)) {
                temp.add(texture);
            }
        }
        return temp.toArray(new Texture[0]);
    }
           
    public String getBlockStateFileContent(String material) {
        Path file = baseFolder.resolve(getBlockStatePath(material.toLowerCase()));
        //System.out.println(file.toAbsolutePath().toString());
        if(file.toFile().exists()) {
            return Util.readTextFile(file);
        } else {
            //default blockstate file
            if(defaultBaseFolder.toFile().exists()) {
                file = defaultBaseFolder.resolve(getBlockStatePath(material.toLowerCase()));
                if(file.toFile().exists()) {
                    return Util.readTextFile(file);
                } else {
                    return "ERROR: Missing blockstate file in exteral default pack!!!";
                }
            } else {
                try(InputStream in = getClass().getResourceAsStream(internalBaseFolder
                                                                 +"/assets/minecraft/blockstates/"
                                                                 +material.toLowerCase()+".json")) {
                    if(in==null) {
                        return "ERROR: Missing blockstate file in internal default pack!!!";
                    }
                    return Util.readTextFile(in);
                } catch (IOException ex) {
                    Logger.getLogger(ResourcePack.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return "";
    }
    
    public String getModelFileContent(String modelFile) {
        Path file = baseFolder.resolve(getBlockModelPath(modelFile));
        //System.out.println(file.toAbsolutePath().toString());
        if(file.toFile().exists()) {
            //custom model
            return Util.readTextFile(file);
        } else {
            //default model
            if(defaultBaseFolder.toFile().exists()) {
                file = defaultBaseFolder.resolve(getBlockModelPath(modelFile));
                if(file.toFile().exists()) {
                    return Util.readTextFile(file);
                } else {
                    return "ERROR: Missing model file in external default pack!!!";
                }
            } else {
                try(InputStream in = getClass().getResourceAsStream(internalBaseFolder
                                                                 +"/assets/minecraft/models/"
                                                                 +modelFile+".json")) {
                    if(in==null) {
                        return "ERROR: Missing blockstate file in internal default pack!!!";
                    }
                    return Util.readTextFile(in);
                } catch (IOException ex) {
                    Logger.getLogger(ResourcePack.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return "";
    }
    
    public Path getBlockStatePath(String filename) {
        return Paths.get("assets","minecraft","blockstates",filename+".json");
    }
    public Path getBlockModelPath(String filename) {
        return Paths.get("assets","minecraft","models",filename+".json");
    }
    
    public Path getTexturePath(String filename) {
        return Paths.get("assets","minecraft","textures",filename+".png");
    }
}
