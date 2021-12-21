package com.dentist.halodent.Activity;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;

import org.jetbrains.annotations.NotNull;

public class DetailDokterFragment extends DialogFragment {

    public static final String TAG = "detail_dokter_dialog";

    private ImageView iv_image;
    private TextView tv_nama,tv_email,tv_nip,tv_str,tv_sip;
    private Toolbar toolbar;

    public static DetailDokterFragment display(FragmentManager fragmentManager) {
        DetailDokterFragment fragment = new DetailDokterFragment();
        fragment.show(fragmentManager, TAG);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_dokter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        iv_image = view.findViewById(R.id.iv_photo_detail_dokter);
        tv_nama = view.findViewById(R.id.tv_nama_detail_dokter);
        tv_email = view.findViewById(R.id.tv_email_detail_dokter);
        tv_nip = view.findViewById(R.id.tv_nip);
        tv_str = view.findViewById(R.id.tv_str);
        tv_sip =view.findViewById(R.id.tv_sip);

        //iv_image.setImageResource(R.string.);
        Glide.with(getContext())
                .load(Preference.getKeyDokterPhoto(getContext()))
                .placeholder(R.drawable.ic_user)
                .centerCrop()
                .into(iv_image);

        tv_nama.setText(Preference.getKeyDokterNama(getContext()));
        tv_email.setText(Preference.getKeyDokterEmail(getContext()));
        tv_nip.setText(Preference.getKeyDokterNip(getContext()));
        tv_str.setText(Preference.getKeyDokterStr(getContext()));
        tv_sip.setText(Preference.getKeyDokterSip(getContext()));

        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }
}