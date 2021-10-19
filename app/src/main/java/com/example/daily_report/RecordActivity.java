package com.example.daily_report;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

@RequiresApi(api = Build.VERSION_CODES.N)
public class RecordActivity extends AppCompatActivity {

    private static final String TAG = "RecordActivity";

    private Button recordPlusButton;

    private RecordAdapter recordAdapter;
    private RecyclerView recordRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<RecordData> recordList;


    //추가하기 버튼을 누르면 새로운 액티비티로 이동 후, 정보들을 callback할 때, 경우에 따라서 어떻게 행동할지 정의하는 선언 및 함수정의
    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {


                    if (result.getResultCode() == RESULT_OK) {

                        Intent intent = result.getData();
                        Bundle bundle = intent.getExtras();

                        //번들로부터 각 자료들 분류해서 얻어내기
                        String startTime = bundle.getString("startTime");
                        String finishTime = bundle.getString("finishTime");
                        String actContent = bundle.getString("actContent");
                        String concentrate = bundle.getString("concentrate");


                        //recyclerView에 데이터 추가하고 표시해주는 작업할 곳
                        //인텐트로 가져온 이미지가, 없을 경우와 이미지가 존재할 경우 나누어서 코드작성함

                        try {
                            byte[] arr = bundle.getByteArray("image");
                            Bitmap image = BitmapFactory.decodeByteArray(arr, 0, arr.length);

                            //RecordPlusActivity에서 사진을 잘 전송할 경우  전송한 사진을 기반으로, recyclerView만들기
                            RecordData recordData = new RecordData(image, startTime, finishTime, actContent, concentrate);
                            recordList.add(recordData);
                            recordAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            Log.e(TAG, "message : 사진 안찍어서 오류67");
                            //RecordPlusActivity에서 사진을 안찍어서 전송할 경우, 전송한 사진 말고 기본 사진을 갖고 recyclerView만들기
                            RecordData recordData = new RecordData(null, startTime, finishTime, actContent, concentrate);
                            recordList.add(recordData);
                            recordAdapter.notifyDataSetChanged();
                            Log.e("123", "my message : adapter.notifyDataSetChanged() 뒤에 찍는 로그");


                        }

                    }
                }
            }
    );
    private ActivityResultLauncher<Intent> updateLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == 100) {

                        Bitmap image;
                        Intent intent = result.getData();
                        Bundle bundle = intent.getExtras();
                        int position = bundle.getInt("position");
                        if (bundle.getByteArray("image") == null) {

                            image = null;

                        } else {

                            byte[] arr = bundle.getByteArray("image");
                            image = BitmapFactory.decodeByteArray(arr, 0, arr.length);

                        }
                        RecordData item = new RecordData(image,bundle.getString("startTime"),bundle.getString("finishTime"),bundle.getString("actContent"),bundle.getString("concentrate"));
                        recordAdapter.setItem(position, item);
                        recordAdapter.notifyItemChanged(position);

                    }

                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        //화면 생성 로그 및 화면 생성
        Log.e(TAG, "RecordActivity 생성했습니다");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //recyclerview 선언하고, recyclerView 에 장착할 것들 => layoutManger + Adapter
        //adapter는 어떤 데이터 리스트들을 처리할 것인지 생성자로 객체 인스턴스화 시키고, 생성

        recordRecyclerView = findViewById(R.id.recyclerview_record);
        linearLayoutManager = new LinearLayoutManager(this);

        recordRecyclerView.setLayoutManager(linearLayoutManager);
        recordList = new ArrayList<RecordData>();
        recordAdapter = new RecordAdapter(recordList);
        recordRecyclerView.setAdapter(recordAdapter);


        //  각, itemView 클릭할시 행동하는 함수 정의
        //  interface listener를 만들어서, 리사이클러뷰 있는 곳에서 클릭이벤트 재정의 한다.
        recordAdapter.setItemClickListener(new OnRecordItemClickListener() {
            @Override
            public void onItemClick(RecordAdapter.RecordViewHolder recordViewHolder, View view, int position) {
                Log.d("123", "my message: RecordActivity 에서 실행 되는 onItemClick 함수 ");

                // 아이템 클릭했을 시, 그에 해당하는 정보를 번들에 넣고 recordPlusActivity로 화면 전환하는 상황.
                RecordData item = recordAdapter.getItem(position);


                Intent i = new Intent(RecordActivity.this, RecordPlusActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("startTime", item.getStartTime().toString());
                bundle.putString("finishTime", item.getFinishTime().toString());
                bundle.putString("actContent", item.getActContent().toString());
                bundle.putString("concentrate", item.getConcentrate().toString());
                bundle.putString("update", "true");
                bundle.putInt("position", position);
                if (item.getBitmapImage() == null) {
                    bundle.putByteArray("image", null);
                } else {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    item.getBitmapImage().compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    bundle.putByteArray("image", byteArray);
                }

                i.putExtras(bundle);
                updateLauncher.launch(i);
                //Toast.makeText(RecordActivity.this,"어떤 아이템이 선택 : "+item.getStartTime(),Toast.LENGTH_SHORT).show();


            }

            //롱클릭 했을시 재선언 (interface내용)
            @Override
            public void onItemLongClick(RecordAdapter.RecordViewHolder recordViewHolder, View view, int position) {

                //Log.e("123","my message : RecordActivity에 있는 onItemLongclick 재정의 1");

                AlertDialog.Builder deleteDialog =new AlertDialog.Builder(RecordActivity.this);
                deleteDialog.setMessage("이 기록을 삭제하시겠습니까?");
                deleteDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        recordList.remove(position);
                        recordAdapter.notifyItemRemoved(position);
                        dialogInterface.dismiss();
                        //Log.e("123","my message : RecordActivity에 있는 onItemLongclick 재정의 2");

                    }
                });
                deleteDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        //Log.e("123","my message : RecordActivity에 있는 onItemLongclick 재정의 3");
                    }
                });

                deleteDialog.show();
            }
        });

        //상단 horizontal 관련 내용 정의
        // 이 달력은 현재보다 한달 느린 달력
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        // 이 달력은 현재까지
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 0);



        HorizontalCalendar.Builder horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView);
        horizontalCalendar.range(startDate,endDate);
        horizontalCalendar.datesNumberOnScreen(5);
        horizontalCalendar.configure().showTopText(false);
        horizontalCalendar.configure().end();
        HorizontalCalendar horizontalCalendar1 =horizontalCalendar.build();

        TextView headerMonth = findViewById(R.id.header_month);
        long now =System.currentTimeMillis();
        Date date= new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월");
        String getMonth = dateFormat.format(date);
        headerMonth.setText(getMonth);

        horizontalCalendar1.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM월");
                String getMonth = dateFormat.format(horizontalCalendar1.getSelectedDate().getTimeInMillis());

                headerMonth.setText(getMonth);
                horizontalCalendar1.refresh();
                //Toast.makeText(RecordActivity.this,"date : "+date+"position : "+position,Toast.LENGTH_SHORT).show();

            }

        });




        //추가하기 버튼을 누르면 새로운 액티비티로 이동 후, 정보들을 callback한다.
        recordPlusButton = findViewById(R.id.RecordPlusButton);
        recordPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecordActivity.this, RecordPlusActivity.class);

                resultLauncher.launch(i);
            }

        });


    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "액티비티 종료하였습니다");
        super.onDestroy();
    }

}