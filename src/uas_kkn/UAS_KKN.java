/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uas_kkn;

import view.FMenuUtama;

/**
 *
 * @author fadhl
 */
public class UAS_KKN {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Membuat dan menampilkan jendela utama
                FMenuUtama mainMenu = new FMenuUtama();
                mainMenu.setVisible(true);
            }
        });
    }
    
}
