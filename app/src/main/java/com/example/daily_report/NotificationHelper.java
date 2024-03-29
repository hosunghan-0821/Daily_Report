package com.example.daily_report;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {

   public static final String notiGroup = "notification_group";
   public static final String summaryChannelId="summaryChannelID";
    private NotificationManager mManager;

    public static final String channelId = "channelID";
    public static final String channelName = "channel";

    public NotificationHelper(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannels();
        }

    }

    public void createChannels(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(R.color.main_color1);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager(){

        if(mManager==null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(String title, String message){

        Intent intent = new Intent(this,DiaryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification =new NotificationCompat.Builder(getApplicationContext(),channelId);

        notification.setContentTitle(title);
        notification.setContentText(message);
        notification.setSmallIcon(R.drawable.on_alarm);
        notification.setContentIntent(pendingIntent);
        notification.setAutoCancel(true);
        notification.setGroup(notiGroup);
        Log.e("123","알림 시작 3");

        return notification;
    }
    public NotificationCompat.Builder getChannelSummaryNotification(String title, String message){

        NotificationCompat.Builder summaryNotification = new NotificationCompat.Builder(getApplicationContext(),channelId);

        //summaryNotification.setContentTitle(title);
        summaryNotification.setAutoCancel(true);
        summaryNotification.setSmallIcon(R.drawable.on_alarm);
        summaryNotification.setOnlyAlertOnce(true);
        summaryNotification.setGroup(notiGroup);
        summaryNotification.setGroupSummary(true);



        return summaryNotification;
    }


}

