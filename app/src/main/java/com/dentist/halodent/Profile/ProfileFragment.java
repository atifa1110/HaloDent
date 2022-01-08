package com.dentist.halodent.Profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Model.NodeNames;
import com.dentist.halodent.R;
import com.dentist.halodent.Activity.SignInActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private TextView nama, email;
    private Button btn_kuesioner, btn_edit_profile, btn_keluar;
    private ImageView ivProfile;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private Uri serverFileUri;

    private DatabaseReference databaseReferenceUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nama = view.findViewById(R.id.tv_nama_profile_fragment);
        email = view.findViewById(R.id.tv_email_profile_fragment);
        ivProfile = view.findViewById(R.id.iv_profile_profile_fragment);
        btn_kuesioner = view.findViewById(R.id.btn_kuesioner);
        btn_edit_profile = view.findViewById(R.id.btn_edit_profile);
        btn_keluar = view.findViewById(R.id.btn_keluar);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS).child(currentUser.getUid());

        if (firebaseAuth != null) {
            nama.setText(currentUser.getDisplayName());
            email.setText(currentUser.getEmail());
            serverFileUri = currentUser.getPhotoUrl();
            if (serverFileUri != null) {
                Glide.with(this)
                        .load(serverFileUri)
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(ivProfile);
            } else {
                ivProfile.setImageResource(R.drawable.ic_user);
            }

            btn_edit_profile.setOnClickListener(this);
            btn_kuesioner.setOnClickListener(this);
            btn_keluar.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_kuesioner:
                Intent semuaKonselor = new Intent(getActivity(), KuesionerActivity.class);
                startActivity(semuaKonselor);
                break;
            case R.id.btn_edit_profile :
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_keluar:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Apakah Anda yakin ingin keluar?")
                        .setCancelable(false)
                        .setPositiveButton("iya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               logout();
                            }
                        }).setNegativeButton("tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
        }
    }

    public void logout(){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        databaseReferenceUser.child(NodeNames.ONLINE).setValue("Offline");
        startActivity(new Intent(getActivity(), SignInActivity.class));
        getActivity().finish();
    }
}