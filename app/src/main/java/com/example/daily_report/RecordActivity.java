package com.example.daily_report;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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

                        try{
                            byte[] arr= bundle.getByteArray("image");
                            Bitmap image = BitmapFactory.decodeByteArray(arr, 0, arr.length);

                            //RecordPlusActivity에서 사진을 잘 전송할 경우  전송한 사진을 기반으로, recyclerView만들기
                            RecordData recordData =new RecordData(image,startTime,finishTime,actContent,concentrate);
                            recordList.add(recordData);
                            recordAdapter.notifyDataSetChanged();

                        }catch (Exception e){
                            Log.e(TAG, "message : 사진 안찍어서 오류67" );
                            //RecordPlusActivity에서 사진을 안찍어서 전송할 경우, 전송한 사진 말고 기본 사진을 갖고 recyclerView만들기
                            RecordData recordData =new RecordData(null,startTime,finishTime,actContent,concentrate);
                            recordList.add(recordData);
                            recordAdapter.notifyDataSetChanged();
                            Log.e("123","my message : adapter.notifyDataSetChanged() 뒤에 찍는 로그");


                        }







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

        //recyclerView 선언하고, 어댑터도 선언하고, 배열도 선언하고

        recordRecyclerView=findViewById(R.id.recyclerview_record);
        linearLayoutManager=new LinearLayoutManager(this);

        recordRecyclerView.setLayoutManager(linearLayoutManager);
        recordList= new ArrayList<RecordData>();
        recordAdapter= new RecordAdapter(recordList);
        recordRecyclerView.setAdapter(recordAdapter);


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