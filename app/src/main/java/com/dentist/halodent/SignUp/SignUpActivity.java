package com.dentist.halodent.SignUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dentist.halodent.SignIn.SignInActivity;
import com.dentist.halodent.Model.Pasiens;
import com.dentist.halodent.Utils.Preference;
import com.dentist.halodent.R;
import com.dentist.halodent.Utils.NodeNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private TextInputLayout tilEmail,tilName,tilPassword,tilConfirmPassword,tilUsia;
    private TextInputEditText etEmail,etName,etPassword,etConfirmPassword,etUsia;
    private TextView hasil;
    private Button btn_daftar,btn_masuk;
    private String email,nama,password,confirmPassword,usia;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReferenceUser,databaseReferenceSurvey;

    private String step1,step2,step3,step4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //inisialisasi view
        tilEmail = findViewById(R.id.til_email_signup);
        tilName = findViewById(R.id.til_nama_signup);
        tilPassword = findViewById(R.id.til_password_signup);
        tilConfirmPassword = findViewById(R.id.til_confirm_signup);
        tilUsia = findViewById(R.id.til_usia_signup);
        etEmail = findViewById(R.id.et_email_signup);
        etName = findViewById(R.id.et_nama_signup);
        etPassword = findViewById(R.id.et_password_signup);
        etConfirmPassword = findViewById(R.id.et_confirm_signup);
        etUsia = findViewById(R.id.et_usia_signup);
        btn_daftar = findViewById(R.id.btn_daftar);
        btn_masuk = findViewById(R.id.btn_masuk);
        hasil  = findViewById(R.id.hasil);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Daftar..Silahkan Tunggu..");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etEmail.addTextChangedListener(this);
        etName.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        etConfirmPassword.addTextChangedListener(this);
        etUsia.addTextChangedListener(this);

        //inisialisasi sharedPreference
        step1 = Preference.getKeyStep1(getApplicationContext());
        step2 = Preference.getKeyStep2(getApplicationContext());
        step3 = Preference.getKeyStep3(getApplicationContext());
        step4 = Preference.getKeyStep4(getApplicationContext());

        hasil.setText(step1+"\n"+step2+"\n"+step3+"\n"+step4);
        String text = "Sudah punya akun? Masuk disini";
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan blue = new ForegroundColorSpan(ContextCompat.getColor(getApplication(), R.color.blue));
        ForegroundColorSpan gray = new ForegroundColorSpan(Color.GRAY);

        // It is used to set the span to the string
        spannableString.setSpan(gray, 0, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(blue, 18, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        btn_masuk.setText(spannableString);

        //set button click
        btn_masuk.setOnClickListener(this);
        btn_daftar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_daftar:
                signUp();
                break;
            case R.id.btn_masuk:
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void updateDatabase(){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString().trim())
                .setPhotoUri(null)
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    String id = firebaseUser.getUid();
                    //inisialisasi database
                    databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(NodeNames.PASIENS);
                    databaseReferenceSurvey = FirebaseDatabase.getInstance().getReference().child(NodeNames.SURVEY);

                    Pasiens pasiens = new Pasiens(id,etName.getText().toString(),email,"","","Online","Pasien","",etUsia.getText().toString(),"");
                    Interviews interviews = new Interviews(step1,step2,step3,step4);

                    Preference.setKeyUserAge(SignUpActivity.this,Integer.parseInt(pasiens.getUsia()));
                    databaseReferenceUser.child(id).setValue(pasiens).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                databaseReferenceSurvey.child(id).child(Interviews.class.getSimpleName()).setValue(interviews).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if(task.isSuccessful()){
                                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                            finish();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(SignUpActivity.this, R.string.failed_to_update, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(SignUpActivity.this,getString(R.string.failed_to_update, task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signUp(){
        email = etEmail.getText().toString().trim();
        password =etPassword.getText().toString().trim();

        if(inputValidated()){
            progressDialog.show();
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        firebaseUser = firebaseAuth.getCurrentUser();
                        updateDatabase();
                    }else {
                        Toast.makeText(SignUpActivity.this,getString(R.string.sign_up_failed, task.getException()), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public boolean inputValidated(){
        boolean res = true;

        email = etEmail.getText().toString().trim();
        nama = etName.getText().toString().trim();
        password =etPassword.getText().toString().trim();
        confirmPassword = etConfirmPassword.getText().toString().trim();
        usia = etUsia.getText().toString().trim();

        if(email.isEmpty()){
            res =false;
            tilEmail.setError("Error : Email Kosong");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            res =false;
            tilEmail.setError("Error : Email salah");
        }else if(nama.isEmpty()){
            res=false;
            tilName.setError("Error : Nama Kosong");
        }else if(password.isEmpty() || password.length()<6){
            res = false;
            tilPassword.setError("Error : Minimal 6 Karakter");
        }else if(confirmPassword.isEmpty()){
            res = false;
            tilConfirmPassword.setError("Error : Password Kosong");
        }else if(!password.equals(confirmPassword)){
            res = false;
            tilPassword.setError("Error : Password tidak cocok");
            tilConfirmPassword.setError("Error : Password tidak cocok");
        }else if(usia.isEmpty()){
            res = false;
            tilUsia.setError("Error : Usia Kosong");
        }
        return res;
    }

    //remove key step 4 from shared preference
    @Override
    public void onBackPressed() {
        Preference.removeStep4(getApplicationContext());
        super.onBackPressed();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        tilName.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tilUsia.setError(null);
        tilEmail.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}