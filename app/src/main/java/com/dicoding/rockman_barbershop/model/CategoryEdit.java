package com.dicoding.rockman_barbershop.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class CategoryEdit implements Serializable {

    String kategori;
    int urutan;
//    String gambar;

    public CategoryEdit() {
    }

    public CategoryEdit(String kategori, int urutan) {
        this.kategori = kategori;
        this.urutan = urutan;
//        this.gambar = gambar;
    }

//    public String getGambar() {
//        return gambar;
//    }
//
//    public void setGambar(String gambar) {
//        this.gambar = gambar;
//    }

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
