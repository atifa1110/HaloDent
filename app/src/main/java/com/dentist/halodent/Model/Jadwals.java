package com.dentist.halodent.Model;

public class Jadwals {

    String konselorId,dokterId,mulai,selesai,tanggal;

    public Jadwals(){
    }

    public Jadwals(String konselorId, String dokterId, String mulai, String selesai, String tanggal) {
        this.konselorId = konselorId;
        this.dokterId = dokterId;
        this.mulai = mulai;
        this.selesai = selesai;
        this.tanggal = tanggal;
    }

    public String getKonselorId() {
        return konselorId;
    }

    public void setKonselorId(String konselorId) {
        this.konselorId = konselorId;
    }

    public String getDokterId() {
        return dokterId;
    }

    public void setDokterId(String dokterId) {
        this.dokterId = dokterId;
    }

    public String getMulai() {
        return mulai;
    }

    public void setMulai(String mulai) {
        this.mulai = mulai;
    }

    public String getSelesai() {
        return selesai;
    }

    public void setSelesai(String selesai) {
        this.selesai = selesai;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
