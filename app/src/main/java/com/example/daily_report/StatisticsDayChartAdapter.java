package com.example.daily_report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StatisticsDayChartAdapter extends RecyclerView.Adapter<StatisticsDayChartAdapter.statisticsDayChartViewHolder>{

    private ArrayList<StatisticsData> statisticsDataArrayList;

    public StatisticsDayChartAdapter(ArrayList<StatisticsData> statisticsDataArrayList) {
        this.statisticsDataArrayList = statisticsDataArrayList;
    }

    @NonNull
    @Override
    public statisticsDayChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_piedaychart_layout,parent,false);
        statisticsDayChartViewHolder viewHolder = new statisticsDayChartViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull statisticsDayChartViewHolder holder, int position) {

        String str = String.valueOf(position+1);
        holder.number.setText(str);
        holder.content.setText(statisticsDataArrayList.get(position).getContent());
        holder.contentTime.setText(statisticsDataArrayList.get(position).getContentTime());

    }

    @Override
    public int getItemCount() {
        return statisticsDataArrayList.size();
    }


    public class statisticsDayChartViewHolder extends RecyclerView.ViewHolder{

        private TextView number,content,contentTime;

        public statisticsDayChartViewHolder(@NonNull View itemView) {
            super(itemView);
            number=itemView.findViewById(R.id.rankNumber);
            content=itemView.findViewById(R.id.pie_chart_day_content);
            contentTime=itemView.findViewById(R.id.pie_chart_day_content_time);
        }
    }
}
