package com.dicoding.rockman_barbershop.model;


import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Category implements Serializable {

    String gambar;
    String kategori;
    int urutan;

    public Category() {
    }

    public Category(String gambar, String kategori, int urutan) {
        this.gambar = gambar;
        this.kategori = kategori;
        this.urutan = urutan;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getUrutan() {
        return urutan;
    }

    public void setUrutan(int urutan) {
        this.urutan = urutan;
    }
}
