package com.dentist.halodent.Chat;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Model.NodeNames;
import com.dentist.halodent.Model.UserModel;
import com.dentist.halodent.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {

    private Context context;
    private List<UserModel> participantList;

    public ParticipantAdapter(Context context, List<UserModel> participantList) {
        this.context = context;
        this.participantList = participantList;
    }

    @NonNull
    @NotNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_participant_list,parent,false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ParticipantViewHolder holder, int position) {
        UserModel user = participantList.get(position);

        if (user.getRole().equals("Pasien")){
            setUserName(user,holder);
        }else if(user.getRole().equals("Konselor")){
            setKonselorName(user,holder);
        }else if(user.getRole().equals("Dokter Pengawas")){
            setDokterName(user,holder);
        }

        holder.roleParticipant.setText(user.getRole());
    }

    private void setUserName(UserModel user, ParticipantViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
        ref.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String namaPasien = snapshot.child(NodeNames.NAME).getValue().toString();
                String userOnline = snapshot.child(NodeNames.ONLINE).getValue().toString();
                final String photoPasien = snapshot.child(NodeNames.PHOTO).getValue().toString();
                Log.d(photoPasien,"photo");
                holder.userName.setText(namaPasien);
                holder.userOnline.setText(userOnline);
                holder.userOnline.setTextColor(setColor(userOnline));
                Glide.with(context).load(photoPasien).placeholder(R.drawable.ic_user)
                        .into(holder.photoUser);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setKonselorName(UserModel user, ParticipantViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(NodeNames.KONSELORS);
        ref.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String namaKonselor = snapshot.child(NodeNames.NAME).getValue().toString();
                String userOnline = snapshot.child(NodeNames.ONLINE).getValue().toString();
                String photoKonselor = snapshot.child(NodeNames.PHOTO).getValue().toString();
                holder.userName.setText(namaKonselor);
                holder.userOnline.setText(userOnline);
                holder.userOnline.setTextColor(setColor(userOnline));
                Glide.with(context).load(photoKonselor).placeholder(R.drawable.ic_user)
                        .into(holder.photoUser);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setDokterName(UserModel userModel, ParticipantViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(NodeNames.DOKTERS);
        ref.child(userModel.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String namaDokter = snapshot.child(NodeNames.NAME).getValue().toString();
                String userOnline = snapshot.child(NodeNames.ONLINE).getValue().toString();
                String photoDokter = snapshot.child(NodeNames.PHOTO).getValue().toString();
                holder.userName.setText(namaDokter);
                holder.userOnline.setText(userOnline);
                holder.userOnline.setTextColor(setColor(userOnline));
                Glide.with(context).load(photoDokter).placeholder(R.drawable.ic_user)
                        .into(holder.photoUser);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public Integer setColor(String online){
        if(online.equals("Online")){
            return Color.parseColor("#00CCBB");
        }else{
            return Color.parseColor("#7C7C7C");
        }
    }
    @Override
    public int getItemCount() {
        return participantList.size();
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView photoUser;
        private TextView userName,userOnline,roleParticipant;

        public ParticipantViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            photoUser = itemView.findViewById(R.id.iv_profile_participant);
            userName = itemView.findViewById(R.id.tv_nama_participant);
            userOnline = itemView.findViewById(R.id.tv_online);
            roleParticipant = itemView.findViewById(R.id.tv_role_participant);
        }
    }
}
