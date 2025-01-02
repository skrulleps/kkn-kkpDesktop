# Aplikasi Pendaftaran KKP & KKN

Proyek ini adalah aplikasi pendaftaran Kuliah Kerja Profesi (KKP) dan Kuliah Kerja Nyata (KKN) yang dikembangkan menggunakan Java dan Apache NetBeans. Aplikasi ini dirancang untuk mempermudah proses administrasi KKP dan KKN bagi mahasiswa, dosen, dan admin.

## Fitur Utama
- **Login Multi-Level**: Mendukung login untuk admin, mahasiswa, dan dosen dengan hak akses yang berbeda.
- **Manajemen Data**: 
  - Mahasiswa: Data mahasiswa, pendaftaran KKP/KKN, laporan.
  - Dosen: Penilaian KKP/KKN.
  - Admin: Manajemen data master (mahasiswa, dosen, tempat KKP/KKN).
- **Pendaftaran KKP & KKN**: Proses pendaftaran yang terintegrasi dengan database.
- **Evaluasi dan Laporan**: Menyediakan fitur evaluasi KKP/KKN dan laporan data.

## Teknologi yang Digunakan
- **Bahasa Pemrograman**: Java
- **Framework**: Apache NetBeans
- **Database**: MySQL
- **Arsitektur**: Model-View-Controller (MVC)

## Instalasi
1. **Persyaratan Sistem**:
   - Java Development Kit (JDK) 8 atau lebih baru.
   - Apache NetBeans 11 atau lebih baru.
   - MySQL Server.
   
2. **Langkah-Langkah Instalasi**:
   - Clone repository ini: `git clone https://github.com/username/repo-name.git`
   - Impor proyek ke Apache NetBeans.
   - Konfigurasi file koneksi database di folder `koneksi`:
     ```java
     String dbUrl = "jdbc:mysql://localhost:3306/db_kkn?user=root&password=your_password";
     ```
   - Jalankan file SQL yang ada di folder `database` untuk membuat tabel yang dibutuhkan.
   - Jalankan aplikasi dari `FMenuUtama`.

## Screenshot Aplikasi
### Halaman Login
![Halaman Login](https://github.com/skrulleps/kkn-kkpDesktop/blob/main/Menu%20Utama%20Sebelum%20Login.png)

### Dashboard Admin
![Dashboard Admin](images/admin_dashboard.png)

### Form Pendaftaran KKN/KKP
![Form Pendaftaran KKN/KKP](images/registration_form.png)

> **Catatan**: Pastikan Anda menyimpan file gambar di folder `images` di dalam repository.

## Struktur Proyek
- `src/koneksi/`: Konfigurasi koneksi database.
- `src/view/`: Tampilan GUI (login, pendaftaran, evaluasi, dll).
- `src/session/`: Manajemen sesi pengguna.
- `src/report/`: Template laporan.

## Diagram UML
- **Use Case Diagram**: Mengilustrasikan peran admin, mahasiswa, dan dosen.
- **Activity Diagram**: Menunjukkan alur aktivitas pendaftaran dan evaluasi.
- **Sequence Diagram**: Menggambarkan interaksi antar komponen sistem.
- **Class Diagram**: Menampilkan struktur kelas dan relasinya.

## Kontributor
- Ilham Aditya Putra Budi  
- Mochamed Fadhlan Tuhairi  
- Eka Putu Miharja  
- Muhammad Zaenal Abby S.

## Lisensi
Proyek ini dilisensikan di bawah [MIT License](LICENSE).

## Kontak
Untuk informasi lebih lanjut, silakan hubungi:
- Email: example@example.com
- Telepon: +62 812-3456-7890
