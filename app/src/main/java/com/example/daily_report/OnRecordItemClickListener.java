package com.example.daily_report;

import android.view.View;

public interface OnRecordItemClickListener {

      void onItemClick(RecordAdapter.RecordViewHolder recordViewHolder, View view, int position);
      void onItemLongClick(RecordAdapter.RecordViewHolder recordViewHolder,View view,int position);
}
