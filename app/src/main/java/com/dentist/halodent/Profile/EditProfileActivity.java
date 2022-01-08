package com.dentist.halodent.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Model.PasienModel;
import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;
import com.dentist.halodent.Model.NodeNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText etNama , etUsia , etAlamat ,etNomor;
    private AutoCompleteTextView etJenisKel;
    private Button btn_simpan;
    private ImageView ivProfile;
    private String id,email,nama,usia,kelamin,photo,alamat,ponsel,status,role;
    private ArrayAdapter<String> jenisAdapter;

    private PasienModel pasien;
    private ProgressDialog progress;
    private BottomSheetDialog bottomSheetDialog;
    private Uri localFileUri, serverFileUri;
    private StorageReference fileStorage;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setActionBar();

        //inisialisasi view
        etNama = findViewById(R.id.et_nama_profile);
        etJenisKel = findViewById(R.id.et_jenis_kelamin);
        etUsia = findViewById(R.id.et_usia_profile);
        etAlamat = findViewById(R.id.et_alamat_profile);
        etNomor = findViewById(R.id.et_nomor_profile);
        btn_simpan = findViewById(R.id.btn_simpan);
        ivProfile = findViewById(R.id.iv_profile_profile);

        //set menu
        setDropDownMenu();

        progress = new ProgressDialog(this);
        progress.setMessage("Silahkan Tunggu..");

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_photo);

        //inisialisasi database
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
        fileStorage = FirebaseStorage.getInstance().getReference();

        btn_simpan.setOnClickListener(this);
        ivProfile.setOnClickListener(this);

        //jika current user tidak kosong maka akan set data ke dalam input text
        if(currentUser!=null) {
            etNama.setText(currentUser.getDisplayName());
            serverFileUri= currentUser.getPhotoUrl();
            readUserDatabase();
            if(serverFileUri!=null){
                Glide.with(this)
                        .load(serverFileUri)
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(ivProfile);
            }
        }else{
            etNama.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_simpan :
                if(localFileUri!=null){
                    updateUserProfilePhoto();
                }else{
                    updateUserProfile();
                }
                break;
            case R.id.btn_kuesioner:
                Intent Konselor = new Intent(EditProfileActivity.this,KuesionerActivity.class);
                startActivity(Konselor);
                break;
            case R.id.iv_profile_profile:
                changeImage();
                break;
            case R.id.delete:
                removePhoto();
                break;
            case R.id.choose:
                chooseGallery();
                break;
            default:
                break;
        }
    }

    private void changeImage(){
        LinearLayout remove = bottomSheetDialog.findViewById(R.id.delete);
        LinearLayout choose = bottomSheetDialog.findViewById(R.id.choose);

        remove.setOnClickListener(this);
        choose.setOnClickListener(this);

        bottomSheetDialog.show();
    }

    private void chooseGallery(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //pindah ke galley
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},102);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                localFileUri = data.getData();
                ivProfile.setImageURI(localFileUri);
                bottomSheetDialog.dismiss();
            }
        }
    }

    private void setDropDownMenu(){
        //set dropdown menu using array and adapter
        List<String> gender = new ArrayList<String>();
        gender.add("Perempuan");
        gender.add("Laki-Laki");
        jenisAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_menu,gender);
        etJenisKel.setAdapter(jenisAdapter);
    }

    private void removePhoto(){
        progress.show();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etNama.getText().toString().trim())
                .setPhotoUri(null)
                .build();

        currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    String userId = currentUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                    databaseReference.child(userId).child(NodeNames.PHOTO).setValue(" ").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            progress.dismiss();
                            String filelocal = userId +".jpg";
                            //child dengan file images/ didalamnya ada file local name
                            final StorageReference fileRef = fileStorage.child("images/"+ filelocal);
                            fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    ivProfile.setImageResource(R.drawable.ic_user);
                                    bottomSheetDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void readUserDatabase(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
        databaseReference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    etJenisKel.setText(snapshot.child(NodeNames.JENIS_KELAMIN).getValue().toString());
                    etUsia.setText(snapshot.child(NodeNames.USIA).getValue().toString());
                    etAlamat.setText(snapshot.child(NodeNames.ALAMAT).getValue().toString());
                    etNomor.setText(snapshot.child(NodeNames.PHONE_NUMBER).getValue().toString());

                    Preference.setKeyUserAge(getApplication(),Integer.parseInt(snapshot.child(NodeNames.USIA).getValue().toString()));
                }else{
                    Toast.makeText(EditProfileActivity.this,"Data tidak ada",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this,getString(R.string.failed_to_read_data , error.getMessage()),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfilePhoto(){
        progress.show();
        //diberi file dengan nama user id .jpg
        String filelocal = firebaseAuth.getUid() +".jpg";
        //child dengan file images/ didalamnya ada file local name
        final StorageReference fileRef = fileStorage.child("images/"+ filelocal);

        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            serverFileUri = uri;
                            String link = serverFileUri.toString();
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etNama.getText().toString().trim())
                                    .setPhotoUri(serverFileUri)
                                    .build();

                            currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        id = currentUser.getUid();
                                        email = currentUser.getEmail();
                                        nama = etNama.getText().toString();
                                        usia = etUsia.getText().toString();
                                        kelamin = etJenisKel.getText().toString();
                                        photo = link;
                                        alamat = etAlamat.getText().toString();
                                        ponsel = etNomor.getText().toString();
                                        status = "Online";
                                        role = "Pasien";

                                        pasien = new PasienModel(id,nama,email,photo,ponsel,status,role,usia,kelamin,alamat);

                                        databaseReference.child(id).setValue(pasien).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                progress.dismiss();
                                                Toast.makeText(EditProfileActivity.this, R.string.update_successful, Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(EditProfileActivity.this,
                                                getString(R.string.failed_to_update, task.getException()), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void updateUserProfile(){
        progress.show();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etNama.getText().toString().trim())
                .build();

        currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    id = currentUser.getUid();
                    email = currentUser.getEmail();
                    nama = etNama.getText().toString();
                    usia = etUsia.getText().toString();
                    kelamin = etJenisKel.getText().toString();
                    alamat = etAlamat.getText().toString();
                    ponsel = etNomor.getText().toString();
                    status = "Online";
                    role = "Pasien";

                    if(currentUser.getPhotoUrl()==null){
                        photo = "";
                    }else{
                        fileStorage.child(currentUser.getPhotoUrl().getLastPathSegment()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("uri",uri.toString());
                                photo = uri.toString();

                                pasien = new PasienModel(id,nama,email,photo,ponsel,status,role,usia,kelamin,alamat);

                                databaseReference.child(id).setValue(pasien).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progress.dismiss();
                                            Toast.makeText(EditProfileActivity.this, R.string.update_successful, Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else{
                                            Toast.makeText(EditProfileActivity.this,
                                                    getString(R.string.failed_to_update, task.getException()), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }

                    pasien = new PasienModel(id,nama,email,photo,ponsel,status,role,usia,kelamin,alamat);
                    databaseReference.child(id).setValue(pasien).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progress.dismiss();
                                Toast.makeText(EditProfileActivity.this, R.string.update_successful, Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(EditProfileActivity.this,
                                        getString(R.string.failed_to_update, task.getException()), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(EditProfileActivity.this,
                            getString(R.string.failed_to_update, task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //set action bar
    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Edit Profile");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setElevation(0);
            actionBar.setDisplayOptions(actionBar.getDisplayOptions());
        }
    }

    // this event will enable the back , function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}