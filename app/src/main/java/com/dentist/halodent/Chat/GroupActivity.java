package com.dentist.halodent.Chat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Model.Constant;
import com.dentist.halodent.Model.NodeNames;
import com.dentist.halodent.Model.Util;
import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivSend,ivAttachment, ivProfile;
    private TextView tvUserName;
    private TextInputEditText etMessage;
    private LinearLayout llProgress,llSendChat;

    private BottomSheetDialog bottomSheetDialog;

    private TextView tokenlist;
    private View llSnackbar;

    private RecyclerView rv_message;
    private SwipeRefreshLayout srlMessage;
    private List<Messages> messageList;
    private MessageAdapter messageAdapter;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserId,groupId,groupName,groupPhoto,downloadUrl;

    List <String> tokens = new ArrayList<>();
    String msg="";
    String image="";

    private DatabaseReference databaseReferenceGroups,databaseReferenceToken;
    //listen new message
    private ChildEventListener childEventListener;

    //default page = 1
    private int currentPage = 1;
    //show 30 record in 1 page
    private static final int RECORD_PER_PAGE = 30;
    //permission constant
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    //permission to request
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 2000;
    //permision to be requested
    private String[] cameraPermission;
    private String[] storagePermission;
    //uri of picked image
    private Uri image_uri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        setActionBar();

        //init required permission
        cameraPermission = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        storagePermission = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_chat_photo);

        //inisialisasi semua view
        llProgress = findViewById(R.id.llProgress);
        llSendChat = findViewById(R.id.llSendChat);
        llSnackbar = findViewById(R.id.llSnackbar);
        ivSend = findViewById(R.id.ivSend);
        ivAttachment = findViewById(R.id.ivAttachment);
        ivProfile = findViewById(R.id.iv_profile_action);
        tvUserName = findViewById(R.id.tv_userName_action);
        //tvUserStatus = findViewById(R.id.tv_userStatus_action);
        etMessage = findViewById(R.id.etMessage);
        srlMessage = findViewById(R.id.srlMessages);

        tokenlist = findViewById(R.id.tokenlist);

        //inisialisasi firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        currentUserId = mAuth.getCurrentUser().getUid();

        //inisialisasi
        rv_message = findViewById(R.id.rvMessages);
        rv_message.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(GroupActivity.this,messageList);
        rv_message.setAdapter(messageAdapter);

        //click button
        ivSend.setOnClickListener(this);
        ivAttachment.setOnClickListener(this);

        //set database
        databaseReferenceGroups = FirebaseDatabase.getInstance().getReference().child(NodeNames.GROUPS);
        databaseReferenceToken = FirebaseDatabase.getInstance().getReference().child(NodeNames.TOKENS);

        //get data from preference
        groupId = Preference.getKeyGroupId(getApplicationContext());
        groupName = Preference.getKeyGroupName(getApplicationContext());
        groupPhoto = Preference.getKeyGroupPhoto(getApplicationContext());

        tvUserName.setText(groupName);
        if(groupPhoto!=null){
            Glide.with(this)
                    .load(groupPhoto)
                    .placeholder(R.drawable.ic_group)
                    .error(R.drawable.ic_group)
                    .into(ivProfile);
        }else{
            ivProfile.setImageResource(R.drawable.ic_group);
        }

        loadGroupMessages();
        rv_message.scrollToPosition(messageList.size() - 1);
        srlMessage.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                loadGroupMessages();
                session();
            }
        });

        session();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivSend:
                String message = etMessage.getText().toString().trim();
                //validate if it's empty
                if(TextUtils.isEmpty(message)){
                    //empty? dont sent
                    Toast.makeText(GroupActivity.this,"Can't send empty message...",Toast.LENGTH_SHORT).show();
                }else{
                    //send message
                    sendMessage(groupName,message,Constant.MESSAGE_TYPE_TEXT);
                }
                break;
            case R.id.ivAttachment:
                //pick image from gallery or gallery
                //showImageImportDialog();
                showImage();
                break;
            case R.id.camera:
                if(!checkCameraPermission()){
                    //not granted -- request
                    requestCameraPermission();
                }else{
                    pickCamera();
                }
                break;
            case R.id.gallery:
                if(!checkStoragePermission()){
                    //not granted -- request
                    requestStoragePermission();
                }else{
                    pickGallery();
                }
                break;
        }
    }

    private void session(){
        databaseReferenceGroups.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String status = snapshot.child(NodeNames.STATUS).getValue().toString();

                    if(status.equals("selesai")){
                        llSnackbar.setVisibility(View.VISIBLE);
                        llSendChat.setVisibility(View.GONE);
                    }else{
                        llSnackbar.setVisibility(View.GONE);
                        llSendChat.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    private void showImage(){
        MaterialButton camera = bottomSheetDialog.findViewById(R.id.camera);
        MaterialButton gallery = bottomSheetDialog.findViewById(R.id.gallery);

        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);

        bottomSheetDialog.show();
    }

    private void pickGallery(){
        //intent pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "GroupImageTitle");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "GroupImageDesc");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                //got image from gallery
                image_uri = data.getData();
                try{
                    sendImageMessage(image_uri);
                }catch (IOException e){
                    Toast.makeText(GroupActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //picked from camera
                try{
                    sendImageMessage(image_uri);
                }catch (IOException e){
                    Toast.makeText(GroupActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(GroupActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(GroupActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        else if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(GroupActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GroupActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendImageMessage(Uri image_uri) throws IOException {
        //progress dialog
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Silahkan tunggu..");
        pd.setMessage("Mengirim Gambar...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        //set storage file name
        String fileName = "message_images/"+""+System.currentTimeMillis();

        //set image bitmap
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,60,outputStream);
        byte[] data = outputStream.toByteArray(); // convert image to array

        //set file in storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileName);

        //upload file
        storageReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                while(!task.isSuccessful());
                String url = task.getResult().toString();

                if(task.isSuccessful()){
                    //image uri receiver , save in db
                    sendMessage(groupName,url,Constant.MESSAGE_TYPE_IMAGE);
                    finish();
                    bottomSheetDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(GroupActivity.this,"Gagal mengirim",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String title,String message,String msgType){
        //timestamp
        String timestamp = ""+System.currentTimeMillis();

        Messages messages = new Messages(message,currentUser.getUid(),timestamp,msgType);

        //add to db
        databaseReferenceGroups.child(groupId).child("Messages").child(timestamp)
                .setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //message sent
                databaseReferenceGroups.child(groupId).child(NodeNames.TIME_STAMP).setValue(timestamp);
                Toast.makeText(GroupActivity.this,"Pesan berhasil terkirim",Toast.LENGTH_SHORT).show();
                etMessage.setText("");

                if(msgType.equals(Constant.MESSAGE_TYPE_TEXT)){
                    msg = currentUser.getDisplayName()+" : "+message;
                    image = " ";
                }else if(msgType.equals(Constant.MESSAGE_TYPE_IMAGE)){
                    msg = currentUser.getDisplayName()+" : "+"New Image";
                    image = message;
                }

                databaseReferenceGroups.child(groupId).child(NodeNames.PARTICIPANTS).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String id = ds.child(NodeNames.ID).getValue().toString();
                            if(!id.equals(currentUser.getUid())){
                                tokens.add(id);
                            }
                        }

                        //Toast.makeText(GroupActivity.this,"Token berhasil teridentifilasi",Toast.LENGTH_SHORT).show();
                        Util.sendNotification(GroupActivity.this,tokens,title,msg,image,currentUser.getUid());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //Toast.makeText(GroupActivity.this,"Token gagal teridentifilasi",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                //message sent failed
                Toast.makeText(GroupActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGroupMessages(){
        messageList.clear();
        Query messageQuery = databaseReferenceGroups.child(groupId).child(Messages.class.getSimpleName()).limitToLast(currentPage * RECORD_PER_PAGE);

        if (childEventListener != null)
            messageQuery.removeEventListener(childEventListener);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable  String previousChildName) {
                Messages message = snapshot.getValue(Messages.class);
                if (!messageList.contains(message)) {
                    messageList.add(message);
                    messageAdapter.notifyDataSetChanged();
                    rv_message.scrollToPosition(messageList.size() - 1);
                    srlMessage.setRefreshing(false);
                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                loadGroupMessages();
            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                srlMessage.setRefreshing(false);
            }
        };
        messageQuery.addChildEventListener(childEventListener);
    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_action_bar, null);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setElevation(0);
            actionBar.setCustomView(actionBarLayout);
            actionBar.setDisplayOptions(actionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_action_bar, menu);
        MenuItem search = menu.findItem(R.id.menu_search);
        search.setVisible(false);
        return true;
    }

    // this event will enable the back , function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.menu_info:
                Intent intent = new Intent(GroupActivity.this, GroupInfoActivity.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}