package com.dicoding.rockman_barbershop.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Collection implements Serializable {

    private String nama_barang;
    private String harga_barang;
    private String stock_barang;
    private String keterangan;

    public Collection(String nama_barang, String harga_barang, String stock_barang, String keterangan) {
        this.nama_barang = nama_barang;
        this.harga_barang = harga_barang;
        this.stock_barang = stock_barang;
        this.keterangan = keterangan;
    }

    public Collection() {
    }

    public String getJudul_entry() {
        return nama_barang;
    }

    public void setJudul_entry(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public String getKategori_entry() {
        return harga_barang;
    }

    public void setKategori_entry(String harga_barang) {
        this.harga_barang = harga_barang;
    }

    public String getProvinsi_entry() {
        return stock_barang;
    }

    public void setProvinsi_entry(String stock_barang) {
        this.stock_barang = stock_barang;
    }

    public String getDaerah_entri() {
        return keterangan;
    }

    public void setDaerah_entri(String keterangan) {
        this.keterangan = keterangan;
    }
}
