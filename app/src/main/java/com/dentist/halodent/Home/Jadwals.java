package com.dentist.halodent.Home;

public class Jadwals {

    String konselor_id,dokter_id,mulai,selesai,tanggal;

    public Jadwals(){

    }
    public Jadwals(String konselor_id, String dokter_id, String tanggal, String mulai, String selesai) {
        this.konselor_id = konselor_id;
        this.dokter_id = dokter_id;
        this.tanggal = tanggal;
        this.mulai = mulai;
        this.selesai = selesai;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getKonselor_id() {
        return konselor_id;
    }

    public void setKonselor_id(String konselor_id) {
        this.konselor_id = konselor_id;
    }

    public String getDokter_id() {
        return dokter_id;
    }

    public void setDokter_id(String dokter_id) {
        this.dokter_id = dokter_id;
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
}
