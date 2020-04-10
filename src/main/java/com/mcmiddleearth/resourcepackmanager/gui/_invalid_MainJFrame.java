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
import com.mcmiddleearth.resourcepackmanager.data.MaterialList;
import com.mcmiddleearth.resourcepackmanager.data.ResourcePack;
import com.mcmiddleearth.resourcepackmanager.data.Texture;
import java.awt.Image;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Eriol_Eandur
 */
public class _invalid_MainJFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainJFrame
     * @param materialList
     */
    public _invalid_MainJFrame(MaterialList materialList) {
        initComponents();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new RPTreeContent("Materials",null));
        final RPTreeCellRenderer renderer = new RPTreeCellRenderer();//materialList.getRp());
        jTree1.setCellRenderer(renderer);
        jTree1.setSelectionModel(null);
        jTree1.setModel(new DefaultTreeModel(root));
        materialList.getMaterials().forEach((material) -> {
            ImageIcon materialIcon = null;
            if(material.isSingleState()) {
                Texture[] textures = material.getBlockStates().get(0).getModels()[0].getTextures();
                materialIcon = createImageIcon(materialList.getRp()
                                   .getTextureURL((textures!=null?textures[0].getTextureFile():"")));
            }
            DefaultMutableTreeNode materialNode = new DefaultMutableTreeNode(new RPTreeContent(material,materialIcon));
            root.add(materialNode);
            material.getBlockStates().forEach((state) -> {
                ImageIcon stateIcon;// = null;
                Texture[] textures = state.getModels()[0].getTextures();
                stateIcon = createImageIcon(materialList.getRp().getTextureURL(
                                                (textures!=null?textures[0].getTextureFile():"")));
                //if(state.isSingleModel()) {
                //}
                DefaultMutableTreeNode stateNode = new DefaultMutableTreeNode(new RPTreeContent(state,stateIcon));
                materialNode.add(stateNode);
                addModelNodes(materialList.getRp(),state, stateNode);
            });
            //if(!material.isSingleState()) {
            /*} else {
                addModelNodes(materialList.getRp(),material.getBlockStates().get(0),materialNode);
            }*/
        });
        jTree1.expandRow(0);
    }

    private void addModelNodes(ResourcePack rp, BlockState state, DefaultMutableTreeNode parent) {
        ImageIcon modelIcon = null;
        for(BlockModel model: state.getModels()) {
            modelIcon = createImageIcon(rp.getTextureURL((model.getTextures()!=null?
                                                            model.getTextures()[0].getTextureFile():
                                                            "No Textures!!!")));
            //if(model.isSingleTexture()) {
            //}
            DefaultMutableTreeNode modelNode = new DefaultMutableTreeNode(new RPTreeContent(model,modelIcon));
            parent.add(modelNode);
            addTextureNodes(rp, model, modelNode);
        }
        //if(!state.isSingleModel()) {
        /*} else {
            addTextureNodes(rp, state.getModels()[0],parent);
        }*/
    }
    
    private void addTextureNodes(ResourcePack rp, BlockModel model, DefaultMutableTreeNode parent) {
        if(model.getTextures()!=null) { // && !model.isSingleTexture()) {
            for(Texture texture: model.getTextures()) {
                ImageIcon icon = createImageIcon(rp.getTextureURL(texture.getTextureFile()));
                DefaultMutableTreeNode textureNode = new DefaultMutableTreeNode(new RPTreeContent(texture,icon));
                parent.add(textureNode);                
            }
        }
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    private ImageIcon createImageIcon(URL file) {
        //java.net.URL imgURL = getClass().getResource(path.toString());
        if(file==null) {
            return null;
        }
        //File file = new File(path.toString());
        if (!file.getPath().equals("")){//new File(file.getPath()).exists()) {
            ImageIcon icon = new ImageIcon(file);
            Image image = icon.getImage();
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            if(height>width);
            Image cropped = createImage(new FilteredImageSource(image.getSource(),
                    new CropImageFilter(0,0,width,width)));
            Image scaled = createImage(new FilteredImageSource(cropped.getSource(),
                    new AreaAveragingScaleFilter(16,16)));
            icon.setImage(scaled);
            return icon;
        } else {
            //System.err.println("Couldn't find file: " + file);
            return null;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jDialog2 = new javax.swing.JDialog();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jTree1);

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}
