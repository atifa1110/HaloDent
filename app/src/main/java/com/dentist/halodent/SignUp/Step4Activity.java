package com.dentist.halodent.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class Step4Activity extends AppCompatActivity {

    private CheckBox diabetes,jantung,hipertensi,alergi,tidak_ada;
    private Button btn_next;
    private TextView hasil;
    private Toolbar toolbar;

    private List<String> checkbox = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step4);

        hasil = findViewById(R.id.hasil);
        diabetes = findViewById(R.id.checkBox_diabetes);
        jantung = findViewById(R.id.checkBox_jantung);
        hipertensi = findViewById(R.id.checkBox_hipertensi);
        alergi = findViewById(R.id.checkBox_alergi);
        tidak_ada = findViewById(R.id.checkBox_tidak_ada);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_next = findViewById(R.id.btn_next_4);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalSelection();
                Intent intent = new Intent(Step4Activity.this, SignUpActivity.class);
                startActivity(intent);
                checkbox.clear();
            }
        });
    }

    public void selecItem(View view) {
        boolean check = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.checkBox_diabetes:
                if (check) {
                    checkbox.add("diabetes");
                } else {
                    checkbox.remove("diabetes");
                }
                break;
            case R.id.checkBox_hipertensi:
                if (check) {
                    checkbox.add("hipertensi");
                } else {
                    checkbox.remove("hipertensi");
                }
                break;
            case R.id.checkBox_jantung:
                if (check) {
                    checkbox.add("jantung");
                } else {
                    checkbox.remove("jantung");
                }
                break;
            case R.id.checkBox_alergi:
                if (check) {
                    checkbox.add("alergi");
                } else {
                    checkbox.remove("alergi");
                }
                break;
            case R.id.checkBox_tidak_ada:
                if (check) {
                    checkbox.add("tidak ada");
                } else {
                    checkbox.remove("tidak ada");
                }
                break;
            default:
                break;
        }
    }

    public void finalSelection(){
        String final_selection = "";
        for (String selection : checkbox){
            final_selection = final_selection + selection +"\n";
        }
        Preference.setKeyStep4(getApplicationContext(),final_selection);
        //String a = Preference.getKeyStep1(getApplicationContext())+"\n"+Preference.getKeyStep2(getApplicationContext())+"\n"+Preference.getKeyStep3(getApplicationContext());
        //hasil.setText(a+"\n"+final_selection);
    }

    //remove key step 4 from shared preference
    @Override
    public void onBackPressed() {
        Preference.removeStep4(getApplicationContext());
        super.onBackPressed();
    }

}