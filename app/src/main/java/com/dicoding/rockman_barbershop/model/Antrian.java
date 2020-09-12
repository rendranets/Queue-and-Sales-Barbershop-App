package com.dicoding.rockman_barbershop.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
@IgnoreExtraProperties

public class Antrian implements Serializable {

    String kode_antrian, kode_user, nama_user, status, tanggal_antrian;
    int nomor_antrian;

    public Antrian() {
    }

    public Antrian(String kode_antrian, String nama_user, String status, String tanggal_antrian) {
        this.kode_antrian = kode_antrian;
        this.nama_user = nama_user;
        this.status = status;
        this.tanggal_antrian = tanggal_antrian;
    }

    public Antrian(String kode_antrian, String kode_user, String nama_user, int nomor_antrian, String status, String tanggal_antrian) {
        this.kode_antrian = kode_antrian;
        this.kode_user = kode_user;
        this.nama_user = nama_user;
        this.nomor_antrian = nomor_antrian;
        this.status = status;
        this.tanggal_antrian = tanggal_antrian;
    }

    public String getKode_antrian() {
        return kode_antrian;
    }

    public void setKode_antrian(String kode_antrian) {
        this.kode_antrian = kode_antrian;
    }

    public String getKode_user() {
        return kode_user;
    }

    public void setKode_user(String kode_user) {
        this.kode_user = kode_user;
    }

    public String getNama_user() {
        return nama_user;
    }

    public void setNama_user(String nama_user) {
        this.nama_user = nama_user;
    }

    public int getNomor_antrian() {
        return nomor_antrian;
    }

    public void setNomor_antrian(int nomor_antrian) {
        this.nomor_antrian = nomor_antrian;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTanggal_antrian() {
        return tanggal_antrian;
    }

    public void setTanggal_antrian(String tanggal_antrian) {
        this.tanggal_antrian = tanggal_antrian;
    }
}
