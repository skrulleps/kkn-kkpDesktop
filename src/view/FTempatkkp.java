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
public class FTempatkkp extends javax.swing.JFrame {
    Connection connection;
    DefaultTableModel model,model2,model3;
    /**
     * Creates new form FDaftarkkp
     */
    public FTempatkkp() {
        initComponents();
        connection = koneksi.getConnection();
        setTanggal();
        tabelPendaftaran();
        tabeltempatkkp();
        tCari.requestFocus();
        generateAutoNumber();
//        refresh();
        akunUser();
        getcbDosen();
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
    
    private void tabelPendaftaran() {
    model = (DefaultTableModel) table.getModel();
    model.setRowCount(0); // Menghapus data lama

    // SQL query untuk mengambil data dari tabel pendaftaran_kkp dan tabel lainnya dengan alias yang sesuai
    String sql = "SELECT pendaftaran_kkp.id_pendaftaran, mahasiswa.nama, tempat_kkp.nama_perusahaan, pendaftaran_kkp.tgl_daftar "
               + "FROM pendaftaran_kkp "
               + "JOIN mahasiswa ON pendaftaran_kkp.id_mahasiswa = mahasiswa.id_mahasiswa "
               + "JOIN tempat_kkp ON pendaftaran_kkp.id_tempat = tempat_kkp.id_tempat "
               + "ORDER BY pendaftaran_kkp.id_pendaftaran DESC";


    try (Statement stat = connection.createStatement();
         ResultSet res = stat.executeQuery(sql)) {

        while (res.next()) {
            // Ambil data dari ResultSet
            Object[] obj = new Object[4];
            obj[0] = res.getString("id_pendaftaran");  // ID Pendaftaran
            obj[1] = res.getString("nama");             // Nama Mahasiswa
            obj[2] = res.getString("nama_perusahaan");      // Nama Tempat
            obj[3] = res.getDate("tgl_daftar");         // Tanggal Daftar (gunakan getDate untuk tipe SQL Date)

            // Tambahkan baris ke model tabel
            model.addRow(obj);
        }
    } catch (SQLException err) {
        err.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Terjadi kesalahan saat memuat data pendaftaran.",
            "Kesalahan",
            JOptionPane.ERROR_MESSAGE);
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

        // SQL query dengan kondisi pencarian berdasarkan nama mahasiswa
        String sql = "SELECT pendaftaran_kkp.id_pendaftaran, mahasiswa.nama, tempat_kkp.nama_perusahaan AS nama_tempat, pendaftaran_kkp.tgl_daftar "
                   + "FROM pendaftaran_kkp "
                   + "JOIN mahasiswa ON pendaftaran_kkp.id_mahasiswa = mahasiswa.id_mahasiswa "
                   + "JOIN tempat_kkp ON pendaftaran_kkp.id_tempat = tempat_kkp.id_tempat "
                   + "WHERE mahasiswa.nama LIKE ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            // Menetapkan parameter untuk pencarian
            String searchKeyword = "%" + tCari.getText().trim() + "%";
            pst.setString(1, searchKeyword);

            // Menjalankan query dan memproses hasil
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Object[] obj = new Object[4];
                    obj[0] = rs.getString("id_pendaftaran");
                    obj[1] = rs.getString("nama");
                    obj[2] = rs.getString("nama_tempat");
                    obj[3] = rs.getString("tgl_daftar");
                    model.addRow(obj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Terjadi kesalahan saat melakukan pencarian.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void refresh() {
        model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        model2 = (DefaultTableModel) table2.getModel();
        model2.setRowCount(0);
        tCari.setText("");
        tabelPendaftaran();
        tabeltempatkkp();
        getcbDosen(); 
    }
    
     private void tabeltempatkkp() {
        model2 = (DefaultTableModel) table2.getModel();
        model2.setRowCount(0);
        try {
            Statement stat = connection.createStatement();
            String sql = "SELECT * FROM tempat_kkp WHERE id_tempat ";
                       
            ResultSet res = stat.executeQuery(sql);
            while (res.next()) {
                Object[] obj = new Object[7];
                obj[0] = res.getString("id_tempat");
                obj[1] = res.getString("nama_perusahaan");
                obj[2] = res.getString("alamat");
                obj[3] = res.getString("nama_pembimbing");
                obj[4] = res.getString("notelp");
                obj[5] = res.getString("tgl_mulai");
                obj[6] = res.getString("tgl_berakhir");
                model2.addRow(obj);
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }
     
     
private void getcbDosen() {
    cbDosen.removeAllItems(); // Kosongkan JComboBox sebelum menambah item
    String sqlSelectDosen = "SELECT id_dosen, nama_dosen FROM dosen WHERE status = 'kkp'";
    String sqlCheckDosenAssigned = "SELECT COUNT(*) FROM pendaftaran_kkpdetail WHERE id_dosen = ?";

    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sqlSelectDosen)) {

        while (resultSet.next()) {
            String idDosen = resultSet.getString("id_dosen");
            String namaDosen = resultSet.getString("nama_dosen");

            // Periksa apakah dosen sudah terdaftar di pendaftaran_kkpdetail
            try (PreparedStatement checkStatement = connection.prepareStatement(sqlCheckDosenAssigned)) {
                checkStatement.setString(1, idDosen);
                try (ResultSet checkResultSet = checkStatement.executeQuery()) {
                    if (checkResultSet.next() && checkResultSet.getInt(1) > 0) {
                        namaDosen += "";
                    }
                }
            }

            // Tambah item ke JComboBox
            cbDosen.addItem(idDosen + " - " + namaDosen);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Terjadi kesalahan saat memuat data dosen.",
            "Kesalahan",
            JOptionPane.ERROR_MESSAGE);
    }
}



     
     private void generateAutoNumber() {
        try {
            Statement stat = connection.createStatement();
            String sql = "SELECT MAX(id_tempat) AS max_id FROM tempat_kkp";
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
        String idTempat = tID.getText().trim();
        String namaPerusahaan = tNmPerusahaan.getText().trim();
        String alamat = tAlamat.getText().trim();
        String namaPembimbing = tNmPembimbing.getText().trim();
        String notelp = tNotelp.getText().trim();
        java.sql.Date tglMulai = new java.sql.Date(jdcTglMulai.getDate().getTime());
        java.sql.Date tglBerakhir = new java.sql.Date(jdcTglakhir.getDate().getTime());

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
                bUbah.setEnabled(true);
                bHapus.setEnabled(true);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Terjadi kesalahan saat menyimpan data.",
                "Kesalahan",
                JOptionPane.ERROR_MESSAGE);

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
     
//     private void autoNumberTempatkkp() {
//        try {
//            Statement stat = connection.createStatement();
//            String sql = "SELECT MAX(id_tempat) AS max_id FROM tempat_kkp";
//            ResultSet res = stat.executeQuery(sql);
//
//            if (res.next()) {
//                int maxId = res.getInt("max_id");
//                int newId = maxId + 1;
//                tIDtempat.setText(String.valueOf(newId));
//            } else {
//                // Handle the case where no records exist yet
//                tIDtempat.setText("1");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//     
     private void reset() {
         tID.setText("");
         tNmPerusahaan.setText("");
        tAlamat.setText("");
        tNmPembimbing.setText("");
        tNotelp.setText("");
        generateAutoNumber();
        bSimpan.setEnabled(true);
    }
     
     private void updateTempatKkp() {
    // Mengambil data dari text fields
        String idTemp = tID.getText().trim();
        String namaPerusahaan = tNmPerusahaan.getText().trim();
        String alamat = tAlamat.getText().trim();
        String namaPembimbing = tNmPembimbing.getText().trim();
        String notelp = tNotelp.getText().trim();
        java.util.Date tglMulai = jdcTglMulai.getDate();
        java.util.Date tglAkhir = jdcTglakhir.getDate();

        // Validasi data (misalnya, periksa apakah ID Tempat tidak kosong)
        if (idTemp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Tempat tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Query SQL untuk update data pada tabel tempat_kkp
        String sql = "UPDATE tempat_kkp SET "
                   + "nama_perusahaan = ?, "
                   + "alamat = ?, "
                   + "nama_pembimbing = ?, "
                   + "notelp = ?, "
                   + "tgl_mulai = ?, "
                   + "tgl_berakhir = ? "
                   + "WHERE id_tempat = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            // Menetapkan parameter untuk PreparedStatement
            pst.setString(1, namaPerusahaan);
            pst.setString(2, alamat);
            pst.setString(3, namaPembimbing);
            pst.setString(4, notelp);
            pst.setDate(5, new java.sql.Date(tglMulai.getTime())); // Mengonversi java.util.Date ke java.sql.Date
            pst.setDate(6, new java.sql.Date(tglAkhir.getTime()));  // Mengonversi java.util.Date ke java.sql.Date
            pst.setString(7, idTemp);

            // Menjalankan update query
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data. ID Tempat tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            // Menangani kesalahan SQL
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memperbarui data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
     
    private void deleteTempatKkp() {
        // Mengambil ID Tempat dari text field
        String idTemp = tID.getText().trim();

        // Validasi data (misalnya, periksa apakah ID Tempat tidak kosong)
        if (idTemp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Tempat tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Menampilkan dialog konfirmasi
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Apakah Anda yakin ingin menghapus data dengan ID Tempat: " + idTemp + "?",
            "Konfirmasi Penghapusan",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        // Jika pengguna memilih "YES", lanjutkan dengan penghapusan
        if (confirm == JOptionPane.YES_OPTION) {
            // Query SQL untuk menghapus data dari tabel tempat_kkp
            String sql = "DELETE FROM tempat_kkp WHERE id_tempat = ?";

            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                // Menetapkan parameter untuk PreparedStatement
                pst.setString(1, idTemp);

                // Menjalankan delete query
                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                    // Mengosongkan text field setelah penghapusan
                    tID.setText("");
                    tNmPerusahaan.setText("");
                    tAlamat.setText("");
                    tNmPembimbing.setText("");
                    tNotelp.setText("");
                    jdcTglMulai.setDate(null);
                    jdcTglakhir.setDate(null);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data. ID Tempat tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                // Menangani kesalahan SQL
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Pengguna memilih "NO", tidak melakukan penghapusan
            JOptionPane.showMessageDialog(this, "Penghapusan data dibatalkan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
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
        LidPendaftaran = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        LidMahasiswa = new javax.swing.JLabel();
        LnamaMahasiswa = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        Ljurusan = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        Lnim = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        LnmPerusahaan = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Lalamat = new javax.swing.JTextArea();
        jLabel22 = new javax.swing.JLabel();
        LnmPembimbing = new javax.swing.JLabel();
        label = new javax.swing.JLabel();
        Lnotelp = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        LtglMulai = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        LtglAkhir = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        bNotApprove = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        cbDosen = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        table2 = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        tID = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tNmPerusahaan = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tAlamat = new javax.swing.JTextArea();
        jLabel13 = new javax.swing.JLabel();
        tNmPembimbing = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        tNotelp = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jdcTglMulai = new com.toedter.calendar.JDateChooser();
        jdcTglakhir = new com.toedter.calendar.JDateChooser();
        bSimpan = new javax.swing.JButton();
        bBatal = new javax.swing.JButton();
        bUbah = new javax.swing.JButton();
        bHapus = new javax.swing.JButton();

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

        jLabel3.setText("Masukkan Nama Mahasiswa");

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
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Pendaftaran", "Nama Mahasiswa", "Tempat KKP", "Tgl Daftar"
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
        jLabel9.setText("ID Pendaftaran");

        LidPendaftaran.setText("...");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("Mahasiswa");

        LidMahasiswa.setText("...");

        LnamaMahasiswa.setText("...");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("Jurusan");

        Ljurusan.setText("...");

        Lnim.setText("...");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setText("Nama Perusahaan");

        LnmPerusahaan.setText("...");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel21.setText("Alamat");

        Lalamat.setEditable(false);
        Lalamat.setColumns(20);
        Lalamat.setRows(5);
        jScrollPane2.setViewportView(Lalamat);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel22.setText("Nama Pembimbing");

        LnmPembimbing.setText("...");

        label.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        label.setText("No Telpon");

        Lnotelp.setText("...");

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel24.setText("Tgl Mulai");

        LtglMulai.setText("...");

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel25.setText("Tgl Berakhir");

        LtglAkhir.setText("...");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel23.setText("Mahasiswa");

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel26.setText("Tempat KKP");

        jButton4.setText("Approved");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        bNotApprove.setText("Not Approved");
        bNotApprove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNotApproveActionPerformed(evt);
            }
        });

        jLabel17.setText("*) Pilih Dosen");

        cbDosen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbDosen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDosenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator2)
                .addGap(27, 27, 27))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addGap(35, 35, 35))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel11))
                                        .addGap(32, 32, 32)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(Ljurusan, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(40, 40, 40))
                                    .addComponent(LidPendaftaran, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(LidMahasiswa, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(Lnim, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lTanggal)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(40, 40, 40)
                                        .addComponent(LnamaMahasiswa, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(110, 110, 110)
                                .addComponent(jLabel23)))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabel7))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(32, 32, 32)
                                        .addComponent(LnmPerusahaan, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(31, 31, 31)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addGap(32, 32, 32)
                                        .addComponent(LnmPembimbing, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel24)
                                            .addComponent(label))
                                        .addGap(32, 32, 32)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(Lnotelp, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(LtglMulai, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel25)
                                        .addGap(32, 32, 32)
                                        .addComponent(LtglAkhir, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel26)
                                        .addGap(94, 94, 94)))))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(61, 61, 61)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton4)
                                    .addComponent(bNotApprove)
                                    .addComponent(cbDosen, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(jLabel17))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(188, 188, 188)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tCari, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(bCariMhs)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(bRefresh))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(255, 255, 255))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(452, 452, 452)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(tCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bCariMhs)
                    .addComponent(bRefresh)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbDosen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bNotApprove)
                        .addGap(61, 61, 61))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(LnmPerusahaan))
                                .addGap(8, 8, 8)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel21)
                                        .addGap(46, 46, 46))
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel22)
                                    .addComponent(LnmPembimbing))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(label)
                                    .addComponent(Lnotelp))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel24)
                                    .addComponent(LtglMulai))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(LtglAkhir)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
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
                                    .addComponent(LidPendaftaran))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(LidMahasiswa)
                                    .addComponent(Lnim))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(LnamaMahasiswa)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(Ljurusan))))
                        .addGap(25, 25, 25)))
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(107, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(282, Short.MAX_VALUE)))
        );

        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Tempat", "Nama Perusahaan", "Alamat", "Nama Pembimbing", "No Telpon", "Tgl Mulai", "Tgl Akhir"
            }
        ));
        table2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table2MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(table2);

        jLabel8.setText("ID Tempat");

        tID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tIDActionPerformed(evt);
            }
        });

        jLabel10.setText("Nama Perusahaan");

        tNmPerusahaan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tNmPerusahaanActionPerformed(evt);
            }
        });

        jLabel12.setText("Alamat");

        tAlamat.setColumns(20);
        tAlamat.setRows(5);
        jScrollPane4.setViewportView(tAlamat);

        jLabel13.setText("Nama Pembimbing");

        tNmPembimbing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tNmPembimbingActionPerformed(evt);
            }
        });

        jLabel14.setText("No Telpon");

        tNotelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tNotelpActionPerformed(evt);
            }
        });

        jLabel15.setText("Tgl Mulai");

        jLabel16.setText("Tgl Akhir");

        bSimpan.setBackground(new java.awt.Color(102, 255, 102));
        bSimpan.setText("Simpan");
        bSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSimpanActionPerformed(evt);
            }
        });

        bBatal.setText("Batal");
        bBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBatalActionPerformed(evt);
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(bUbah, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bSimpan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tID, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane4)
                                    .addComponent(tNmPerusahaan)
                                    .addComponent(tNmPembimbing)
                                    .addComponent(tNotelp)
                                    .addComponent(jdcTglMulai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jdcTglakhir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(bBatal, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                .addGap(138, 138, 138)))
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 565, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(tID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(tNmPerusahaan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tNmPembimbing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(tNotelp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jdcTglMulai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jdcTglakhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bSimpan)
                            .addComponent(bBatal))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bUbah)
                    .addComponent(bHapus)))
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
    String idPendaftaran = table.getValueAt(row, 0).toString();

    // Query SQL untuk mendapatkan detail berdasarkan ID Pendaftaran
    String sql = "SELECT pendaftaran_kkp.id_pendaftaran, pendaftaran_kkp.id_mahasiswa, mahasiswa.nim, mahasiswa.nama AS nama_mahasiswa, mahasiswa.jurusan, "
               + "tempat_kkp.nama_perusahaan, tempat_kkp.alamat, tempat_kkp.nama_pembimbing, tempat_kkp.notelp, "
               + "tempat_kkp.tgl_mulai, tempat_kkp.tgl_berakhir "
               + "FROM pendaftaran_kkp "
               + "JOIN mahasiswa ON pendaftaran_kkp.id_mahasiswa = mahasiswa.id_mahasiswa "
               + "JOIN tempat_kkp ON pendaftaran_kkp.id_tempat = tempat_kkp.id_tempat "
               + "WHERE pendaftaran_kkp.id_pendaftaran = ?";

    try (PreparedStatement pst = connection.prepareStatement(sql)) {
        // Menetapkan parameter untuk PreparedStatement
        pst.setString(1, idPendaftaran);

        // Menjalankan query dan memproses hasil
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                // Memperbarui label dengan data yang diperoleh
                LidPendaftaran.setText(rs.getString("id_pendaftaran"));
                LidMahasiswa.setText(rs.getString("id_mahasiswa"));
                Lnim.setText(rs.getString("nim"));
                LnamaMahasiswa.setText(rs.getString("nama_mahasiswa"));
                Ljurusan.setText(rs.getString("jurusan"));
                LnmPerusahaan.setText(rs.getString("nama_perusahaan"));
                Lalamat.setText(rs.getString("alamat"));
                LnmPembimbing.setText(rs.getString("nama_pembimbing"));
                Lnotelp.setText(rs.getString("notelp"));
                LtglMulai.setText(rs.getDate("tgl_mulai").toString());
                LtglAkhir.setText(rs.getDate("tgl_berakhir").toString());
            } else {
                // Menangani kasus jika data tidak ditemukan
                JOptionPane.showMessageDialog(this,
                    "Data tidak ditemukan!",
                    "Notifikasi", JOptionPane.INFORMATION_MESSAGE);

                // Mengosongkan label jika data tidak ditemukan
                LidPendaftaran.setText("");
                LidMahasiswa.setText("");
                Lnim.setText("");
                LnamaMahasiswa.setText("");
                Ljurusan.setText("");
                LnmPerusahaan.setText("");
                Lalamat.setText("");
                LnmPembimbing.setText("");
                Lnotelp.setText("");
                LtglMulai.setText("");
                LtglAkhir.setText("");
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

    private void bNotApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNotApproveActionPerformed
        // TODO add your handling code here:
        String idPendaftaran = LidPendaftaran.getText().trim(); // Contoh: ID Pendaftaran diambil dari text field

    if (idPendaftaran.isEmpty()) {
        JOptionPane.showMessageDialog(null,
            "ID Pendaftaran tidak boleh kosong.",
            "Kesalahan Validasi",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Query SQL untuk menghapus data dari tabel pendaftaran_kkp
    String sqlDelete = "DELETE FROM pendaftaran_kkp WHERE id_pendaftaran = ?";

    try (PreparedStatement deleteStatement = connection.prepareStatement(sqlDelete)) {
        // Set parameter untuk query
        deleteStatement.setString(1, idPendaftaran);

        // Jalankan query dan ambil jumlah baris yang dipengaruhi
        int rowsAffected = deleteStatement.executeUpdate();

        if (rowsAffected > 0) {
            // Notifikasi bahwa data berhasil dihapus
            JOptionPane.showMessageDialog(null,
                "Data berhasil dihapus.",
                "Informasi",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Tidak ada baris yang dihapus
            JOptionPane.showMessageDialog(null,
                "ID Pendaftaran tidak ditemukan.",
                "Informasi",
                JOptionPane.INFORMATION_MESSAGE);
        }
        refresh();
    } catch (SQLException e) {
        // Tampilkan pesan kesalahan jika terjadi SQLException
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Terjadi kesalahan saat menghapus data.",
            "Kesalahan",
            JOptionPane.ERROR_MESSAGE);
    }
        
    }//GEN-LAST:event_bNotApproveActionPerformed

    private void tIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tIDActionPerformed

    private void tNmPerusahaanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tNmPerusahaanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tNmPerusahaanActionPerformed

    private void tNmPembimbingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tNmPembimbingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tNmPembimbingActionPerformed

    private void tNotelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tNotelpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tNotelpActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
      String selectedDosen = (String) cbDosen.getSelectedItem();

    if (selectedDosen != null) {
        // Ambil ID Dosen dari teks cbDosen
        String[] parts = selectedDosen.split(" - ");
        String idDosen = parts[0]; // ID Dosen adalah bagian pertama dari string

        // Ambil ID Pendaftaran dari GUI atau sumber lain yang sesuai
        String idPendaftaran = LidPendaftaran.getText().trim(); // Contoh: ID Pendaftaran diambil dari text field

        if (idPendaftaran.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "ID Pendaftaran tidak boleh kosong.",
                "Kesalahan Validasi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update tabel pendaftaran_kkpdetail dengan ID Dosen dan status 'approve'
        String sqlUpdate = "UPDATE pendaftaran_kkpdetail SET id_dosen = ?, status = 'approve' WHERE id_pendaftaran = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(sqlUpdate)) {
            updateStatement.setString(1, idDosen);
            updateStatement.setString(2, idPendaftaran);

            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Notifikasi bahwa data berhasil diperbarui
                JOptionPane.showMessageDialog(null,
                    "Data berhasil diperbarui.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Tidak ada baris yang diperbarui
                JOptionPane.showMessageDialog(null,
                    "Tidak ada data yang diperbarui.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Terjadi kesalahan saat memperbarui data.",
                "Kesalahan",
                JOptionPane.ERROR_MESSAGE);
        }
    } else {
        // Tidak ada dosen yang dipilih
        JOptionPane.showMessageDialog(null,
            "Pilih dosen terlebih dahulu.",
            "Peringatan",
            JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void table2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table2MouseClicked
        // TODO add your handling code here:
        int row = table2.getSelectedRow();
        String idTemp = table2.getValueAt(row, 0).toString(); // Misalkan kolom ID Tempat ada di kolom 0

        // Query SQL untuk mendapatkan detail dari tabel tempat_kkp
        String sql = "SELECT id_tempat, nama_perusahaan, alamat, nama_pembimbing, notelp, tgl_mulai, tgl_berakhir "
                   + "FROM tempat_kkp "
                   + "WHERE id_tempat = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            // Menetapkan parameter untuk PreparedStatement
            pst.setString(1, idTemp);

            // Menjalankan query dan memproses hasil
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Menampilkan data dari tabel tempat_kkp ke dalam text fields
                    tID.setText(rs.getString("id_tempat"));
                    tNmPerusahaan.setText(rs.getString("nama_perusahaan"));
                    tAlamat.setText(rs.getString("alamat"));
                    tNmPembimbing.setText(rs.getString("nama_pembimbing"));
                    tNotelp.setText(rs.getString("notelp"));
                    jdcTglMulai.setDate(rs.getDate("tgl_mulai"));
                    jdcTglakhir.setDate(rs.getDate("tgl_berakhir"));

                    // Menonaktifkan tombol bSimpan
                    bSimpan.setEnabled(false);
                    bUbah.setEnabled(true);
                    bHapus.setEnabled(true);
                } else {
                    // Menangani kasus jika data tidak ditemukan
                    JOptionPane.showMessageDialog(this,
                        "Data tidak ditemukan!",
                        "Notifikasi", JOptionPane.INFORMATION_MESSAGE);

                    // Mengosongkan text fields jika data tidak ditemukan
                    tID.setText("");
                    tNmPerusahaan.setText("");
                    tAlamat.setText("");
                    tNmPembimbing.setText("");
                    tNotelp.setText("");
                    jdcTglMulai.setDate(null);
                    jdcTglakhir.setDate(null);

                    // Menonaktifkan tombol bSimpan
                    bSimpan.setEnabled(false);
                    
                }
            }
        } catch (SQLException e) {
            // Menangani kesalahan SQL
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Terjadi kesalahan saat mengambil data.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_table2MouseClicked

    private void bSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSimpanActionPerformed
        // TODO add your handling code here:
        insertTempatKkp();
        refresh();
        reset();
    }//GEN-LAST:event_bSimpanActionPerformed

    private void bBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBatalActionPerformed
        // TODO add your handling code here:
        reset();
    }//GEN-LAST:event_bBatalActionPerformed

    private void bUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUbahActionPerformed
        // TODO add your handling code here:
        updateTempatKkp();
        refresh();
        reset();
    }//GEN-LAST:event_bUbahActionPerformed

    private void bHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bHapusActionPerformed
        // TODO add your handling code here:
        deleteTempatKkp();
        refresh();
        reset();
    }//GEN-LAST:event_bHapusActionPerformed

    private void cbDosenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDosenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbDosenActionPerformed

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
            java.util.logging.Logger.getLogger(FTempatkkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FTempatkkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FTempatkkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FTempatkkp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FTempatkkp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea Lalamat;
    private javax.swing.JLabel LidMahasiswa;
    private javax.swing.JLabel LidPendaftaran;
    private javax.swing.JLabel Ljurusan;
    private javax.swing.JLabel LnamaMahasiswa;
    private javax.swing.JLabel Lnim;
    private javax.swing.JLabel LnmPembimbing;
    private javax.swing.JLabel LnmPerusahaan;
    private javax.swing.JLabel Lnotelp;
    private javax.swing.JLabel LtglAkhir;
    private javax.swing.JLabel LtglMulai;
    private javax.swing.JButton bBatal;
    private javax.swing.JButton bCariMhs;
    private javax.swing.JButton bHapus;
    private javax.swing.JButton bNotApprove;
    private javax.swing.JButton bRefresh;
    private javax.swing.JButton bSimpan;
    private javax.swing.JButton bUbah;
    private javax.swing.JComboBox<String> cbDosen;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
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
    private com.toedter.calendar.JDateChooser jdcTglMulai;
    private com.toedter.calendar.JDateChooser jdcTglakhir;
    private javax.swing.JLabel lTanggal;
    private javax.swing.JLabel label;
    private javax.swing.JTextField lblUsername;
    private javax.swing.JTextArea tAlamat;
    private javax.swing.JTextField tCari;
    private javax.swing.JTextField tID;
    private javax.swing.JTextField tNmPembimbing;
    private javax.swing.JTextField tNmPerusahaan;
    private javax.swing.JTextField tNotelp;
    private javax.swing.JTable table;
    private javax.swing.JTable table2;
    // End of variables declaration//GEN-END:variables
}
