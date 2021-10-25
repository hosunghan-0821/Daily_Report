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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private int[] intTimeArray;

    public int[] stringTimeToIntTime(String startTime, String finishTime) {
        int timeArray[] = new int[2];

        int startHour,startMinute,finishHour,finishMinute;
        startHour= Integer.parseInt(startTime.substring(0, 2));
        startMinute=Integer.parseInt(startTime.substring(3, 5));

        finishHour=Integer.parseInt(finishTime.substring(0, 2));
        finishMinute= Integer.parseInt(finishTime.substring(3, 5));

        if(finishMinute<startMinute){
            finishMinute+=60;
            finishHour-=1;
        }
        timeArray[0] = finishHour-startHour;
        timeArray[1] = finishMinute-startMinute;

        return timeArray; //Index 0 : 시간 차이 Index 1 : 분 차이;;
    }

    //추가하기 버튼을 누르면 새로운 액티비티로 이동 후, 정보들을 callback할 때, 경우에 따라서 어떻게 행동할지 정의하는 선언 및 함수정의
    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    intTimeArray= new int[2];
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String imageFileName = format.format(date);


                    Log.e("image", "imageFileName : " + imageFileName);

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


                            //RecordPlusActivity에서 사진을 잘 전송할 경우 이미지를 내부저장소에 저장하고, 파일절대경로를 arrayList 에 저장하기
                            String absoluteFilePath = saveBitmapToFileDir(image, imageFileName);

                            //Log.e("image","absolutFilePath : "+ absoluteFilePath);

                            // 전송한 사진을 기반으로, recyclerView만들기
                            RecordData recordData = new RecordData(absoluteFilePath, startTime, finishTime, actContent, concentrate);

                            intTimeArray=stringTimeToIntTime(startTime,finishTime);
                            recordData.setHour(intTimeArray[0]);
                            recordData.setMinute(intTimeArray[1]);
                            recordList.add(recordData);
                            recordAdapter.notifyDataSetChanged();
                            MySharedPreference.setRecordArrayList(RecordActivity.this, MainActivity.dateControl, recordList);

                        } catch (Exception e) {
                            //Log.e(TAG, "message : 사진 안찍어서 오류67");
                            //RecordPlusActivity에서 사진을 안찍어서 전송할 경우, 전송한 사진 말고 기본 사진을 갖고 recyclerView만들기
                            RecordData recordData = new RecordData("sampleImage", startTime, finishTime, actContent, concentrate);

                            intTimeArray=stringTimeToIntTime(startTime,finishTime);
                            recordData.setHour(intTimeArray[0]);
                            recordData.setMinute(intTimeArray[1]);

                            recordList.add(recordData);
                            recordAdapter.notifyDataSetChanged();
                            MySharedPreference.setRecordArrayList(RecordActivity.this, MainActivity.dateControl, recordList);
                        }

                    }
                }
            }
    );

    //수정하기 버튼 눌렀을 떄 데이터 처리
    private ActivityResultLauncher<Intent> updateLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    //사진 저장할떄, 날짜_시간으로 파일이름 설정하기
                    long now = System.currentTimeMillis();                                      //시스템으로 부터 현재 정보를 받아온다.
                    Date date = new Date(now);                                                  //시스템의 현재 정보로 부터 날짜를 얻어낸다.
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");   //날짜 정보를 어떤식으로 나타낼지 format을 설정한다
                    String imageFileName = format.format(date);                                 //날짜를 내가 현재 지정한 String 패턴으로 바꾼다.

                    if (result.getResultCode() == 100) {

                        Bitmap image;
                        String fileName;
                        RecordData item;
                        Intent intent = result.getData();
                        Bundle bundle = intent.getExtras();

                        int position = bundle.getInt("position");

                        if (bundle.getByteArray("image") == null) {

                            //intent로 넘어온 데이터의 byteArray가 null 일 경우, 기본이미지로 대체하기 위해서 sampleImage 를 파일이름으로 설정
                            item = new RecordData("sampleImage", bundle.getString("startTime"), bundle.getString("finishTime"), bundle.getString("actContent"), bundle.getString("concentrate"));

                        } else {

                            byte[] arr = bundle.getByteArray("image");
                            image = BitmapFactory.decodeByteArray(arr, 0, arr.length);

                            // 원래 지정되어 있었던 사진경로를 이용해서 지우는 함수
                            deleteBitmapFromFileDir(recordList.get(position).getFileName());


                            //새로운 이미지를 활용해여서, 다시 사진 저장하고, 그 경로를 arrayList에 넣어주는 부분.
                            String absoluteFilePath = saveBitmapToFileDir(image, imageFileName);
                            item = new RecordData(absoluteFilePath, bundle.getString("startTime"), bundle.getString("finishTime"), bundle.getString("actContent"), bundle.getString("concentrate"));
                        }

                        // 추가할 때, 종료시간 시작시간을 활용해서, String-> Int로 총 시간, 분에 대한 정보 갖고 있도록 하기.
                        intTimeArray=stringTimeToIntTime(bundle.getString("startTime"),bundle.getString("finishTime"));
                        item.setHour(intTimeArray[0]);
                        item.setMinute(intTimeArray[1]);


                        // adapter에게 변경된 데이터 정보를 알려주고, notify시켜서 bindviewholder에 의해서 화면에 뿌리라는 것을 시킨다.
                        recordAdapter.setItem(position, item);
                        recordAdapter.notifyItemChanged(position);
                        MySharedPreference.setRecordArrayList(RecordActivity.this, MainActivity.dateControl, recordList);
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

        //Activity 시작할 떄, 당일 날짜를 기본 세팅으로 저장해놨기 때문에, 그에 해당하는 RecordRecyclerView를 불러와야함.
        Intent recordGetIntent = getIntent();
        MainActivity.dateControl = recordGetIntent.getStringExtra("date");
        recordList = MySharedPreference.getRecordArrayList(RecordActivity.this, MainActivity.dateControl);
        recordAdapter.setRecordList(recordList);
        recordAdapter.notifyDataSetChanged();

        //  각, itemView 클릭할시 행동하는 함수 정의
        //  interface listener를 만들어서, 리사이클러뷰 있는 곳에서 클릭이벤트 재정의 한다.
        recordAdapter.setItemClickListener(new OnRecordItemClickListener() {
            @Override
            public void onItemClick(RecordAdapter.RecordViewHolder recordViewHolder, View view, int position) {
                //Log.d("123", "my message: RecordActivity 에서 실행 되는 onItemClick 함수 ");

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


                //기존에 수정하려고 아이템 클릭 했을 때, 이미지에 대한 경로가 없을 경우, sampleImage일 경우, 원본사진이 있을 경우,
                // 그에 해당하는 경로를 bundle에 넣고 전달하는 역할을 한다.
                if (item.getFileName() == null) {
                    bundle.putString("image", "sampleImage");
                } else if (item.getFileName().equals("sampleImage")) {
                    bundle.putString("image", "sampleImage");
                } else {
                    bundle.putString("image", item.getFileName());
                }

                //수정하기 위해 아이템 클릭 했을시 위에 저장된 번들 내용을 갖고 recordPlusActivity 으로 이동하면서,
                // 수정을 하기 위한 Activity가 나타난다 updateLaucher 사용
                i.putExtras(bundle);
                updateLauncher.launch(i);

            }

            //롱클릭 했을시 재선언 (interface내용)
            @Override
            public void onItemLongClick(RecordAdapter.RecordViewHolder recordViewHolder, View view, int position) {

                //Log.e("123","my message : RecordActivity에 있는 onItemLongclick 재정의 1");

                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(RecordActivity.this);
                deleteDialog.setMessage("이 기록을 삭제하시겠습니까?");
                deleteDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {
                            // 지우는 함수
                            deleteBitmapFromFileDir(recordList.get(position).getFileName());

                        } catch (Exception e) {
                            Log.e("123", "파일 지우기 오류");
                        }


                        recordList.remove(position);
                        recordAdapter.notifyItemRemoved(position);
                        MySharedPreference.setRecordArrayList(RecordActivity.this, MainActivity.dateControl, recordList);
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

        //horizontalCalendar builder를 이용해서 약간의 커스텀 부분
        HorizontalCalendar.Builder horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView);
        horizontalCalendar.range(startDate, endDate);
        horizontalCalendar.datesNumberOnScreen(5);
        horizontalCalendar.configure().showTopText(false);
        horizontalCalendar.configure().end();
        HorizontalCalendar horizontalCalendar1 = horizontalCalendar.build();

        //현재 시간을 이용하여서, 달력 초기 값 선택
        TextView headerMonth = findViewById(R.id.header_month);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월");
        String getMonth = dateFormat.format(date);
        headerMonth.setText(getMonth);

        //horizontalCalendar 를 내가 앱에서 스크롤을 사용하여 원하는 날짜 선택했을 타이밍에 작동하는 코드..
        horizontalCalendar1.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM월");
                String getMonth = dateFormat.format(horizontalCalendar1.getSelectedDate().getTimeInMillis());

                SimpleDateFormat dateForamtControl = new SimpleDateFormat("yyyy-MM-dd-E");
                MainActivity.dateControl = dateForamtControl.format(horizontalCalendar1.getSelectedDate().getTimeInMillis());

                Log.e("123", "날짜 바뀌는거 제대로 되는지 확인 : " + MainActivity.dateControl);

                headerMonth.setText(getMonth);


                recordList = MySharedPreference.getRecordArrayList(RecordActivity.this, MainActivity.dateControl);
                Log.e("123", "recordList 날짜에 맞는 리스트로 가져오는지 확인 : " + recordList);

                recordAdapter.setRecordList(recordList);
                recordAdapter.notifyDataSetChanged();

                horizontalCalendar1.refresh();
            }

        });


        //추가하기 버튼을 누르면 새로운 액티비티로 이동 후, 정보들을 callback한다.
        recordPlusButton = findViewById(R.id.RecordPlusButton);
        recordPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecordActivity.this, RecordPlusActivity.class);
                Bundle bundle = new Bundle();
                //기록 추가 할 경우 arrayList의 사이즈가 0이 아니면 0번 방의 끝 시간을 보낸다.
                if (recordList.size() >= 1) {
                    bundle.putString("finishTimePlus", recordList.get(recordList.size() - 1).getFinishTime());
                } else {
                    bundle.putString("finishTimePlus", "00:00");
                }
                bundle.putString("update", "false");


                i.putExtras(bundle);
                resultLauncher.launch(i);
            }

        });


    }

    //이미지 파일 내부저장소 저장 관련 메소드
    public String saveBitmapToFileDir(Bitmap bitmap, String name) {


        //각각 무슨 역할인지 주석달기
        File storage = getFilesDir();                           // 내부 저장소의 file Directory 경로를 가져온다
        String fileName = name + ".png";                        // 내부 저장소에 저장할 파일의 이름을 지정합니다.

        File tempFile = new File(storage, fileName);            // storage에 지정된 파일이름을 올립니다. (인스턴스 생성)

        try {

            //각각 무슨 역할인지 주석달기
            tempFile.createNewFile();                                                //자동으로 빈 파일을 만든다..? 이 부분이 의미가  있는 부분인가?
            FileOutputStream outputStream = new FileOutputStream(tempFile);          // 파일을 쓸 수 있는 스트림을 준비한다. (내가 만든 파일에 저장할 수 잇게 해주는 stream)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);     //파일에 비트맵 이미지를 compress해서 저장한다. jpeg 파일로
            outputStream.close();                                                     // 스트림을 사용했으니. 닫아준다


        } catch (FileNotFoundException e) {
            Log.e("MyTag", "FileNotFoundException : " + e.getMessage());

            return "sampleImage";
        } catch (IOException e) {
            Log.e("MyTag", "IOException : " + e.getMessage());
            return "sampleImage";
        }

        return storage + "/" + fileName;                                            // 이후에 저장한 절대경로를 return해서 arrayList에 저장하기 위해 사용

    }

    //이미지 파일 내부저장소 저장 관련 메소드
    public void deleteBitmapFromFileDir(String fileAbsolutePath) {

        //파일 이미지 삭제하는 자르는 코드...
        //파일 삭제하기 위해서는, 경로가 아닌, 파일명 그 자체가 필요해서, String.replace를 사용해서
        // 파일 경로에서 파일명만 얻어낸후, 파일 삭제하는 코드
        String fileNameOnly = fileAbsolutePath;
        File path = getFilesDir();
        fileNameOnly = fileNameOnly.replace(path + "/", "");
        deleteFile(fileNameOnly);

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "액티비티 종료하였습니다");
        super.onDestroy();
    }

}