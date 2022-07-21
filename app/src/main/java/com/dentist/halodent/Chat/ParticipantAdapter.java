package com.dentist.halodent.Chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Home.Dokters;
import com.dentist.halodent.Home.Konselors;
import com.dentist.halodent.Profile.Pasiens;
import com.dentist.halodent.Model.Users;
import com.dentist.halodent.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {

    private Context context;
    private List<Users> participantList;

    public ParticipantAdapter(Context context, List<Users> participantList) {
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
        Users users = participantList.get(position);

        if (users.getRole().equals("Pasien")){
            setUserName(users,holder);
        }else if(users.getRole().equals("Konselor")){
            setKonselorName(users,holder);
        }else if(users.getRole().equals("Dokter Pengawas")){
            setDokterName(users,holder);
        }

        holder.roleParticipant.setText(users.getRole());
    }

    private void setUserName(Users users, ParticipantViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Pasiens.class.getSimpleName());
        ref.child(users.getId()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Pasiens pasiens = snapshot.getValue(Pasiens.class);
                holder.userName.setText(pasiens.getNama());
                holder.userOnline.setText(pasiens.getStatus());
                holder.userOnline.setTextColor(setColor(pasiens.getStatus()));
                holder.imageView.setImageDrawable(setDrawable(pasiens.getStatus()));
                Glide.with(context).load(pasiens.getPhoto()).error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                        .into(holder.photoUser);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setKonselorName(Users users, ParticipantViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Konselors.class.getSimpleName());
        ref.child(users.getId()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Konselors konselors = snapshot.getValue(Konselors.class);
                holder.userName.setText(konselors.getNama());
                holder.userOnline.setText(konselors.getStatus());
                holder.userOnline.setTextColor(setColor(konselors.getStatus()));
                holder.imageView.setImageDrawable(setDrawable(konselors.getStatus()));
                if(konselors.getPhoto()!=null){
                    Glide.with(context).load(konselors.getPhoto()).error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                            .into(holder.photoUser);
                }else{
                    holder.imageView.setImageResource(R.drawable.ic_group);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setDokterName(Users users, ParticipantViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Dokters.class.getSimpleName());
        ref.child(users.getId()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Dokters dokters = snapshot.getValue(Dokters.class);
                holder.userName.setText(dokters.getNama());
                holder.userOnline.setText(dokters.getStatus());
                holder.userOnline.setTextColor(setColor(dokters.getStatus()));
                holder.imageView.setImageDrawable(setDrawable(dokters.getStatus()));
                if(dokters.getPhoto()!=null){
                    Glide.with(context).load(dokters.getPhoto()).error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                            .into(holder.photoUser);
                }else{
                    holder.imageView.setImageResource(R.drawable.ic_group);
                }
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Drawable setDrawable(String online){
        if (online.equals("Online")){
            return context.getDrawable(R.drawable.ic_circle_green);
        }else{
            return context.getDrawable(R.drawable.ic_circle_gray);
        }
    }

    @Override
    public int getItemCount() {
        return participantList.size();
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView photoUser,imageView;
        private TextView userName,userOnline,roleParticipant;

        public ParticipantViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            photoUser = itemView.findViewById(R.id.iv_profile_user);
            imageView = itemView.findViewById(R.id.iv_circle);
            userName = itemView.findViewById(R.id.tv_nama_user);
            userOnline = itemView.findViewById(R.id.tv_online);
            roleParticipant = itemView.findViewById(R.id.tv_role);
        }
    }
}
