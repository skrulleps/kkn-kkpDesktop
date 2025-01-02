/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;
import koneksi.koneksi;
import session.session;
import model.ComboBoxItem;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author fadhl
 */
public class FDaftarkkn extends javax.swing.JFrame {
    Connection connection;
    DefaultTableModel model,model2;
//    private JComboBox cbTempatKkn;
    
  
//    String nis, id_pengguna, id_spp;
    /**
     * Creates new form FBayarSPP
     */
    public FDaftarkkn() {
        initComponents();
        connection = koneksi.getConnection();
        setTanggal();
        tabelMhs();
        tCari.requestFocus();
        getcbTempatKkn();
        generateAutoNumber();
//        this.id_pengguna=id_pengguna;
//        getCmbPetugas();
        ambilDataTableBayar();
        refresh();
        akunUser();
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
    
    private void tabelMhs() {
        model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Menghapus data lama

        String sql = "SELECT * FROM mahasiswa";

        try (Statement stat = connection.createStatement();
             ResultSet res = stat.executeQuery(sql)) {
            while (res.next()) {
                Object[] obj = new Object[3];
                obj[0] = res.getString("nim");
                obj[1] = res.getString("nama");
                obj[2] = res.getString("jurusan");
                model.addRow(obj);
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    private void generateAutoNumber() {
        try {
            Statement stat = connection.createStatement();
            String sql = "SELECT MAX(id_pendaftaran) AS max_id FROM pendaftaran_kkn";
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
    
private void insert() {
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    String sqlInsert = "INSERT INTO pendaftaran_kkn "
                     + "(id_pendaftaran, id_mahasiswa, id_tempat, id_dosen, tgl_daftar, status)"
                     + " VALUES (?, ?, ?, ?, ?, ?)";
    String sqlCheck = "SELECT COUNT(*) FROM pendaftaran_kkn WHERE id_mahasiswa = ?";

    // Ambil dan memangkas teks dari lblUsername
    String lblUsernameText = lblUsername.getText().trim();
    System.out.println("lblUsername: [" + lblUsernameText + "]");

    // Ambil 3 huruf depan dari lblUsername, dan ubah menjadi huruf kecil
    String prefix = lblUsernameText.length() >= 3 ? lblUsernameText.substring(0, 3).toLowerCase() : "";
    System.out.println("Prefix: [" + prefix + "]");

    // Ambil id_mahasiswa dari Lnim, dan ubah menjadi huruf kecil
    String idMahasiswa = Lnim.getText().trim().toLowerCase();
    System.out.println("ID Mahasiswa: [" + idMahasiswa + "]");

    // Validasi bahwa id_mahasiswa dimulai dengan prefix dari lblUsername
    if (!idMahasiswa.startsWith(prefix)) {
        JOptionPane.showMessageDialog(null,
            "ID Mahasiswa tidak sesuai.",
            "Kesalahan Validasi",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Cek apakah kuota sudah penuh
    String kuotaStatus = lblKuota.getText().trim();
    if ("kuota penuh".equalsIgnoreCase(kuotaStatus)) {
        JOptionPane.showMessageDialog(null,
            "Kuota sudah penuh. Cari tempat KKN yang lain.",
            "Peringatan Kuota",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Cek apakah checkbox telah dipilih
    if (!ckbSetuju.isSelected()) {
        JOptionPane.showMessageDialog(null,
            "Anda belum menyetujui.",
            "Kesalahan",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Periksa apakah mahasiswa sudah terdaftar
    try {
        // Cek apakah mahasiswa sudah terdaftar
        statement = connection.prepareStatement(sqlCheck);
        statement.setString(1, idMahasiswa);
        resultSet = statement.executeQuery();
        
        if (resultSet.next() && resultSet.getInt(1) > 0) {
            // Jika mahasiswa sudah terdaftar
            JOptionPane.showMessageDialog(null,
                "Anda sudah terdaftar.",
                "Informasi",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Jika mahasiswa belum terdaftar, lanjutkan dengan penyisipan data
        statement.close(); // Tutup statement sebelumnya
        
        statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, tID.getText());
        statement.setString(2, Lnim.getText()); // Pastikan penggunaan variabel yang konsisten
        statement.setString(3, Lidtempatkkn.getText());
        statement.setString(4, Liddosen.getText());
        statement.setDate(5, java.sql.Date.valueOf(java.time.LocalDate.now()));
        statement.setString(6, ckbSetuju.isSelected() ? "Terdaftar" : "Tidak");
        statement.executeUpdate();
        System.out.println("Insert successful");

    } catch (SQLException ex) {
        ex.printStackTrace();
    } finally {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}






private void getcbTempatKkn() {
    cbTempatKkn.removeAllItems(); // Hapus semua item dari combo box

    // Ambil data dari tabel tempat_kkn dan dosen
    String sql = "SELECT * "
               + "FROM tempat_kkn "
               + "JOIN dosen ON tempat_kkn.id_dosen = dosen.id_dosen";

    try (Statement stat = connection.createStatement();
         ResultSet res = stat.executeQuery(sql)) {

        while (res.next()) {
            String idTempat = res.getString("id_tempat");
            String namaTempat = res.getString("nama_tempat");
            String namaDosen = res.getString("nama_dosen");
            int jumlahMahasiswa = res.getInt("jumlah_mahasiswa");

            // Format item untuk JComboBox
            String item = idTempat + " - " + namaTempat;
            cbTempatKkn.addItem(item);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Terjadi kesalahan saat memuat data tempat KKN.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}




    
//     Method merubah tahun ke id_spp
    private int getIDDosen(String nama_dosen){
        int id = 0;
        try {
            PreparedStatement st = connection.prepareStatement
            ("SELECT nama_dosen FROM dosen WHERE id_dosen", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, nama_dosen);
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                id = rs.getInt("id_dosen");
            }
            return id;
        } catch (SQLException ex) {
            Logger.getLogger(FDaftarkkn.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
    
    private void ambilDataTableBayar() {
        model2 = (DefaultTableModel) table2.getModel();
        model2.setRowCount(0);
        try {
            Statement stat = connection.createStatement();
            String sql = "SELECT * FROM pendaftaran_kkn, mahasiswa, tempat_kkn, dosen WHERE "
                       + "pendaftaran_kkn.id_mahasiswa = mahasiswa.id_mahasiswa AND "
                       + "pendaftaran_kkn.id_tempat = tempat_kkn.id_tempat AND "
                       + "pendaftaran_kkn.id_dosen = dosen.id_dosen ";
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
                Object[] obj = new Object[6];
                obj[0] = res.getString("id_pendaftaran");
                obj[1] = res.getString("nama");
                obj[2] = res.getString("nama_tempat");
                obj[3] = res.getString("nama_dosen");
                obj[4] = res.getString("tgl_daftar");
                obj[5] = res.getString("status");
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

// Method merubah nominal ke id_spp
    private int getIDSPP(String nominal) {
            int id = 0;
            try {
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id_spp FROM spp WHERE nominal=?", Statement.RETURN_GENERATED_KEYS);
                st.setString(1, nominal);
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    id = rs.getInt("id_spp");
                }
                return id;
            } catch (SQLException ex) {
                Logger.getLogger(FDaftarkkn.class.getName()).log(Level.SEVERE, null, ex);
            }
            return id;
        }

        private void refresh() {
        model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        model2 = (DefaultTableModel) table2.getModel();
        model2.setRowCount(0);
        tCari.setText("");
        tabelMhs();
        ambilDataTableBayar();
    }

    private void search() {
        model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Menghapus data lama

        String sql = "SELECT * FROM mahasiswa WHERE nim LIKE ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            String searchKeyword = "%" + tCari.getText().trim() + "%";
            pst.setString(1, searchKeyword);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Object[] obj = new Object[3];
                    obj[0] = rs.getString("nim");
                    obj[1] = rs.getString("nama");
                    obj[2] = rs.getString("jurusan");
                    model.addRow(obj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    private void reset() {
        LidMhs.setText("...");
        Lnim.setText("...");
        Lnama.setText("...");
        Ljurusan.setText("..");
        Lidtempatkkn.setText("..");
        Lnamatempat.setText("..");
        Liddosen.setText("..");
        Lnmdosen.setText("..");
        ckbSetuju.setSelected(false);
        bDaftar.setEnabled(true);
    }
    
private void akunUser() {
    // Ambil username pengguna dari SessionManager
    String username = session.getInstance().getUsername();
    // Update teks label dengan username
    lblUsername.setText(username);
}

private void search2() {
    DefaultTableModel model = (DefaultTableModel) table2.getModel();
    model2.setRowCount(0); // Menghapus data lama

    // SQL query dengan JOIN
     String sql = "SELECT pendaftaran_kkn.id_pendaftaran, mahasiswa.nama AS nama_mahasiswa, tempat_kkn.nama_tempat, dosen.nama_dosen AS nama_dosen, pendaftaran_kkn.tgl_daftar, pendaftaran_kkn.status "
               + "FROM pendaftaran_kkn "
               + "INNER JOIN mahasiswa ON pendaftaran_kkn.id_mahasiswa = mahasiswa.id_mahasiswa "
               + "INNER JOIN tempat_kkn ON pendaftaran_kkn.id_tempat = tempat_kkn.id_tempat "
               + "INNER JOIN dosen ON pendaftaran_kkn.id_dosen = dosen.id_dosen "
               + "WHERE LOWER(tempat_kkn.nama_tempat) LIKE ?";

    try (PreparedStatement pst = connection.prepareStatement(sql)) {
        // Menyiapkan parameter pencarian dengan wildcards untuk LIKE
        String searchKeyword = "%" + tCariKkn.getText().trim().toLowerCase() + "%";
        pst.setString(1, searchKeyword);

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Object[] obj = new Object[6];
                obj[0] = rs.getString("id_pendaftaran");
                obj[1] = rs.getString("nama_mahasiswa");
                obj[2] = rs.getString("nama_tempat");
                obj[3] = rs.getString("nama_dosen");
                obj[4] = rs.getDate("tgl_daftar");
                obj[5] = rs.getString("status");
                model.addRow(obj);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tCari = new javax.swing.JTextField();
        bCariMhs = new javax.swing.JButton();
        bRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        bDaftar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table2 = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ckbSetuju = new javax.swing.JCheckBox();
        Lnim = new javax.swing.JLabel();
        Lnama = new javax.swing.JLabel();
        Ljurusan = new javax.swing.JLabel();
        Lidtempatkkn = new javax.swing.JLabel();
        Liddosen = new javax.swing.JLabel();
        lTanggal = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        LidMhs = new javax.swing.JLabel();
        Lnmdosen = new javax.swing.JLabel();
        Lnamatempat = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        lblUsername = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        cbTempatKkn = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        lblLokasi = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblProgram = new javax.swing.JLabel();
        tCariKkn = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        bCariKkn = new javax.swing.JButton();
        lblKuota = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        tID = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setText("PENDAFTARAN KKN");

        jLabel2.setText("Pencarian Data");

        jLabel3.setText("NIM");

        tCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tCariActionPerformed(evt);
            }
        });

        bCariMhs.setText("Cari");
        bCariMhs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCariMhsActionPerformed(evt);
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
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NIM", "Nama", "Jurusan"
            }
        ));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setText("Tanggal Bayar");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("NIM");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setText("Nama");

        bDaftar.setText("Daftar");
        bDaftar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDaftarActionPerformed(evt);
            }
        });

        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Pendaftaran", "Nama Mahasiswa", "Tempat KKN", "Dosen", "Tgl Daftar", "Keterangan"
            }
        ));
        jScrollPane2.setViewportView(table2);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("Jurusan");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel8.setText("Tempat KKN");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel10.setText("Dosen Pembimbing");

        ckbSetuju.setText("Setuju");
        ckbSetuju.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckbSetujuActionPerformed(evt);
            }
        });

        Lnim.setText("...");

        Lnama.setText("...");

        Ljurusan.setText("...");

        Lidtempatkkn.setText("...");

        Liddosen.setText("...");

        lTanggal.setText("dd/mm/yyyy");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel9.setText("ID Mahasiswa");

        LidMhs.setText("...");

        Lnmdosen.setText("...");

        Lnamatempat.setText("...");

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel11.setText("Akun");

        lblUsername.setEditable(false);

        jLabel12.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N
        jLabel12.setText("*) Pilih Tempat KKN");

        cbTempatKkn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTempatKknActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel13.setText("Lokasi");

        lblLokasi.setText("...");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel14.setText("Program");

        lblProgram.setText("...");

        tCariKkn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tCariKknActionPerformed(evt);
            }
        });

        jLabel15.setText("Tempat KKN");

        bCariKkn.setText("Cari");
        bCariKkn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCariKknActionPerformed(evt);
            }
        });

        lblKuota.setText(".....");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel16.setText("ID Pendaftaran");

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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tCari, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(bCariMhs)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bRefresh)
                                .addGap(166, 166, 166))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(421, 421, 421))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(29, 29, 29))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING))
                                                            .addGap(91, 91, 91))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                            .addComponent(jLabel4)
                                                            .addGap(50, 50, 50)))
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lTanggal)
                                                        .addComponent(Lnim, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(Lnama, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                            .addComponent(tID, javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(cbTempatKkn, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(Ljurusan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))))
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                    .addComponent(jLabel9)
                                                    .addGap(53, 53, 53)
                                                    .addComponent(LidMhs, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(Liddosen, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(ckbSetuju)
                                                        .addGroup(layout.createSequentialGroup()
                                                            .addComponent(jLabel8)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(Lidtempatkkn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(Lnmdosen, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(Lnamatempat, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGap(24, 24, 24))
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                    .addComponent(jLabel13)
                                                    .addGap(102, 102, 102)
                                                    .addComponent(lblLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(jLabel10)
                                            .addComponent(jLabel12)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(59, 59, 59)
                                                .addComponent(bDaftar, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel14)
                                                .addGap(87, 87, 87)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(lblProgram, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(lblKuota, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGap(15, 15, 15))
                                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(27, 27, 27)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 563, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(41, 41, 41)
                                        .addComponent(jLabel15)
                                        .addGap(18, 18, 18)
                                        .addComponent(tCariKkn, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(bCariKkn)))))
                        .addContainerGap(67, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                    .addComponent(bCariMhs)
                    .addComponent(bRefresh))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(lTanggal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(LidMhs))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(Lnim))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(Lnama))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(Ljurusan))
                                .addGap(8, 8, 8)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel16)
                                    .addComponent(tID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(cbTempatKkn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(Lidtempatkkn)
                                    .addComponent(Lnamatempat))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Liddosen)
                                    .addComponent(Lnmdosen))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblLokasi))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblProgram))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblKuota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ckbSetuju)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(bDaftar)
                                .addContainerGap())
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(22, 22, 22))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(tCariKkn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bCariKkn))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void bCariMhsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCariMhsActionPerformed
        // TODO add your handling code here:
        if (!tCari.getText().trim().isEmpty()) {
        search();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Masukkan NIM yang ingin dicari!",
                    "Notifikasi", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_bCariMhsActionPerformed

    private void bRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRefreshActionPerformed
        // TODO add your handling code here:
        refresh();
    }//GEN-LAST:event_bRefreshActionPerformed

    private void tCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tCariActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_tCariActionPerformed

    private void bDaftarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDaftarActionPerformed
        // TODO add your handling code here:
        insert();
        reset();
        refresh();
    }//GEN-LAST:event_bDaftarActionPerformed

    private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
        // TODO add your handling code here:
        int row = table.getSelectedRow();
        String getNIM = table.getValueAt(row, 0).toString();

        // Query SQL untuk mendapatkan detail mahasiswa berdasarkan NIM
        String sql = "SELECT id_mahasiswa, nim, nama, jurusan "
                   + "FROM mahasiswa "
                   + "WHERE nim = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            // Menetapkan parameter untuk PreparedStatement
            pst.setString(1, getNIM);

            // Menjalankan query dan memproses hasil
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Memperbarui label dengan data yang diperoleh
                    LidMhs.setText(rs.getString("id_mahasiswa"));
                    Lnim.setText(rs.getString("nim"));
                    Lnama.setText(rs.getString("nama"));
                    Ljurusan.setText(rs.getString("jurusan"));
                } else {
                    // Menangani kasus jika data tidak ditemukan
                    JOptionPane.showMessageDialog(this,
                            "Data mahasiswa tidak ditemukan!",
                            "Notifikasi", JOptionPane.INFORMATION_MESSAGE);

                    // Mengosongkan label jika data tidak ditemukan
                    LidMhs.setText("");
                    Lnim.setText("");
                    Lnama.setText("");
                    Ljurusan.setText("");
                }
            }
        } catch (SQLException e) {
            // Menangani kesalahan SQL
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Terjadi kesalahan saat mengambil data.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_tableMouseClicked

    private void ckbSetujuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckbSetujuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ckbSetujuActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
       dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tCariKknActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tCariKknActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tCariKknActionPerformed

    private void bCariKknActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCariKknActionPerformed
        // TODO add your handling code here:
         if (!tCariKkn.getText().trim().isEmpty()) {
            search2();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Masukkan nama tempat yang ingin dicari!",
                    "Notifikasi", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_bCariKknActionPerformed

    private void cbTempatKknActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTempatKknActionPerformed
        // TODO add your handling code here:
         // Ambil item yang dipilih
    String selectedItem = (String) cbTempatKkn.getSelectedItem();

    if (selectedItem != null) {
        // Ambil ID tempat dari item yang dipilih
        String idTempat = selectedItem.split(" - ")[0];

        // Ambil data terkait dari database
        String sql = "SELECT tempat_kkn.*, dosen.nama_dosen "
                   + "FROM tempat_kkn "
                   + "JOIN dosen ON tempat_kkn.id_dosen = dosen.id_dosen "
                   + "WHERE tempat_kkn.id_tempat = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idTempat);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String namaTempat = rs.getString("nama_tempat");
                String lokasi = rs.getString("lokasi");
                String program = rs.getString("program");
                String namaDosen = rs.getString("nama_dosen");
                int jumlahMahasiswa = rs.getInt("jumlah_mahasiswa");

                // Update label dengan data dari database
                Lidtempatkkn.setText(idTempat);
                Lnamatempat.setText(namaTempat);
                Liddosen.setText(rs.getString("id_dosen"));
                Lnmdosen.setText(namaDosen);
                lblLokasi.setText(lokasi);
                lblProgram.setText(program);

                // Tambahkan tanda kuota penuh jika diperlukan
                if (jumlahMahasiswa >= 4) {
                    lblKuota.setText("Kuota Penuh");
                } else {
                    lblKuota.setText("");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Terjadi kesalahan saat memuat detail tempat KKN.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_cbTempatKknActionPerformed

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
            java.util.logging.Logger.getLogger(FDaftarkkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FDaftarkkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FDaftarkkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FDaftarkkn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FDaftarkkn().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LidMhs;
    private javax.swing.JLabel Liddosen;
    private javax.swing.JLabel Lidtempatkkn;
    private javax.swing.JLabel Ljurusan;
    private javax.swing.JLabel Lnama;
    private javax.swing.JLabel Lnamatempat;
    private javax.swing.JLabel Lnim;
    private javax.swing.JLabel Lnmdosen;
    private javax.swing.JButton bCariKkn;
    private javax.swing.JButton bCariMhs;
    private javax.swing.JButton bDaftar;
    private javax.swing.JButton bRefresh;
    private javax.swing.JComboBox<String> cbTempatKkn;
    private javax.swing.JCheckBox ckbSetuju;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
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
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lTanggal;
    private javax.swing.JLabel lblKuota;
    private javax.swing.JLabel lblLokasi;
    private javax.swing.JLabel lblProgram;
    private javax.swing.JTextField lblUsername;
    private javax.swing.JTextField tCari;
    private javax.swing.JTextField tCariKkn;
    private javax.swing.JTextField tID;
    private javax.swing.JTable table;
    private javax.swing.JTable table2;
    // End of variables declaration//GEN-END:variables
}
