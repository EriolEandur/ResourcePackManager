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
package com.mcmiddleearth.resourcepackmanager.gui;

import com.mcmiddleearth.resourcepackmanager.data.BlockModel;
import com.mcmiddleearth.resourcepackmanager.data.BlockState;
import com.mcmiddleearth.resourcepackmanager.data.Material;
import com.mcmiddleearth.resourcepackmanager.data.Texture;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Eriol_Eandur
 */
public class RPTreeCellRenderer extends DefaultTreeCellRenderer{

    //private ResourcePack rp;
    
    public RPTreeCellRenderer() {
        //this.rp = rp;
    }

    @Override
    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
        setOpaque(true);
        setForeground(Color.BLACK);
        setBackground(Color.WHITE);
        Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
        if(userObject instanceof RPTreeContent) {
            RPTreeContent content = (RPTreeContent) userObject;
            if(content.getIcon() != null) {
                setIcon(content.getIcon());
            }
            if(content.getData() instanceof Material) {
                Material material = (Material)content.getData();
                setText(material.getName());
                //BlockModel[] models = material.getBlockStates().get(0).getModels();
                //Texture[] textures = models[0].getTextures();
                setBackgroundColor(material.hasDefaultTexture(),hasFocus,false);
            } else if(content.getData() instanceof BlockState) {
                BlockState state = (BlockState) content.getData();
                setText((state.getAttributes().equals("")?"normal":state.getAttributes())
                          +(state.getComment().equals("")?"":" <<<--- "+state.getComment()));
                setBackgroundColor(state.hasDefaultTexture(),hasFocus, state.isUrpsBlockState());
                /*if(!state.isSingleModel()) {
                    setText("S "+state.getAttributes()+" -> Multimodel!");
                } else {
                    setText("S "+state.getAttributes()+" -> "+state.getModels()[0].getModelFile());
                }*/
            } else if(content.getData() instanceof BlockModel) {
                BlockModel model = (BlockModel) content.getData();
                setIcon(null);
                setText("Model file: " + model.getModelFile());
                setBackgroundColor((model.hasDefaultTexture()),hasFocus,false);
                /*if(!model.isSingleTexture()) {
                    setText("M "+ "-> Multitexture!");
                } else {
                    setText("M "+model.getTextures()[0].getTextureFile());
                }*/
            } else if(content.getData() instanceof Texture) {
                Texture texture = (Texture) content.getData();
                setText("Texture file: "+texture.getTextureFile());
                setBackgroundColor(texture.isDefaultTexture(),hasFocus,false);
            } else { //root node contains a String
                setText((String)content.getData());
            }
        }
            /*if (leaf && isTutorialBook(value)) {
                setIcon(tutorialIcon);
                setToolTipText("This book is in the Tutorial series.");
            } else {
                setToolTipText(null); //no tool tip
            } */

        return this;
    }

    private void setBackgroundColor(boolean defaultTexture,boolean hasFocus,boolean isUrps) {
        if(isUrps) {
            if(!hasFocus) {
                this.setBackground(Constants.URPS_COLOR);
            } else {
                this.setBackground(Constants.URPS_COLOR_SELECTED);
            }
        } else if(defaultTexture) {
            if(!hasFocus) {
                this.setBackground(Constants.DEFAULT_COLOR);
            } else {
                this.setBackground(Constants.DEFAULT_COLOR_SELECTED);
            }
            //this.setBackgroundSelectionColor(new Color(131,255,137));
            //this.setBorderSelectionColor(new Color(131,255,136));
        } else {
            if(!hasFocus) {
                this.setBackground(Constants.CUSTOM_COLOR);
            } else {
                this.setBackground(Constants.CUSTOM_COLOR_SELECTED);
            }
            //this.setBackgroundSelectionColor(new Color(130,130,135));
            //this.setBorderSelectionColor(new Color(130,130,132));
        }
    }
    /*protected boolean isTutorialBook(Object value) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        BookInfo nodeInfo =
                (BookInfo)(node.getUserObject());
        String title = nodeInfo.bookName;
        if (title.indexOf("Tutorial") >= 0) {
            return true;
        }

        return false;
    }*/
}
