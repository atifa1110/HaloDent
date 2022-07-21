package com.dentist.halodent.SignUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;
import com.google.android.material.textfield.TextInputEditText;

public class Step3Activity extends AppCompatActivity {

    private TextInputEditText et_jawaban;
    private Button btn_next;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3);

        //inisialisasi view
        et_jawaban = findViewById(R.id.et_isian_step3);
        btn_next = findViewById(R.id.btn_next_3);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //buttom click
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit dan masukkan data
                if(inputValidated()) {
                    Preference.setKeyStep3(getApplicationContext(),et_jawaban.getText().toString());

                    //pindah halaman
                    Intent intent = new Intent(Step3Activity.this, Step4Activity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private Boolean inputValidated(){
        boolean res = true;
        String jawaban = et_jawaban.getText().toString().trim();

        if(jawaban.isEmpty()) {
            res = false;
            et_jawaban.setError("Silahkan isi field");
        }
        return res;
    }


    //remove key step 3 from shared preference
    @Override
    public void onBackPressed() {
        Preference.removeStep3(getApplicationContext());
        super.onBackPressed();
    }

}