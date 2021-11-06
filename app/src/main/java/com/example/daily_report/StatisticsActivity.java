package com.example.daily_report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Date;

public class StatisticsActivity extends AppCompatActivity {

    private DayStatisticsFragment dayStatisticsFragment;
    private WeekStatisticsFragment weekStatisticsFragment;
    ViewPager2 viewPager2;

    ViewPagerAdapterStatistics adapter;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dayStatisticsFragment = new DayStatisticsFragment();
        weekStatisticsFragment=new WeekStatisticsFragment();

        tabLayout = findViewById(R.id.statistics_tabLayout);
        viewPager2 = findViewById(R.id.statistics_viewpager);
        adapter = new ViewPagerAdapterStatistics(this);
        viewPager2.setAdapter(adapter);

        viewPager2.setUserInputEnabled(false);

        ArrayList<String> tabElement = new ArrayList<String>();
        tabElement.add("일간통계");
        tabElement.add("주간통계");

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(StatisticsActivity.this);
                textView.setText(tabElement.get(position));
                textView.setTextColor(Color.parseColor("#000000"));
                tab.setCustomView(textView);
            }
        }).attach();

        long now =System.currentTimeMillis();                   //현재 시스템으로 부터 현재 정보를 받아온다.
        Date date= new Date(now);
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd-E");
        MainActivity.dateControl=dateFormat.format(date);

    }

    public class ViewPagerAdapterStatistics extends FragmentStateAdapter {

        public ViewPagerAdapterStatistics(@NonNull StatisticsActivity fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            if (position == 0) {
                return dayStatisticsFragment;
            } else if (position == 1) {

                return weekStatisticsFragment;

            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }


}