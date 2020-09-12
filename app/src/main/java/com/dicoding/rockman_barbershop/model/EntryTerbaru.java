package com.dicoding.rockman_barbershop.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;

@IgnoreExtraProperties
public class EntryTerbaru implements Serializable {

    private String judul_entry;
    private String kategori_entry;
    private String provinsi_entry;
    private String daerah_entry;
    private Date tanggal_entry;


    public EntryTerbaru(String judul_entry, String kategori_entry, String provinsi_entry,
                        String daerah_entry, Date tanggal_entry) {
        this.judul_entry = judul_entry;
        this.kategori_entry = kategori_entry;
        this.provinsi_entry = provinsi_entry;
        this.daerah_entry = daerah_entry;
        this.tanggal_entry = tanggal_entry;
    }

    public EntryTerbaru() {
    }

    public String getJudul_entry() {
        return judul_entry;
    }

    public void setJudul_entry(String judul_entry) {
        this.judul_entry = judul_entry;
    }

    public String getKategori_entry() {
        return kategori_entry;
    }

    public void setKategori_entry(String kategori_entry) {
        this.kategori_entry = kategori_entry;
    }

    public String getProvinsi_entry() {
        return provinsi_entry;
    }

    public void setProvinsi_entry(String provinsi_entry) {
        this.provinsi_entry = provinsi_entry;
    }

    public String getDaerah_entry() {
        return daerah_entry;
    }

    public void setDaerah_entry(String daerah_entry) {
        this.daerah_entry = daerah_entry;
    }

    public Date getTanggal_entry() {
        return tanggal_entry;
    }

    public void setTanggal_entry(Date tanggal_entry) {
        this.tanggal_entry = tanggal_entry;
    }
}
