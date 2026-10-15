-- Database: db_people
-- Modifikasi untuk TP5 DPBO 2526 C2 (Sesuai Kolom TP4)

CREATE DATABASE IF NOT EXISTS db_people;
USE db_people;

CREATE TABLE IF NOT EXISTS person (
  id varchar(50) PRIMARY KEY,
  nama varchar(255) NOT NULL,
  thn_lahir int NOT NULL,
  kategori varchar(100) NOT NULL,
  shift_kerja varchar(50) NOT NULL
);

-- Data Awal (Dummy Data Karyawan Medis & Rumah Sakit dari TP4)
INSERT INTO person (id, nama, thn_lahir, kategori, shift_kerja) VALUES
('MED001', 'dr. Hendra Wijaya, Sp.JP', 1982, 'Dokter Spesialis', 'Pagi'),
('MED002', 'dr. Siti Aminah, Sp.A', 1986, 'Dokter Spesialis', 'Siang'),
('MED003', 'dr. Budi Santoso', 1993, 'Dokter Umum', 'Malam'),
('MED004', 'Ns. Dewi Lestari, S.Kep', 1996, 'Perawat', 'Pagi'),
('MED005', 'apt. Rian Pratama, S.Farm', 1994, 'Apoteker', 'Siang'),
('MED006', 'Nadia Putri', 1998, 'Staf Administrasi', 'Pagi'),
('MED007', 'Ns. Ahmad Fauzi, S.Kep', 1995, 'Perawat', 'Malam'),
('MED008', 'dr. Maya Indah', 1992, 'Dokter Umum', 'Pagi'),
('MED009', 'apt. Clarissa Yunita, S.Farm', 1997, 'Apoteker', 'Malam'),
('MED010', 'Rizky Ramadhan', 1999, 'Staf Administrasi', 'Siang'),
('MED011', 'dr. Bagas Saputra, Sp.PD', 1980, 'Dokter Spesialis', 'Pagi'),
('MED012', 'Ns. Lestari Wulandari, S.Kep', 1997, 'Perawat', 'Siang'),
('MED013', 'dr. Fajar Nugroho', 1991, 'Dokter Umum', 'Siang'),
('MED014', 'Dina Maharani', 2000, 'Staf Administrasi', 'Malam'),
('MED015', 'Ns. Ilham Kurniawan, S.Kep', 1994, 'Perawat', 'Pagi');
