package com.example.daily_report;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private Button diaryButton, recordButton, statisticsButton, settingButton,routineButton;
    private TextView date_;




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("123","my message : 메인 액티비티 onCreate ");

        //화면 시작
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //각 xml의 아이디를 자바코드 내에서 인식하기위해 따로 변수 설정
        recordButton =(Button) findViewById(R.id.RecordButton);
        diaryButton=(Button) findViewById(R.id.diaryButton);
        statisticsButton =(Button) findViewById(R.id.statisticsButton);
        settingButton =(Button) findViewById(R.id.settingButton);
        routineButton=(Button) findViewById(R.id.routineButton);



        // 시스템으로부터 날짜 받아오는 코드
        long now =System.currentTimeMillis();
        Date date= new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String getDay = dateFormat.format(date);
        date_=findViewById(R.id.date);
        date_.setText(getDay);


        //onclickListener click 객체를 따로 만들어서 각 요소마다 onclickListner 장착 시켜줌
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.RecordButton:
                        Intent i1 =new Intent (MainActivity.this,RecordActivity.class);
                        startActivity(i1);
                        break;
                    case R.id.diaryButton:
                        Intent i2= new Intent (MainActivity.this,DiaryActivity.class);
                        i2.putExtra("date",date_.getText().toString());
                        startActivity(i2);
                        break;
                    case R.id.statisticsButton:
                        Intent i3= new Intent (MainActivity.this,StatisticsActivity.class);
                        startActivity(i3);
                        break;
                    case R.id.settingButton:
                        Intent i4= new Intent (MainActivity.this,SettingActivity.class);
                        startActivity(i4);
                        break;
                    case R.id.routineButton:
                        Intent i5 =new Intent(MainActivity.this,DailyRoutineActivity.class);
                        i5.putExtra("date",date_.getText().toString());
                        startActivity(i5);

                }
            }
        };
        //click 객체 각 버튼에 장착
        routineButton.setOnClickListener(click);
        recordButton.setOnClickListener(click);
        diaryButton.setOnClickListener(click);
        statisticsButton.setOnClickListener(click);
        settingButton.setOnClickListener(click);

        /* //todo 다른 방법으로 onclickListener사용해보기
        recordButton.setOnClickListener(new View.OnClickListener() { //기록 버튼 눌렀을 때 활동
            @Override
            public void onClick(View view) {
                Intent i =new Intent (MainActivity.this,RecordActivity.class);
                startActivity(i);
            }
        });

        diaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent (MainActivity.this,CalenderActivity.class);
                startActivity(i);
            }
        });

        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent (MainActivity.this,StatisticsActivity.class);
                startActivity(i);
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent (MainActivity.this,SettingActivity.class);
                startActivity(i);
            }
        });*/





    }


}