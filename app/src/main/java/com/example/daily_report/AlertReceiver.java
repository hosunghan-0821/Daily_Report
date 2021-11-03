package com.example.daily_report;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class AlertReceiver extends BroadcastReceiver {


    public final static  int NOTIFY_ID=1234;

    @Override
    public void onReceive(Context context, Intent intent) {



        String str = intent.getStringExtra("content");
        int serialNumber = intent.getIntExtra("serialNumber", -1);


        // 시스템으로부터 날짜 받아오는 코드
        String getDay = null;
        long now = System.currentTimeMillis();                   //현재 시스템으로 부터 현재 정보를 받아온다.
        Date date = new Date(now);                               //현재 시스템 시간 날짜 정보로부터, 날짜를 얻어온다.
        SimpleDateFormat dateFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd-E");
            getDay = dateFormat.format(date);
        }


        if (serialNumber != -1) {

            SharedPreferences sharedPreferences = context.getSharedPreferences(getDay, context.MODE_PRIVATE);
            SharedPreferences.Editor editor= sharedPreferences.edit();

            ArrayList<DiaryToDoData> savedList;
            savedList = new ArrayList<DiaryToDoData>();
            Gson gson = new GsonBuilder().create();

            String stringToObject = sharedPreferences.getString("toDoListRecyclerView", "");
            Type arraylistType = new TypeToken<ArrayList<DiaryToDoData>>() {
            }.getType();

            savedList = gson.fromJson(stringToObject, arraylistType);
            try {
                for(int i=0;i<savedList.size();i++){
                    if(savedList.get(i).getSerialNumber()==serialNumber){

                        savedList.get(i).setAlarm(false);
                        String objectToString = gson.toJson(savedList, arraylistType);
                        editor.putString("toDoListRecyclerView",objectToString);
                        editor.apply();

                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();

            }


        }
        Log.e("123", "알림 시작 2");
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder notificationSummary = notificationHelper.getChannelSummaryNotification("할 일 알림", "group");
        NotificationCompat.Builder notificationBuilder = notificationHelper.getChannelNotification("할 일 알림", "할일 : " + str);
        Log.e("123", "알림 시작 4");
        Log.e("123", "" + intent.getStringExtra("content"));
        Log.e("123",""+intent.getIntExtra("serialNumber",-1));
        notificationHelper.getManager().notify(serialNumber, notificationBuilder.build());
        notificationHelper.getManager().notify(NOTIFY_ID,notificationSummary.build());
    }


}
