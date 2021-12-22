package com.dentist.halodent.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;

public class KuesionerActivity extends AppCompatActivity {

    Button btn_mulai;
    ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuesioner);

        btn_back = findViewById(R.id.btn_back_kuesioner);
        btn_mulai = findViewById(R.id.btn_start);

        if (Preference.getKeyButtonClick(getApplicationContext())){
            btn_mulai.setText("Lihat Hasil");
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_mulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KuesionerActivity.this,MainQuizActivity.class);
                Preference.setKeyButtonClick(getApplicationContext(),true);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}