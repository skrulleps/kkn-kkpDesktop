/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;
import koneksi.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Fadhlan
 */
public class FTempatkkn extends javax.swing.JFrame {
    Connection connection;
    DefaultTableModel model;

    /**
     * Creates new form FrameKelas
     */
    public FTempatkkn() {
        initComponents();
        connection = koneksi.getConnection();
        tNamatempat.requestFocus();
        getcbDosen();
        getDataTable(); 
        generateAutoNumber();
        tID.setEditable(false);
    }
    
    private void getDataTable() {
        model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            Statement stat = connection.createStatement();
            String sql = "SELECT * FROM tempat_kkn, dosen "
                    + "WHERE tempat_kkn.id_dosen=dosen.id_dosen ";
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
                Object[] obj = new Object[6];
                obj[0] = res.getString("id_tempat");
                obj[1] = res.getString("nama_tempat");
                obj[2] = res.getString("lokasi");
                obj[3] = res.getString("program");
                obj[4] = res.getString("id_dosen");
                obj[5] = res.getString("jumlah_mahasiswa");
                model.addRow(obj);
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    
    private void generateAutoNumber() {
        try {
            Statement stat = connection.createStatement();
            String sql = "SELECT MAX(id_tempat) AS max_id FROM tempat_kkn";
            ResultSet res = stat.executeQuery(sql);

            if (res.next()) {
                int maxId = res.getInt("max_id");
                int newId = maxId + 1;
                tID.setText(String.valueOf(newId));
            } else {
                // Handle the case where no records exist yet
                tID.setText("1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void refresh() {
        model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        getcbDosen();
        getDataTable();
        generateAutoNumber();
    }
    
    private void reset() {
        tNamatempat.setText("");
        tProgram.setText("");
        tID.setText("");
        tLokasi.setText("");
        getcbDosen();
        tNamatempat.setEditable(true);
        bSimpan.setEnabled(true);
        generateAutoNumber();
    }
    
    private void insert() {
        PreparedStatement statement = null;
        String sql = "INSERT INTO tempat_kkn (id_tempat, nama_tempat,lokasi,program,id_dosen,jumlah_mahasiswa) " + "VALUES(?,?,?,?,?,0);";
        try {
            statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, tID.getText());
            statement.setString(2, tNamatempat.getText());
            statement.setString(3, tLokasi.getText());
            statement.setString(4, tProgram.getText());
            statement.setInt(5, getIDDosen(cbDosen.getSelectedItem().toString()));
            statement.executeUpdate();
        } catch(SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
private int getIDDosen(String selectedItem) {
    int id = 0;
    if (selectedItem != null && selectedItem.contains(" ")) {
        // Memisahkan ID dan nama dari format "ID_NAMA"
        String[] parts = selectedItem.split(" ", 2);
        try {
            id = Integer.parseInt(parts[0]); // ID adalah bagian pertama
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    return id;
}



private int extractIDFromComboBox(String item) {
    // Memisahkan ID dan nama dari format "ID_NAMA"
    String[] parts = item.split(" ", 2);
    try {
        return Integer.parseInt(parts[0]); // ID adalah bagian pertama
    } catch (NumberFormatException e) {
        e.printStackTrace();
        return 0; // Return 0 atau nilai default jika parsing gagal
    }
}


    
//    private int getIDTempat(String nama_tempat){
//    int id = 0;
//    try {
//        PreparedStatement st = connection.prepareStatement
//        ("SELECT id_tempat FROM dosen WHERE nama_dosen=?", Statement.RETURN_GENERATED_KEYS);
//        st.setString(1, nama_dosen);
//        ResultSet rs = st.executeQuery();
//        while (rs.next()){
//            id = rs.getInt("id_dosen");
//        }
//        return id;
//    } catch (SQLException ex) {
//        Logger.getLogger(FMahasiswa.class.getName()).log(Level.SEVERE, null, ex);
//    }
//    return id;
//}
    
  private void getcbDosen() {
    cbDosen.removeAllItems(); // Hapus semua item dari combo box

    // Ambil ID dosen yang sudah terdaftar dari tabel tempat_kkn
    Set<Integer> registeredDosenIDs = new HashSet<>();
    String checkRegisteredSQL = "SELECT id_dosen FROM tempat_kkn";

    try (Statement stat = connection.createStatement();
         ResultSet res = stat.executeQuery(checkRegisteredSQL)) {

        while (res.next()) {
            registeredDosenIDs.add(res.getInt("id_dosen"));
        }
        
    } catch (SQLException err) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "SQL Error in getcbDosen", err);
    }

    // Ambil semua dosen dari tabel dosen
    String sql = "SELECT id_dosen, nama_dosen FROM dosen WHERE status = 'kkn'";

    try (Statement stat = connection.createStatement();
         ResultSet res = stat.executeQuery(sql)) {

        while (res.next()) {
            int idDosen = res.getInt("id_dosen");
            String namaDosen = res.getString("nama_dosen");
            if (registeredDosenIDs.contains(idDosen)) {
                cbDosen.addItem(idDosen + " " + namaDosen + " (V)"); // Dosen yang sudah terdaftar
            } else {
                cbDosen.addItem(idDosen + " " + namaDosen); // Dosen yang belum terdaftar
            }
        }

    } catch (SQLException err) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "SQL Error in getcbDosen", err);
    }
}



    
    private void update() {
    String sql = "UPDATE tempat_kkn SET nama_tempat = ?, lokasi = ?, program = ?, id_dosen = ? WHERE id_tempat = ?";
    
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, tNamatempat.getText().trim());
        statement.setString(2, tLokasi.getText().trim());
        statement.setString(3, tProgram.getText().trim());
        
        // Mengambil item yang dipilih dan mengekstrak ID dosen
        String selectedDosen = cbDosen.getSelectedItem().toString();
        int idDosen = extractIDFromComboBox(selectedDosen);
        
        System.out.println("Updating with ID Dosen: " + idDosen); // Baris debugging
        
        statement.setInt(4, idDosen);
        statement.setString(5, tID.getText().trim());
        
        int rowsAffected = statement.executeUpdate();
        System.out.println("Rows affected: " + rowsAffected); // Baris debugging
        
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}



    
    private void delete() {
        PreparedStatement statement = null;
        String sql = "DELETE FROM tempat_kkn WHERE id_tempat=?";
        try {
            statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, tID.getText());
            statement.executeUpdate();
        } catch(SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void search() {
        model = (DefaultTableModel) table.getModel();
        PreparedStatement statement = null;
        try {
            String sql = "SELECT * FROM dosen,tempat_kkn "
                    + "WHERE tempat_kkn.id_dosen=dosen.id_dosen AND id_tempat like ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + tCari.getText() + "%");
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                Object[ ] obj = new Object[4];
                obj[0] = res.getString("id_tempat");
                obj[1] = res.getString("nama_tempat");
                obj[2] = res.getString("lokasi");
                obj[3] = res.getString("nama_dosen");
                model.addRow(obj);
            }
        } catch(SQLException err) {
            err.printStackTrace();
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
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tNamatempat = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        bSimpan = new javax.swing.JButton();
        bUbah = new javax.swing.JButton();
        bHapus = new javax.swing.JButton();
        bReset = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tProgram = new javax.swing.JTextArea();
        cbDosen = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        tID = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tLokasi = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        tCari = new javax.swing.JTextField();
        bCari = new javax.swing.JButton();
        bRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Kelola Tempat");

        jLabel2.setText("Dosen");

        jLabel3.setText("Nama Tempat");

        tNamatempat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tNamatempatActionPerformed(evt);
            }
        });

        jLabel4.setText("Lokasi");

        bSimpan.setText("Simpan");
        bSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSimpanActionPerformed(evt);
            }
        });

        bUbah.setText("Ubah");
        bUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bUbahActionPerformed(evt);
            }
        });

        bHapus.setText("Hapus");
        bHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bHapusActionPerformed(evt);
            }
        });

        bReset.setText("Reset");
        bReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bResetActionPerformed(evt);
            }
        });

        tProgram.setColumns(20);
        tProgram.setRows(5);
        jScrollPane2.setViewportView(tProgram);

        cbDosen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("ID Tempat");

        tID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tIDActionPerformed(evt);
            }
        });

        tLokasi.setColumns(20);
        tLokasi.setRows(5);
        jScrollPane3.setViewportView(tLokasi);

        jLabel8.setText("Program");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bHapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bSimpan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bUbah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bReset, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)))
                    .addComponent(tNamatempat)
                    .addComponent(tID)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(cbDosen, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(4, 4, 4)
                .addComponent(tID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tNamatempat, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbDosen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bSimpan)
                    .addComponent(bUbah))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bHapus)
                    .addComponent(bReset))
                .addContainerGap())
        );

        jLabel5.setText("Cari Data ");

        bCari.setText("Cari");
        bCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCariActionPerformed(evt);
            }
        });

        bRefresh.setText("Refresh");
        bRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRefreshActionPerformed(evt);
            }
        });

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Tempat", "Nama Kelas", "Lokasi", "Program", "Dosen", "Jumlah Mahasiswa"
            }
        ));
        table.setToolTipText("");
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table);

        jLabel7.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N
        jLabel7.setText("(Masukkan ID Tempat)");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tCari, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(bCari)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bRefresh))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7)))
                        .addGap(0, 2, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bCari)
                    .addComponent(bRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

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
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tNamatempatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tNamatempatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tNamatempatActionPerformed

    private void bSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSimpanActionPerformed
        // TODO add your handling code here:
        
         if (!tNamatempat.getText().trim().isEmpty() &&
        !tLokasi.getText().trim().isEmpty() &&
        !tProgram.getText().trim().isEmpty() &&
        cbDosen.getSelectedItem() != null) {
        
        // Ambil dosen yang dipilih
        String selectedDosen = (String) cbDosen.getSelectedItem();
        if (selectedDosen != null && selectedDosen.contains("(V)")) {
            JOptionPane.showMessageDialog(this,
                    "Dosen yang dipilih sudah terdaftar. Silakan pilih dosen lain.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; // Keluar dari metode untuk mencegah pengiriman
        }

        // Ambil ID dosen dari item yang dipilih di cbDosen
        int idDosen = getIDDosen(selectedDosen);

        // Lakukan penyimpanan dengan idDosen dan data lainnya
        insert();
        refresh();
        reset();
        JOptionPane.showMessageDialog(this,
            "Data Tempat berhasil ditambahkan",
            "Notifikasi", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this,
            "Lengkapi form terlebih dahulu!",
            "Notifikasi", JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_bSimpanActionPerformed

    private void bUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUbahActionPerformed
        // TODO add your handling code here:
         if (!tID.getText().trim().isEmpty()) {
        if (!tNamatempat.getText().trim().isEmpty() &&
            !tLokasi.getText().trim().isEmpty() &&
            !tProgram.getText().trim().isEmpty() &&
            cbDosen.getSelectedItem() != null) {
            
            update(); // Ensure update method works as expected
            
            // Refresh and reset UI
            refresh();
            reset();
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Data Tempat berhasil diubah",
                "Notifikasi", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Lengkapi form terlebih dahulu!",
                "Notifikasi", JOptionPane.WARNING_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this,
            "Pilih data terlebih dahulu!",
            "Notifikasi", JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_bUbahActionPerformed

    private void bHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bHapusActionPerformed
        // TODO add your handling code here:
        if(!tID.getText().trim().isEmpty()) {
            int alert = JOptionPane.showConfirmDialog(this,
                    "Anda yakin ingin menghapus Data ini?",
                    "Notifikasi", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if(alert == JOptionPane.YES_OPTION) {
               delete();
               refresh();
               reset();
               JOptionPane.showMessageDialog(this,
                    "Data Tempat berhasil dihapus",
                    "Notifikasi", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
               JOptionPane.showMessageDialog(this,
                    "Pilih data terlebih dahulu!",
                    "Notifikasi", JOptionPane.WARNING_MESSAGE);
             }
    }//GEN-LAST:event_bHapusActionPerformed

    private void bResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bResetActionPerformed
        // TODO add your handling code here:
        reset();
    }//GEN-LAST:event_bResetActionPerformed

    private void bRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRefreshActionPerformed
        // TODO add your handling code here:
        refresh();
    }//GEN-LAST:event_bRefreshActionPerformed

    private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
        // TODO add your handling code here:
        String idTempat = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
    String namaTempat = table.getModel().getValueAt(table.getSelectedRow(), 1).toString();
    String lokasi = table.getModel().getValueAt(table.getSelectedRow(), 2).toString();
    String program = table.getModel().getValueAt(table.getSelectedRow(), 3).toString();
    String namaDosen = table.getModel().getValueAt(table.getSelectedRow(), 4).toString();

    // Set data ke text fields
    tID.setText(idTempat);
    tNamatempat.setText(namaTempat);
    tLokasi.setText(lokasi);
    tProgram.setText(program);

    // Cek item yang ada di combo box
    for (int i = 0; i < cbDosen.getItemCount(); i++) {
        String item = cbDosen.getItemAt(i).toString();
        // Asumsi item di combo box dalam format "ID_NAMA"
        if (item.contains(namaDosen)) {
            cbDosen.setSelectedIndex(i);
            break;
        }
    }

    tID.setEditable(false);
    bSimpan.setEnabled(false);
    }//GEN-LAST:event_tableMouseClicked

    private void bCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCariActionPerformed
        // TODO add your handling code here:
        model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        search();
    }//GEN-LAST:event_bCariActionPerformed

    private void tIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tIDActionPerformed

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
            java.util.logging.Logger.getLogger(FTempatkkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FTempatkkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FTempatkkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FTempatkkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FTempatkkn().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCari;
    private javax.swing.JButton bHapus;
    private javax.swing.JButton bRefresh;
    private javax.swing.JButton bReset;
    private javax.swing.JButton bSimpan;
    private javax.swing.JButton bUbah;
    private javax.swing.JComboBox<String> cbDosen;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField tCari;
    private javax.swing.JTextField tID;
    private javax.swing.JTextArea tLokasi;
    private javax.swing.JTextField tNamatempat;
    private javax.swing.JTextArea tProgram;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
