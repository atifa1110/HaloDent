package com.dentist.halodent.SignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import com.dentist.halodent.Utils.Util;
import com.dentist.halodent.R;
import com.dentist.halodent.SignUp.Step1Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener,TextWatcher{

    private TextInputLayout tilEmail,tilPassword;
    private TextInputEditText etEmail, etPassword;
    private String email,password;
    private Button btnMasuk,btnDaftar;
    private TextView tv_lupa_pass;

    private FirebaseAuth mAuth;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //inisialisasi view
        etEmail = findViewById(R.id.et_email_signin);
        etPassword = findViewById(R.id.et_pass_signin);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        btnMasuk = findViewById(R.id.btn_masuk);
        btnDaftar = findViewById(R.id.btn_daftar);
        tv_lupa_pass = findViewById(R.id.tv_lupa_pass);

        etPassword.addTextChangedListener(this);
        etEmail.addTextChangedListener(this);

        String text = "Belum punya akun? Daftar disini";
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan blue = new ForegroundColorSpan(ContextCompat.getColor(getApplication(), R.color.blue));
        ForegroundColorSpan gray = new ForegroundColorSpan(Color.GRAY);

        // It is used to set the span to the string
        spannableString.setSpan(gray, 0, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(blue, 18, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        btnDaftar.setText(spannableString);

        //inisialisasi database auth
        mAuth = FirebaseAuth.getInstance();

        //inisialisasi progress bar
        progress = new ProgressDialog(this);
        progress.setMessage("Sign In .. Silahkan tunggu..");

        //inisialisasi view yang di click
        btnDaftar.setOnClickListener(this);
        btnMasuk.setOnClickListener(this);
        tv_lupa_pass.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        //dalam keaadan sudah start
        super.onStart();
        //cek apakah sudah login atau belum sesuai dengan user yang masuk
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //jika sudah login maka pindah ke main activity
        if (currentUser!=null){
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<String> task) {
                    try{
                        String message = task.getResult();
                        Util.updateDeviceToken(SignInActivity.this,message);
                    }catch (Exception e){
                        Toast.makeText(SignInActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_masuk:
                signIn();
                break;
            case R.id.btn_daftar:
                signUp();
                break;
            case R.id.tv_lupa_pass:
                lupaPassword();
                break;
        }
    }

    private void lupaPassword(){
        Intent intent = new Intent(SignInActivity.this, LupaPasswordActivity.class);
        startActivity(intent);
    }

    private void signUp(){
        Intent intent = new Intent(SignInActivity.this, Step1Activity.class);
        startActivity(intent);
    }

    public void signIn(){
        // jika input data sudah benar
        if (inputValidated()) {
            progress.show();
            //set text menjadi string supaya data bisa dibaca dalam bentuk string
            email = etEmail.getText().toString().trim();
            password = etPassword.getText().toString().trim();

            //signin dengan email dan password
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //progress ilang
                        progress.dismiss();
                        //jika berhasil masuk akan muncul komen bahwa login berhasil
                        Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                        //pindah ke mainActivity
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        //progress ilang
                        progress.dismiss();
                        //keluar error
                        Toast.makeText(SignInActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean inputValidated(){
        boolean res = true;
        //jika text email kosong
        if (etEmail.getText().toString().isEmpty()){
            res = false;
            tilEmail.setError("Error : Email Kosong");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()){
            res = false;
            tilEmail.setError("Error : Email salah");
        }else if (etPassword.getText().toString().isEmpty() || etPassword.length()<6){
            res = false;
            tilPassword.setError("Error : Minimal 6 Karakter");
        }
        return res;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        tilPassword.setError(null);
        tilEmail.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}