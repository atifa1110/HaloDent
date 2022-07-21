package com.dentist.halodent.Chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Home.Dokters;
import com.dentist.halodent.Home.Konselors;
import com.dentist.halodent.Model.NodeNames;
import com.dentist.halodent.Model.Util;
import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.Profile.Pasiens;
import com.dentist.halodent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private Context context;
    private List<Groups> groupList;

    public GroupAdapter(Context context, List<Groups> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @NotNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_list,parent,false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull GroupAdapter.GroupViewHolder holder, int position) {
        Groups groups = groupList.get(position);

        if(groups.getStatus().equals("selesai")){
            holder.divider.setVisibility(View.VISIBLE);
            holder.groupSelesai.setVisibility(View.VISIBLE);
        }else{
            holder.divider.setVisibility(View.GONE);
            holder.groupSelesai.setVisibility(View.GONE);
        }

        final String groupId = groups.getGroupId();
        holder.groupName.setText(groups.getGroupTitle());
        loadLastMessage(groups,holder);
        try{
            Glide.with(context)
                    .load(R.drawable.ic_group).
                    fitCenter()
                    .error(R.drawable.ic_group)
                    .into(holder.groupPhoto);
        }catch (Exception e){
            holder.groupPhoto.setImageResource(R.drawable.ic_group);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupActivity.class);
                Preference.setKeyGroupId(context,groupId);
                Preference.setKeyGroupName(context, groups.getGroupTitle());
                Preference.setKeyGroupPhoto(context, groups.getGroupIcon());
                context.startActivity(intent);
            }
        });
    }

    private void loadLastMessage(Groups groups, GroupViewHolder holder){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //get last message from group
        DatabaseReference message = FirebaseDatabase.getInstance().getReference().child("Groups");
        message.child(groups.getGroupId()).child("Messages").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds :snapshot.getChildren()){
                    //Messages message = snapshot.getValue(Messages.class);
                    String message = ds.child("message").getValue().toString();
                    String messageFrom = ds.child("messageFrom").getValue().toString();
                    String messageTime = ds.child("messageTime").getValue().toString();
                    String messageType = ds.child("messageType").getValue().toString();

                    //set length message max 30 character
                    message = message.length()>30?message.substring(0,30):message;

                    //check message if empty and message type whether text or image
                    if(message.isEmpty()){
                        holder.groupLastMessage.setText("");
                    }else if(messageType.equals("text")){
                        holder.groupLastMessage.setText(message);
                    }else if(messageType.equals("image")){
                        holder.groupLastMessage.setText("Photo");
                    }

                    holder.groupLastMessageTime.setText(Util.getTimeAgo(Long.parseLong(messageTime)));

                    if(messageFrom.equals(currentUser.getUid())){
                        holder.groupSender.setText("You : ");
                    }else{
                        setSenderName(messageFrom,holder);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(context,"Data tidak ada",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSenderName(String messageFrom,GroupViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(NodeNames.KONSELORS);
        ref.child(messageFrom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Konselors konselors = snapshot.getValue(Konselors.class);
                    holder.groupSender.setText(konselors.getNama()+": ");
                }else{
                    setDokterName(messageFrom,holder);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setDokterName(String messageFrom,GroupViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(NodeNames.DOKTERS);
        ref.child(messageFrom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Dokters dokters = snapshot.getValue(Dokters.class);
                holder.groupSender.setText(dokters.getNama()+": ");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder{

        private ImageView groupPhoto;
        private TextView groupName, groupSender, groupLastMessage, groupLastMessageTime,groupSelesai;
        private View divider;

        public GroupViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            divider = itemView.findViewById(R.id.divider);
            groupSelesai = itemView.findViewById(R.id.tv_selesai);
            groupPhoto = itemView.findViewById(R.id.iv_group_chat);
            groupName = itemView.findViewById(R.id.tv_group_chat);
            groupSender = itemView.findViewById(R.id.tv_sender_message_chat);
            groupLastMessage = itemView.findViewById(R.id.tv_last_message);
            groupLastMessageTime = itemView.findViewById(R.id.tv_last_message_time);
        }
    }
}
