package com.example.daily_report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class WeekStatisticsFragment extends Fragment {


    private TextView headerMonth;
    private BarChart barChart;
    private ArrayList<BarEntry> barEntryArrayList;
    private ArrayList<String> labelsName;
    private HorizontalCalendar horizontalCalendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_week_statistics,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart=view.findViewById(R.id.bar_chart_week);
        headerMonth=view.findViewById(R.id.statistics_week_header_month);
        barEntryArrayList= new ArrayList<>();
        labelsName = new ArrayList<>();

        //상단 horizontal 관련 내용 정의
        //상단월 초기값 설정
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM월");
        String getMonth = dateFormat.format(date);
        headerMonth.setText(getMonth);


        // 이 달력은 현재보다 한달 느린 달력
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        // 이 달력은 현재까지
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 0);

        //horizontalCalendar builder를 이용해서 약간의 커스텀 부분
        HorizontalCalendar.Builder builder = new HorizontalCalendar.Builder(view, R.id.statistics_week_horizontal_calendar);
        builder.range(startDate,endDate);
        builder.datesNumberOnScreen(5);
        builder.configure().showTopText(false);
        builder.configure().end();
        horizontalCalendar = builder.build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM월");
                String getMonth = dateFormat.format(horizontalCalendar.getSelectedDate().getTimeInMillis());

                java.text.SimpleDateFormat dateForamtControl = new java.text.SimpleDateFormat("yyyy-MM-dd-E");
                MainActivity.dateControl = dateForamtControl.format(horizontalCalendar.getSelectedDate().getTimeInMillis());

                headerMonth.setText(getMonth);


            }
        });


        loadBarChartData();


    }

    public void loadBarChartData(){

        //일주일치 정보를 가져오는 방법.





    }

    private int getWeekOfYear(String date){

        Calendar calendar = Calendar.getInstance();
        String[] dates = date.split("-");
        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]);
        int day = Integer.parseInt(dates[2]);
        calendar.set(year, month - 1, day);
        return calendar.get(Calendar.WEEK_OF_YEAR);

    }
    private int getDayOfWeek(String date){
        Calendar calendar = Calendar.getInstance();
        String[] dates = date.split("-");
        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]);
        int day = Integer.parseInt(dates[2]);
        calendar.set(year, month - 1, day);
        return calendar.get(Calendar.DAY_OF_WEEK);

    }

}
