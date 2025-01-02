SELECT pendaftaran_kkn.id_pendaftaran, mahasiswa.nama AS nama_mahasiswa, mahasiswa.nim, mahasiswa.jurusan, tempat_kkn.nama_tempat, dosen.nama_dosen AS nama_dosen, pendaftaran_kkn.tgl_daftar, pendaftaran_kkn.status
FROM pendaftaran_kkn
INNER JOIN mahasiswa ON pendaftaran_kkn.id_mahasiswa = mahasiswa.id_mahasiswa
INNER JOIN tempat_kkn ON pendaftaran_kkn.id_tempat = tempat_kkn.id_tempat
INNER JOIN dosen ON pendaftaran_kkn.id_dosen = dosen.id_dosen
ORDER BY tempat_kkn.nama_tempat