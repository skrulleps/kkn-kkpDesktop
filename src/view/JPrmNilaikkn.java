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

/**
 *
 * @author fadhl
 */
public class JPrmNilaikkn extends javax.swing.JFrame {
    JasperReport jasRep;
    JasperPrint jasPrint;
    JasperDesign jasDes;
   
    /**
     * Creates new form JPrmkkn
     */
    public JPrmNilaikkn() {
        initComponents();
        getcbJurusan();
    }
    
   private void getcbJurusan() {
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    String query = "SELECT DISTINCT mahasiswa.jurusan " +
                   "FROM evaluasi " +
                   "INNER JOIN pendaftaran_kkn ON evaluasi.id_pendaftaran = pendaftaran_kkn.id_pendaftaran " +
                   "INNER JOIN mahasiswa ON pendaftaran_kkn.id_mahasiswa = mahasiswa.id_mahasiswa " +
                   "INNER JOIN tempat_kkn ON pendaftaran_kkn.id_tempat = tempat_kkn.id_tempat " +
                   "INNER JOIN dosen ON pendaftaran_kkn.id_dosen = dosen.id_dosen " +
                   "ORDER BY mahasiswa.jurusan";

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
        conn = koneksi.getConnection(); // Gunakan koneksi yang sudah ada
        if (conn != null && !conn.isClosed()) {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                model.addElement(rs.getString("jurusan"));
            }
            cbJurusan.setModel(model);
        } else {
            JOptionPane.showMessageDialog(this, "Koneksi database tidak tersedia.", "Error", JOptionPane.ERROR_MESSAGE);
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
        // Database connection details
        String dbUrl = "jdbc:mysql://localhost:3306/db_kkn";
        String dbUser = "root";
        String dbPassword = "";

        // Load the JRXML file
        File reportFile = new File("src/report/repNilaikkn.jrxml");
        JasperDesign jasDes = JRXmlLoader.load(reportFile);
        JasperReport jasRep = JasperCompileManager.compileReport(jasDes);

        // Check the state of the checkbox
        String parameterValue;
        if (ckbAll.isSelected()) {
            // If checkbox is selected, disable the combobox and set parameter to null
            cbJurusan.setEnabled(false); // Disable the combobox
            parameterValue = null; // Null parameter for all data
        } else {
            // If checkbox is not selected, enable the combobox and use the selected value
            cbJurusan.setEnabled(true); // Ensure the combobox is enabled
            String selectedJurusan = (String) cbJurusan.getSelectedItem();
            parameterValue = (selectedJurusan != null && !selectedJurusan.isEmpty()) ? selectedJurusan : null;
        }

        // Set parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rkkn", parameterValue);

        // Create the connection
        connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

        // Fill the report
        JasperPrint jasPrint = JasperFillManager.fillReport(jasRep, parameters, connection);

        // View the report
        JasperViewer.viewReport(jasPrint, false);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Gagal Membuka Laporan: " + e.getMessage(), "Cetak Laporan", JOptionPane.ERROR_MESSAGE);
    } finally {
        // Ensure connection is closed if it was opened
        // Commented out to keep connection open
        /*
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Print stack trace if closing connection fails
            }
        }
        */
    }
}



    private void refresh(){
    cbJurusan.setEnabled(true);
    ckbAll.setSelected(false);
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
        cbJurusan = new javax.swing.JComboBox<>();
        bBatal = new javax.swing.JButton();
        bOK = new javax.swing.JButton();
        ckbAll = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 3, 13)); // NOI18N
        jLabel1.setText("*)Pilih Jurusan");

        cbJurusan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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

        ckbAll.setText("All Data");

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(bOK)
                .addGap(18, 18, 18)
                .addComponent(bBatal)
                .addGap(137, 137, 137))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ckbAll)
                            .addComponent(cbJurusan, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(154, 154, 154)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbJurusan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ckbAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bOK)
                    .addComponent(bBatal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBatalActionPerformed
        // TODO add your handling code here:
       refresh();
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
            java.util.logging.Logger.getLogger(JPrmNilaikkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JPrmNilaikkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JPrmNilaikkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JPrmNilaikkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JPrmNilaikkn().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton bBatal;
    private static javax.swing.JButton bOK;
    private static javax.swing.JComboBox<String> cbJurusan;
    private static javax.swing.JCheckBox ckbAll;
    private static javax.swing.JButton jButton3;
    private static javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
