package com.example.pasta.ahbapapp.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.pasta.ahbapapp.MainActivity;
import com.example.pasta.ahbapapp.R;
import com.example.pasta.ahbapapp.model.NotificationMessage;
import com.example.pasta.ahbapapp.model.NotificationModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(RemoteMessage message) {

        String image = message.getNotification().getIcon();
        String title = message.getNotification().getTitle();
        String text = message.getNotification().getBody();
        String sound = message.getNotification().getSound();

        int id = 0;
        Object obj = message.getData().get("id");
        if (obj != null) {
            id = Integer.valueOf(obj.toString());
        }

        this.sendNotification(new NotificationMessage(image, id, title, text, sound));
    }

   private void sendNotification(NotificationMessage notificationData) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(NotificationMessage.TEXT, notificationData.getText());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = null;
        try {

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.ic_launcher))
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle(URLDecoder.decode(notificationData.getTitle(), "UTF-8"))
                    .setContentText(URLDecoder.decode(notificationData.getText(), "UTF-8"))
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (notificationBuilder != null) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationData.getId(), notificationBuilder.build());
        } else {
            Log.d(TAG, "Não foi possível criar objeto notificationBuilder");
        }
    }
}
