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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author fadhl
 */
public class FEvaluasikkn extends javax.swing.JFrame {
    Connection connection;
    DefaultTableModel model,model2;
//    String nis, id_pengguna, id_spp;
    /**
     * Creates new form FBayarSPP
     */
    public FEvaluasikkn() {
        initComponents();
        connection = koneksi.getConnection();
        setTanggal();
        getDataTable();
        tCari.requestFocus();
        generateAutoNumber();

//        this.id_pengguna=id_pengguna;
//        getCmbPetugas();
        ambilDataTableBayar();
        refresh();
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
    
    private void getDataTable() {
        model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        try {
            Statement stat = connection.createStatement();
            String sql = "SELECT * FROM pendaftaran_kkn,mahasiswa,tempat_kkn "
                    + "WHERE pendaftaran_kkn.id_mahasiswa=mahasiswa.id_mahasiswa AND "
                    + "pendaftaran_kkn.id_tempat=tempat_kkn.id_tempat ";
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
                Object[] obj = new Object[5];
                obj[0] = res.getString("id_pendaftaran");
                obj[1] = res.getString("nim");
                obj[2] = res.getString("nama");
                obj[3] = res.getString("jurusan");
                obj[4] = res.getString("nama_tempat");
                model.addRow(obj);
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }
    
    private void insert() {
        PreparedStatement statement = null;
        String sql = "INSERT INTO evaluasi "
                    + "(id_evaluasi, tgl, id_pendaftaran,id_mahasiswa, nilai, keterangan)"
                    + " VALUES (?, ?, ?, ?, ?, ?)";
        try {
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//            statement.setInt(1, getIDPengguna(cmbPengguna.getSelectedItem().toString()));
            // statement.setString(1, id_pengguna)
            statement.setString(1, LidEval.getText());
            statement.setString(2, java.sql.Date.valueOf(java.time.LocalDate.now()).toString());
            statement.setString(3, LIDPendaftaran.getText());
            statement.setString(4, LidMhs.getText());
            statement.setString(5, tNilai.getText());
            statement.setString(6, LKet.getText());
//            statement.setInt(5, tTahun.getYear());
//            statement.setInt(6, getIDDosen(Liddosen.getText()));
//            statement.setString(7, tJumlah.getText());
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
//     Method merubah tahun ke id_spp
//    private int getIDDosen(String nama_dosen){
//        int id = 0;
//        try {
//            PreparedStatement st = connection.prepareStatement
//            ("SELECT nama_dosen FROM dosen WHERE id_dosen", Statement.RETURN_GENERATED_KEYS);
//            st.setString(1, nama_dosen);
//            ResultSet rs = st.executeQuery();
//            while (rs.next()){
//                id = rs.getInt("id_dosen");
//            }
//            return id;
//        } catch (SQLException ex) {
//            Logger.getLogger(FEvaluasi.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return id;
//    }
    
    private void ambilDataTableBayar() {
        model2 = (DefaultTableModel) table2.getModel();
        model2.setRowCount(0);
        try {
            Statement stat = connection.createStatement();
            String sql = "SELECT * FROM pendaftaran_kkn, mahasiswa, tempat_kkn, evaluasi WHERE "
                       + "evaluasi.id_pendaftaran = pendaftaran_kkn.id_pendaftaran AND "
                       + "pendaftaran_kkn.id_tempat = tempat_kkn.id_tempat AND "
                       + "pendaftaran_kkn.id_mahasiswa = mahasiswa.id_mahasiswa ";
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
                Object[] obj = new Object[7];
                obj[0] = res.getString("tgl_daftar");
                obj[1] = res.getString("id_pendaftaran");
                obj[2] = res.getString("nim");
                obj[3] = res.getString("nama");
                obj[4] = res.getString("nama_tempat");
                obj[5] = res.getString("nilai");
                obj[6] = res.getString("keterangan");
                model2.addRow(obj);
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }
    
//    private void getCmbPetugas() {
//        cmbPengguna.removeAllItems();
//        try {
//            Statement stat = connection.createStatement();
//            String sql = "SELECT * FROM pengguna";
//            ResultSet res = stat.executeQuery(sql);
//            while (res.next()) {
//                cmbPengguna.addItem(res.getString("nama_pengguna"));
//            }
//        } catch (SQLException err) {
//            err.printStackTrace();
//        }
//    }
    
    // Method merubah nama_pengguna ke id_pengguna
//    private int getIDPengguna(String nama_pengguna) {
//        int id = 0;
//        try {
//            PreparedStatement st = connection.prepareStatement(
//                    "SELECT id_pengguna FROM pengguna WHERE nama_pengguna=?", Statement.RETURN_GENERATED_KEYS);
//            st.setString(1, nama_pengguna);
//            ResultSet rs = st.executeQuery();
//            while (rs.next()) {
//                id = rs.getInt("id_pengguna");
//            }
//            return id;
//        } catch (SQLException ex) {
//            Logger.getLogger(FDaftarkkn.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return id;
//    }

private void generateAutoNumber() {
        try {
            Statement stat = connection.createStatement();
            String sql = "SELECT MAX(id_evaluasi) AS max_id FROM evaluasi";
            ResultSet res = stat.executeQuery(sql);

            if (res.next()) {
                int maxId = res.getInt("max_id");
                int newId = maxId + 1;
                LidEval.setText(String.valueOf(newId));
            } else {
                // Handle the case where no records exist yet
                LidEval.setText("1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
}

        private void refresh() {
        model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        model2 = (DefaultTableModel) table2.getModel();
        model2.setRowCount(0);
        tCari.setText("");
        getDataTable();
        ambilDataTableBayar();
        LKet.setText("..");
        generateAutoNumber();
    }

   private void search() {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    model.setRowCount(0); // Menghapus data lama

    // SQL query dengan JOIN
    String sql = "SELECT pendaftaran_kkn.id_pendaftaran, mahasiswa.nim, mahasiswa.nama, mahasiswa.jurusan, tempat_kkn.nama_tempat "
               + "FROM pendaftaran_kkn "
               + "INNER JOIN mahasiswa ON pendaftaran_kkn.id_mahasiswa = mahasiswa.id_mahasiswa "
               + "INNER JOIN tempat_kkn ON pendaftaran_kkn.id_tempat = tempat_kkn.id_tempat "
               + "WHERE LOWER(tempat_kkn.nama_tempat) LIKE ?";

    try (PreparedStatement pst = connection.prepareStatement(sql)) {
        // Menyiapkan parameter pencarian dengan wildcards untuk LIKE
        String searchKeyword = "%" + tCari.getText().trim().toLowerCase() + "%";
        pst.setString(1, searchKeyword);

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Object[] obj = new Object[5];
                obj[0] = rs.getString("id_pendaftaran");
                obj[1] = rs.getString("nim");
                obj[2] = rs.getString("nama");
                obj[3] = rs.getString("jurusan");
                obj[4] = rs.getString("nama_tempat");
                model.addRow(obj);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}



    
    private void reset() {
        LIDPendaftaran.setText("...");
        Lnim.setText("...");
        Lnama.setText("...");
        Ljurusan.setText("..");
        Lidtempatkkn.setText("..");
        Lnamatempat.setText("..");
        tNilai.setText(" ");
        LKet.setText("..");
        bSubmit.setEnabled(true);
    }
//    private void update(){
//    PreparedStatement statement = null;
//    String sql = "UPDATE evaluasi SET nilai=?, keterangan=? WHERE id_evaluasi=?";
//    try {
//        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//        statement.setString(1, tNilai.getText());
//        statement.setString(2, LKet.getText());
//        statement.setString(3, LID.getText());
//        statement.executeUpdate();
//    } catch (SQLException ex) {
//        ex.printStackTrace();
//    } finally {
//        try {
//            statement.close();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//    }
//}
    
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tCari = new javax.swing.JTextField();
        bCari = new javax.swing.JButton();
        bRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        bSubmit = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table2 = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        Lnim = new javax.swing.JLabel();
        Lnama = new javax.swing.JLabel();
        Ljurusan = new javax.swing.JLabel();
        Lidtempatkkn = new javax.swing.JLabel();
        lTanggal = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        LIDPendaftaran = new javax.swing.JLabel();
        LKet = new javax.swing.JLabel();
        Lnamatempat = new javax.swing.JLabel();
        tNilai = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        LidMhs = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        LidEval = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setText("Evaluasi / Penilaian KKN");

        jLabel2.setText("Pencarian Data");

        jLabel3.setText("Masukan Nama Tempat");

        tCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tCariActionPerformed(evt);
            }
        });

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

        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseClicked(evt);
            }
        });

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Pendaftaran", "NIM", "Nama", "Jurusan", "Tempat KKN"
            }
        ));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setText("Tanggal");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("NIM");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setText("Nama");

        bSubmit.setText("Submit");
        bSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSubmitActionPerformed(evt);
            }
        });

        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Tanggal", "ID Pendaftaran", "NIM", "Nama Mahasiswa", "Tempat KKN", "Nilai", "Keterangan"
            }
        ));
        table2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(table2);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("Jurusan");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel8.setText("Tempat KKN");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel10.setText("Nilai");

        Lnim.setText("...");

        Lnama.setText("...");

        Ljurusan.setText("...");

        Lidtempatkkn.setText("...");

        lTanggal.setText("dd/mm/yyyy");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel9.setText("ID Pendaftaran");

        LIDPendaftaran.setText("...");

        LKet.setText("...");

        Lnamatempat.setText("...");

        tNilai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tNilaiActionPerformed(evt);
            }
        });
        tNilai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tNilaiKeyPressed(evt);
            }
        });

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        LidMhs.setText("...");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel11.setText("ID Evaluasi");

        LidEval.setText("...");

        jButton2.setText("Cetak Mahasiswa Terdaftar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Cetak");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(421, 421, 421))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(LKet, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(25, 25, 25))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING))
                                                    .addGap(91, 91, 91))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel9)
                                                    .addGap(43, 43, 43))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel5)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(LidMhs, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel11)
                                                    .addComponent(jLabel4))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(LidEval, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(LIDPendaftaran, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lTanggal)
                                            .addComponent(Lnim, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Lnama, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Ljurusan, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(60, 60, 60))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING))
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGap(22, 22, 22)
                                                        .addComponent(Lidtempatkkn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(Lnamatempat, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                        .addGap(64, 64, 64)
                                                        .addComponent(tNilai, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addGap(11, 11, 11)
                                                .addComponent(bSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton3)))
                                        .addGap(91, 91, 91)))
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 563, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(tCari, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(bCari)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bRefresh)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 867, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(20, 20, 20))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(315, 315, 315)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(tCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bCari)
                            .addComponent(bRefresh)
                            .addComponent(jButton2))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(LidEval))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(lTanggal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(LIDPendaftaran))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(Lnim)
                            .addComponent(LidMhs))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(Lnama))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(Ljurusan))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(Lidtempatkkn)
                            .addComponent(Lnamatempat))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jLabel10))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(tNilai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LKet)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bSubmit)
                            .addComponent(jButton3))))
                .addGap(22, 22, 22))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void bCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCariActionPerformed
        // TODO add your handling code here:
         if (!tCari.getText().trim().isEmpty()) {
        search(); // Pastikan search() adalah metode yang benar
    } else {
        JOptionPane.showMessageDialog(this,
                "Masukkan nama tempat yang ingin dicari!",
                "Notifikasi", JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_bCariActionPerformed

    private void bRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRefreshActionPerformed
        // TODO add your handling code here:
        refresh();
    }//GEN-LAST:event_bRefreshActionPerformed

    private void tCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tCariActionPerformed

    private void bSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSubmitActionPerformed
        // TODO add your handling code here:
        insert();
        reset();
        refresh();
    }//GEN-LAST:event_bSubmitActionPerformed

    private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
        // TODO add your handling code here:
        String getNIM = table.getValueAt(table.getSelectedRow(), 0).toString();
        String getNama = table.getValueAt(table.getSelectedRow(), 1).toString();
        // get data dari Table berdasarkan NIS
        try{
            String sql = "SELECT * FROM pendaftaran_kkn,mahasiswa,tempat_kkn "
                    + "WHERE pendaftaran_kkn.id_mahasiswa=mahasiswa.id_mahasiswa AND "
                    + "pendaftaran_kkn.id_tempat=tempat_kkn.id_tempat AND id_pendaftaran = '"+getNIM+"'";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()) {
                LIDPendaftaran.setText(rs.getString("id_pendaftaran"));
                LidMhs.setText(rs.getString("id_mahasiswa"));
                Lnim.setText(rs.getString("nim"));
                Lnama.setText(rs.getString("nama"));
                Ljurusan.setText(rs.getString("jurusan"));
                Lidtempatkkn.setText(rs.getString("id_tempat"));
                Lnamatempat.setText(rs.getString("nama_tempat"));
//                Liddosen.setText(rs.getString("id_dosen"));
//                LKet.setText(rs.getString("nama_dosen"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_tableMouseClicked

    private void tNilaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tNilaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tNilaiActionPerformed

    private void tNilaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tNilaiKeyPressed
        // TODO add your handling code here:
       String nilaiStr = tNilai.getText();
        int nilai = 0;

        try {
            nilai = Integer.parseInt(nilaiStr);
        } catch (NumberFormatException e) {
            LKet.setText("Nilai harus berupa angka.");
            return;
        }

        String keterangan;
        if (nilai >= 80 && nilai <= 100) {
            keterangan = "A (Sangat Baik)";
        } else if (nilai >= 70 && nilai < 80) {
            keterangan = "B (Baik)";
        } else if (nilai >= 60 && nilai < 70) {
            keterangan = "C (Sangat Cukup)";
        } else if (nilai >= 50 && nilai < 60) {
            keterangan = "D (Cukup)";
        } else if (nilai >= 0 && nilai < 50) {
            keterangan = "E (Ulang Tahun Depan)";
        } else {
            keterangan = "Nilai di luar jangkauan.";
        }

        LKet.setText(keterangan);


    }//GEN-LAST:event_tNilaiKeyPressed

    private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane1MouseClicked

    private void table2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_table2MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        JPrmkkn F = new JPrmkkn();
        F.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        JPrmNilaikkn rkkn = new JPrmNilaikkn();
        rkkn.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    /**^
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
            java.util.logging.Logger.getLogger(FEvaluasikkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FEvaluasikkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FEvaluasikkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FEvaluasikkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new FEvaluasikkn().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LIDPendaftaran;
    private javax.swing.JLabel LKet;
    private javax.swing.JLabel LidEval;
    private javax.swing.JLabel LidMhs;
    private javax.swing.JLabel Lidtempatkkn;
    private javax.swing.JLabel Ljurusan;
    private javax.swing.JLabel Lnama;
    private javax.swing.JLabel Lnamatempat;
    private javax.swing.JLabel Lnim;
    private javax.swing.JButton bCari;
    private javax.swing.JButton bRefresh;
    private javax.swing.JButton bSubmit;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lTanggal;
    private javax.swing.JTextField tCari;
    private javax.swing.JTextField tNilai;
    private javax.swing.JTable table;
    private javax.swing.JTable table2;
    // End of variables declaration//GEN-END:variables
}
