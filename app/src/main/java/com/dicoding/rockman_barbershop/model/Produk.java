package com.dicoding.rockman_barbershop.model;

import com.google.firebase.Timestamp;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Produk implements Serializable {

    private String nama_barang;
    private String keterangan_barang;
    private int harga_barang;
    private int stock_barang;
    private String foto_url_barang;
    private String kode_barang;

    private Timestamp tanggal_barang;

    private String key;

    public Produk(String kode_barang, String nama_barang, int harga_barang, int stock_barang) {
        this.nama_barang = nama_barang;
        this.harga_barang = harga_barang;
        this.stock_barang = stock_barang;
        this.kode_barang = kode_barang;

    }

    public Produk() {
    }

    public Produk(String nama_barang, String keterangan_barang, int harga_barang, int stock_barang, String foto_url_barang, String kode_barang, Timestamp tanggal_barang, String key) {
        this.nama_barang = nama_barang;
        this.keterangan_barang = keterangan_barang;
        this.harga_barang = harga_barang;
        this.stock_barang = stock_barang;
        this.foto_url_barang = foto_url_barang;
        this.kode_barang = kode_barang;
        this.tanggal_barang = tanggal_barang;
        this.key = key;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public String getKeterangan_barang() {
        return keterangan_barang;
    }

    public void setKeterangan_barang(String keterangan_barang) {
        this.keterangan_barang = keterangan_barang;
    }

    public int getHarga_barang() {
        return harga_barang;
    }

    public void setHarga_barang(int harga_barang) {
        this.harga_barang = harga_barang;
    }

    public int getStock_barang() {
        return stock_barang;
    }

    public void setStock_barang(int stock_barang) {
        this.stock_barang = stock_barang;
    }

    public String getFoto_url_barang() {
        return foto_url_barang;
    }

    public void setFoto_url_barang(String foto_url_barang) {
        this.foto_url_barang = foto_url_barang;
    }

    public String getKode_barang() {
        return kode_barang;
    }

    public void setKode_barang(String kode_barang) {
        this.kode_barang = kode_barang;
    }

    public Timestamp getTanggal_barang() {
        return tanggal_barang;
    }

    public void setTanggal_barang(Timestamp tanggal_barang) {
        this.tanggal_barang = tanggal_barang;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    //    public Produk(String nama_barang, String keterangan_barang, String harga_barang,
//                  String kode_barang, String stock_barang) {
//        this.nama_barang = nama_barang;
//        this.keterangan_barang = keterangan_barang;
//        this.harga_barang = harga_barang;
//        this.stock_barang = stock_barang;
//        this.kode_barang = kode_barang;
//    }

}
