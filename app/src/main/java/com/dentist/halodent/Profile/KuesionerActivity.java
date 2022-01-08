package com.dentist.halodent.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;

public class KuesionerActivity extends AppCompatActivity {

    private Button btn_mulai;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuesioner);

        btn_mulai = findViewById(R.id.btn_start);
        toolbar = findViewById(R.id.toolbar);

        if (Preference.getKeyButtonClick(getApplicationContext())){
            btn_mulai.setText("Lihat Hasil");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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