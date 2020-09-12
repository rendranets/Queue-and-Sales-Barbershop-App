package com.dicoding.rockman_barbershop.model;

import java.io.Serializable;

public class Location implements Serializable {

    private String lokasi;

    public Location(){

    }

    public Location(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }
}
