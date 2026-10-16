# Tugas Praktikum 5 DPBO 2526 C2 (TP5DPBO2526C2)
Aplikasi Manajemen Data Personel Rumah Sakit (**PeopleData**) berbasis **Java Swing GUI** dan **Database Back-End (JDBC MySQL)** dengan fitur **CRUD (Create, Read, Update, Delete)** penuh yang terhubung langsung ke database MySQL (Laragon).

---

## Janji

> Saya Muhammad Zidan Mirza Fedrieka dengan NIM 2507692 mengerjakan Tugas Praktikum 5 dalam mata kuliah Desain Pemrograman Berorientasi Objek untuk keberkahan-Nya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.

---

## 1. Desain dan Fungsi Program

### A. Perubahan Arsitektur dari TP4 ke TP5
Pada Tugas Praktikum 5 ini, sistem penyimpanan data di memori sementara (`ArrayList<Person>`) **dihapus sepenuhnya** dan dialihkan ke database relasional **MySQL** melalui mekanisme **Java Database Connectivity (JDBC)** menggunakan driver `mysql-connector-j-26.7.0.jar`.

Setiap operasi manipulasi data (tambah, tampil, ubah, dan hapus) secara instan mengirimkan perintah SQL ke server database lokal (port 3306), sehingga integritas dan persistensi data tetap terjaga sekalipun aplikasi ditutup.

### B. Diagram Class

Program **PeopleData** dibangun menggunakan tiga class Java (`Database`, `Person`, dan `PeopleMenu`) serta antarmuka visual GUI Designer (`PeopleMenu.form`):

```mermaid
classDiagram
    JFrame <|-- PeopleMenu : Inheritance (extends)
    PeopleMenu *-- Database : Komposisi (Memiliki objek Database)
    PeopleMenu ..> Person : Dependensi (Data Model)

    class Database {
        -connection : Connection
        -statement : Statement
        +Database()
        +select(sql : String) ResultSet
        +insert(sql : String) int
        +close() void
    }

    class Person {
        -id : String
        -nama : String
        -tahunLahir : int
        -kategori : String
        -shiftKerja : String
        +Person(id : String, nama : String, tahunLahir : int, kategori : String, shiftKerja : String)
        +getId() String
        +setId(id : String) void
        +getNama() String
        +setNama(nama : String) void
        +getTahunLahir() int
        +setTahunLahir(tahunLahir : int) void
        +getKategori() String
        +setKategori(kategori : String) void
        +getShiftKerja() String
        +setShiftKerja(shiftKerja : String) void
    }

    class PeopleMenu {
        -selectedIndex : int
        -database : Database
        -mainPanel : JPanel
        -idField : JTextField
        -namaField : JTextField
        -tahunLahirField : JTextField
        -kategoriComboBox : JComboBox~String~
        -shiftPanel : JPanel
        -pagiRadioButton : JRadioButton
        -siangRadioButton : JRadioButton
        -malamRadioButton : JRadioButton
        -shiftButtonGroup : ButtonGroup
        -addUpdateButton : JButton
        -cancelButton : JButton
        -deleteButton : JButton
        -peopleTable : JTable
        +main(args : String[]) void
        +PeopleMenu()
        +setTable() DefaultTableModel
        +insertData() void
        +updateData() void
        +deleteData() void
        +clearForm() void
        -getSelectedShift() String
        -setSelectedShift(shift : String) void
    }
```

---

### C. Penjelasan Class, Atribut, dan Database

#### 1. Class `Database` (`Database.java`)
Class pembungkus (*wrapper*) JDBC untuk mengelola siklus hidup koneksi dan eksekusi query ke MySQL:
- `connection` (`Connection`): Objek koneksi JDBC ke database `jdbc:mysql://localhost:3306/db_people` dengan user `root` dan tanpa password.
- `statement` (`Statement`): Objek statement untuk mengeksekusi instruksi SQL.
- `select(String sql)` (`ResultSet`): Method pembantu untuk menjalankan query `SELECT`.
- `insert(String sql)` (`int`): Method pembantu untuk mengeksekusi manipulasi data (`INSERT`, `UPDATE`, `DELETE`) menggunakan `executeUpdate()`.
- `close()` (`void`): Menutup koneksi dan statement secara aman setelah transaksi selesai.

#### 2. Class `Person` (`Person.java`)
Class model entitas personel rumah sakit dengan enkapsulasi atribut:
- `id` (`String`): ID identitas unik personel (Primary Key pada tabel).
- `nama` (`String`): Nama lengkap beserta gelar.
- `tahunLahir` (`int`): Tahun kelahiran personel.
- `kategori` (`String`): Profesi/jabatan (`Dokter Spesialis`, `Dokter Umum`, `Perawat`, `Apoteker`, `Staf Administrasi`).
- `shiftKerja` (`String`): Jadwal shift kerja (`Pagi`, `Siang`, `Malam`).

#### 3. Class `PeopleMenu` (`PeopleMenu.java` & `PeopleMenu.form`)
Class turunan `JFrame` yang menjadi pengendali tampilan GUI dan perantara aksi pengguna ke database:
- **Komponen Input Form:**
  - `idField`, `namaField`, `tahunLahirField` (`JTextField`): Input teks ID, Nama, dan Tahun Lahir.
  - `kategoriComboBox` (`JComboBox<String>`): Pilihan dropdown profesi tenaga medis/administrasi.
  - `pagiRadioButton`, `siangRadioButton`, `malamRadioButton` (`JRadioButton` dalam `ButtonGroup`): Pilihan jadwal shift kerja.
- **Komponen Tombol & Tabel:**
  - `addUpdateButton` (`JButton`): Mode tambah (`Add`) jika belum ada baris terpilih, atau mode simpan perubahan (`Update`) jika salah satu baris tabel dipilih.
  - `cancelButton` (`JButton`): Mengosongkan form dan membatalkan seleksi baris tabel.
  - `deleteButton` (`JButton`): Menghapus data terpilih setelah konfirmasi pengguna.
  - `peopleTable` (`JTable`): Menampilkan data hasil query `SELECT * FROM person`.

---

## 2. Penjelasan Alur Program (Operasi CRUD & Validasi)

### 1. Inisialisasi & Pembacaan Data (Read)
- Saat program dijalankan, method `setTable()` dipanggil.
- Program membuka koneksi ke database melalui objek `Database`, lalu mengeksekusi query:
  ```sql
  SELECT * FROM person
  ```
- Hasil `ResultSet` ditelusuri baris demi baris, dimasukkan ke dalam `DefaultTableModel`, dan dirender ke `peopleTable`.
- Form berada dalam kondisi bersih, tombol `Add` aktif, dan tombol `Delete` disembunyikan.

### 2. Penambahan Data (Create / Insert) & Validasi
- Pengguna mengisi data pada form dan menekan tombol **Add**.
- Program mengeksekusi serangkaian validasi sebelum menyentuh query:
  1. **Validasi Field Kosong:** Memastikan seluruh field (`ID`, `Nama`, `Tahun Lahir`) terisi. Jika kosong, muncul `JOptionPane.showMessageDialog` bertipe `WARNING_MESSAGE`: *"Semua field harus diisi!"*.
  2. **Validasi Kategori:** Memastikan dropdown tidak berada pada pilihan default `"???"`. Jika belum dipilih, muncul peringatan: *"Silakan pilih kategori yang valid!"*.
  3. **Validasi Format Angka:** Memeriksa apakah `Tahun Lahir` berupa angka numerik valid. Jika tidak, ditangkap melalui `NumberFormatException` dengan pesan: *"Tahun lahir harus berupa angka!"*.
  4. **Validasi ID Unik ke Database:** Program mengeksekusi query verifikasi:
     ```sql
     SELECT COUNT(*) FROM person WHERE id = 'ID_INPUT'
     ```
     Jika count > 0, operasi dibatalkan dan memunculkan pop-up error: *"ID '...' sudah terdaftar di database! Gunakan ID yang lain."*.
- Jika semua validasi lolos, program menjalankan query insert:
  ```sql
  INSERT INTO person (id, nama, thn_lahir, kategori, shift_kerja) VALUES ('...', '...', ..., '...', '...')
  ```
- Form dibersihkan via `clearForm()`, tabel disegarkan dengan data terbaru dari database, dan muncul notifikasi sukses: *"Data berhasil ditambahkan"*.

### 3. Pemilihan Baris & Pengubahan Data (Update)
- Saat pengguna mengklik salah satu baris di `peopleTable`, event `MouseListener` menangkap indeks baris (`selectedIndex`).
- Nilai data diambil langsung dari baris tabel menggunakan `table.getModel().getValueAt(row, col)` dan diisikan ke komponen form input.
- Teks tombol berubah menjadi **Update** dan tombol **Delete** dimunculkan.
- Saat tombol **Update** ditekan, program menjalankan validasi input lengkap (field tidak kosong, kategori valid, format tahun benar).
- Setelah validasi lolos, program mengeksekusi perintah SQL update berdasarkan ID:
  ```sql
  UPDATE person SET nama = '...', thn_lahir = ..., kategori = '...', shift_kerja = '...' WHERE id = '...'
  ```
- Tabel dimuat ulang secara otomatis dari database dan muncul dialog notifikasi: *"Data berhasil diubah"*.

### 4. Penghapusan Data (Delete) dengan Konfirmasi
- Saat baris dipilih dan pengguna menekan tombol **Delete**, program memunculkan dialog konfirmasi:
  ```java
  JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION)
  ```
- Jika pengguna memilih **Yes**, program mengeksekusi query delete:
  ```sql
  DELETE FROM person WHERE id = '...'
  ```
- Data terhapus secara permanen dari tabel MySQL, form dibersihkan, tombol `Delete` disembunyikan kembali, dan muncul dialog: *"Data berhasil dihapus"*.

### 5. Pembatalan (Cancel)
- Tombol **Cancel** memanggil `clearForm()`, mereset semua input teks, mengembalikan dropdown kategori ke indeks 0, mereset radio button ke shift "Pagi", menyembunyikan tombol `Delete`, dan mengembalikan tombol ke mode `"Add"`.

---

## 3. Dokumentasi Program (CRUD)

> Bukti tangkapan layar pengujian fungsionalitas CRUD dan Error Handling:

### 1. Tambah Data (Create)
![isi form tambah data](Dokumentasi/add_data.png)
![tambah data berhasil](Dokumentasi/add_berhasil.png)

### 2. Ubah Data (Update)
![pilih data untuk update](Dokumentasi/update_pilih.png)
![update data](Dokumentasi/update_data.png)
![update data berhasil](Dokumentasi/update_berhasil.png)

### 3. Hapus Data (Delete)
![pilih data yang ingin dihapus](Dokumentasi/delete_pilih.png)
![konfirmasi hapus data](Dokumentasi/delete_konfirmasi.png)
![hapus data berhasil](Dokumentasi/delete_berhasil.png)

### 4. Error Handling & Dialog Validasi
#### A. Validasi Kolom Kosong
![error kolom kosong](Dokumentasi/eror_handling_form_kosong.png)
![error kolom kosong (lanjutan)](Dokumentasi/eror_handling_form_kosong_2.png)

#### B. Validasi Duplikasi ID (ID Sama)
![error id duplikat](Dokumentasi/eror_handling_id_duplikat.png)
![error id duplikat (lanjutan)](Dokumentasi/eror_handling_id_duplikat_2.png)

#### C. Validasi Tipe Data Tahun Lahir Bukan Angka
![error tipe tahun salah](Dokumentasi/eror_handling_tipe_input.png)
![error tipe tahun salah (lanjutan)](Dokumentasi/eror_handling_tipe_input_2.png)