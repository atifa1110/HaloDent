package com.dentist.halodent.SignUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;

public class Step1Activity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_iya,btn_tidak;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);

        // inisialisasi semua view
        btn_iya = findViewById(R.id.btn_iya);
        btn_tidak = findViewById(R.id.btn_tidak);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

       //btn click
        btn_tidak.setOnClickListener(this);
        btn_iya.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_iya:
                //edit dan masukkan data
                Preference.setKeyStep1(getApplicationContext(),btn_iya.getText().toString());

                //intent pindah halaman
                Intent yes = new Intent(Step1Activity.this,Step2Activity.class);
                startActivity(yes);

                break;
            case R.id.btn_tidak:
                //edit dan masukkan data
                Preference.setKeyStep1(getApplicationContext(),btn_tidak.getText().toString());

                //intent pindah halaman
                Intent no = new Intent(Step1Activity.this,Step2Activity.class);
                startActivity(no);
                break;
        }
    }

    //on back press hapus key step 1
    @Override
    public void onBackPressed() {
        Preference.removeStep1(getApplicationContext());
        super.onBackPressed();
    }

}