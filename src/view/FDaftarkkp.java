/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;
import koneksi.koneksi;
import session.session;
import java.sql.*;
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
public class FDaftarkkp extends javax.swing.JFrame {
    Connection connection;
    DefaultTableModel model,model2,model3;
    /**
     * Creates new form FDaftarkkp
     */
    public FDaftarkkp() {
        initComponents();
        connection = koneksi.getConnection();
        setTanggal();
        tabelMhs();
        tabelDaftarkkp();
        tabelkkpdetail();
        tCari.requestFocus();
        generateAutoNumber();
        autoNumberTempatkkp();
//        refresh();
        akunUser();
        bDaftar.setEnabled(false);
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
    
    private void akunUser() {
        // Ambil username pengguna dari SessionManager
        String username = session.getInstance().getUsername();
        // Update teks label dengan username
        lblUsername.setText(username);
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
    
    private void refresh() {
        model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        model2 = (DefaultTableModel) table2.getModel();
        model2.setRowCount(0);
        tCari.setText("");
        tabelMhs();
        tabelDaftarkkp();
        tabelkkpdetail();
    }
    
     private void tabelDaftarkkp() {
        model2 = (DefaultTableModel) table2.getModel();
        model2.setRowCount(0);
        try {
            Statement stat = connection.createStatement();
           String sql = "SELECT * FROM pendaftaran_kkp, mahasiswa, tempat_kkp WHERE "
                       + "pendaftaran_kkp.id_mahasiswa = mahasiswa.id_mahasiswa AND "
                       + "pendaftaran_kkp.id_tempat = tempat_kkp.id_tempat "
                       + "ORDER BY pendaftaran_kkp.id_mahasiswa DESC";

            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
                Object[] obj = new Object[4];
                obj[0] = res.getString("id_pendaftaran");
                obj[1] = res.getString("nama");
                obj[2] = res.getString("nama_perusahaan");
                obj[3] = res.getString("tgl_daftar");
                model2.addRow(obj);
            }
            checkMahasiswaStatus();
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }
     
     private void tabelkkpdetail() {
        model3 = (DefaultTableModel) tabel3.getModel();
        model3.setRowCount(0);
        try {
            Statement stat = connection.createStatement();
            String sql = "SELECT pendaftaran_kkpdetail.id_pendaftaran, dosen.nama_dosen, pendaftaran_kkpdetail.status "
                   + "FROM pendaftaran_kkpdetail "
                   + "INNER JOIN dosen ON pendaftaran_kkpdetail.id_dosen = dosen.id_dosen "
                   + "ORDER BY pendaftaran_kkpdetail.id_pendaftaran DESC";

            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
                Object[] obj = new Object[3];
                obj[0] = res.getString("id_pendaftaran");
                obj[1] = res.getString("nama_dosen");
                obj[2] = res.getString("status");
                model3.addRow(obj);
            }
            updateLstatusIfTableEmpty();
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }
     
   private void insertPendaftaranKkp() {
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    
    // SQL untuk memasukkan data ke tabel pendaftaran_kkp
    String sqlInsert = "INSERT INTO pendaftaran_kkp "
                     + "(id_pendaftaran, id_mahasiswa, id_tempat, tgl_daftar) "
                     + "VALUES (?, ?, ?, ?)";
    
    // SQL untuk memeriksa apakah mahasiswa sudah terdaftar di tempat tersebut
    String sqlCheck = "SELECT COUNT(*) FROM pendaftaran_kkp "
                      + "WHERE id_mahasiswa = ? AND id_tempat = ?";

    // Ambil data dari GUI
    String idPendaftaran = tID.getText().trim();
    String idMahasiswa = LidMhs.getText().trim();
    String idTempat = tIDtempat.getText().trim();
    java.sql.Date tglDaftar = java.sql.Date.valueOf(java.time.LocalDate.now());

    // Ambil dan memangkas teks dari lblUsername
    String lblUsernameText = lblUsername.getText().trim();
    // Ambil 3 huruf depan dari lblUsername, dan ubah menjadi huruf kecil
    String prefix = lblUsernameText.length() >= 3 ? lblUsernameText.substring(0, 3).toLowerCase() : "";

    // Ambil id_mahasiswa dari Lnim
    String nim = Lnim.getText().trim();
    String nimPrefix = nim.length() >= 3 ? nim.substring(0, 3).toLowerCase() : "";

    // Validasi ID Pendaftaran, ID Mahasiswa, dan ID Tempat tidak kosong
    if (idPendaftaran.isEmpty() || idMahasiswa.isEmpty() || idTempat.isEmpty()) {
        JOptionPane.showMessageDialog(null,
            "ID Pendaftaran, ID Mahasiswa, dan ID Tempat tidak boleh kosong.",
            "Kesalahan Validasi",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Validasi bahwa NIM dimulai dengan prefix dari lblUsername
    if (!nimPrefix.equals(prefix)) {
        JOptionPane.showMessageDialog(null,
            "NIM tidak sesuai dengan tiga huruf pertama dari username.",
            "Kesalahan Validasi",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    try (PreparedStatement checkStatement = connection.prepareStatement(sqlCheck)) {
        // Cek apakah mahasiswa sudah terdaftar di tempat tersebut
        checkStatement.setString(1, idMahasiswa);
        checkStatement.setString(2, idTempat);
        try (ResultSet checkResultSet = checkStatement.executeQuery()) {
            if (checkResultSet.next() && checkResultSet.getInt(1) > 0) {
                // Jika mahasiswa sudah terdaftar di tempat tersebut
                JOptionPane.showMessageDialog(null,
                    "Mahasiswa sudah terdaftar di tempat ini.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        // Jika mahasiswa belum terdaftar, lanjutkan dengan penyisipan data
        try (PreparedStatement insertStatement = connection.prepareStatement(sqlInsert)) {
            insertStatement.setString(1, idPendaftaran); // ID Pendaftaran
            insertStatement.setString(2, idMahasiswa); // ID Mahasiswa
            insertStatement.setString(3, idTempat); // ID Tempat
            insertStatement.setDate(4, tglDaftar); // Tanggal Daftar
            insertStatement.executeUpdate();

            // Notifikasi bahwa data berhasil disimpan
            JOptionPane.showMessageDialog(null,
                "Data berhasil disimpan.",
                "Informasi",
                JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Insert successful");
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Terjadi kesalahan saat menyimpan data.",
            "Kesalahan",
            JOptionPane.ERROR_MESSAGE);
    }
}





     
     private void insertTempatKkp() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        // SQL untuk memasukkan data ke tabel tempat_kkp
        String sqlInsert = "INSERT INTO tempat_kkp "
                         + "(id_tempat, nama_perusahaan, alamat, nama_pembimbing, notelp, tgl_mulai, tgl_berakhir) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // SQL untuk memeriksa apakah ID Tempat sudah ada
        String sqlCheck = "SELECT COUNT(*) FROM tempat_kkp WHERE id_tempat = ?";

        // Ambil data dari GUI
        String idTempat = tIDtempat.getText().trim();
        String namaPerusahaan = tNamaPerusahaan.getText().trim();
        String alamat = tAlamat.getText().trim();
        String namaPembimbing = tNmPembimbing.getText().trim();
        String notelp = tNotelp.getText().trim();
        java.sql.Date tglMulai = new java.sql.Date(jdcMulai.getDate().getTime());
        java.sql.Date tglBerakhir = new java.sql.Date(jdcAkhir.getDate().getTime());

        // Validasi ID Tempat tidak kosong
        if (idTempat.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "ID Tempat tidak boleh kosong.",
                "Kesalahan Validasi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Cek apakah ID Tempat sudah ada
            try (PreparedStatement checkStatement = connection.prepareStatement(sqlCheck)) {
                checkStatement.setString(1, idTempat);
                try (ResultSet checkResultSet = checkStatement.executeQuery()) {
                    if (checkResultSet.next() && checkResultSet.getInt(1) > 0) {
                        // Jika ID Tempat sudah ada
                        JOptionPane.showMessageDialog(null,
                            "ID Tempat sudah ada.",
                            "Informasi",
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }

            // Jika ID Tempat belum ada, lanjutkan dengan penyisipan data
            try (PreparedStatement insertStatement = connection.prepareStatement(sqlInsert)) {
                insertStatement.setString(1, idTempat); // ID Tempat
                insertStatement.setString(2, namaPerusahaan); // Nama Perusahaan
                insertStatement.setString(3, alamat); // Alamat
                insertStatement.setString(4, namaPembimbing); // Nama Pembimbing
                insertStatement.setString(5, notelp); // No Telp
                insertStatement.setDate(6, tglMulai); // Tanggal Mulai
                insertStatement.setDate(7, tglBerakhir); // Tanggal Berakhir
                insertStatement.executeUpdate();

                // Notifikasi bahwa data berhasil disimpan
                JOptionPane.showMessageDialog(null,
                    "Data berhasil disimpan.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Insert successful");

                // Aktifkan tombol bDaftar jika data berhasil disimpan
                bDaftar.setEnabled(true);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Terjadi kesalahan saat menyimpan data.",
                "Kesalahan",
                JOptionPane.ERROR_MESSAGE);

            // Nonaktifkan tombol bDaftar jika terjadi kesalahan
            bDaftar.setEnabled(false);
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


     
     private void generateAutoNumber() {
        try {
            Statement stat = connection.createStatement();
            String sql = "SELECT MAX(id_pendaftaran) AS max_id FROM pendaftaran_kkp";
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
     
     private void autoNumberTempatkkp() {
        try {
            Statement stat = connection.createStatement();
            String sql = "SELECT MAX(id_tempat) AS max_id FROM tempat_kkp";
            ResultSet res = stat.executeQuery(sql);

            if (res.next()) {
                int maxId = res.getInt("max_id");
                int newId = maxId + 1;
                tIDtempat.setText(String.valueOf(newId));
            } else {
                // Handle the case where no records exist yet
                tIDtempat.setText("1");
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
//        Lidtempatkkn.setText("..");
//        Lnamatempat.setText("..");
//        Liddosen.setText("..");
//        Lnmdosen.setText("..");
        bDaftar.setEnabled(false);
        generateAutoNumber();
        autoNumberTempatkkp();
        tNamaPerusahaan.setText("");
        tAlamat.setText("");
        tNmPembimbing.setText("");
        tNotelp.setText("");
        tNotelp.setText("");
    }
     
     private void updateLstatusIfTableEmpty() {
        // Pastikan model tabel yang digunakan adalah DefaultTableModel
        DefaultTableModel model3 = (DefaultTableModel) tabel3.getModel();

        // Cek apakah tabel kosong
        if (model3.getRowCount() == 0) {
            // Jika tabel kosong, set Lstatus menjadi 'Not Approved'
            Lstatus.setText("Not Approved");
        }
    }
     
    private void checkMahasiswaStatus() {
        String idMahasiswa = LidMhs.getText().trim();
        boolean isRegistered = false;

        // Dapatkan model tabel dari tabel2
        DefaultTableModel model = (DefaultTableModel) table2.getModel();

        // Iterasi melalui baris tabel untuk mencari ID Mahasiswa
        for (int i = 0; i < model.getRowCount(); i++) {
            String mahasiswaInTable = model.getValueAt(i, 0).toString(); // Sesuaikan dengan kolom ID Mahasiswa
            if (mahasiswaInTable.equals(idMahasiswa)) {
                isRegistered = true;
                break;
            }
        }

        // Aktifkan atau nonaktifkan tombol berdasarkan status
        bOk.setEnabled(!isRegistered);
        bDaftar.setEnabled(!isRegistered);
    }






    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tCari = new javax.swing.JTextField();
        bCariMhs = new javax.swing.JButton();
        bRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        lblUsername = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lTanggal = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        LidMhs = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        Lnim = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        Lnama = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        Ljurusan = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table2 = new javax.swing.JTable();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel3 = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        tID = new javax.swing.JTextField();
        tIDtempat = new javax.swing.JTextField();
        tNamaPerusahaan = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tAlamat = new javax.swing.JTextArea();
        tNmPembimbing = new javax.swing.JTextField();
        tNotelp = new javax.swing.JTextField();
        jdcMulai = new com.toedter.calendar.JDateChooser();
        jdcAkhir = new com.toedter.calendar.JDateChooser();
        bCancel = new javax.swing.JButton();
        bOk = new javax.swing.JButton();
        bDaftar = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        Lstatus = new javax.swing.JLabel();

        jButton2.setText("OK");

        jButton3.setText("Cancel");

        jCheckBox1.setText("jCheckBox1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setText("PENDAFTARAN KKP");

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel11.setText("Akun");

        lblUsername.setEditable(false);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setText("Tanggal Bayar");

        lTanggal.setText("dd/mm/yyyy");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel9.setText("ID Mahasiswa");

        LidMhs.setText("...");
        LidMhs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                LidMhsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                LidMhsFocusLost(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("NIM");

        Lnim.setText("...");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setText("Nama");

        Lnama.setText("...");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("Jurusan");

        Ljurusan.setText("...");

        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Pendaftaran", "Nama Mahasiswa", "Tempat KKP", "Tgl Daftar"
            }
        ));
        jScrollPane2.setViewportView(table2);

        tabel3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID Pendaftaran", "Nama Dosen", "Status"
            }
        ));
        jScrollPane3.setViewportView(tabel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(187, 187, 187)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(255, 255, 255))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tCari, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(bCariMhs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bRefresh)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(35, 35, 35))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel11))
                                .addGap(32, 32, 32)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lTanggal)
                            .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Ljurusan, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lnama, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LidMhs, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lnim, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator2)))
                .addGap(27, 27, 27))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(21, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 910, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(26, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bCariMhs)
                    .addComponent(bRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 173, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lTanggal)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(LidMhs))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(Lnim))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(Lnama))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(Ljurusan))
                        .addGap(87, 87, 87))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(107, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(282, Short.MAX_VALUE)))
        );

        jLabel12.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N
        jLabel12.setText("*) Isi Form KKP");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel16.setText("ID Pendaftaran");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel8.setText("ID Tempat");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel10.setText("Nama Perusahaan");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel14.setText("Nama Pembimbing Perusahaan");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel15.setText("No Telepon / Handphone PP");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel17.setText("Alamat");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel18.setText("Tanggal Mulai");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel19.setText("Tanggal Berakhir");

        tAlamat.setColumns(20);
        tAlamat.setRows(5);
        jScrollPane4.setViewportView(tAlamat);

        tNotelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tNotelpActionPerformed(evt);
            }
        });

        bCancel.setText("Cancel");
        bCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCancelActionPerformed(evt);
            }
        });

        bOk.setText("OK");
        bOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOkActionPerformed(evt);
            }
        });

        bDaftar.setText("Daftar");
        bDaftar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDaftarActionPerformed(evt);
            }
        });

        jLabel13.setText("*) Jika KKP Disetujui maka akan muncul akan muncul \"Approve\" beserta nama Dosen Pembimibng,");

        jLabel20.setText("Jika tidak \"Not Approve\" ");

        Lstatus.setFont(new java.awt.Font("Tahoma", 3, 24)); // NOI18N
        Lstatus.setText("....");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel16)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tID, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel8)
                                    .addGap(135, 135, 135)
                                    .addComponent(tIDtempat, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(155, 155, 155)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(85, 85, 85)
                                .addComponent(tNamaPerusahaan, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(58, 58, 58)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(bOk)
                                .addGap(18, 18, 18)
                                .addComponent(bCancel)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tNotelp, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tNmPembimbing, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jdcMulai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jdcAkhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(bDaftar, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(153, 153, 153))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel13)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(jLabel20)
                                .addGap(54, 54, 54)
                                .addComponent(Lstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(tID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tNmPembimbing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(tIDtempat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tNotelp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tNamaPerusahaan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jdcMulai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jdcAkhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bOk)
                            .addComponent(bCancel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(bDaftar)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(Lstatus)))
                .addGap(0, 19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tCariActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_tCariActionPerformed

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

    private void tNotelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tNotelpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tNotelpActionPerformed

    private void bDaftarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDaftarActionPerformed
        // TODO add your handling code here:
        insertPendaftaranKkp();
        refresh();
//        reset();
    }//GEN-LAST:event_bDaftarActionPerformed

    private void bOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOkActionPerformed
        // TODO add your handling code here:
        insertTempatKkp();
        refresh();
    }//GEN-LAST:event_bOkActionPerformed

    private void bCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCancelActionPerformed
        // TODO add your handling code here:
        reset();
    }//GEN-LAST:event_bCancelActionPerformed

    private void LidMhsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_LidMhsFocusGained
        // TODO add your handling code here:
        
    }//GEN-LAST:event_LidMhsFocusGained

    private void LidMhsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_LidMhsFocusLost
        // TODO add your handling code here:
        
    }//GEN-LAST:event_LidMhsFocusLost

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
            java.util.logging.Logger.getLogger(FDaftarkkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FDaftarkkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FDaftarkkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FDaftarkkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FDaftarkkp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LidMhs;
    private javax.swing.JLabel Ljurusan;
    private javax.swing.JLabel Lnama;
    private javax.swing.JLabel Lnim;
    private javax.swing.JLabel Lstatus;
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bCariMhs;
    private javax.swing.JButton bDaftar;
    private javax.swing.JButton bOk;
    private javax.swing.JButton bRefresh;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private com.toedter.calendar.JDateChooser jdcAkhir;
    private com.toedter.calendar.JDateChooser jdcMulai;
    private javax.swing.JLabel lTanggal;
    private javax.swing.JTextField lblUsername;
    private javax.swing.JTextArea tAlamat;
    private javax.swing.JTextField tCari;
    private javax.swing.JTextField tID;
    private javax.swing.JTextField tIDtempat;
    private javax.swing.JTextField tNamaPerusahaan;
    private javax.swing.JTextField tNmPembimbing;
    private javax.swing.JTextField tNotelp;
    private javax.swing.JTable tabel3;
    private javax.swing.JTable table;
    private javax.swing.JTable table2;
    // End of variables declaration//GEN-END:variables
}
