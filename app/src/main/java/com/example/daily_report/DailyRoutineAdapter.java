package com.example.daily_report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DailyRoutineAdapter extends RecyclerView.Adapter<DailyRoutineAdapter.DailyRoutineViewHolder> {

    private OnRoutineItemClickListener listener;
    private ArrayList<DailyRoutineData> routineDataList;

    public DailyRoutineAdapter(ArrayList<DailyRoutineData> routineDataList) {
        this.routineDataList=routineDataList;
    }

    @NonNull
    @Override
    public DailyRoutineAdapter.DailyRoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycelrview_routine_layout,parent,false);

        return   new DailyRoutineViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull DailyRoutineAdapter.DailyRoutineViewHolder holder, int position) {

        holder.routineContent.setText(routineDataList.get(position).getRoutineContent().toString());
        holder.routineType.setText(routineDataList.get(position).getRoutineTime().toString());
        if(!routineDataList.get(position).isCheckBox()){
           holder.checkBox.setChecked(false);
        }
        else{
            holder.checkBox.setChecked(true);
        }


    }

    @Override
    public int getItemCount() {
        return routineDataList.size();
    }

    public void setOnclickListener(OnRoutineItemClickListener listener){
        this.listener=listener;

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAbsoluteAdapterPosition();
                    if(listener!=null){
                        listener.setItemClick(DailyRoutineViewHolder.this,view,position);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    int position = getAbsoluteAdapterPosition();

                    if(listener!=null){
                        listener.onItemLongClick(DailyRoutineViewHolder.this,view,position);

                        return true;
                    }

                    return false;
                }
            });


            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position =getAbsoluteAdapterPosition();
                    if(routineDataList.get(position).isCheckBox()){

                        routineDataList.get(position).setCheckBox(false);
                        DailyRoutineAdapter.this.notifyItemChanged(position);

                        if(routineDataList.get(position).getRoutineType().equals("아침")){
                            MySharedPreference.setRoutineArrayList(view.getContext(), MainActivity.dateControl,routineDataList,"아침루틴");
                        }
                        else{
                            MySharedPreference.setRoutineArrayList(view.getContext(), MainActivity.dateControl,routineDataList,"저녁루틴");
                        }


                    }
                    else{
                        routineDataList.get(position).setCheckBox(true);
                        DailyRoutineAdapter.this.notifyItemChanged(position);
                        if(routineDataList.get(position).getRoutineType().equals("아침")){
                            MySharedPreference.setRoutineArrayList(view.getContext(), MainActivity.dateControl,routineDataList,"아침루틴");
                        }
                        else{
                            MySharedPreference.setRoutineArrayList(view.getContext(), MainActivity.dateControl,routineDataList,"저녁루틴");
                        }

                    }

                }
            });

        }
    }
    public void setRoutineDataList(ArrayList<DailyRoutineData> arrayList){
        this.routineDataList=arrayList;
    }

    public interface OnRoutineItemClickListener{
         void setItemClick(DailyRoutineAdapter.DailyRoutineViewHolder dailyRoutineViewHolder, View itemView, int position);
        void onItemLongClick(DailyRoutineAdapter.DailyRoutineViewHolder dailyRoutineViewHolder,View itemView,int position);
    }

}

