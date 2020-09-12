package com.dicoding.rockman_barbershop.model;

public class RiwayatPesanan {

    private String kode_barang;
    private String nama_barang;
    private String foto_barang;
    private String user_id;
    private int  harga_barang, total_harga,kuantitas;

    public RiwayatPesanan() {
    }

    public RiwayatPesanan(String kode_barang, String nama_barang, String foto_barang, String user_id, int harga_barang, int total_harga, int kuantitas) {
        this.kode_barang = kode_barang;
        this.nama_barang = nama_barang;
        this.foto_barang = foto_barang;
        this.user_id = user_id;
        this.harga_barang = harga_barang;
        this.total_harga = total_harga;
        this.kuantitas = kuantitas;
    }

    public String getKode_barang() {
        return kode_barang;
    }

    public void setKode_barang(String kode_barang) {
        this.kode_barang = kode_barang;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public String getFoto_barang() {
        return foto_barang;
    }

    public void setFoto_barang(String foto_barang) {
        this.foto_barang = foto_barang;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getHarga_barang() {
        return harga_barang;
    }

    public void setHarga_barang(int harga_barang) {
        this.harga_barang = harga_barang;
    }

    public int getTotal_harga() {
        return total_harga;
    }

    public void setTotal_harga(int total_harga) {
        this.total_harga = total_harga;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public void setKuantitas(int kuantitas) {
        this.kuantitas = kuantitas;
    }
}
