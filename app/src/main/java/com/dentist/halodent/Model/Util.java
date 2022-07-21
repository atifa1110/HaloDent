package com.dentist.halodent.Model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dentist.halodent.Notification.Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Util {

    private static final String TAG = "informasipesan";

    public static void updateDeviceToken(Context context, String token) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference databaseReference = rootRef.child(NodeNames.TOKENS).child(currentUser.getUid());

            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put(NodeNames.DEVICE_TOKEN,token);

            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(context,"Gagal menyimpan Token Device",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static void sendNotification(Context context, List<String> to, String title, String message, String image,String userId){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference = rootRef.child(NodeNames.TOKENS);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.getChildren()!=null){
                    String deviceToken = snapshot.child(to.get(0)).child(NodeNames.DEVICE_TOKEN).getValue().toString();
                    String deviceToken1 = snapshot.child(to.get(1)).child(NodeNames.DEVICE_TOKEN).getValue().toString();

                    List<String> tokens = new ArrayList<>();
                    tokens.add(deviceToken);
                    tokens.add(deviceToken1);

                    Log.d("informasi token",tokens.toString());

                    String Url = "https://halo-dent.web.app/api/";

                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Url)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    Api api = retrofit.create(Api.class);
                    Call<ResponseBody> call = api.sendNotification(deviceToken,deviceToken1,title,message,image);
                    Log.d(TAG,title);
                    Log.d(TAG,message);
                    Log.d(TAG,image);

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            try{
//                                Toast.makeText(context.getApplicationContext(), response.body().string(),Toast.LENGTH_SHORT).show();
//                            }catch (IOException e){
//                                e.printStackTrace();
//                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(context.getApplicationContext(), t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(context,"token tidak ada",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public static String getTimeAgo(long time) {
        //set date format
        SimpleDateFormat sfd = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
        String dateTime = sfd.format(time);

        //split date format
        String [] splitString = dateTime.split(" ");
        String day = splitString[0];
        String date = splitString[1]+" "+splitString[2]+" "+splitString[3];
        String lastMessageTime = splitString[4];

        long now = System.currentTimeMillis();

        // Get msec from each, and subtract.
        final long diff = now - time;

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours   = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        String text = null;
        if (minutes < 1) {
            return text = "Sekarang";
        } else if (hours < 24) {
            return text = lastMessageTime;
        } else if (hours < 48) {
            return text = "Kemarin";
        }else if(days < 7) {
            return text = day;
        }else{
            return text = date;
        }
    }

    public static String getTime(String time){
        SimpleDateFormat sfd = new SimpleDateFormat("d MMM yyy HH:mm");
        String dateTime = sfd.format(new Date(Long.parseLong(time)));
        String [] splitString = dateTime.split(" ");
        String gettime = splitString[0]+" "+splitString[1]+" "+splitString[2];
        return gettime;
    }
}
