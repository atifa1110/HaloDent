package com.dentist.halodent.SignUp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class Step4Activity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private CheckBox diabetes,jantung,hipertensi,alergi,tidak_ada;
    private TextInputEditText et_alergi;
    private TextInputLayout til_alergi;
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
        alergi =  findViewById(R.id.checkBox_alergi);
        tidak_ada = findViewById(R.id.checkBox_tidak_ada);
        et_alergi = findViewById(R.id.et_alergi);
        til_alergi = findViewById(R.id.til_alergi);
        toolbar = findViewById(R.id.toolbar);;
        btn_next = findViewById(R.id.btn_next_4);

        til_alergi.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_alergi.setEnabled(false);
                checkbox.add("alergi:"+et_alergi.getText().toString());
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        diabetes.setOnCheckedChangeListener(this);
        jantung.setOnCheckedChangeListener(this);
        hipertensi.setOnCheckedChangeListener(this);
        alergi.setOnCheckedChangeListener(this);
        tidak_ada.setOnCheckedChangeListener(this);

        finalSelection();
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox.isEmpty()){
                    Toast.makeText(Step4Activity.this,"Isi data",Toast.LENGTH_SHORT).show();
                }else {
                    finalSelection();
                    Intent intent = new Intent(Step4Activity.this, SignUpActivity.class);
                    startActivity(intent);
                    checkbox.clear();
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.checkBox_diabetes:
                if (isChecked){
                    checkbox.add("diabetes");
                }else{
                    checkbox.remove("diabetes");
                }
                break;
            case R.id.checkBox_hipertensi:
                if (isChecked){
                    checkbox.add("hipertensi");
                }else{
                    checkbox.remove("hipertensi");
                }
                break;
            case R.id.checkBox_jantung:
                if (isChecked){
                    checkbox.add( "jantung");
                }else{
                    checkbox.remove("jantung");
                }
                break;
            case R.id.checkBox_alergi:
                String alergi = et_alergi.getText().toString().toLowerCase();
                if (isChecked){
                    til_alergi.setVisibility(View.VISIBLE);
                    et_alergi.setEnabled(true);
                }else{
                    til_alergi.setVisibility(View.GONE);
                    checkbox.remove("alergi:"+alergi);
                    et_alergi.setText("");
                }
                break;
            case R.id.checkBox_tidak_ada:
                if (isChecked){
                    checkbox.add("tidak ada");
                }else{
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