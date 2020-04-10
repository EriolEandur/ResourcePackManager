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
package com.mcmiddleearth.resourcepackmanager;

import com.mcmiddleearth.resourcepackmanager.data.ResourcePack;
import com.mcmiddleearth.resourcepackmanager.data.MaterialList;
import com.mcmiddleearth.resourcepackmanager.gui._invalid_MainJFrame;
import com.mcmiddleearth.resourcepackmanager.gui.MainJFrame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;

/**
 *
 * @author Eriol_Eandur
 */
public class Main {

    private static MainJFrame mainFrame;
    
    @Getter
    private static MaterialList blockStates;
    
    @Getter
    private static Options options;
    
    public static PrintStream debugStream;
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        options = new Options(args);
        //if(options.has(Options.logFile)) {
            File log = new File("log.txt");//options.getValue(Options.logFile).toFile();
            if(!log.exists()) {
                if(!log.createNewFile()) {
                    //debugStream = System.out;
                    log("Can't create log file: "+log.toString());
                }
            }
            debugStream = new PrintStream(new FileOutputStream(log));
        //}
        if (options.has(Options.help)) {
            options.printHelpMsg(System.out);
            return;
        }
        if(options.getValue(Options.rpDir).toFile().exists()) {
            loadBlockStates(options.getValue(Options.rpDir));
        }
        /*blockStates.saveToFile((options.has(Options.outFile)?
                                options.getValue(Options.outFile):
                                Paths.get(options.getValue(Options.blockStateFile).toString()
                                         +options.getValue(Options.rpDir).toString()+".txt")));*/
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(_invalid_MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            mainFrame = new MainJFrame();
            mainFrame.setVisible(true);
        });
    }
    
    public static void loadBlockStates(Path path) {
        ResourcePack rp = new ResourcePack(path, options.getValue(Options.defaultRpDir));
        try {
            //File blockListFile = options.getValue(Options.blockStateFile).toFile();
            /*if(blockListFile.exists()) {
                try(Scanner fileScanner = new Scanner(blockListFile)) {
                    blockStates = new MaterialList(fileScanner,rp);
                }
            } else {*/
                try(Scanner scanner = new Scanner(Main.class.getResourceAsStream("/blockList.txt"))) {
                    blockStates = new MaterialList(scanner,rp);
                }
            //}
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
        
    public static void log(String message) {
        System.out.println(message);
        if(debugStream!=null){
            debugStream.println(message);
            debugStream.flush();
        }
    }
}
