package com.example.daily_report;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class DailyRoutineActivity extends AppCompatActivity {

    private EveningRoutineFragment eveningRoutineFragment;
    private FragmentManager fragmentManager;
    private MorningRoutineFragment morningRoutineFragment;
    private FragmentTransaction fragmentTransaction;
    TextView headerText;
    ViewPager2 pager;
    ViewPagerAdapter adapter;
    TabLayout tabLayout;
    private ImageView routinePlusImage;
    private ActivityResultLauncher<Intent> resultLauncher;
    private HorizontalCalenderSelectedListener morningListener,eveningListener;
    ArrayList<DailyRoutineData> totalArrayList;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_routine);


        headerText = findViewById(R.id.header_month);
        routinePlusImage = findViewById(R.id.routine_plus_button);


        Intent intent = getIntent();
        headerText.setText(intent.getStringExtra("date"));
        //Log.e("123","onCreate() 함수호출");
        //프래그먼트를 이용하여서 화면전환 다른 예시중 하나.
         /*
         fragmentManager =getSupportFragmentManager();
        fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,morningRoutineFragment).commitAllowingStateLoss();
        */
        morningRoutineFragment = new MorningRoutineFragment();
        eveningRoutineFragment = new EveningRoutineFragment();


        //ViewPager2 + TabLayout 사용하기
        tabLayout=findViewById(R.id.tabLayout);
        pager = findViewById(R.id.routine_viewpager);
        adapter = new ViewPagerAdapter(this);

        pager.setAdapter(adapter);
         ArrayList<String> tabElement = new ArrayList<String>();
         tabElement.add("아침루틴");
         tabElement.add("저녁루틴");

        new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(DailyRoutineActivity.this);
                textView.setText(tabElement.get(position));
                textView.setTextColor(Color.parseColor("#000000"));
                tab.setCustomView(textView);
            }
        }).attach();


        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == 1000) {

                            Intent intent = result.getData();
                            Bundle bundle = intent.getExtras();

                            if (bundle != null) {

                                totalArrayList = MySharedPreference.getRoutineArrayList(DailyRoutineActivity.this, "나의루틴", "전체루틴");
                                //여기서 번들바꿔서 리사이클러뷰 정보 바꿔주기.

                                Log.e("123","DailyRoutineActivity에서 전체 루틴 array에 신규추가");
                                // 전체루틴에는 무조건 추가가 된다. 그 때, 일련번호를 삽입하여서, 나중에 추가 ,삭제할 때 사용하자.
                                int serialNumber;
                                int randomNum;
                                while(true){
                                    boolean check=false;
                                    randomNum=(int)(Math.random()*1000+1);
                                    for(int i=0; i<totalArrayList.size();i++){
                                        if(randomNum==totalArrayList.get(i).getSerialNumber()){
                                            check=true;
                                            break;
                                        }
                                    }
                                    if(check==false){
                                        break;
                                    }
                                }
                                serialNumber=randomNum;
                                Log.e("123","시리얼 넘버 : "+ serialNumber);

                                DailyRoutineData routineData = new DailyRoutineData(bundle.getString("routineTime"), bundle.getString("routineName"), bundle.getString("routineType"), bundle.getString("routineRepeat"), false,serialNumber);

                                totalArrayList.add(routineData);
                                MySharedPreference.setRoutineArrayList(DailyRoutineActivity.this, "나의루틴", totalArrayList, "전체루틴");

                                bundle.putInt("serialNumber",serialNumber);


                                if (bundle.getString("routineType").equals("아침")) {
                                    Log.e("123","routineActivity 에서 아침 fragment로 이동  bundle : "+ bundle);
                                    morningRoutineFragment.setArguments(bundle);
                                } else {
                                    Log.e("123","routineActivity 에서 저녁 fragment로 이동  bundle : "+ bundle);
                                    eveningRoutineFragment.setArguments(bundle);
                                }
                            }

                        }

                    }
                }
        );


        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {

                    case R.id.routine_plus_button:

                        //ActivityResultLauncher 사용해서 launcher사용
                        Intent i1 = new Intent(DailyRoutineActivity.this, DailyRoutinePlusActivity.class);
                        resultLauncher.launch(i1);
                        break;

                    default:
                        break;
                }


            }
        };
        routinePlusImage.setOnClickListener(click);


        //상단 horizontal 관련 내용 정의
        // 이 달력은 현재보다 한달 느린 달력
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -2);

        // 이 달력은 현재까지
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_WEEK_IN_MONTH,+0);

        //horizontalCalendar builder를 이용해서 약간의 커스텀 부분
        HorizontalCalendar.Builder horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView);
        horizontalCalendar.range(startDate, endDate);
        horizontalCalendar.datesNumberOnScreen(5);
        horizontalCalendar.configure().showTopText(false);
        horizontalCalendar.configure().end();
        HorizontalCalendar horizontalCalendar1 = horizontalCalendar.build();

        //현재 시간을 이용하여서, 달력 초기 값 선택
        TextView headerMonth = findViewById(R.id.header_month);
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM월");
        String getMonth = dateFormat.format(date);
        headerMonth.setText(getMonth);

        //초기 oncreate했을 때,
        java.text.SimpleDateFormat dateForamtControl = new java.text.SimpleDateFormat("yyyy-MM-dd-E");
        MainActivity.dateControl = dateForamtControl.format(date);

        Log.e("123","onCreate : 할 떄 MainActivtiy.dateControl"+ MainActivity.dateControl);

        horizontalCalendar1.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                java.text.SimpleDateFormat dateForamtControl1 = new java.text.SimpleDateFormat("MM월");
                String getMonth = dateForamtControl1.format(horizontalCalendar1.getSelectedDate().getTimeInMillis());

                headerMonth.setText(getMonth);

                java.text.SimpleDateFormat dateForamtControl = new java.text.SimpleDateFormat("yyyy-MM-dd-E");
                MainActivity.dateControl = dateForamtControl.format(horizontalCalendar1.getSelectedDate().getTimeInMillis());

                //Log.e("123","morningListener : "+ morningListener);
                //Log.e("123","eveningListener : "+ eveningListener);

                if(morningListener !=null){

                    morningListener.onDateSelected(date,position);

                }
                if(eveningListener!=null){

                    eveningListener.onDateSelected(date,position);
                }


            }
        });


    }
    public  void setHorizontalMorningListener(HorizontalCalenderSelectedListener listener){
        this.morningListener =listener;
    }
    public void setHorizontalEveningListener(HorizontalCalenderSelectedListener listener){
        this.eveningListener =listener;
    }




    public interface HorizontalCalenderSelectedListener{
        void onDateSelected(Calendar date,int position);
        boolean isMorning();
    }


    public class ViewPagerAdapter extends FragmentStateAdapter {


        public ViewPagerAdapter(@NonNull FragmentActivity fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {

                return morningRoutineFragment;
            } else if (position == 1) {

                return eveningRoutineFragment;
            }

            return null;
        }

        @Override
        public int getItemCount() {
            return 2;
        }


    }


    @Override
    protected void onPause() {

        Log.d("123", "my message : routine 액티비티 onPause ");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("123", "my message :  routine 액티비티 onResume ");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d("123", "my message : routine 액티비티 onStop ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("123", "my message : routine 액티비티 onDestroy ");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d("123", "my message : routine 액티비티 onRestart ");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.d("123", "my message : 다이어리 액티비티 onStart ");
        super.onStart();
    }


}