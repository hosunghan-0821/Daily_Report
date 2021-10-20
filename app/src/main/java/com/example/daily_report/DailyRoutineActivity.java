package com.example.daily_report;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DailyRoutineActivity extends AppCompatActivity {

    private EveningRoutineFragment eveningRoutineFragment;
    private FragmentManager fragmentManager;
    private MorningRoutineFragment morningRoutineFragment;
    private FragmentTransaction fragmentTransaction;
    TextView headerText;
    ViewPager2 pager;
    ViewPagerAdapter adapter;
    private ImageView routinePlusImage;
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_routine);

        headerText = findViewById(R.id.header_date);
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

        pager = findViewById(R.id.routine_viewpager);
        adapter = new ViewPagerAdapter(this);

        pager.setAdapter(adapter);

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == 1000) {

                            Intent intent = result.getData();
                            Bundle bundle = intent.getExtras();

                            if (bundle != null) {
                                if (bundle.getString("routineType").equals("아침")) {
                                    morningRoutineFragment.setArguments(bundle);
                                } else {
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