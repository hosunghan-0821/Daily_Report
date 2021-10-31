package com.example.daily_report;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private Button diaryButton, recordButton, statisticsButton, settingButton,routineButton;
    private TextView date_,adviceText;
    private Handler handler;
    static String dateControl;
    private Thread adviceThread;
    private String[] advice =  new String[]{
            "나는 실패한 게 아니다. 나는 잘 되지 않는 방법 1만 가지를 발견한 것이다. – 토마스 에디슨",
            "성공적인 삶의 비밀은 무엇을 하는 게 자신의 운명인지 찾아낸 다음 그걸 하는 것이다. – 헨리 포드",
            "위대한 것으로 향하기 위해 좋은 것을 포기하는 걸 두려워하지 마라. - 존 록펠러",
            "성공(success)이 노력(work)보다 먼저 나타나는 유일한 곳은 사전이다. – 비달 사순",
            "실패에서부터 성공을 만들어 내라. 좌절과 실패는 성공으로 가는 가장 확실한 디딤돌이다. – 데일 카네기",
            "성공이란 절대 실수를 하지 않는 게 아니라 같은 실수를 두 번 하지 않는 것에 있다. – 조지버나드 쇼",
            "성공하려면 당신을 찾아오는 모든 도전을 다 받아들여야 한다. 마음에 드는 것만 골라 받을 수는 없다. – 마이크 가프카",
            "무엇을 하든, 그건 언제나 당신의 선택이다. –웨인 다이어"};



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
        adviceText = (TextView) findViewById(R.id.advice);


        // 한줄로 Ticker 배너 String 나오게 하기
        adviceText.setSingleLine(true);    // 한줄로 표시하기
        adviceText.setEllipsize(TextUtils.TruncateAt.MARQUEE); // 흐르게 만들기
        adviceText.setSelected(true);      // 선택하기


        // 시스템으로부터 날짜 받아오는 코드
        long now =System.currentTimeMillis();                   //현재 시스템으로 부터 현재 정보를 받아온다.
        Date date= new Date(now);                               //현재 시스템 시간 날짜 정보로부터, 날짜를 얻어온다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-E");
        String getDay = dateFormat.format(date);
        dateControl=dateFormat.format(date);
        date_=findViewById(R.id.date);
        date_.setText(dateControl);


        //Handler 사용하여 상단 배너 돌리게하기

        handler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                adviceText.setText(advice[msg.arg1]);


            }
        };



        //onclickListener click 객체를 따로 만들어서 각 요소마다 onclickListner 장착 시켜줌
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.RecordButton:
                        Intent i1 =new Intent (MainActivity.this,RecordActivity.class);
                        i1.putExtra("date",date_.getText().toString());
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

    class MyRunnable implements Runnable{

        @Override
        public void run() {

            while(true){

                int ranNum= (int)(Math.random()*(advice.length));
                Message msg = new Message();
                msg.arg1=ranNum;



                handler.sendMessage(msg);


                try{
                    Thread.sleep((350*advice[ranNum].length()));
                }catch(Exception e){
                    e.printStackTrace();
                    break;
                }



            }
            
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyRunnable myRunnable= new MyRunnable();
        adviceThread=new Thread(myRunnable);
        adviceThread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        adviceThread.interrupt();
    }
}