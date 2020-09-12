package com.dicoding.rockman_barbershop.model;

import java.sql.Timestamp;

public class User {

    String user_id, nama_profile, email_profile, phone_profile;
        Timestamp waktu_Daftar;


    public User() {
    }

    public User(String user_id, String nama_profile, String email_profile, String phone_profile, Timestamp waktu_Daftar) {
        this.user_id = user_id;
        this.nama_profile = nama_profile;
        this.email_profile = email_profile;
        this.phone_profile = phone_profile;
        this.waktu_Daftar = waktu_Daftar;

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNama_profile() {
        return nama_profile;
    }

    public void setNama_profile(String nama_profile) {
        this.nama_profile = nama_profile;
    }

    public String getEmail_profile() {
        return email_profile;
    }

    public void setEmail_profile(String email_profile) {
        this.email_profile = email_profile;
    }

    public String getPhone_profile() {
        return phone_profile;
    }

    public void setPhone_profile(String phone_profile) {
        this.phone_profile = phone_profile;
    }

    public Timestamp getWaktu_Daftar() {
        return waktu_Daftar;
    }

    public void setWaktu_Daftar(Timestamp waktu_Daftar) {
        this.waktu_Daftar = waktu_Daftar;
    }

}
