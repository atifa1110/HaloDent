package com.dentist.halodent.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dentist.halodent.Group.GroupActivity;
import com.dentist.halodent.Utils.Preference;
import com.dentist.halodent.Utils.Util;
import com.dentist.halodent.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ChatMessagesService extends FirebaseMessagingService {

    private static final String TAG = "ChatMessageService";
    private static final String CHANNEL_ID = "channel";
    Bitmap image_bitmap = null;

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        Util.updateDeviceToken(this,s);
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(!remoteMessage.getData().isEmpty()){
            Map<String,String> data = remoteMessage.getData();
            Log.d(TAG,data.get("title"));
            Log.d(TAG,data.get("body"));
            Log.d(TAG,data.get("image"));
            Log.d(TAG,data.get("chatid"));
            Log.d(TAG,data.get("to"));

            //check if the app is running in foreground or background
            //cause we want to get notification when the app is closed
            if (!Util.isAppInForeground(getApplicationContext())) {
                if(data.get("image").startsWith("https://firebasestorage")){
                    image_bitmap = getBitmapFromURL(data.get("image"));
                    showNotification(data.get("title"),data.get("body"),image_bitmap,data.get("chatid"));
                }else{
                    //create and display notification
                    showNotification(data.get("title"),data.get("body"),null,data.get("chatid"));
                }
            }
        }
    }

    private Bitmap getBitmapFromURL(String img_url) {
        try {
            URL url = new URL(img_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showNotification(String title, String message,Bitmap image,String groupId){
        //create notification channel for API 26+
        createNotificationChannel();

        Intent intentGroup = new Intent(getApplicationContext(), GroupActivity.class);
        Preference.setKeyGroupId(getApplicationContext(),groupId);
        intentGroup.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntentGroup = PendingIntent.getActivity(getApplicationContext(), 0, intentGroup, 0);

        Uri defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo_tooth)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntentGroup)
                .setSound(defaultNotificationSound)
                .setLights(Color.GREEN,500,200)
                .setColor(getResources().getColor(R.color.design_default_color_primary))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if(image!=null){
            builder.setLargeIcon(image);
        }

        //notification ID is unique for each notification you create
        notificationManager.notify(2,builder.build());
    }

    private void createNotificationChannel(){
        //create notification channel only on API level 26+
        //Notification channel is a new class and not a support library
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String name = "My Chat Channel ";
            String description = "My Chat Channel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(R.color.green);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            //Register the channel with the system
            //You cannot change importance or other notification behaviours after this
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
