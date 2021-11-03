package com.example.daily_report;

import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {


    private ArrayList<DiaryToDoData> diaryToDoDataList;
    OnDiaryItemClickListener listener;


    public DiaryAdapter(ArrayList<DiaryToDoData> diaryToDoDataList) {
        this.diaryToDoDataList = diaryToDoDataList;
    }

    @NonNull
    @Override
    public DiaryAdapter.DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_diary_todo_layout, parent, false);
        DiaryViewHolder diaryViewHolder = new DiaryViewHolder(view);

        return diaryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.DiaryViewHolder diaryViewHolder, int position) {

        // 시스템으로부터 날짜 받아오는 코드
        long now = System.currentTimeMillis();                   //현재 시스템으로 부터 현재 정보를 받아온다.
        Date date = new Date(now);                               //현재 시스템 시간 날짜 정보로부터, 날짜를 얻어온다.
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd-E");
        String getDay = dateFormat.format(date);

        String positionStr = String.valueOf(position + 1);
        diaryViewHolder.toDoListNumber.setText(positionStr + ".");
        diaryViewHolder.toDoListContent.setText(diaryToDoDataList.get(position).getToDoListContent());
        if (diaryToDoDataList.get(position).isCheckBox() == false) {
            diaryViewHolder.toDoListCheckBox.setChecked(false);
        } else {
            diaryViewHolder.toDoListCheckBox.setChecked(true);
        }


        if (diaryToDoDataList.get(position).isAlarm() == false) {
            diaryViewHolder.toDoListAlarmImage.setImageResource(R.drawable.add_alarm);
        } else {
            diaryViewHolder.toDoListAlarmImage.setImageResource(R.drawable.on_alarm);
        }


    }

    @Override
    public int getItemCount() {

        return (null != diaryToDoDataList ? diaryToDoDataList.size() : 0);
    }


    public void setItemClickListener(OnDiaryItemClickListener listener) {
        this.listener = listener;
    }

    public void setDiaryToDoDataList(ArrayList<DiaryToDoData> diaryToDoDataList) {
        this.diaryToDoDataList = diaryToDoDataList;
    }


    public class DiaryViewHolder extends RecyclerView.ViewHolder {

        protected TextView toDoListNumber, toDoListContent;
        protected CheckBox toDoListCheckBox;
        protected ImageView toDoListAlarmImage;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);

            toDoListNumber = itemView.findViewById(R.id.todo_list_number);
            toDoListContent = itemView.findViewById(R.id.todo_list_content);
            toDoListCheckBox = itemView.findViewById(R.id.todo_list_checkBox);
            toDoListAlarmImage = itemView.findViewById(R.id.todo_list_alarm);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if (listener != null) {

                        listener.onItemClick(DiaryViewHolder.this, view, position);

                    }
                }
            });

            toDoListCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e("123", "my message : my check botx on click");
                    int position = getAbsoluteAdapterPosition();
                    if (diaryToDoDataList.get(position).isCheckBox()) {

                        diaryToDoDataList.get(position).setCheckBox(false);

                        DiaryAdapter.this.notifyItemChanged(position);
                        MySharedPreference.setToDoList(view.getContext(), MainActivity.dateControl, diaryToDoDataList);


                    } else {
                        diaryToDoDataList.get(position).setCheckBox(true);
                        DiaryAdapter.this.notifyItemChanged(position);
                        MySharedPreference.setToDoList(view.getContext(), MainActivity.dateControl, diaryToDoDataList);

                    }
                }
            });

            //알람설정하는 곳
            toDoListAlarmImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if (listener != null) {
                        listener.onAlarmClick(DiaryViewHolder.this, view, position);
                    }

                }
            });

        }
    }

    public interface OnDiaryItemClickListener {
        void onItemClick(DiaryAdapter.DiaryViewHolder diaryViewHolder, View itemView, int position);

        void onAlarmClick(DiaryAdapter.DiaryViewHolder diaryViewHolder, View itemView, int position);

    }
}
