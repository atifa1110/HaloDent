package com.dentist.halodent.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;

public class ScoreActivity extends AppCompatActivity {

    private TextView cara_jaga_karies,tv_nilai,tv_kategori;
    private Button btn_lanjutkan;
    private int score=0;
    private int umur= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        btn_lanjutkan = findViewById(R.id.button_lanjutkan);
        tv_kategori = findViewById(R.id.tv_kategori);
        tv_nilai = findViewById(R.id.tv_nilai);
        cara_jaga_karies = findViewById(R.id.cara_jaga);

        btn_lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //receive score through intent
        score = Preference.getKeyQuizScore(getApplicationContext());
        umur = Preference.getKeyUserAge(getApplicationContext());

        getKariesScore(score);
    }

    private void getKariesScore(int score){
        if(umur<6){
            getKariesAnak(score);
        }else{
            getKariesDewasa(score);
        }
    }

    private void getKariesDewasa (int score){
        tv_nilai.setText(String.valueOf(score)+"/20");
        if(score>=8 && score<=12){
            tv_kategori.setText(R.string.karies_rendah);
            cara_jaga_karies.setText(R.string.cara_jaga_karies_rendah);
        }else if(score>=13 && score<=16){
            tv_kategori.setText(R.string.karies_sedang);
            cara_jaga_karies.setText(R.string.cara_jaga_karies_sedang);
        }else if(score>=17 && score<=20){
            tv_kategori.setText(R.string.karies_tinggi);
            cara_jaga_karies.setText(R.string.cara_jaga_karies_tinggi);
        }
    }
    private void getKariesAnak(int score){
        tv_nilai.setText(String.valueOf(score)+"/24");
        if(score>=10 && score<=13){
            tv_kategori.setText(R.string.karies_rendah);
            cara_jaga_karies.setText(R.string.cara_jaga_karies_rendah);
        }else if(score>=14 && score<=19){
            tv_kategori.setText(R.string.karies_sedang);
            cara_jaga_karies.setText(R.string.cara_jaga_karies_sedang);
        }else if(score>=20 && score<=24){
            tv_kategori.setText(R.string.karies_tinggi);
            cara_jaga_karies.setText(R.string.cara_jaga_karies_tinggi);
        }
    }

}