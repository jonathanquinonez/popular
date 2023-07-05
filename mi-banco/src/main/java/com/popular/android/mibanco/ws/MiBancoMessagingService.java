package com.popular.android.mibanco.ws;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.IntroScreen;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.activity.Accounts;
import com.popular.android.mibanco.util.PushUtils;

import java.util.Date;

public class MiBancoMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MiBancoMsgService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "FCM TOKEN: " + s);
        PushUtils.savePushToken(this, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "MESSAGE RECEIVED FROM" + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0)
            Log.d(TAG, "MESSAGE DATA PAYLOAD: " + remoteMessage.getData());

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            displayNotification(remoteMessage.getNotification().getBody());
        }

    }

    //Display received message as a Push Notification
    private void displayNotification(String message) {
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nManager != null) {
            nManager.notify(createID(), buildNotification(message).build());
        }
    }

    //Creates a unique ID based on milliseconds
    private int createID () {
        int id = (int)(new Date().getTime() % Integer.MAX_VALUE);
        Log.d(TAG,"Notification ID created: " + id);
        return id;
    }

    private NotificationCompat.Builder buildNotification (String message) {
        Class pushRedirect = (App.getApplicationInstance().getCurrentUser() != null) ? Accounts.class : IntroScreen.class;
        Intent i = new Intent(this, pushRedirect);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = createPendingIntentGetBroadCast(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
        return new NotificationCompat.Builder(this, PushUtils.FCM_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_transparent)
                .setColor(getResources().getColor(R.color.icon_notification_color))
                .setContentTitle(getApplicationInfo().loadLabel(getApplicationContext().getPackageManager()))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
    }

    private static PendingIntent createPendingIntentGetBroadCast(Context context, int id, Intent intent, int flag){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE | flag);
        } else {
            return PendingIntent.getBroadcast(context, id, intent, flag);
        }
    }
}
