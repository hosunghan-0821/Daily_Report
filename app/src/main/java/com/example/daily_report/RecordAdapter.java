package com.example.daily_report;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> implements OnRecordItemClickListener {

    private ArrayList<RecordData> recordList;
    OnRecordItemClickListener listener;

    public RecordAdapter(ArrayList<RecordData> recordList) {
        this.recordList = recordList;
    }


    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View 라는 것은 내가 나타낼 화면인데 내가 레이아웃으로 만든 xml 파일을 자바 언어로 바꾸는 작업을 inflate 작업이라고한다
        //이 부분에서는 view를 만들고, view를 갖고 있는 customViewHolder를 만들어준다.
        //customViewholder 인 RecrodViewHolder는  선언되는 부분에서 각 요소에 대해xml에 어떤 영역과 매치되는지 설정해준다 아래쪽에 선언해놓은 class봐라

        Log.e("123", "my message : onCreateViewHolder ()");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_record_layout, parent, false);
        RecordViewHolder recordViewHolder = new RecordViewHolder(view, listener);
        return recordViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder recordViewHolder, int position) {

        Log.e("123", "my message : onBindViewHolder ()");
        recordViewHolder.startTime.setText(recordList.get(position).getStartTime());
        recordViewHolder.finishTime.setText(recordList.get(position).getFinishTime());
        recordViewHolder.actContent.setText(recordList.get(position).getActContent());
        recordViewHolder.concentrate.setText(recordList.get(position).getConcentrate());

        if(recordList.get(position).getFileName()==null){
            recordViewHolder.recordImage.setImageResource(R.drawable.camera_image);
        }
        else if(recordList.get(position).getFileName().equals("sampleImage")){
            recordViewHolder.recordImage.setImageResource(R.drawable.camera_image);
        }
        else{
            Log.e("image","absolutefilePath : "+  recordList.get(position).getFileName());
            Bitmap bitmapImage =BitmapFactory.decodeFile( recordList.get(position).getFileName());
            recordViewHolder.recordImage.setImageBitmap(bitmapImage);
        }

      /*
      if (recordList.get(position).getBitmapImage() == null) {
            recordViewHolder.recordImage.setImageResource(R.drawable.camera_image);
        } else {
            recordViewHolder.recordImage.setImageBitmap(recordList.get(position).getBitmapImage());
        }
        */

    }


    @Override
    public int getItemCount() {

        Log.e("123", "my message : getItemCount 여러번 실행될 것으로 생각 순서를 잘 파악하자    갯수 : " + recordList.size());
        return (null != recordList ? recordList.size() : 0);
    }

    @Override
    public void onItemClick(RecordViewHolder recordViewHolder, View view, int position) {
        /*      Log.d("123","my message: RecordAdapter 에서 실행 되는 onItemClick override 함수 ");

        if(listener!=null){
            listener.onItemClick(recordViewHolder,view,position);
        }*/

    }

    @Override
    public void onItemLongClick(RecordViewHolder recordViewHolder, View view, int position) {

    }

    public void setItemClickListener(OnRecordItemClickListener listener) {
        this.listener = listener;
    }

    public void setRecordList(ArrayList<RecordData> recordList) {
        this.recordList = recordList;
    }


    // custom ViewHolder로써 내가 직접 만들고 각 요소들에 데이터 정보값을 넣기 위해
    // xml파일의 어떤 부분과 연결지을 것인지 정해주는 곳,
    // 무엇을 상속받는 것인지 잘 확인하기,
    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        protected TextView startTime, finishTime, actContent, concentrate;
        protected ImageView recordImage;


        public RecordViewHolder(@NonNull View itemView, OnRecordItemClickListener listener) {

            super(itemView);
            this.startTime = itemView.findViewById(R.id.start_time);
            this.finishTime = itemView.findViewById(R.id.finish_time);
            this.actContent = itemView.findViewById(R.id.act_content);
            this.concentrate = itemView.findViewById(R.id.concentrate_text);
            this.recordImage = itemView.findViewById(R.id.record_image);
            //Log.e("123","my message : CustomViewHolder 선언하는 곳 이것도 한번만 실행될거임");


            //생성자에서 온클릭 리스너를 만들 때, 내가 만든 커스텀 리스너에 이제, RecordViewholder, view 정보, 인덱스같은 것들을 보낸다.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //Log.d("123","my message: RecordAdapter customviewholder 에서 생성자로 존재하는  onClick override 함수 ");
                    int position = getAbsoluteAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(RecordViewHolder.this, view, position);
                    }

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //Log.e("123","my message : adapter에 있는 온 롱클릭 리스너 실행1");

                    int position = getAbsoluteAdapterPosition();

                    if (listener != null) {
                        listener.onItemLongClick(RecordViewHolder.this, view, position);
                        //Log.e("123","my message : adapter에 있는 온 롱클릭 리스너 실행2");
                        return true;
                    }

                    return false;
                }
            });


        }


    }

    public RecordData getItem(int position) {

        return recordList.get(position);
    }

    public void setItem(int position, RecordData item) {
        recordList.set(position, item);
    }


}

