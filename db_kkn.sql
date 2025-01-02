-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Aug 01, 2024 at 05:20 PM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_kkn`
--

-- --------------------------------------------------------

--
-- Table structure for table `dosen`
--

CREATE TABLE `dosen` (
  `id_dosen` int NOT NULL,
  `nip` int NOT NULL,
  `nama_dosen` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `Alamat` text NOT NULL,
  `no_telp` varchar(13) NOT NULL,
  `status` enum('none','kkn','kkp','penguji/penilai') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `dosen`
--

INSERT INTO `dosen` (`id_dosen`, `nip`, `nama_dosen`, `Alamat`, `no_telp`, `status`) VALUES
(1, 1001, 'Pak Komeng', 'Jl Uno', '025885200145', 'kkn'),
(2, 1002, 'Bu Titi', 'Gg. Koxak', '021545699874', 'kkn'),
(3, 1003, 'Pak Kim', 'Seoul', '0214569', 'kkn'),
(4, 1004, 'Bu Jihyo', 'Busan', '0123321', 'kkp'),
(5, 1005, 'Pak Dedi', 'Jl Huan', '45697', 'kkp'),
(6, 1006, 'Pak Omar', 'sadsadas	', '12312', 'kkp'),
(7, 1007, 'Bu amel', 'asdsad', '123213213', 'penguji/penilai'),
(8, 1008, 'Bu Soed', 'Jonggol	', '0987452', 'penguji/penilai'),
(9, 1009, 'Pak Deni', 'sdasda', '312213', 'kkn'),
(10, 1000, 'none', 'none', '000', 'none');

-- --------------------------------------------------------

--
-- Table structure for table `evaluasi`
--

CREATE TABLE `evaluasi` (
  `id_evaluasi` int NOT NULL,
  `tgl` date NOT NULL,
  `id_pendaftaran` int NOT NULL,
  `id_mahasiswa` int NOT NULL,
  `nilai` int NOT NULL,
  `keterangan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `evaluasi`
--

INSERT INTO `evaluasi` (`id_evaluasi`, `tgl`, `id_pendaftaran`, `id_mahasiswa`, `nilai`, `keterangan`) VALUES
(1, '2024-08-01', 1, 1, 90, 'A (Sangat Baik)');

-- --------------------------------------------------------

--
-- Table structure for table `evaluasi_kkp`
--

CREATE TABLE `evaluasi_kkp` (
  `id_evaluasi` int NOT NULL,
  `tgl` date NOT NULL,
  `id_pendaftaran` int NOT NULL,
  `id_mahasiswa` int NOT NULL,
  `nilai` int NOT NULL,
  `keterangan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `evaluasi_kkp`
--

INSERT INTO `evaluasi_kkp` (`id_evaluasi`, `tgl`, `id_pendaftaran`, `id_mahasiswa`, `nilai`, `keterangan`) VALUES
(1, '2024-08-01', 1, 5, 90, 'A (Sangat Baik)');

-- --------------------------------------------------------

--
-- Table structure for table `mahasiswa`
--

CREATE TABLE `mahasiswa` (
  `id_mahasiswa` int NOT NULL,
  `nim` varchar(10) NOT NULL,
  `nama` varchar(50) NOT NULL,
  `alamat` text NOT NULL,
  `no_telp` varchar(13) NOT NULL,
  `jurusan` varchar(25) NOT NULL,
  `kegiatan` enum('kkn','kkp','none') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `mahasiswa`
--

INSERT INTO `mahasiswa` (`id_mahasiswa`, `nim`, `nama`, `alamat`, `no_telp`, `jurusan`, `kegiatan`) VALUES
(1, '001', 'Agus', 'Jl Huang', '123123', 'Teknik Informatika', 'kkn'),
(2, '002', 'Ayu', 'Jl Gong	', '088129312', 'Kedokteran', 'kkn'),
(3, '003', 'Inyong', 'Tuay	`', '08432346', 'Teknik Sipil', 'kkn'),
(4, '004', 'Fauzi', 'Yuya	', '81239123', 'Soshum', 'kkn'),
(5, '005', 'Uta', 'HUat', '123123', 'Soshum', 'kkp');

-- --------------------------------------------------------

--
-- Table structure for table `pendaftaran_kkn`
--

CREATE TABLE `pendaftaran_kkn` (
  `id_pendaftaran` int NOT NULL,
  `id_mahasiswa` int NOT NULL,
  `id_tempat` int NOT NULL,
  `id_dosen` int NOT NULL,
  `tgl_daftar` date NOT NULL,
  `status` varchar(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `pendaftaran_kkn`
--

INSERT INTO `pendaftaran_kkn` (`id_pendaftaran`, `id_mahasiswa`, `id_tempat`, `id_dosen`, `tgl_daftar`, `status`) VALUES
(1, 1, 1, 1, '2024-07-31', 'Terdaftar'),
(2, 2, 2, 2, '2024-07-31', 'Terdaftar');

--
-- Triggers `pendaftaran_kkn`
--
DELIMITER $$
CREATE TRIGGER `update_jumlah_mahasiswa` AFTER INSERT ON `pendaftaran_kkn` FOR EACH ROW BEGIN
    -- Update jumlah_mahasiswa di tabel tempat_kkn
    UPDATE tempat_kkn
    SET jumlah_mahasiswa = jumlah_mahasiswa + 1
    WHERE id_tempat = NEW.id_tempat;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `pendaftaran_kkp`
--

CREATE TABLE `pendaftaran_kkp` (
  `id_pendaftaran` int NOT NULL,
  `id_mahasiswa` int NOT NULL,
  `id_tempat` int NOT NULL,
  `tgl_daftar` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `pendaftaran_kkp`
--

INSERT INTO `pendaftaran_kkp` (`id_pendaftaran`, `id_mahasiswa`, `id_tempat`, `tgl_daftar`) VALUES
(1, 5, 1, '2024-07-31');

--
-- Triggers `pendaftaran_kkp`
--
DELIMITER $$
CREATE TRIGGER `after_delete_pendaftaran_kkp` AFTER DELETE ON `pendaftaran_kkp` FOR EACH ROW BEGIN
    DELETE FROM pendaftaran_kkpdetail
    WHERE id_pendaftaran = OLD.id_pendaftaran;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `after_insert_pendaftaran_kkp` AFTER INSERT ON `pendaftaran_kkp` FOR EACH ROW BEGIN
    -- Insert data ke tabel pendaftaran_kkpdetail
    INSERT INTO pendaftaran_kkpdetail (id_pendaftaran, id_dosen, status)
    VALUES (NEW.id_pendaftaran, 10, 'Waiting');
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `after_pendaftaran_kkp_delete` AFTER DELETE ON `pendaftaran_kkp` FOR EACH ROW BEGIN
    -- Menghapus baris dari tabel tempat_kkp berdasarkan id_tempat
    DELETE FROM tempat_kkp
    WHERE id_tempat = OLD.id_tempat;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `pendaftaran_kkpdetail`
--

CREATE TABLE `pendaftaran_kkpdetail` (
  `id_pendaftaran` int NOT NULL,
  `id_dosen` int DEFAULT NULL,
  `status` enum('waiting','approve','not approve') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `pendaftaran_kkpdetail`
--

INSERT INTO `pendaftaran_kkpdetail` (`id_pendaftaran`, `id_dosen`, `status`) VALUES
(1, 4, 'approve');

-- --------------------------------------------------------

--
-- Table structure for table `pengguna`
--

CREATE TABLE `pengguna` (
  `id_pengguna` int NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(25) NOT NULL,
  `nama_pengguna` varchar(50) NOT NULL,
  `level` enum('admin','dosen','mahasiswa','') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `pengguna`
--

INSERT INTO `pengguna` (`id_pengguna`, `email`, `password`, `nama_pengguna`, `level`) VALUES
(1, 'admin', 'admin', 'admin', 'admin'),
(2, 'dosen', 'dosen', 'dosen', 'dosen'),
(3, 'mahasiswa', 'mahasiswa', 'mahasiswa', 'mahasiswa'),
(4, '001@gmail.com', '12345', 'Agus', 'mahasiswa'),
(5, '002@gmail.com', '12345', 'Ayu', 'mahasiswa'),
(6, '003@gmail.com', '003', 'Inyong', 'mahasiswa'),
(7, '004@gmail.com', '004', 'Fauzi', 'mahasiswa'),
(8, '005@gmail.com', '005', 'Uta', 'mahasiswa'),
(9, '1001@gmail.com', '1001', 'Pak Komeng', 'dosen'),
(10, '1008@gmail.com', '1008', 'Bu Soed', 'dosen'),
(11, '1004@gmail.com', '1004', 'Bu Jihyo', 'dosen');

-- --------------------------------------------------------

--
-- Table structure for table `tempat_kkn`
--

CREATE TABLE `tempat_kkn` (
  `id_tempat` int NOT NULL,
  `nama_tempat` varchar(50) NOT NULL,
  `lokasi` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `program` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `id_dosen` int NOT NULL,
  `jumlah_mahasiswa` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tempat_kkn`
--

INSERT INTO `tempat_kkn` (`id_tempat`, `nama_tempat`, `lokasi`, `program`, `id_dosen`, `jumlah_mahasiswa`) VALUES
(1, 'Ds. Sukamaju12', 'Kec. Rajeg, Tangerang', 'Pengabdian untuk masyarakat', 1, 1),
(2, 'Ds. Mantap', 'Kec. Mauk12', 'Pengabdian untuk masyarakat', 2, 1),
(3, 'Ds Ciraos', 'Kab. Bandung12', 'Pengabdian untuk masyarakat', 3, 0),
(4, 'Ds. sasa', 'ususus', 'asasa', 9, 0);

-- --------------------------------------------------------

--
-- Table structure for table `tempat_kkp`
--

CREATE TABLE `tempat_kkp` (
  `id_tempat` int NOT NULL,
  `nama_perusahaan` text NOT NULL,
  `alamat` text NOT NULL,
  `nama_pembimbing` varchar(50) NOT NULL,
  `notelp` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `tgl_mulai` date NOT NULL,
  `tgl_berakhir` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tempat_kkp`
--

INSERT INTO `tempat_kkp` (`id_tempat`, `nama_perusahaan`, `alamat`, `nama_pembimbing`, `notelp`, `tgl_mulai`, `tgl_berakhir`) VALUES
(1, 'dasd', 'asdsad', 'asdsa', '213', '2024-07-11', '2024-07-25');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `dosen`
--
ALTER TABLE `dosen`
  ADD PRIMARY KEY (`id_dosen`);

--
-- Indexes for table `evaluasi`
--
ALTER TABLE `evaluasi`
  ADD PRIMARY KEY (`id_evaluasi`),
  ADD KEY `id_pendaftaran` (`id_pendaftaran`);

--
-- Indexes for table `evaluasi_kkp`
--
ALTER TABLE `evaluasi_kkp`
  ADD PRIMARY KEY (`id_evaluasi`),
  ADD KEY `id_pendaftaran` (`id_pendaftaran`);

--
-- Indexes for table `mahasiswa`
--
ALTER TABLE `mahasiswa`
  ADD PRIMARY KEY (`id_mahasiswa`);

--
-- Indexes for table `pendaftaran_kkn`
--
ALTER TABLE `pendaftaran_kkn`
  ADD PRIMARY KEY (`id_pendaftaran`),
  ADD KEY `id_mhs.mhs` (`id_mahasiswa`),
  ADD KEY `id.tmpt.tmpt_kkn` (`id_tempat`),
  ADD KEY `id_dsn.dosen` (`id_dosen`);

--
-- Indexes for table `pendaftaran_kkp`
--
ALTER TABLE `pendaftaran_kkp`
  ADD PRIMARY KEY (`id_pendaftaran`);

--
-- Indexes for table `pengguna`
--
ALTER TABLE `pengguna`
  ADD PRIMARY KEY (`id_pengguna`);

--
-- Indexes for table `tempat_kkn`
--
ALTER TABLE `tempat_kkn`
  ADD PRIMARY KEY (`id_tempat`);

--
-- Indexes for table `tempat_kkp`
--
ALTER TABLE `tempat_kkp`
  ADD PRIMARY KEY (`id_tempat`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `dosen`
--
ALTER TABLE `dosen`
  MODIFY `id_dosen` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `evaluasi`
--
ALTER TABLE `evaluasi`
  MODIFY `id_evaluasi` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `evaluasi_kkp`
--
ALTER TABLE `evaluasi_kkp`
  MODIFY `id_evaluasi` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `mahasiswa`
--
ALTER TABLE `mahasiswa`
  MODIFY `id_mahasiswa` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `pengguna`
--
ALTER TABLE `pengguna`
  MODIFY `id_pengguna` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `pendaftaran_kkn`
--
ALTER TABLE `pendaftaran_kkn`
  ADD CONSTRAINT `id.tmpt.tmpt_kkn` FOREIGN KEY (`id_tempat`) REFERENCES `tempat_kkn` (`id_tempat`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `id_dsn.dosen` FOREIGN KEY (`id_dosen`) REFERENCES `dosen` (`id_dosen`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
