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

import com.mcmiddleearth.resourcepackmanager.Main;
import com.mcmiddleearth.resourcepackmanager.Options;
import com.mcmiddleearth.resourcepackmanager.data.BlockModel;
import com.mcmiddleearth.resourcepackmanager.data.BlockState;
import com.mcmiddleearth.resourcepackmanager.data.Material;
import com.mcmiddleearth.resourcepackmanager.data.MaterialList;
import com.mcmiddleearth.resourcepackmanager.data.ResourcePack;
import com.mcmiddleearth.resourcepackmanager.data.ResourcePackFile;
import com.mcmiddleearth.resourcepackmanager.data.Texture;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Eriol_Eandur
 */
public final class MainJFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainJFrame
     */
    public MainJFrame() {
        initComponents();
        setTitle("MCME Resource Pack Editor");
        jEditorPane1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                jButton3.setEnabled(true);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                jButton3.setEnabled(true);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                jButton3.setEnabled(true);
            }
        });
        //jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jFileChooser1.setCurrentDirectory(Paths.get("").toAbsolutePath().toFile());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new RPTreeContent("No RP loaded.",null));
        jTree2.setModel(new DefaultTreeModel(root));
        final RPTreeCellRenderer renderer = new RPTreeCellRenderer();//materialList.getRp());
        jTree2.setCellRenderer(renderer);
        TreeSelectionModel selModel = jTree2.getSelectionModel();
        selModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        //jTree2.setSelectionModel(null);
        addTreeNodes();
    }
    
    private void addTreeNodes() {
        if(Main.getBlockStates()!=null) {
            MaterialList materialList = Main.getBlockStates();
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(new RPTreeContent(materialList.
                                                   getRp().getBaseFolder().getFileName().toString(),null));
            jTree2.setModel(new DefaultTreeModel(root));
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
            jTree2.expandRow(0);
        }
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
    
    public void log(String out) {
        Main.log(out);
        jTextField2.setText(out);
        //jTextField2.updateUI();
        //jTextField2.repaint();
    }

    private void resetEditor() {
        jButton3.setEnabled(false);
        jEditorPane1.setText("");
        jTextField1.setText("no file");
        jTextField1.setBackground(Color.white);
        jEditorPane1.setEditable(false);
    }
    
    private void setBlockStateEditorEnabled(String comment, boolean enabled, boolean urps) {
        if(!enabled) {
            jTextField3.setText("");
            jTextField3.setEditable(false);
            jRadioButton1.setSelected(false);
            jRadioButton1.setEnabled(false);
        } else {
            jTextField3.setText(comment);
            jTextField3.setEditable(true);
            jRadioButton1.setSelected(urps);
            jRadioButton1.setEnabled(true);
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
        jFileChooser1 = new javax.swing.JFileChooser();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTree2 = new javax.swing.JTree();
        jRadioButton1 = new javax.swing.JRadioButton();
        jTextField3 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

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

        jFileChooser1.setCurrentDirectory(new java.io.File("C:\\"));
            jFileChooser1.setDialogTitle("");
            jFileChooser1.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(java.awt.event.WindowEvent evt) {
                    formWindowClosed(evt);
                }
            });

            jButton1.setLabel("Open RP");
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });

            jButton2.setText("Refresh RP Tree");
            jButton2.setToolTipText("");
            jButton2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });

            jSplitPane2.setDividerLocation(300);
            jSplitPane2.setPreferredSize(new java.awt.Dimension(550, 277));

            jEditorPane1.setEditable(false);
            jEditorPane1.setToolTipText("");
            jEditorPane1.setPreferredSize(new java.awt.Dimension(200, 20));
            jScrollPane2.setViewportView(jEditorPane1);

            jTextField1.setEditable(false);
            jTextField1.setText("no file");

            jTextField2.setEditable(false);

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addComponent(jTextField1)
                .addComponent(jTextField2)
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            );

            jSplitPane2.setRightComponent(jPanel1);

            jTree2.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
                public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                    jTree2ValueChanged(evt);
                }
            });
            jScrollPane3.setViewportView(jTree2);

            jRadioButton1.setText("URPS State");
            jRadioButton1.setEnabled(false);
            jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jRadioButton1ActionPerformed(evt);
                }
            });

            jTextField3.setEditable(false);
            jTextField3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jTextField3ActionPerformed(evt);
                }
            });

            jLabel1.setText("Block State Comment:");

            javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3)
                .addComponent(jRadioButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1)
                    .addContainerGap(184, Short.MAX_VALUE))
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jRadioButton1)
                    .addContainerGap())
            );

            jSplitPane2.setLeftComponent(jPanel2);

            jButton3.setText("Save File");
            jButton3.setEnabled(false);
            jButton3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });

            jButton4.setText("Save Block List");
            jButton4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jButton1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton4)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3)
                    .addContainerGap())
                .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2)
                        .addComponent(jButton3)
                        .addComponent(jButton4))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    //Handle open button action.
        int returnVal = jFileChooser1.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            log("Loading RP tree, this will take a few moments . . .  ");
            File file = jFileChooser1.getSelectedFile();
            log("Opening: " + file.getName() + "." );
            Main.loadBlockStates(Paths.get(file.toString()));
            addTreeNodes();
            jTextField2.setText("RP tree was loaded.");
            resetEditor();
        } else {
            log("Open command cancelled by user." );
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        log("Updating RP tree, this will take a few moments . . .  ");
        if(Main.getBlockStates()!=null) {
            Main.loadBlockStates(Main.getBlockStates().getRp().getBaseFolder());
            addTreeNodes();
        }
        log("RP tree was updated.");
        resetEditor();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTree2ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree2ValueChanged
        //System.out.println(evt.getPaths().length);
        //for(TreePath path: evt.getPaths()) {
          //  path.
        //}
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree2.getLastSelectedPathComponent();
        if(node==null || Main.getBlockStates() == null) {
            return;
        }
        Object userObject = node.getUserObject();
        if(userObject instanceof RPTreeContent) {
            RPTreeContent content = (RPTreeContent) userObject;
            ResourcePack rp = Main.getBlockStates().getRp();
            String text = "";
            String filename = "no file";
            setBlockStateEditorEnabled("",false,false);
            /*if(content.getData() instanceof ResourcePackFile) {
                text = Util.readTextFile(((ResourcePackFile)content.getData()).getInputStream());
            }*/
            if(content.getData() instanceof Material) {
                text = rp.getBlockStateFileContent(((Material)content.getData()).getName());
                filename = "blockstates/"+((Material)content.getData()).getName().toLowerCase()+".json";
            } else if(content.getData() instanceof BlockState) {
                BlockState state = (BlockState)content.getData();
                text = rp.getBlockStateFileContent(state.getMaterial());
                filename = "blockstates/"+state.getMaterial().toLowerCase()+".json";
                setBlockStateEditorEnabled(state.getComment(),true,state.isUrpsBlockState());
            } else if(content.getData() instanceof BlockModel) {
                text = rp.getModelFileContent(((BlockModel)content.getData()).getModelFile());
                filename = "materials/"+((BlockModel)content.getData()).getModelFile()+".json";
            } else if(content.getData() instanceof Texture) {
                text = "no texture editor implemented yet, sorry";
            } else { //root node contains a String
            }
            resetEditor();
            jEditorPane1.setText(text);
            jButton3.setEnabled(false);
            if(content.getData() instanceof ResourcePackFile) {
                if(((ResourcePackFile)content.getData()).isDefault()) {
                    jTextField1.setBackground(Constants.DEFAULT_COLOR);
                    filename = filename+" (default file)";
                } else {
                    jTextField1.setBackground(Constants.CUSTOM_COLOR);
                    filename = filename+" (custom file)";
                }
                jEditorPane1.setEditable(true);
            } else {
                jTextField1.setBackground(Color.white);
            }
            jTextField1.setText(filename);
        }
    }//GEN-LAST:event_jTree2ValueChanged

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        //System.out.println("button 3 action performed");
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree2.getLastSelectedPathComponent();
        if(node==null || Main.getBlockStates() == null) {
            System.out.println("no node found");
            return;
        }
        //System.out.println("node found");
        Object userObject = node.getUserObject();
        if(userObject instanceof RPTreeContent) {
            RPTreeContent content = (RPTreeContent) userObject;
            ResourcePack rp = Main.getBlockStates().getRp();
            if(content.getData() instanceof ResourcePackFile) {
                OutputStream out = ((ResourcePackFile)content.getData()).getOutputStream();
                if(out!=null) {
                    try {
                        jEditorPane1.write(new OutputStreamWriter(out));
                        log("File saved. You may want to refresh the RP tree?");
                        jButton3.setEnabled(false);
                    } catch (IOException ex) {
                        Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    log("Can't save file!");
                }
            }
        }
        //jEditorPane1.write(out);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree2.getLastSelectedPathComponent();
        if(node==null || Main.getBlockStates() == null) {
            System.out.println("no node found");
            return;
        }
        //System.out.println("node found");
        Object userObject = node.getUserObject();
        if(userObject instanceof RPTreeContent) {
            RPTreeContent content = (RPTreeContent) userObject;
            if(content.getData() instanceof BlockState) {
                BlockState state = (BlockState) content.getData();
                state.setComment(jTextField3.getText());
                jTree2.updateUI();
            }
        }
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        log("field3 action");
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree2.getLastSelectedPathComponent();
        if(node==null || Main.getBlockStates() == null) {
            System.out.println("no node found");
            return;
        }
        //System.out.println("node found");
        Object userObject = node.getUserObject();
        if(userObject instanceof RPTreeContent) {
            RPTreeContent content = (RPTreeContent) userObject;
            if(content.getData() instanceof BlockState) {
                BlockState state = (BlockState) content.getData();
                state.setUrpsBlockState(jRadioButton1.isSelected());
                jTree2.updateUI();
            }
            
        }
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if(Main.getBlockStates() != null) {
            if(!Main.getBlockStates().saveToFile(Main.getOptions().getValue(Options.blockStateFile))) {
                log("Error: Wasn't able to save BlockList file.");
            } else {
                log("BlockList file saved.");
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        Main.debugStream.close();
    }//GEN-LAST:event_formWindowClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTree jTree2;
    // End of variables declaration//GEN-END:variables
    
}
