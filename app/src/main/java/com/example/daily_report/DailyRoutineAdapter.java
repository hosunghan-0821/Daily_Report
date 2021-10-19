package com.example.daily_report;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DailyRoutineAdapter extends RecyclerView.Adapter<DailyRoutineAdapter.DailyRoutineViewHolder> {


    @NonNull
    @Override
    public DailyRoutineAdapter.DailyRoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycelrview_routine_layout,parent,false);

        return   new DailyRoutineViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull DailyRoutineAdapter.DailyRoutineViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class DailyRoutineViewHolder extends RecyclerView.ViewHolder{

        protected TextView routineType,routineContent;
        protected CheckBox checkBox;

        public DailyRoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            routineType=itemView.findViewById(R.id.routine_active_type);
            routineContent=itemView.findViewById(R.id.routine_content);
            checkBox=itemView.findViewById(R.id.routine_checkbox);

            //onclickliistener 만들기

        }
    }

}

