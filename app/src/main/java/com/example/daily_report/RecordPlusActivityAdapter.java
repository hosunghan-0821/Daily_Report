package com.example.daily_report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecordPlusActivityAdapter extends RecyclerView.Adapter<RecordPlusActivityAdapter.DialogViewHolder> {

     ArrayList<RecordPlusActivityActData> actDataList ;
     OnDialogItemClickListener listener;

    public RecordPlusActivityAdapter(ArrayList<RecordPlusActivityActData> actDataList) {
        this.actDataList = actDataList;
    }


    @NonNull
    @Override
    public DialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_dialog_actcontent_layout,parent,false);
        DialogViewHolder dialogViewHolder= new DialogViewHolder(view);
        return dialogViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DialogViewHolder dialogViewHolder, int position) {
       dialogViewHolder.actContentChoice.setText(actDataList.get(position).getActContentChoice());


    }

    @Override
    public int getItemCount() {
        return actDataList.size();
    }

    public void setItemClickListener(OnDialogItemClickListener listener){
        this.listener=listener;
    }

    public class DialogViewHolder extends RecyclerView.ViewHolder {

        protected TextView actContentChoice;

        public DialogViewHolder(@NonNull View itemView) {
            super(itemView);
            actContentChoice=itemView.findViewById(R.id.act_content_choice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAbsoluteAdapterPosition();

                    if(listener!=null){
                        listener.setItemClick(DialogViewHolder.this,view,position);
                    }

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    return false;
                }
            });

        }
    }

    public interface OnDialogItemClickListener {
        public void setItemClick(DialogViewHolder dialogViewHolder,View itemView, int position);

    }
}