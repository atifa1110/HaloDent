package com.dentist.halodent.Chat;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dentist.halodent.Model.NodeNames;
import com.dentist.halodent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    private RecyclerView rvChat;
    private List<Groups> groupList;
    private GroupAdapter groupAdapter;
    private View emptyChat;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReferenceGroups,databaseReferenceKonselors;
    private FirebaseUser currentUser;

    private ChildEventListener childEventListener;
    private Query query;

    private List<String> userIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        //inisialisasi semua view
        rvChat = view.findViewById(R.id.rv_chats);
        emptyChat = view.findViewById(R.id.ll_empty_chat);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Silahkan Tunggu..");
        progressDialog.show();

        ////set array list
        userIds = new ArrayList<>();
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(getContext(),groupList);

        //set layout
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setReverseLayout(true);
        linearLayout.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayout);
        rvChat.setAdapter(groupAdapter);

        //inisialisasi database
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceKonselors = FirebaseDatabase.getInstance().getReference().child(NodeNames.KONSELORS);
        databaseReferenceGroups = FirebaseDatabase.getInstance().getReference().child(NodeNames.GROUPS);

        loadGroupChat();
        emptyChat.setVisibility(View.VISIBLE);
    }

    private void loadGroupChat(){
        //set query dengan diurutkan dengan waktu kirim
        query = databaseReferenceGroups.orderByChild(NodeNames.TIME_STAMP);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();
                progressDialog.dismiss();
                for (DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("Participants").child(currentUser.getUid()).exists()) {
                        emptyChat.setVisibility(View.GONE);
                        Groups groups = ds.getValue(Groups.class);
                        groupList.add(groups);
                    }
                    groupAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGroupChat(final String query) {
        emptyChat.setVisibility(View.GONE);
        databaseReferenceGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String groupId = ds.getKey();
                    if (ds.child("Participants").child(currentUser.getUid()).exists()) {
                        progressDialog.dismiss();
                        if(ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase())){
                            Groups groups =ds.getValue(Groups.class);
                            groupList.add(groups);
                        }
                    }
                    groupAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        MenuItem info = menu.findItem(R.id.menu_info);
        info.setVisible(false);
        MenuItem search = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //called when submit text
                if(!TextUtils.isEmpty(query.trim())){
                    searchGroupChat(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //called when typing text
                if(!TextUtils.isEmpty(newText.trim())){
                    searchGroupChat(newText);
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}