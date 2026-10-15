import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PeopleMenu extends JFrame {
    public static void main(String[] args) {
        // buat object window
        PeopleMenu menu = new PeopleMenu();

        // atur ukuran window
        menu.setSize(700, 600);

        // letakkan window di tengah layar
        menu.setLocationRelativeTo(null);

        // isi window
        menu.setContentPane(menu.mainPanel);

        // ubah warna background
        menu.getContentPane().setBackground(Color.WHITE);

        // tampilkan window
        menu.setVisible(true);

        // agar program ikut berhenti saat window diclose
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // objek Database untuk koneksi MySQL
    private Database database;

    private JPanel mainPanel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField tahunLahirField;
    private JTable peopleTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox<String> kategoriComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel idLabel;
    private JLabel namaLabel;
    private JLabel tahunLahirLabel;
    private JLabel kategoriLabel;
    private JLabel shiftLabel;
    private JPanel shiftPanel;
    private JRadioButton pagiRadioButton;
    private JRadioButton siangRadioButton;
    private JRadioButton malamRadioButton;
    private ButtonGroup shiftButtonGroup;

    // constructor
    public PeopleMenu() {
        // inisialisasi koneksi database
        database = new Database();

        // isi tabel orang langsung dari database
        peopleTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // atur isi combo box kategori
        String[] kategoriData = { "???", "Dokter Spesialis", "Dokter Umum", "Perawat", "Apoteker", "Staf Administrasi" };
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));

        // kelompokkan radio button shift kerja
        shiftButtonGroup = new ButtonGroup();
        shiftButtonGroup.add(pagiRadioButton);
        shiftButtonGroup.add(siangRadioButton);
        shiftButtonGroup.add(malamRadioButton);
        pagiRadioButton.setSelected(true);

        // sesuaikan background panel radio button agar menyatu dengan frame putih
        if (shiftPanel != null) {
            shiftPanel.setOpaque(false);
        }
        pagiRadioButton.setOpaque(false);
        siangRadioButton.setOpaque(false);
        malamRadioButton.setOpaque(false);

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });

        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex >= 0) {
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "Apakah Anda yakin ingin menghapus data ini?",
                            "Konfirmasi Hapus",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteData();
                    }
                }
            }
        });

        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        // saat salah satu baris tabel ditekan (mengambil data langsung dari baris tabel tanpa ArrayList)
        peopleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = peopleTable.getSelectedRow();

                if (selectedIndex != -1) {
                    // simpan value textfield, combo box, dan radio button dari kolom tabel
                    String curId = peopleTable.getModel().getValueAt(selectedIndex, 1).toString();
                    String curNama = peopleTable.getModel().getValueAt(selectedIndex, 2).toString();
                    String curTahunLahir = peopleTable.getModel().getValueAt(selectedIndex, 3).toString();
                    String curKategori = peopleTable.getModel().getValueAt(selectedIndex, 4).toString();
                    String curShift = peopleTable.getModel().getValueAt(selectedIndex, 5).toString();

                    // ubah isi textfield, combo box, dan radio button
                    idField.setText(curId);
                    namaField.setText(curNama);
                    tahunLahirField.setText(curTahunLahir);
                    kategoriComboBox.setSelectedItem(curKategori);
                    setSelectedShift(curShift);

                    // ubah button "Add" menjadi "Update"
                    addUpdateButton.setText("Update");

                    // tampilkan button delete
                    deleteButton.setVisible(true);
                }
            }
        });
    }

    // mengambil nilai shift kerja dari JRadioButton yang dipilih
    private String getSelectedShift() {
        if (siangRadioButton.isSelected()) {
            return "Siang";
        } else if (malamRadioButton.isSelected()) {
            return "Malam";
        }
        return "Pagi";
    }

    // mengatur pilihan JRadioButton berdasarkan nilai shift kerja
    private void setSelectedShift(String shift) {
        if ("Siang".equalsIgnoreCase(shift)) {
            siangRadioButton.setSelected(true);
        } else if ("Malam".equalsIgnoreCase(shift)) {
            malamRadioButton.setSelected(true);
        } else {
            pagiRadioButton.setSelected(true);
        }
    }

    // setTable: mengambil data langsung dari database MySQL
    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] cols = { "No", "ID", "Nama", "Tahun Lahir", "Kategori", "Shift Kerja" };

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel tmp = new DefaultTableModel(null, cols);

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM person");
            int no = 1;
            while (resultSet.next()) {
                Object[] row = {
                        no,
                        resultSet.getString("id"),
                        resultSet.getString("nama"),
                        resultSet.getInt("thn_lahir"),
                        resultSet.getString("kategori"),
                        resultSet.getString("shift_kerja")
                };
                tmp.addRow(row);
                no++;
            }
        } catch (SQLException e) {
            System.err.println("Error memuat data tabel: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Gagal mengambil data dari database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return tmp;
    }

    // memeriksa apakah ID sudah ada di dalam database
    private boolean isIdExist(String id, String excludeId) {
        try {
            String sql = "SELECT id FROM person WHERE id = '" + id.replace("'", "''") + "'";
            if (excludeId != null && !excludeId.isEmpty()) {
                sql += " AND id != '" + excludeId.replace("'", "''") + "'";
            }
            ResultSet rs = database.selectQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error cek ID di database: " + e.getMessage());
            return false;
        }
    }

    // insertData: menghubungkan Create ke database MySQL
    public void insertData() {
        // ambil value dari form
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        String tahunLahirStr = tahunLahirField.getText().trim();
        String kategori = kategoriComboBox.getSelectedItem().toString();
        String shiftKerja = getSelectedShift();

        // 1. Validasi field tidak boleh kosong
        if (id.isEmpty() || nama.isEmpty() || tahunLahirStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Semua kolom input harus diisi!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validasi kategori harus dipilih
        if ("???".equals(kategori)) {
            JOptionPane.showMessageDialog(null, "Silakan pilih kategori yang valid!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Validasi keunikan ID di database (tidak boleh duplikat saat insert)
        if (isIdExist(id, null)) {
            JOptionPane.showMessageDialog(null, "ID '" + id + "' sudah terdaftar di database! Gunakan ID yang lain.", "Error ID Duplikat",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int tahunLahir = Integer.parseInt(tahunLahirStr);

            // query insert ke database
            String sql = "INSERT INTO person (id, nama, thn_lahir, kategori, shift_kerja) VALUES ('"
                    + id.replace("'", "''") + "', '"
                    + nama.replace("'", "''") + "', "
                    + tahunLahir + ", '"
                    + kategori.replace("'", "''") + "', '"
                    + shiftKerja.replace("'", "''") + "')";

            database.insertUpdateDeleteQuery(sql);

            // update tampilan tabel
            peopleTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Insert berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun lahir harus berupa angka!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan data: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // updateData: menghubungkan Update ke database MySQL
    public void updateData() {
        // ambil data dari form
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        String tahunLahirStr = tahunLahirField.getText().trim();
        String kategori = kategoriComboBox.getSelectedItem().toString();
        String shiftKerja = getSelectedShift();

        // 1. Validasi field tidak boleh kosong
        if (id.isEmpty() || nama.isEmpty() || tahunLahirStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Semua kolom input harus diisi!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validasi kategori harus dipilih
        if ("???".equals(kategori)) {
            JOptionPane.showMessageDialog(null, "Silakan pilih kategori yang valid!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ambil ID asli dari baris tabel yang sedang dipilih
        String originalId = peopleTable.getModel().getValueAt(selectedIndex, 1).toString();

        // 3. Validasi keunikan ID (jika ID diubah, pastikan tidak bertabrakan dengan ID lain)
        if (isIdExist(id, originalId)) {
            JOptionPane.showMessageDialog(null, "ID '" + id + "' sudah digunakan oleh data lain di database!", "Error ID Duplikat",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int tahunLahir = Integer.parseInt(tahunLahirStr);

            // query update ke database
            String sql = "UPDATE person SET id = '" + id.replace("'", "''")
                    + "', nama = '" + nama.replace("'", "''")
                    + "', thn_lahir = " + tahunLahir
                    + ", kategori = '" + kategori.replace("'", "''")
                    + "', shift_kerja = '" + shiftKerja.replace("'", "''")
                    + "' WHERE id = '" + originalId.replace("'", "''") + "'";

            database.insertUpdateDeleteQuery(sql);

            // update tampilan tabel
            peopleTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Update berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun lahir harus berupa angka!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // deleteData: menghubungkan Delete ke database MySQL
    public void deleteData() {
        try {
            // ambil ID dari baris tabel yang dipilih
            String selectedId = peopleTable.getModel().getValueAt(selectedIndex, 1).toString();

            // query delete dari database
            String sql = "DELETE FROM person WHERE id = '" + selectedId.replace("'", "''") + "'";
            database.insertUpdateDeleteQuery(sql);

            // update tabel
            peopleTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Delete berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // clearForm
    public void clearForm() {
        // kosongkan semua textfield, combo box, dan reset radio button
        idField.setText("");
        namaField.setText("");
        tahunLahirField.setText("");
        kategoriComboBox.setSelectedIndex(0);
        pagiRadioButton.setSelected(true);

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }
}
