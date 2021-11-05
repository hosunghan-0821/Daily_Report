package com.example.daily_report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StopWatchTimerActivity extends AppCompatActivity {

    private TextView stopWatch, startText, finishText, writeText, tempStop;

    private Handler handler;
    int i = 1;

    //BindService 관련 변수
    ServiceConnection mConnection;
    StopWatchService stopWatchService;
    boolean mBound;


    //StopWatch 조작 관련 변수
    boolean tempStopCheck = false;

    //RecyclerView 관련 변수
    private ArrayList<StopWatchRecord> stopWatchRecordList;
    private RecyclerView recyclerView;
    private StopWatchAdapter adapter;
    LinearLayoutManager linearLayoutManager;


    //StopWatchService 로부터 서비스 된 내용을 받을 때, 사용되는 Local Broadcast Receiver
    //service_thread 부터 10ms 마다 계속해서 갱신되는 (i 값,전체 ms값) 을 여기서 받고,
    //handler 를 사용해서 , 메인 ui에 찍어주는 역할을 하는 부분이다.

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            i = intent.getIntExtra("number", 0);

            // intent로 부터 받은 ms 를 분 초로 바꾸고 그것을 String 으로 바꿔주는 함수 정의해서 사용
            String time = msTimeToString(i);


            //String time 을 메시지 객체를 이용하여 핸들러에게 처리하라고 전달하는 코드
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("time", time);
            msg.setData(bundle);
            handler.sendMessage(msg);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_watch_timer);

        stopWatch = findViewById(R.id.stop_watch);
        startText = findViewById(R.id.start_text);
        finishText = findViewById(R.id.finish_text);
        writeText = findViewById(R.id.write_text);
        tempStop = findViewById(R.id.temp_finish_text);
        recyclerView = findViewById(R.id.recyclerview_stop_watch);

        stopWatchRecordList = new ArrayList<StopWatchRecord>();
        linearLayoutManager = new LinearLayoutManager(StopWatchTimerActivity.this);

        adapter = new StopWatchAdapter(stopWatchRecordList);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        stopWatch.setText("00 분 00초 00");


        //handler를 사용하여, 메인 Ui를 갱신해준다.
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                stopWatch.setText(msg.getData().getString("time"));
            }
        };

        //Bind Service _ Connection 됬을 때, 해제 됬을 때, 정의 , override 정의
        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                StopWatchService.LocalBinder binder = (StopWatchService.LocalBinder) service;
                stopWatchService = binder.getService();
                mBound = true;

                String time = msTimeToString(stopWatchService.i);

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("time", time);
                msg.setData(bundle);

                handler.sendMessage(msg);

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

                mBound = false;

            }
        };


        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!mBound) {
                    Intent intent = new Intent(StopWatchTimerActivity.this, StopWatchService.class);
                    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                    mBound = true;
                }

                Log.e("123", "stopwatch isRunning " + stopWatchService.isRunning);
                //맨 처음 시작 할 때,
                if (!stopWatchService.isRunning) {

                    Intent intent = new Intent(StopWatchTimerActivity.this, StopWatchService.class);
                    intent.putExtra("firstStart", "true");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }
                    if(tempStopCheck){
                        tempStopCheck=false;
                    }

                }


            }
        });
        finishText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.e("123", "종료시키기 안눌림?");

                //정지버튼 눌렀을 때, service 종료. service에서 돌아가고 있는 Thread도 종료될 것으로 예상.

                if (stopWatchService.isRunning) {

                    stopWatchService.thread.interrupt();
                    stopWatchService.isRunning = false;
                    stopWatchService.i = 0;
                    Toast.makeText(StopWatchTimerActivity.this, "타이머를 종료합니다.", Toast.LENGTH_SHORT).show();

                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("time", "00분 00초 00");
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                    //BoundService 해제
                    if (mBound) {
                        unbindService(mConnection);
                        mBound = false;
                    }

                    //BoundService 해제후, foreground Service 도 종료
                    Intent foregroundServiceIntent = new Intent(StopWatchTimerActivity.this, StopWatchService.class);
                    stopService(foregroundServiceIntent);

                }
                if(tempStopCheck){
                    Toast.makeText(StopWatchTimerActivity.this, "일시정지 상태입니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    writeText.setText("기록 초기화");
                }




            }
        });

        tempStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (stopWatchService.isRunning) {
                    tempStopCheck=true;
                    stopWatchService.thread.interrupt();
                    stopWatchService.isRunning = false;
                    stopWatchService.builder.setContentTitle("스탑워치 일시정지");
                    stopWatchService.notificationManager.notify(StopWatchService.FOREGROUND_SERVICE_ID,stopWatchService.builder.build());

                }

            }
        });
        writeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(stopWatchService.isRunning){
                    StopWatchRecord data = new StopWatchRecord(stopWatch.getText().toString());
                    stopWatchRecordList.add(data);
                    adapter.notifyDataSetChanged();
                }
                if(writeText.getText().toString().equals("기록 초기화")){
                    stopWatchRecordList.clear();
                    adapter.notifyDataSetChanged();
                    writeText.setText("기록");
                }



            }
        });

    }

    public String msTimeToString(int number) {


        int millisec, sec, minute;

        millisec = number % 100;
        sec = (number / 100) % 60;
        minute = (number / 100) / 60;


        String time = minute + " 분" + sec + " 초 " + millisec;
        if (sec == 0 && minute == 0) {
            time = "00 분 00 초" + millisec;
        } else if (sec == 0) {
            time = minute + " 분 00 초 " + millisec;
        } else if (minute == 0) {
            time = "00 분 " + sec + " 초 " + millisec;
        }

        return time;
    }

    /*

    class NewThread extends Thread {

        @Override
        public void run() {

            if(tempStopCheck){
                tempStopCheck=false;
            }
            else{
                i=0;
            }
            while (true) {

                try {
                    Thread.sleep(10);
                    i+=1;
                } catch (Exception e) {

                    break;
                }

                // create Runnable instance.

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        int millisec,sec,minute;

                        millisec=i%100;
                        sec= (i/100)%60;
                        minute=(i/100)/60;

                        String time = minute+" 분"+sec+" 초 "+millisec;
                        if(sec==0&&minute==0){
                            time= "00 분 00 초"+millisec;
                        }
                        else if(sec==0){
                            time = minute +" 분 00 초 "+millisec;
                        }
                        else if(minute ==0){
                            time = "00 분 "+sec+" 초 "+millisec;
                        }
                        stopWatch.setText(time);
                    }
                };

                // send runnable object.
                handler.post(runnable);
            }
        }

    }
    */


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {

        super.onResume();
        Intent intent = new Intent(this, StopWatchService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;
        LocalBroadcastManager.getInstance(StopWatchTimerActivity.this).registerReceiver(mMessageReceiver, new IntentFilter(StopWatchService.serviceToStopWatchActivity));

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //로컬 브로드캐스터 연결 끊고
        LocalBroadcastManager.getInstance(StopWatchTimerActivity.this).unregisterReceiver(mMessageReceiver);

        //bindService 종료
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

    }


}