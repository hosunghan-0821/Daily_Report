package com.example.daily_report;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class WeekStatisticsFragment extends Fragment {


    private TextView headerDate;
    private BarChart barChart;
    private ArrayList<BarEntry> barEntryArrayList;
    private ArrayList<String> labelsName;
    private HorizontalCalendar horizontalCalendar;
    private ImageView calendarImage;
    private ArrayList<RecordData> weekList;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private StatisticsDayChartAdapter adapter;
    private ArrayList<StatisticsData> statisticsDataArrayList;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_week_statistics,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart=view.findViewById(R.id.bar_chart_week);
        headerDate=view.findViewById(R.id.statistics_week_header_date);
        calendarImage=view.findViewById(R.id.statistics_week_calender_image);

        barEntryArrayList= new ArrayList<>();
        labelsName = new ArrayList<>();

        //recyclerView 관련
        recyclerView=view.findViewById(R.id.statistics_week_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        statisticsDataArrayList = new ArrayList<>();
        adapter = new StatisticsDayChartAdapter(statisticsDataArrayList);


        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


        long now = System.currentTimeMillis();
        Date date = new Date(now);
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-W");
        String weekOfMonth = dateFormat.format(date);
        headerDate.setText("["+weekOfMonth+"주차]");
        loadBarChartData(weekOfMonth);
        barChartRecyclerViewLoad();


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                Calendar calendar= Calendar.getInstance();
                calendar.set(year,monthOfYear,dayOfMonth);
                Date date = calendar.getTime();
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-W");
                String weekOfMonth = dateFormat.format(date);
                headerDate.setText("["+weekOfMonth+"주차]");

                //여기서 이제 저장된 데이터를 불러오면서, BarChart 만드는 공간으로 사용하면된다. => Shared의 key 값 == weekOfMonth
                loadBarChartData(weekOfMonth);
                barChartRecyclerViewLoad();


            }
        },Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));



        calendarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();

            }
        });




    }
    public void barChartRecyclerViewLoad(){
       statisticsDataArrayList.clear();

       for(int i=0;i<weekList.size();i++){

           statisticsDataArrayList.add(new StatisticsData(weekList.get(i).getActContent(),weekList.get(i).getHour()+" 시간 "+weekList.get(i).getMinute()+" 분 "));

       }
       adapter.notifyDataSetChanged();

    }

    public void loadBarChartData(String key){

        weekList=new ArrayList<>();
        barEntryArrayList = new ArrayList<>();
        labelsName= new ArrayList<>();

        weekList=MySharedPreference.getWeekRecordArrayList(getActivity(),"주간통계",key);

        if(weekList.isEmpty()){
            barChart.clear();
            return;
        }
        //weekList 저장되어있는 상태는 중복체크 안되어있다. 원하는 통계처리를 하기 위해선 중복체크를 하면서 시간을 바꾸어야한다.
        //weekList 에 중복될 경우, 시간(hour, minute)만 올리는 함수

        weekList = weekListOverLapCheck(weekList);

        //중복처리 한 후 sort 해서 값이 큰 것부터 통계처리
        weekListSort();

        //통계에 값 넣기
        for(int i=0;i<weekList.size();i++){

            float time = (float)weekList.get(i).getHour() + ((float)weekList.get(i).getMinute())/60.0f;
            barEntryArrayList.add(new BarEntry(i,time));

        }


        BarDataSet barDataSet = new BarDataSet(barEntryArrayList,"시간");
        barDataSet.setColor(R.color.main_color1);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsName));

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelsName.size());

        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setTouchEnabled(false);
        barChart.invalidate();


    }

    public ArrayList<RecordData> weekListOverLapCheck(ArrayList<RecordData> dataArrayList){

        //통계에 들어갈 자료 한번 정리해주는 함수 여기서, 중복Check 및 시간 정리하기.
        ArrayList<RecordData> overLapCheckArrayList = new ArrayList<>();
        boolean overLapCheck;

        for(int i=0;i<weekList.size();i++){

            overLapCheck=false;
            if(labelsName.isEmpty()){
                labelsName.add(weekList.get(i).getActContent());
                overLapCheckArrayList.add(weekList.get(i));
            }
            else{

                for(int j=0;j<labelsName.size();j++){
                    try{
                        //만약 활동유형이 겹칠 경우  시간만 추가해서 올리고, label과 checkArrayList에 추가하지 않는다.
                        if(labelsName.get(j).equals(weekList.get(i).getActContent())){

                            overLapCheck=true;
                            int hour = overLapCheckArrayList.get(j).getHour();
                            int minute = overLapCheckArrayList.get(j).getMinute();
                            hour +=weekList.get(i).getHour();
                            minute += weekList.get(i).getMinute();

                            if(minute>=60){
                                minute-=60;
                                hour+=1;
                            }
                            overLapCheckArrayList.get(j).setHour(hour);
                            overLapCheckArrayList.get(j).setMinute(minute);
                            break;
                        }


                    }catch(Exception e){
                        Log.e("456","WeekList.get(i).getActContent() : " +weekList.get(i).getActContent());
                    }


                }
                //만약 활동유형이 겹치지 않는다면 label 추가하고, overLapArrrayList에 항목추가
                if(!overLapCheck){
                    labelsName.add(weekList.get(i).getActContent());
                    overLapCheckArrayList.add(weekList.get(i));
                }

            }

        }


        return overLapCheckArrayList;
    }

    public void weekListSort(){



        for(int i=0;i<weekList.size();i++){

            for(int j=i+1;j<weekList.size();j++){

                float iTime = (float)weekList.get(i).getHour() + ((float)weekList.get(i).getMinute())/60.0f;
                float jTime = (float)weekList.get(j).getHour() + ((float)weekList.get(j).getMinute())/60.0f;
                if(iTime<jTime){
                    Collections.swap(weekList,i,j);
                    Collections.swap(labelsName,i,j);
                }

            }

        }


    }



}
