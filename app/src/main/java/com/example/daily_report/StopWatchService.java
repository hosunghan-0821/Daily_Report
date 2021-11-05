package com.example.daily_report;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class StopWatchService extends Service {

    public static final int FOREGROUND_SERVICE_ID=10000;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String serviceToStopWatchActivity = "toStopWatchActivity";

    private IBinder mBinder = new LocalBinder();
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    int i = 0;
    boolean isTempStop, isRunning=false; // 일시정지상태인지, 진행중인 상태인지.

    StopWatchThread thread;

    public StopWatchService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //ForeGroundService를 하기위해 Notification 사용하기 위해 채널만들기

        createNotificationChannel();

        //Notifcation 에서 알림을 클릭했을시, activity로 이동하기 위해서 , PendingIntent 사용
        Intent notificationIntent = new Intent(StopWatchService.this,StopWatchTimerActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder =new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("스탑워치 사용")
                .setSmallIcon(R.drawable.timer_image)
                .setContentIntent(pendingIntent);



        // 이 부분 까찌 ForeGroundService 이용하기 위해 준비 작업한 부분.
        // ( NotifcationManger 를 정의하고, Manger를 이용해서 NotificationChannel 만들고, 만든 Channel을 통해 NotifcationCompat.Builder로 빌드)
        // 위 절차를 걸치고 난후 ,startForeground() 하면된다.


        if (intent.getStringExtra("firstStart") != null) {

            //정지상태에서 맨 처음 stopWatch를 시작시킬 때,

            if (intent.getStringExtra("firstStart").equals("true")) {

                isRunning=true;
                thread=new StopWatchThread();

                //Thread 와 ForegroundService start
                startForeground(FOREGROUND_SERVICE_ID,builder.build());
                thread.start();

            }


        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        Log.e("123","foregroundService 죽이는 타이밍 언젠지 확인");
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    public void createNotificationChannel(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            //NotificationChannel 만들기, (Channel_Id,name,중요도 Parameter 사용)
            NotificationChannel serviceChannel=new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            //SystemService 로부터 NotificationManger 정의하고 정의 한걸로 createNotificationChannel 함수 다시호출.
            //즉 채널을 다시만든다 .. -> 구조를 이렇게 짤필요가 있나..? 좀 코드 서순이 안맞는거 같다..
            notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager!= null){
                notificationManager.createNotificationChannel(serviceChannel);
            }

        }

    }

    public class StopWatchThread extends Thread implements Runnable {


        @Override
        public void run() {

            while (true) {

                try {

                    Thread.sleep(10);
                    i += 1;
                    //10ms 마다 전체 ms 를 LocalBroadcastManager를 통해서 Activity로 보낸다.

                    Intent intent = new Intent(serviceToStopWatchActivity);
                    intent.putExtra("number", i);
                    LocalBroadcastManager.getInstance(StopWatchService.this).sendBroadcast(intent);

                    //10ms 마다 전체 ms 를 ForegroundService에 보내서 notify를 업데이트한다.



                    int sec, minute;

                    sec = (i / 100) % 60;
                    minute = (i / 100) / 60;

                    String time = minute + " 분" + sec + " 초 ";
                    if (sec == 0 && minute == 0) {
                        time = "00 분 00 초" ;
                    } else if (sec == 0) {
                        time = minute + " 분 00 초 ";
                    } else if (minute == 0) {
                        time = "00 분 " + sec + " 초 ";
                    }

                    if(i%100==0){
                        builder.setContentText(time);
                        notificationManager.notify(FOREGROUND_SERVICE_ID,builder.build());
                    }


                } catch (Exception e) {
                    break;
                }


            }
        }
    }

    public class LocalBinder extends Binder {

        public StopWatchService getService(){
            return StopWatchService.this;
        }
    }

}