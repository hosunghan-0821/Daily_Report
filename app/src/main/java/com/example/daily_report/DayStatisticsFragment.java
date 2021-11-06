package com.example.daily_report;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class DayStatisticsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private StatisticsDayChartAdapter adapter;
    private ArrayList<StatisticsData> statisticsDataArrayList;
    TextView headerMonth;
    HorizontalCalendar horizontalCalendar;
    PieChart pieChart;
    ArrayList<PieEntry> pieArrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_day_statistics,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pieChart=view.findViewById(R.id.pie_chart_day);
        headerMonth=view.findViewById(R.id.header_month);

        //리사이클러뷰 관련 선언
        recyclerView=view.findViewById(R.id.pie_chart_day_recyclerview);
        statisticsDataArrayList=new ArrayList<StatisticsData>();

        adapter=new StatisticsDayChartAdapter(statisticsDataArrayList);
        linearLayoutManager=new LinearLayoutManager(getActivity());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        //상단 horizontal 관련 내용 정의
        // 이 달력은 현재보다 한달 느린 달력
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        // 이 달력은 현재까지
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 0);

        //horizontalCalendar builder를 이용해서 약간의 커스텀 부분
        HorizontalCalendar.Builder builder = new HorizontalCalendar.Builder(view, R.id.statistics_horizontal_calendar);
        builder.range(startDate, endDate);
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




                pieChartLoadData();
                pieRecyclerviewLoad();
                adapter.notifyDataSetChanged();

                horizontalCalendar.refresh();
            }

        });

        //아래는 파이차트
        pieChartLoadData();
        pieRecyclerviewLoad();
        adapter.notifyDataSetChanged();


    }


    public void pieChartLoadData(){

        boolean overLap=false;

        pieArrayList = new ArrayList<PieEntry>();
        ArrayList<StatisticsData> statisticsData = new ArrayList<StatisticsData>();

        //Shared에 저장되어있는 정보들 가져오기
        ArrayList<RecordData> recordArrayList = new ArrayList<RecordData>();
        ArrayList<RecordPlusActivityActData> actContentArrayList = new ArrayList<RecordPlusActivityActData>();

        //
        recordArrayList=MySharedPreference.getRecordArrayList(getActivity(),MainActivity.dateControl);
        //actContentArrayList =MySharedPreference.getActContentArrayList(getActivity(),"활동유형");


        if(recordArrayList.isEmpty()){
            pieChart.setVisibility(View.GONE);
            return;
        }
        else{
            pieChart.setVisibility(View.VISIBLE);
        }
        for(int i=0;i<recordArrayList.size();i++){

            int hour =recordArrayList.get(i).getHour();
            int minute = recordArrayList.get(i).getMinute();

            float time = (float)hour + ((float)minute)/60.0f;

            overLap=false;

            //맨처음 pieArray에 넣을 때,
            if(i==0){

                pieArrayList.add(new PieEntry(time,recordArrayList.get(i).getActContent()));
            }

            //한 개 이상 pieArray에 들어 있을 때,
            else{
                for(int j=0;j<pieArrayList.size();j++){
                    if(pieArrayList.get(j).getLabel().equals(recordArrayList.get(i).getActContent())){
                        //활동유형이 겹칠경우 시간을 float으로 환산해서, 데이터에 넣어주면됨 pieArray[J].
                        overLap=true;
/*

                      int tempHour =recordArrayList.get(i).getHour();
                      int tempMinute = recordArrayList.get(i).getMinute();

*/
                        float changeTime = time;
                        changeTime+=pieArrayList.get(j).getValue();
                        pieArrayList.set(j,new PieEntry(changeTime,pieArrayList.get(j).getLabel()));
                        break;
                    }
                }
                if(!overLap){
                    pieArrayList.add(new PieEntry(time,recordArrayList.get(i).getActContent()));
                }
            }
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for(int color : ColorTemplate.MATERIAL_COLORS){
            colors.add(color);
        }
        for(int color:ColorTemplate.VORDIPLOM_COLORS){
            colors.add(color);
        }

        PieDataSet pieDataSet = new PieDataSet(pieArrayList,"");
        pieDataSet.setColors(colors);


        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieData.setValueTextColor(Color.BLACK);
        pieData.setValueTextSize(12f);


        pieChart.setData(pieData);
        pieChart.setDrawEntryLabels(true);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("일간 통계");
        pieChart.getDescription().setEnabled(false);

    }
    public void pieRecyclerviewLoad(){

        statisticsDataArrayList.clear();

        for(int i=0;i<pieArrayList.size();i++){

            float fTime=pieArrayList.get(i).getValue();
            int fullTime = (int)(fTime*60);
            int intHour,intMinute;
            intHour=fullTime/60;
            intMinute=fullTime%60;
            statisticsDataArrayList.add(new StatisticsData(pieArrayList.get(i).getLabel(),intHour+" 시간 "+intMinute+" 분 " ));

        }
    }
}
