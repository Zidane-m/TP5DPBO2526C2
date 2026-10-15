public class Person {
    private String id;
    private String nama;
    private int tahunLahir;
    private String kategori;
    private String shiftKerja;

    public Person(String id, String nama, int tahunLahir, String kategori, String shiftKerja) {
        this.id = id;
        this.nama = nama;
        this.tahunLahir = tahunLahir;
        this.kategori = kategori;
        this.shiftKerja = shiftKerja;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setTahunLahir(int tahunLahir) {
        this.tahunLahir = tahunLahir;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public void setShiftKerja(String shiftKerja) {
        this.shiftKerja = shiftKerja;
    }

    public String getId() {
        return this.id;
    }

    public String getNama() {
        return this.nama;
    }

    public int getTahunLahir() {
        return this.tahunLahir;
    }

    public String getKategori() {
        return this.kategori;
    }

    public String getShiftKerja() {
        return this.shiftKerja;
    }
}

