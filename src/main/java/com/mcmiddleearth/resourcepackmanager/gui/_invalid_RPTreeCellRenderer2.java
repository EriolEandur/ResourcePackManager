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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Eriol_Eandur
 */
public final class _invalid_RPTreeCellRenderer2  extends JPanel implements TreeCellRenderer {
    private JLabel info = new JLabel();
    private JLabel comment = new JLabel("<html><b>comments here</b></html>");
    private final static Border NO_FOCUS_BORDER = 
      new EmptyBorder(1, 1, 1, 1);

    public _invalid_RPTreeCellRenderer2() {
      setLayout(new GridLayout(1,2));
      add(info);//,BorderLayout.WEST);
      add(comment);//, BorderLayout.EAST);

      info.setOpaque(false);
      comment.setOpaque(false);
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

        setOpaque(true);
        //setBackground(Color.WHITE);
        Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
        if(userObject instanceof RPTreeContent) {
            RPTreeContent content = (RPTreeContent) userObject;
            if(content.getIcon() != null) {
                info.setIcon(content.getIcon());
            }
            if(content.getData() instanceof Material) {
                info.setText(((Material)content.getData()).getName());
            } else if(content.getData() instanceof BlockState) {
                BlockState state = (BlockState) content.getData();
                if(!state.isSingleModel()) {
                    info.setText("S "+state.getAttributes()+" -> Multimodel!");
                } else {
                    info.setText("S "+state.getAttributes()+" -> "+state.getModels()[0].getModelFile());
                }
            } else if(content.getData() instanceof BlockModel) {
                BlockModel model = (BlockModel) content.getData();
                if(!model.isSingleTexture()) {
                    info.setText("M "+ "-> Multitexture!");
                } else {
                    info.setText("M "+model.getTextures()[0].getTextureFile());
                }
            } else if(content.getData() instanceof Texture) {
                Texture texture = (Texture) content.getData();
                info.setText("T "+texture.getTextureFile());
                if(texture.isDefaultTexture()) {
                    this.setBackground(new Color(131,255,138));
                    //this.setBackgroundSelectionColor(new Color(131,255,137));
                    //this.setBorderSelectionColor(new Color(131,255,136));
                } else {
                    this.setBackground(new Color(130, 130, 137));
                    //this.setBackgroundSelectionColor(new Color(130,130,135));
                    //this.setBorderSelectionColor(new Color(130,130,132));
                }
            } else { //root node contains a String
                info.setText((String)content.getData());
            }
        }
        setForeground(sel ? 
           tree.getForeground() : 
           tree.getForeground());
        setBackground(sel ? 
           tree.getBackground() : 
           tree.getBackground());
        setBorder((hasFocus) ? 
           UIManager.getBorder(          
                  "List.focusCellHighlightBorder") : 
           NO_FOCUS_BORDER);
        return this;
    }
}
