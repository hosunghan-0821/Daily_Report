package com.example.daily_report;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {



    private ArrayList<DiaryToDoData> diaryToDoDataList;
    OnDiaryItemClickListener listener;


    public DiaryAdapter(ArrayList<DiaryToDoData> diaryToDoDataList) {
        this.diaryToDoDataList=diaryToDoDataList;
    }

    @NonNull
    @Override
    public DiaryAdapter.DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_diary_todo_layout,parent,false);
        DiaryViewHolder diaryViewHolder = new DiaryViewHolder(view);

        return diaryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.DiaryViewHolder diaryViewHolder, int position) {

        String positionStr = String.valueOf(position+1);
        diaryViewHolder.toDoListNumber.setText(positionStr+".");
        diaryViewHolder.toDoListContent.setText(diaryToDoDataList.get(position).getToDoListContent());
        if(diaryToDoDataList.get(position).isCheckBox()==false){
            diaryViewHolder.toDoListCheckBox.setChecked(false);
        }
        else{
            diaryViewHolder.toDoListCheckBox.setChecked(true);
        }

    }

    @Override
    public int getItemCount() {

        return (null != diaryToDoDataList ? diaryToDoDataList.size() : 0);
    }


    public void setItemClickListener(OnDiaryItemClickListener listener){
        this.listener=listener;
    }

    public class DiaryViewHolder extends RecyclerView.ViewHolder{

        protected TextView toDoListNumber,toDoListContent;
        protected CheckBox toDoListCheckBox;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            toDoListNumber=itemView.findViewById(R.id.todo_list_number);
            toDoListContent=itemView.findViewById(R.id.todo_list_content);
            toDoListCheckBox=itemView.findViewById(R.id.todo_list_checkBox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if(listener != null){

                        listener.onItemClick(DiaryViewHolder.this,view,position);
                    }
                }
            });

            toDoListCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e("123","my message : my check botx on click");
                    int position =getAbsoluteAdapterPosition();
                    if(diaryToDoDataList.get(position).isCheckBox()){

                        diaryToDoDataList.get(position).setCheckBox(false);
                        DiaryAdapter.this.notifyItemChanged(position);

                    }
                    else{
                        diaryToDoDataList.get(position).setCheckBox(true);
                        DiaryAdapter.this.notifyItemChanged(position);

                    }
                }
            });

        }
    }
    public interface OnDiaryItemClickListener {
        void onItemClick(DiaryAdapter.DiaryViewHolder diaryViewHolder,View itemView,int position);

    }
}
