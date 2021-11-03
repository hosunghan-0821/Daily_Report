package com.example.daily_report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StopWatchAdapter extends RecyclerView.Adapter<StopWatchAdapter.StopWatchViewHolder> {

    private ArrayList<StopWatchRecord> stopWatchRecordList;

    public StopWatchAdapter(ArrayList<StopWatchRecord> stopWatchRecordList) {
        this.stopWatchRecordList = stopWatchRecordList;
    }

    @NonNull
    @Override
    public StopWatchAdapter.StopWatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_stopwatch_layout,parent,false);

        return new StopWatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StopWatchAdapter.StopWatchViewHolder holder, int position) {

        holder.stopWatchRecord.setText(stopWatchRecordList.get(position).getStopwatchRecord());

    }

    @Override
    public int getItemCount() {
        return stopWatchRecordList.size();
    }



    public class StopWatchViewHolder extends RecyclerView.ViewHolder{

        private TextView stopWatchRecord;


        public StopWatchViewHolder(@NonNull View itemView) {
            super(itemView);

            stopWatchRecord=itemView.findViewById(R.id.stop_watch_record);

        }
    }

}