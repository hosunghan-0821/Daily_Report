package com.example.daily_report;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        String str = intent.getStringExtra("content");

        Log.e("123","알림 시작 2");
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder notificationBuilder = notificationHelper.getChannelNotification("할 일 알림", "할일 : "+str);
        Log.e("123","알림 시작 4");
        Log.e("123",""+intent.getStringExtra("content"));
        notificationHelper.getManager().notify(1,notificationBuilder.build());
    }


}
