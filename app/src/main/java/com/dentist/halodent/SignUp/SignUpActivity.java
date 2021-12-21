package com.dentist.halodent.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dentist.halodent.Activity.MainActivity;
import com.dentist.halodent.Model.Interview;
import com.dentist.halodent.Model.PasienModel;
import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.Model.UserModel;
import com.dentist.halodent.R;
import com.dentist.halodent.Model.NodeNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private TextInputEditText etEmail, etName, etPassword, etConfirmPassword, etNomor;
    private Button btn_daftar;
    private View progressBar;
    private String email,nama,password,confirmPassword,ponsel;
    private Toolbar toolbar;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReferenceUser,databaseReferenceSurvey;

    private String step1,step2,step3,step4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //inisialisasi view
        etEmail = findViewById(R.id.et_email_signup);
        etName = findViewById(R.id.et_nama_signup);
        etPassword = findViewById(R.id.et_pass_signup);
        etConfirmPassword = findViewById(R.id.et_confirmpass_signup);
        etNomor = findViewById(R.id.et_nomor_signup);
        btn_daftar = findViewById(R.id.btn_daftar);
        progressBar = findViewById(R.id.progressBar);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //inisialisasi sharedPreference
        step1 = Preference.getKeyStep1(getApplicationContext());
        step2 = Preference.getKeyStep2(getApplicationContext());
        step3 = Preference.getKeyStep3(getApplicationContext());
        step4 = Preference.getKeyStep4(getApplicationContext());

        //set button click
        btn_daftar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_daftar:
                signUp(v);
                break;
        }
    }

    private void updateDatabase(){
        progressBar.setVisibility(View.VISIBLE);
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString().trim())
                .setPhotoUri(null)
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    String id = firebaseUser.getUid();
                    //Log.d(userId,"user");

                    //inisialisasi database
                    databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
                    databaseReferenceSurvey = FirebaseDatabase.getInstance().getReference().child(NodeNames.SURVEY);

                    nama = etName.getText().toString();
                    ponsel = etNomor.getText().toString();

                    PasienModel userModel = new PasienModel(id,nama,email,"",ponsel,"","","","","");
                    Interview interview = new Interview(step1,step2,step3,step4);

                    progressBar.setVisibility(View.VISIBLE);
                    databaseReferenceUser.child(id).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                databaseReferenceSurvey.child(id).child(NodeNames.INTERVIEW).setValue(interview).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressBar.setVisibility(View.GONE);
                                            //Toast.makeText(SignUpActivity.this, "berhasil tambah interview", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                            finish();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(SignUpActivity.this, "gagal menambahkan", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(SignUpActivity.this,getString(R.string.failed_to_update, task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signUp(View v){
        email = etEmail.getText().toString().trim();
        password =etPassword.getText().toString().trim();

        if(inputValidated()){
            progressBar.setVisibility(View.VISIBLE);
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
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
        ponsel = etNomor.getText().toString().trim();

        if(email.isEmpty()){
            res =false;
            etEmail.setError("Silahkan isi field");
        }else if(nama.isEmpty()){
            res=false;
            etName.setError("Silahkan isi field");
        }else if(password.isEmpty()){
            res = false;
            etPassword.setError("Silahkan isi field");
        }else if(confirmPassword.isEmpty()){
            res = false;
            etConfirmPassword.setError("Silahkan isi field");
        }else if(ponsel.isEmpty()){
            res = false;
            etNomor.setError("Silahkan isi field");
        }else if(!password.equals(confirmPassword)){
            res = false;
            etConfirmPassword.setError("Password tidak cocok");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            res =false;
            etEmail.setError("Masukkan Email yang benar");
        }
        return res;
    }

    //remove key step 4 from shared preference
    @Override
    public void onBackPressed() {
        Preference.removeStep4(getApplicationContext());
        super.onBackPressed();
    }
}