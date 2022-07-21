package com.dentist.halodent.SignUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;
import com.google.android.material.textfield.TextInputEditText;

public class Step2Activity extends AppCompatActivity {

    private TextInputEditText et_jawaban;
    private Button btn_next;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);

        //inisialisasi view
        et_jawaban = findViewById(R.id.et_isian_step2);
        btn_next = findViewById(R.id.btn_next_2);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //btn di klik
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputValidated()){
                    Preference.setKeyStep2(getApplicationContext(),et_jawaban.getText().toString());

                    //intent pindah halaman
                    Intent intent = new Intent(Step2Activity.this,Step3Activity.class);
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

    //back press akan hapus key step2
    @Override
    public void onBackPressed() {
        Preference.removeStep2(getApplicationContext());
        super.onBackPressed();
    }
}