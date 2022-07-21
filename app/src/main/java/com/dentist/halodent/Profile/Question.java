package com.dentist.halodent.Profile;

public class Question {

    private int umur;

    public Question(int umur){
        this.umur = umur;
    }

    // array question and answer untuk umur diatas 6 tahun
    private String questionListDewasa [] = {
            "Berapa kali anda menggosok gigi dalam sehari?",
            "Apakah anda rutin menggosok gigi malam hari sebelum tidur ?",
            "Setiap gosok gigi menggunakan pasta gigi",
            "Apakah saat ini gigi anda ada yang berlubang ?",
            "Jika ya berapa jumlah gigi anda yang berlubang ?..jika tidak lanjut ke pertanyaan berikutnya",
            "Apakah dalam 1 tahun terakhir ini anda pernah periksa ke dokter gigi ?",
            "Apakah anda rutin ke dokter gigi minimal 1 kali dalam setahun",
            "Apakah dalam 1 hari anda meminum minuman manis lebih dari 1 gelas/botol ?",
            "Apakah dalam 1 hari anda makan makanan manis (seperti permen/coklat/kue dll) lebih dari 1 kali ?"
    };

    private String multipleChoiceDewasa [][] = {
            {"Lebih dari 2 kali","2 kali","1 kali","Tidak pernah/jarang"},
            {"Ya","null","Tidak","null"},
            {"Ya","null","Tidak","null"},
            {"Tidak","null","Ya","null"},
            {"1","null","2-4","Lebih dari 4"},
            {"Ya","null","Tidak","null"},
            {"Ya","null","Tidak","null"},
            {"Tidak","null","Ya","null"},
            {"Tidak","null","Ya","null"}
    };

    // array question and answer untuk umur dibawah 5 tahun
    private String questionList [] = {
            "Berapa kali anak anda menggosok gigi dalam sehari?",
            "Apakah anak anda rutin menggosok gigi malam hari sebelum tidur ?",
            "Setiap gosok gigi menggunakan pasta gigi?",
            "Apakah saat ini gigi anak anda ada yang berlubang ?",
            "Jika ya berapa jumlah gigi anak anda yang berlubang ?..jika 'tidak' lanjut ke pertanyaan berikutnya",
            "Apakah dalam 1 tahun terakhir ini anda pernah periksakan gigi anak anda ke dokter gigi ?",
            "Apakah anda rutin memeriksakan gigi anak anda ke dokter gigi minimal 1 kali dalam setahun?",
            "Apakah anak anda meminum susu botol saat tidur malam?",
            "Apakah dalam 1 hari anda meminum minuman manis lebih dari 1 gelas/botol?",
            "Apakah dalam 1 hari anda makan makanan manis (seperti permen/coklat/kue dll) lebih dari 1 kali ?",
            "Apakah anda (orang tua/pengasuh anak) memiliki gigi yang berlubang ?"
    };

    private String multipleChoice [][] = {
            {"Lebih dari 2 kali","2 kali","1 kali","Tidak pernah/jarang"},
            {"Ya","null","Tidak","null"},
            {"Ya","null","Tidak","null"},
            {"Tidak","null","Ya","null"},
            {"1","null","2-4","Lebih dari 4"},
            {"Ya","null","Tidak","null"},
            {"Ya","null","Tidak","null"},
            {"Tidak","null","Ya","null"},
            {"Tidak","null","Ya","null"},
            {"Tidak","null","Ya","null"},
            {"Tidak","null","Ya","null"}
    };

    public int getLength(){
        if(getUmur()<6){
            return questionList.length;
        }else{
            return questionListDewasa.length;
        }
    }

    public String getQuestion(int a){
        String question;
        if(getUmur()<6){
            question = questionList[a];
        }else{
            question = questionListDewasa[a];
        }
        return question;
    }

    public String getChoice(int index,int num){
        String choice;
        if(getUmur()<6){
            choice = multipleChoice[index][num-1];
        }else{
            choice = multipleChoiceDewasa[index][num-1];
        }
        return choice;
    }

    public int getUmur() {
        return umur;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }
}
