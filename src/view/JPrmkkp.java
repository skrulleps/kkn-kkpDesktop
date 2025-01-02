/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;
import koneksi.koneksi;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import org.apache.log4j.Logger;
import net.sf.jasperreports.engine.JRException;
/**
 *
 * @author fadhl
 */
public class JPrmkkp extends javax.swing.JFrame {
    JasperReport jasRep;
    JasperPrint jasPrint;
    JasperDesign jasDes;
    private static final Logger log = Logger.getLogger(JPrmkkn.class.getName());
    private Connection connection;

    /**
     * Creates new form JPrmkkn
     */
    public JPrmkkp() {
        initComponents();
        connection = koneksi.getConnection();
        getcbTempatkkp();
    }
    
     private void getcbTempatkkp() {
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    String query = "SELECT pendaftaran_kkp.id_pendaftaran, " +
                   "mahasiswa.nim AS nim_mahasiswa, " +
                   "mahasiswa.nama AS nama_mahasiswa, " +
                   "mahasiswa.jurusan, " +
                   "tempat_kkp.nama_perusahaan, " +
                   "tempat_kkp.tgl_mulai, " +
                   "tempat_kkp.tgl_berakhir, " +
                   "tempat_kkp.nama_pembimbing, " +
                   "pendaftaran_kkp.tgl_daftar " +
                   "FROM pendaftaran_kkp " +
                   "INNER JOIN mahasiswa ON pendaftaran_kkp.id_mahasiswa = mahasiswa.id_mahasiswa " +
                   "INNER JOIN tempat_kkp ON pendaftaran_kkp.id_tempat = tempat_kkp.id_tempat " +
                   "ORDER BY tempat_kkp.nama_perusahaan";

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
        conn = koneksi.getConnection();
        if (conn != null && !conn.isClosed()) {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                model.addElement(rs.getString("nama_perusahaan"));
            }
            cbPerusahaan.setModel(model);
        } else {
            JOptionPane.showMessageDialog(this, "Connection is closed or null.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            // Note: Not closing the connection here
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error closing resources: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


private void generateReport() {
    Connection connection = null;
    try {
        connection = koneksi.getConnection();
        if (connection == null || connection.isClosed()) {
            JOptionPane.showMessageDialog(null, "Koneksi database tidak tersedia.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Load the JRXML file
        File reportFile = new File(koneksi.PathReport + "/repkkp.jrxml");
        JasperDesign jasDes = JRXmlLoader.load(reportFile);
        JasperReport jasRep = JasperCompileManager.compileReport(jasDes);

        // Get the selected item from the combobox
        String selectedTempat = (String) cbPerusahaan.getSelectedItem();
        if (selectedTempat == null || selectedTempat.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih tempat KKP terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Set parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("kkp", selectedTempat);

        // Fill the report
        JasperPrint jasPrint = JasperFillManager.fillReport(jasRep, parameters, connection);

        // View the report
        JasperViewer.viewReport(jasPrint, false);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Cetak Laporan", JOptionPane.ERROR_MESSAGE);
    } catch (JRException e) {
        JOptionPane.showMessageDialog(null, "JasperReports error: " + e.getMessage(), "Cetak Laporan", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), "Cetak Laporan", JOptionPane.ERROR_MESSAGE);
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

        jLabel1 = new javax.swing.JLabel();
        cbPerusahaan = new javax.swing.JComboBox<>();
        bBatal = new javax.swing.JButton();
        bOK = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 3, 13)); // NOI18N
        jLabel1.setText("*)Pilih Tempat KKP");

        cbPerusahaan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        bBatal.setText("Batal");
        bBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBatalActionPerformed(evt);
            }
        });

        bOK.setText("OK");
        bOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOKActionPerformed(evt);
            }
        });

        jButton3.setText("Close");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cbPerusahaan, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(134, 134, 134)
                        .addComponent(bOK)
                        .addGap(18, 18, 18)
                        .addComponent(bBatal))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(159, 159, 159)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbPerusahaan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bOK)
                    .addComponent(bBatal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBatalActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_bBatalActionPerformed

    private void bOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOKActionPerformed
          generateReport();
    }//GEN-LAST:event_bOKActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JPrmkkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JPrmkkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JPrmkkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JPrmkkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JPrmkkp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton bBatal;
    private static javax.swing.JButton bOK;
    private static javax.swing.JComboBox<String> cbPerusahaan;
    private static javax.swing.JButton jButton3;
    private static javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
