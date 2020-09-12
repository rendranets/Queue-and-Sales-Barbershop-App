package com.dicoding.rockman_barbershop.model;

public class RiwayatTransaksi {
    int harga;
    String kode_transaksi, status, tanggal_transaksi, user_id;

    public RiwayatTransaksi() {
    }

    public RiwayatTransaksi(int harga, String kode_transaksi, String status, String tanggal_transaksi, String user_id) {
        this.harga = harga;
        this.kode_transaksi = kode_transaksi;
        this.status = status;
        this.tanggal_transaksi = tanggal_transaksi;
        this.user_id = user_id;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getKode_transaksi() {
        return kode_transaksi;
    }

    public void setKode_transaksi(String kode_transaksi) {
        this.kode_transaksi = kode_transaksi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTanggal_transaksi() {
        return tanggal_transaksi;
    }

    public void setTanggal_transaksi(String tanggal_transaksi) {
        this.tanggal_transaksi = tanggal_transaksi;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
