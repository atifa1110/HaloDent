package com.dentist.halodent.Group;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Model.Dokters;
import com.dentist.halodent.Model.Groups;
import com.dentist.halodent.Model.Konselors;
import com.dentist.halodent.Model.Messages;
import com.dentist.halodent.Utils.MemoryData;
import com.dentist.halodent.Utils.NodeNames;
import com.dentist.halodent.Utils.Util;
import com.dentist.halodent.Utils.Preference;
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

        try{
            if(groups.getStatus().equals("selesai")){
                holder.divider.setVisibility(View.VISIBLE);
                holder.groupSelesai.setVisibility(View.VISIBLE);
            }else{
                holder.divider.setVisibility(View.GONE);
                holder.groupSelesai.setVisibility(View.GONE);
            }

            holder.groupName.setText(groups.getGroupTitle());
            loadLastMessage(groups,holder);

            Glide.with(context).load(R.drawable.ic_group).fitCenter()
                    .error(R.drawable.ic_group)
                    .into(holder.groupPhoto);

        }catch (Exception e){
            holder.groupPhoto.setImageResource(R.drawable.ic_group);
            holder.groupName.setText(" ");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupActivity.class);
                Preference.setKeyGroupId(context,groups.getGroupId());
                Preference.setKeyGroupName(context, groups.getGroupTitle());
                Preference.setKeyGroupPhoto(context, groups.getGroupIcon());
                context.startActivity(intent);
            }
        });

    }

    private void loadLastMessage(Groups groups, GroupViewHolder holder){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //get last message from group
        DatabaseReference message = FirebaseDatabase.getInstance().getReference().child(NodeNames.GROUPS);
        message.child(groups.getGroupId()).child(NodeNames.MESSAGES).limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds :snapshot.getChildren()){
                    Messages messages = ds.getValue(Messages.class);
                    try {
                        //set length message max 30 character
                        String message = "";
                        message = messages.getMessage().length() > 30 ? messages.getMessage().substring(0, 30) : messages.getMessage();

                        //check message if empty and message type whether text or image
                        if (message.isEmpty()) {
                            holder.groupLastMessage.setText("");
                        } else if (messages.getMessageType().equals("text")) {
                            holder.groupLastMessage.setText(message);
                        } else if (messages.getMessageType().equals("image")) {
                            holder.groupLastMessage.setText("Foto");
                        }

                        holder.groupLastMessageTime.setText(Util.getTimeAgo(Long.parseLong(messages.getMessageTime())));

                        if (messages.getMessageFrom().equals(currentUser.getUid())) {
                            holder.groupSender.setText("You : ");
                        } else {
                            setSenderName(messages.getMessageFrom(), holder);
                        }

//                    long getLastSeenMessage = Long.parseLong(MemoryData.getLastMessage(context,groups.getGroupId()));
//                    long getMessageKey = Long.parseLong(messages.getMessageTime());
//
//                    Log.d("LastSeen", String.valueOf(getLastSeenMessage));
//                    Log.d("LastSeen",String.valueOf(getMessageKey));
//
//                    if(getMessageKey > getLastSeenMessage){
//                        unread++;
//                    }
//
//                    if(unread == 0){
//                        holder.unreadCount.setVisibility(View.GONE);
//                    }else{
//                        holder.unreadCount.setVisibility(View.VISIBLE);
//                        holder.unreadCount.setText(String.valueOf(unread));
//                    }

                    }catch (Exception e){
                        e.printStackTrace();
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
        private TextView groupName, groupSender, groupLastMessage, groupLastMessageTime,groupSelesai,unreadCount;
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
            unreadCount = itemView.findViewById(R.id.tv_unread_count_chat);
        }
    }
}
