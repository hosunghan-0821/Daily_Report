package com.example.daily_report;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private ArrayList<RecordData> recordList;

    public RecordAdapter(ArrayList<RecordData> recordList) {
        this.recordList = recordList;
    }


    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View 라는 것은 내가 나타낼 화면인데 내가 레이아웃으로 만든 xml 파일을 자바 언어로 바꾸는 작업을 inflate 작업이라고한다
        //이 부분에서는 view를 만들고, view를 갖고 있는 customViewHolder를 만들어준다.
        //customViewholder 인 RecrodViewHolder는  선언되는 부분에서 각 요소에 대해xml에 어떤 영역과 매치되는지 설정해준다 아래쪽에 선언해놓은 class봐라


        Log.e("123","my message : onCreateViewHolder 이건 아마 한번만 만들어두고 재활용해서 쓸거임");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_record_layout, parent, false);
        RecordViewHolder recordViewHolder = new RecordViewHolder(view);
        return recordViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder recordViewHolder, int position) {

        //Log.e("123","my message : onBindViewHolder 이건 요소 추가할 때마다, 발생시킬거 같은데");
        recordViewHolder.startTime.setText(recordList.get(position).getStartTime());
        recordViewHolder.finishTime.setText(recordList.get(position).getFinishTime());
        recordViewHolder.actContent.setText(recordList.get(position).getActContent());
        recordViewHolder.concentrate.setText(recordList.get(position).getConcentrate());
        if(recordList.get(position).getBitmapImage()==null){
            recordViewHolder.recordImage.setImageResource(R.drawable.camera_image);
        }
        else {
            recordViewHolder.recordImage.setImageBitmap(recordList.get(position).getBitmapImage());
        }
    }


    @Override
    public int getItemCount() {

        //Log.e("123","my message : getItemCount 여러번 실행될 것으로 생각 순서를 잘 파악하자    갯수 : "+recordList.size());
        return (null != recordList ? recordList.size() : 0);
    }


    // custom ViewHolder로써 내가 직접 만들고 각 요소들에 데이터 정보값을 넣기 위해
    // xml파일의 어떤 부분과 연결지을 것인지 정해주는 곳,
    // 무엇을 상속받는 것인지 잘 확인하기,
    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        protected TextView startTime, finishTime, actContent, concentrate;
        protected ImageView recordImage;


        public RecordViewHolder(@NonNull View itemView, TextView startTime) {
            super(itemView);
            this.startTime = startTime;
        }

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            this.startTime = itemView.findViewById(R.id.start_time);
            this.finishTime = itemView.findViewById(R.id.finish_time);
            this.actContent = itemView.findViewById(R.id.act_content);
            this.concentrate = itemView.findViewById(R.id.concentrate_text);
            this.recordImage = itemView.findViewById(R.id.record_image);
            //Log.e("123","my message : CustomViewHolder 선언하는 곳 이것도 한번만 실행될거임");

        }
    }

    public interface OnRecordItemClickListener{
        public void onItemClick(RecordAdapter.RecordViewHolder recordViewHolder, View view);
    }

}

