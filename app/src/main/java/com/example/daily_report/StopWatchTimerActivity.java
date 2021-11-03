package com.example.daily_report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class StopWatchTimerActivity extends AppCompatActivity {

    private TextView stopWatch, startText, finishText, writeText, tempStop;
    private Handler handler;
    NewThread thread;
    int i = 1;
    boolean isRunning=false,tempStopCheck=false;
    private ArrayList<StopWatchRecord> stopWatchRecordList;
    private RecyclerView recyclerView;
    private StopWatchAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_watch_timer);

        stopWatch = findViewById(R.id.stop_watch);
        startText = findViewById(R.id.start_text);
        finishText = findViewById(R.id.finish_text);
        writeText = findViewById(R.id.write_text);
        tempStop = findViewById(R.id.temp_finish_text);
        recyclerView=findViewById(R.id.recyclerview_stop_watch);

        stopWatchRecordList=new ArrayList<StopWatchRecord>();
        linearLayoutManager = new LinearLayoutManager(StopWatchTimerActivity.this);

        adapter= new StopWatchAdapter(stopWatchRecordList);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        stopWatch.setText("00 분 00초 00");
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                stopWatch.setText("00 분 00초 00");
            }
        };


        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRunning){
                    thread=new NewThread();
                    thread.start();
                    isRunning=true;
                }

            }
        });
        finishText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRunning){
                    thread.interrupt();
                    isRunning=false;
                    handler.sendEmptyMessage(0);
                    stopWatchRecordList.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        tempStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRunning){
                    thread.interrupt();
                    isRunning=false;
                    tempStopCheck=true;
                }
            }
        });
        writeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRunning){
                    StopWatchRecord data= new StopWatchRecord(stopWatch.getText().toString());
                    stopWatchRecordList.add(data);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

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


}