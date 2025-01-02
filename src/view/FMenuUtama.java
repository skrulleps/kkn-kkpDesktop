/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;
import javax.swing.JFrame;
import session.session;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.Logger;
import net.sf.jasperreports.engine.JRException;
import java.sql.Connection;


/**
 *
 * @author fadhl
 */
public class FMenuUtama extends javax.swing.JFrame {
     private static final Logger logger = Logger.getLogger(FMenuUtama.class);
     public static String levelUser;
  
    //Deklarasi Variabel
    private Map<String, Object> param = new HashMap<>();
    //Deklarasi Variabel utk Laporan
    JasperReport jasRep;
    JasperPrint jasPrint;
    JasperDesign jasDes;
    /**
     * Creates new form FMenuUtama
     */
    public FMenuUtama() {
        initComponents();
         loginGagal();
        //method tanggal
        setTanggal();
        setMenuAkses();
    }
    
    public void setTanggal() {
    // Mendapatkan tanggal saat ini
    LocalDate today = LocalDate.now();
    
    // Menentukan format tanggal
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    // Mengonversi tanggal ke format yang diinginkan
    String formattedDate = today.format(formatter);
    
    // Menetapkan tanggal yang diformat ke komponen
    lTanggal.setText(formattedDate);
}

    public static void loginGagal () {
        mnPendaftaran.setVisible(false);
        mnData.setVisible(false);
        mnLaporan.setVisible(false);
        mnPenilaian.setVisible(false);
        smLogin.setText("Login");
    }
    
   public static void loginSukses() {
    // Akses Admin
    mnPendaftaran.setVisible(true);
    mnData.setVisible(true);
    mnLaporan.setVisible(true);
    mnPenilaian.setVisible(true);
    smLapMhs.setVisible(true);
    smLapkkn.setVisible(true);
    smLapkkp.setVisible(true);
    smLapNilaikkp.setVisible(true);
    smLapNilaikkn.setVisible(true);
    smKKN.setVisible(true);
    
    // Ambil email dari sesi dan tampilkan di lblAkun
    String email = session.getInstance().getUsername();
    if (lblAkun != null) {
        lblAkun.setText("Email: " + email); // Set label dengan email pengguna
    }
    
    if (levelUser != null) {
        switch (levelUser) {
            case "admin":
                // Akses admin
                break;

            case "dosen":
                // Pengecekan status dosen
                String statusDosen = session.getInstance().getStatus();
                
                if ("penguji/penilai".equals(statusDosen)) {
                    mnPendaftaran.setVisible(false);
                    mnData.setVisible(false);
                    mnLaporan.setVisible(true);
                    mnPenilaian.setVisible(true);
                    smPenilaiankkp.setVisible(true);
                    smPenilaiankkn.setVisible(true);
                    smLapMhs.setVisible(true);
                    smLapkkn.setVisible(true);
                    smLapkkp.setVisible(true);
                    smLapNilaikkp.setVisible(true);
                    smLapNilaikkn.setVisible(true);
                } else if ("kkp".equals(statusDosen) || "kkn".equals(statusDosen)) {
                    mnPendaftaran.setVisible(false);
                    mnData.setVisible(false);
                    mnLaporan.setVisible(true);
                    mnPenilaian.setVisible(true);
                    smLapMhs.setVisible(true);
                    smLapkkn.setVisible(true);
                    smLapkkp.setVisible(true);
                    smLapNilaikkp.setVisible(true);
                    smLapNilaikkn.setVisible(true);
                    smPenilaiankkp.setVisible(true);
                    smPenilaiankkn.setVisible(true);
                } else {
                    // Status dosen tidak dikenali
                    loginGagal();
                }
                break;

            case "mahasiswa":
                // Akses mahasiswa
                String kegiatanMahasiswa = session.getInstance().getKegiatan();
                
                if ("kkn".equals(kegiatanMahasiswa)) {
                   mnData.setVisible(false);
                    mnPendaftaran.setVisible(true);
                    smLapMhs.setVisible(false);
                    mnPenilaian.setVisible(false);
                    mnLaporan.setVisible(true);
                    // Hanya tampilkan submenu KKN
                    smKKN.setVisible(true); // Pastikan submenu ini ada
                    smKKP.setVisible(false);
                    smLapMhs.setVisible(true);
                    smLapkkn.setVisible(true);
                    smLapkkp.setVisible(true);
                    smLapNilaikkp.setVisible(true);
                    smLapNilaikkn.setVisible(true);
                } else if ("kkp".equals(kegiatanMahasiswa)) {
                    mnData.setVisible(false);
                    mnPendaftaran.setVisible(true);
                    smLapMhs.setVisible(false);
                    mnPenilaian.setVisible(false);
                    mnLaporan.setVisible(true);
                    // Hanya tampilkan submenu KKP
                    smKKP.setVisible(true); // Pastikan submenu ini ada
                    smKKN.setVisible(false);
                    smLapMhs.setVisible(true);
                    smLapkkn.setVisible(true);
                    smLapkkp.setVisible(true);
                    smLapNilaikkp.setVisible(true);
                    smLapNilaikkn.setVisible(true);
                } else {
                    // Kegiatan mahasiswa tidak dikenali
                    loginGagal();
                }
                break;

            default:
                // Jika levelUser tidak dikenali, sembunyikan semua menu
                loginGagal();
                break;
        }
    } else {
        loginGagal();
    }

    smLogin.setText("Logout");
}





    
    private void setMenuAkses() {
        if (session.getInstance().getUsername() != null) {
            loginSukses();
        } else {
            loginGagal();
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
        lTanggal = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblAkun = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnUtilitas = new javax.swing.JMenu();
        smLogin = new javax.swing.JMenuItem();
        smKeluar = new javax.swing.JMenuItem();
        mnData = new javax.swing.JMenu();
        smPengguna = new javax.swing.JMenuItem();
        smMahasiswa = new javax.swing.JMenuItem();
        smDosen = new javax.swing.JMenuItem();
        smTempatKKN = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        mnPendaftaran = new javax.swing.JMenu();
        smKKN = new javax.swing.JMenuItem();
        smKKP = new javax.swing.JMenuItem();
        mnPenilaian = new javax.swing.JMenu();
        smPenilaiankkn = new javax.swing.JMenuItem();
        smPenilaiankkp = new javax.swing.JMenuItem();
        mnLaporan = new javax.swing.JMenu();
        smLapMhs = new javax.swing.JMenuItem();
        smLapkkn = new javax.swing.JMenuItem();
        smLapkkp = new javax.swing.JMenuItem();
        smLapNilaikkn = new javax.swing.JMenuItem();
        smLapNilaikkp = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Tanggal :");

        lTanggal.setText("dd/mm/yyyy");

        jLabel2.setText("Account");

        lblAkun.setText("...");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/LgoGlobal.png"))); // NOI18N

        mnUtilitas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/kelas.png"))); // NOI18N
        mnUtilitas.setText("Utilitas");

        smLogin.setText("Login");
        smLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smLoginActionPerformed(evt);
            }
        });
        mnUtilitas.add(smLogin);

        smKeluar.setText("Keluar");
        smKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smKeluarActionPerformed(evt);
            }
        });
        mnUtilitas.add(smKeluar);

        jMenuBar1.add(mnUtilitas);

        mnData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dudction book.png"))); // NOI18N
        mnData.setText("Data master");

        smPengguna.setText("Data Pengguna");
        smPengguna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smPenggunaActionPerformed(evt);
            }
        });
        mnData.add(smPengguna);

        smMahasiswa.setText("Data Mahasiswa");
        smMahasiswa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smMahasiswaActionPerformed(evt);
            }
        });
        mnData.add(smMahasiswa);

        smDosen.setText("Data Dosen");
        smDosen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smDosenActionPerformed(evt);
            }
        });
        mnData.add(smDosen);

        smTempatKKN.setText("Data Tempat KKN");
        smTempatKKN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smTempatKKNActionPerformed(evt);
            }
        });
        mnData.add(smTempatKKN);

        jMenuItem1.setText("Data Tempat KKP");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        mnData.add(jMenuItem1);

        jMenuBar1.add(mnData);

        mnPendaftaran.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/nilai1.png"))); // NOI18N
        mnPendaftaran.setText("Pendaftaran");

        smKKN.setText("KKN");
        smKKN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smKKNActionPerformed(evt);
            }
        });
        mnPendaftaran.add(smKKN);

        smKKP.setText("KKP");
        smKKP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smKKPActionPerformed(evt);
            }
        });
        mnPendaftaran.add(smKKP);

        jMenuBar1.add(mnPendaftaran);

        mnPenilaian.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/matkul.png"))); // NOI18N
        mnPenilaian.setText("Penilaian");

        smPenilaiankkn.setText("Penilaian KKN");
        smPenilaiankkn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                smPenilaiankknMouseClicked(evt);
            }
        });
        smPenilaiankkn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smPenilaiankknActionPerformed(evt);
            }
        });
        mnPenilaian.add(smPenilaiankkn);

        smPenilaiankkp.setText("Penilaian KKP");
        smPenilaiankkp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                smPenilaiankkpMouseClicked(evt);
            }
        });
        smPenilaiankkp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smPenilaiankkpActionPerformed(evt);
            }
        });
        mnPenilaian.add(smPenilaiankkp);

        jMenuBar1.add(mnPenilaian);

        mnLaporan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/book1.png"))); // NOI18N
        mnLaporan.setText("Laporan");

        smLapMhs.setText("Laporan Mahasiswa ");
        smLapMhs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smLapMhsActionPerformed(evt);
            }
        });
        mnLaporan.add(smLapMhs);

        smLapkkn.setText("Laporan Mahasiswa KKN");
        smLapkkn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smLapkknActionPerformed(evt);
            }
        });
        mnLaporan.add(smLapkkn);

        smLapkkp.setText("Laporan Mahasiswa KKP");
        smLapkkp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smLapkkpActionPerformed(evt);
            }
        });
        mnLaporan.add(smLapkkp);

        smLapNilaikkn.setText("Laporan Nilai KKN");
        smLapNilaikkn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smLapNilaikknActionPerformed(evt);
            }
        });
        mnLaporan.add(smLapNilaikkn);

        smLapNilaikkp.setText("Laporan Nilai KKP");
        smLapNilaikkp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smLapNilaikkpActionPerformed(evt);
            }
        });
        mnLaporan.add(smLapNilaikkp);

        jMenuBar1.add(mnLaporan);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lTanggal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAkun, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 871, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lTanggal)
                    .addComponent(jLabel2)
                    .addComponent(lblAkun))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void smLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smLoginActionPerformed
        // TODO add your handling code here:
           // TODO add your handling code here:
        if ("Login".equals(FMenuUtama.smLogin.getText())) {
            FLogin login = new FLogin();
            login.setVisible(true);
//            DesktopPane.add(login);
        }
        else {
            FMenuUtama.loginGagal();
        }
    }//GEN-LAST:event_smLoginActionPerformed

    private void smKKPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smKKPActionPerformed
        // TODO add your handling code here:
        FDaftarkkp kkp = new FDaftarkkp();
        kkp.setVisible(true);
//        DesktopPane.add(eval);
    }//GEN-LAST:event_smKKPActionPerformed

    private void smDosenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smDosenActionPerformed
        // TODO add your handling code here:
        FDosen dsn = new FDosen();
        dsn.setVisible(true);
//        DesktopPane.add(dsn);
    }//GEN-LAST:event_smDosenActionPerformed

    private void smLapkknActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smLapkknActionPerformed
         // TODO add your handling code here:
         JPrmkkn rkkn = new JPrmkkn();
        rkkn.setVisible(true);
    }//GEN-LAST:event_smLapkknActionPerformed

    private void smMahasiswaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smMahasiswaActionPerformed
        // TODO add your handling code here:
        FMahasiswa mhs = new FMahasiswa();
        mhs.setVisible(true);
//        DesktopPane.add(mhs);
    }//GEN-LAST:event_smMahasiswaActionPerformed

    private void smTempatKKNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smTempatKKNActionPerformed
        // TODO add your handling code here:
        FTempatkkn kkn = new FTempatkkn();
        kkn.setVisible(true);
//        DesktopPane.add(kkn);
    }//GEN-LAST:event_smTempatKKNActionPerformed

    private void smPenggunaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smPenggunaActionPerformed
        // TODO add your handling code here:
        FPengguna pgn = new FPengguna();
        pgn.setVisible(true);
//        DesktopPane.add(pgn);
    }//GEN-LAST:event_smPenggunaActionPerformed

    private void smKKNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smKKNActionPerformed
        // TODO add your handling code here:
        FDaftarkkn dft = new FDaftarkkn();
        dft.setVisible(true);
//        DesktopPane.add(dft);
    }//GEN-LAST:event_smKKNActionPerformed

    private void smLapMhsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smLapMhsActionPerformed
        // TODO add your handling code here:
         try {
            // Pastikan file laporan ada
            File report = new File("src/report/repMhs.jrxml");
            if (!report.exists()) {
                logger.error("File laporan tidak ditemukan: " + report.getAbsolutePath());
                JOptionPane.showMessageDialog(null, "File laporan tidak ditemukan", "Cetak Laporan",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Muat, kompilasi, dan isi laporan
            JasperDesign jasDes = JRXmlLoader.load(report);
            JasperReport jasRep = JasperCompileManager.compileReport(jasDes);
            // Gunakan koneksi yang sudah ada
            Connection connection = koneksi.koneksi.getConnection();
            if (connection != null && !connection.isClosed()) {
                JasperPrint jasPrint = JasperFillManager.fillReport(jasRep, null, connection);

                // Tampilkan laporan
                JasperViewer.viewReport(jasPrint, false);
            } else {
                logger.error("Connection is closed.");
                JOptionPane.showMessageDialog(null, "Koneksi database tidak tersedia", "Cetak Laporan",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (JRException e) {
            // Log exception dan tampilkan pesan kesalahan
            logger.error("Gagal memproses laporan JasperReports", e);
            JOptionPane.showMessageDialog(null, "Gagal Membuka Laporan", "Cetak Laporan",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Tangani exception umum
            logger.error("Terjadi kesalahan", e);
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan", "Cetak Laporan",
                    JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_smLapMhsActionPerformed

    private void smKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smKeluarActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_smKeluarActionPerformed

    private void smPenilaiankknMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_smPenilaiankknMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_smPenilaiankknMouseClicked

    private void smPenilaiankkpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_smPenilaiankkpMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_smPenilaiankkpMouseClicked

    private void smPenilaiankknActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smPenilaiankknActionPerformed
        // TODO add your handling code here:
        FEvaluasikkn Ekn = new FEvaluasikkn();
        Ekn.setVisible(true);
    }//GEN-LAST:event_smPenilaiankknActionPerformed

    private void smPenilaiankkpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smPenilaiankkpActionPerformed
        // TODO add your handling code here:
        FEvaluasikkp kkp = new FEvaluasikkp();
        kkp.setVisible(true);
    }//GEN-LAST:event_smPenilaiankkpActionPerformed

    private void smLapkkpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smLapkkpActionPerformed
        // TODO add your handling code here:
        JPrmkkp kkp = new JPrmkkp();
        kkp.setVisible(true);
    }//GEN-LAST:event_smLapkkpActionPerformed

    private void smLapNilaikknActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smLapNilaikknActionPerformed
       // TODO add your handling code here:
         JPrmNilaikkn rkkn = new JPrmNilaikkn();
        rkkn.setVisible(true);
    }//GEN-LAST:event_smLapNilaikknActionPerformed

    private void smLapNilaikkpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smLapNilaikkpActionPerformed
        // TODO add your handling code here:
        JPrmNilaikkp nkkp = new JPrmNilaikkp();
        nkkp.setVisible(true);
    }//GEN-LAST:event_smLapNilaikkpActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        FTempatkkp tkkp = new FTempatkkp();
                tkkp.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

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
            java.util.logging.Logger.getLogger(FMenuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FMenuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FMenuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FMenuUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FMenuUtama().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JLabel lTanggal;
    private static javax.swing.JLabel lblAkun;
    private static javax.swing.JMenu mnData;
    private static javax.swing.JMenu mnLaporan;
    private static javax.swing.JMenu mnPendaftaran;
    private static javax.swing.JMenu mnPenilaian;
    private javax.swing.JMenu mnUtilitas;
    private static javax.swing.JMenuItem smDosen;
    private static javax.swing.JMenuItem smKKN;
    private static javax.swing.JMenuItem smKKP;
    private javax.swing.JMenuItem smKeluar;
    private static javax.swing.JMenuItem smLapMhs;
    private static javax.swing.JMenuItem smLapNilaikkn;
    private static javax.swing.JMenuItem smLapNilaikkp;
    private static javax.swing.JMenuItem smLapkkn;
    private static javax.swing.JMenuItem smLapkkp;
    private static javax.swing.JMenuItem smLogin;
    private static javax.swing.JMenuItem smMahasiswa;
    private static javax.swing.JMenuItem smPengguna;
    private static javax.swing.JMenuItem smPenilaiankkn;
    private static javax.swing.JMenuItem smPenilaiankkp;
    private static javax.swing.JMenuItem smTempatKKN;
    // End of variables declaration//GEN-END:variables
}
