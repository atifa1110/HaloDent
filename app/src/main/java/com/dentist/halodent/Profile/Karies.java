package com.dentist.halodent.Profile;

import java.io.Serializable;

public class Karies implements Serializable {
    private String kategori;
    private Integer score;

    public Karies(){

    }

    public Karies(String kategori, Integer score) {
        this.kategori = kategori;
        this.score = score;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
