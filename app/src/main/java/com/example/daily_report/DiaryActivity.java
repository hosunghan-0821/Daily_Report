package com.example.daily_report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DiaryActivity extends AppCompatActivity {


    private TextView headerDate, feedBackText;
    private ImageView headerCalender, searchYoutube, toDoListPlus, selfFeedBackPlus;
    private ArrayList<DiaryToDoData> diaryToDoDataList;
    private RecyclerView diaryRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DiaryAdapter diaryAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("123", "my message : 다이어리 액티비티 onCreate ");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_diary);

        selfFeedBackPlus = (ImageView) findViewById(R.id.self_feedback_plus);
        feedBackText = (TextView) findViewById(R.id.self_feedback);
        searchYoutube = (ImageView) findViewById(R.id.search_youtube);
        headerDate = (TextView) findViewById(R.id.date);
        //toDoList=(TextView)findViewById(R.id.ToDo_List);
        headerCalender = (ImageView) findViewById(R.id.header_calender_image);
        toDoListPlus = (ImageView) findViewById(R.id.todo_list_plus);


        //recyclerview 선언하고, recyclerView 에 장착할 것들 => layoutManger + Adapter

        diaryRecyclerView = findViewById(R.id.recyclerview_todo_list);
        linearLayoutManager = new LinearLayoutManager(this);
        diaryToDoDataList = new ArrayList<DiaryToDoData>();

        diaryAdapter = new DiaryAdapter(diaryToDoDataList);

        diaryRecyclerView.setLayoutManager(linearLayoutManager);
        diaryRecyclerView.setAdapter(diaryAdapter);
        //diaryRecyclerView.setHasFixedSize(true);

        //화면전환할 때 현재 날짜 값 인텐트로 보낸거 받아서, header에 날짜 관련 내용 표시
        Intent diaryGetIntent = getIntent();
        headerDate.setText(diaryGetIntent.getStringExtra("date"));
        MainActivity.dateControl=headerDate.getText().toString();

        //onCreate 할 떄 기본 날짜에 맞춰서, recyclerView 보여주기.
        diaryToDoDataList=MySharedPreference.getToDoListArrayList(DiaryActivity.this,MainActivity.dateControl);
        diaryAdapter.setDiaryToDoDataList(diaryToDoDataList);
        diaryAdapter.notifyDataSetChanged();
        // 좌우로 스와이프 할 때 삭제 하려고 만든 itemtouchhelper 객체
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                diaryToDoDataList.remove(position);
                diaryAdapter.notifyDataSetChanged();
                MySharedPreference.setToDoList(DiaryActivity.this,headerDate.getText().toString(),diaryToDoDataList);

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(diaryRecyclerView);


        diaryAdapter.setItemClickListener(new DiaryAdapter.OnDiaryItemClickListener() {
            @Override
            public void onItemClick(DiaryAdapter.DiaryViewHolder diaryViewHolder, View itemView, int position) {

                AlertDialog.Builder updateDialog = new AlertDialog.Builder(DiaryActivity.this);
                updateDialog.setMessage("수정 할 내용을 입력하세요");
                EditText todo = new EditText(DiaryActivity.this);
                todo.setText(diaryViewHolder.toDoListContent.getText().toString());
                updateDialog.setView(todo);

                updateDialog.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        diaryToDoDataList.get(position).setToDoListContent(todo.getText().toString());
                        diaryAdapter.notifyItemChanged(position);
                        MySharedPreference.setToDoList(DiaryActivity.this,headerDate.getText().toString(),diaryToDoDataList);
                        dialogInterface.dismiss();
                    }
                });
                updateDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


                updateDialog.show();
            }
        });


        //OnCreate 함수에서 강제종료에 따른(ex 화면전환, or 시스템에의한 종료)  데이터 저장 및 복원 값을 판단하여 다시 재입력해주는 코드
        //onCreate 에서 저장하는 방법 이건 조금있다 실험해보자
        /*
        if(savedInstanceState==null){

        }
        else{
            toDoList.setText(savedInstanceState.getString("toDoList"));
            feedBackText.setText(savedInstanceState.getString("feedBack"));
            headerDate.setText(savedInstanceState.getString("headDate"));
        }
        */


        //Plus 버튼 눌렀을 때 나타나는 것들
        toDoListPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder toDoListDialog = new AlertDialog.Builder(DiaryActivity.this);
                final EditText toDo = new EditText(DiaryActivity.this);

                //다이얼로그 안의 내용들 추가해주는 코드
                toDoListDialog.setMessage("오늘의 할일을 입력하세요");
                toDoListDialog.setView(toDo);

                //toDoList 클릭했을 때, 다이얼로그가 보이게 해주는 코드

                //확인 버튼을 눌렀을 때, 일어나는 행동들
                toDoListDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = toDo.getText().toString();

                        DiaryToDoData data = new DiaryToDoData(text,  false);

                        diaryToDoDataList.add(data);
                        diaryAdapter.notifyItemInserted(diaryToDoDataList.size() - 1);

                        MySharedPreference.setToDoList(DiaryActivity.this,headerDate.getText().toString(),diaryToDoDataList);
                        dialogInterface.dismiss();
                        /*for(int j=0; j<diaryToDoDataList.size();j++){
                            Log.e("123","index J +"+j+" - checkBox 상태 : "+diaryToDoDataList.get(j).isCheckBox());
                        }*/
                    }
                });


                //취소 버튼을 눌렀을 때, 일어나는 행동들
                toDoListDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                toDoListDialog.show();


            }
        });

        //셀프피드백 기록하려고 할때
        selfFeedBackPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                view= LayoutInflater.from(DiaryActivity.this).inflate(R.layout.dialog_self_feddback_layout,null,false);

                EditText selfFeedbackFinal =view.findViewById(R.id.self_feedback_final);
                EditText selfFeedbackGood = view.findViewById(R.id.self_feedback_good_point);
                EditText selfFeedbackBad=view.findViewById(R.id.self_feedback_bad_point);
                RatingBar ratingBar = view.findViewById(R.id.self_feedback_rating);
                AlertDialog.Builder feedBackDialog = new AlertDialog.Builder(DiaryActivity.this);
                feedBackDialog.setView(view);


                //확인 버튼을 눌렀을 때, 일어나는 행동들
                feedBackDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // 여기서 각종 Rating bar 점수들이랑 이런 것들 저장하는곳에 저장하고, 평가를 어떻게 할 것인지는 생각해보자
                        feedBackText.setText(selfFeedbackFinal.getText().toString());
                        Toast.makeText(DiaryActivity.this,"별점  : "+ratingBar.getRating(),Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });


                //취소 버튼을 눌렀을 때, 일어나는 행동들
                feedBackDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                //feedback 텍스트 클릭했을 때, 다이얼로그가 보이게 해주는 코드
                feedBackDialog.show();


            }
        });

        //상단의 달력 버튼 눌렀을 때, datePickerDialog 를 활용하여, 날짜를 가져오고, 그 날짜에 따라서 데이터 기록들을 저장해보는 기능을 수행해보자
        DatePickerDialog datePickerDialog = new DatePickerDialog(DiaryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                //기존에 저장했던 todolist 내용들을 불러오고, 날짜도 바꾸기
                monthOfYear += 1;


                Calendar calendar = Calendar.getInstance();
                calendar.set(year,monthOfYear-1,dayOfMonth);
                Date date =calendar.getTime();
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd-E");
                MainActivity.dateControl =dateFormat.format(date);
                headerDate.setText(  MainActivity.dateControl);
                diaryToDoDataList=MySharedPreference.getToDoListArrayList(DiaryActivity.this,  MainActivity.dateControl);
                diaryAdapter.setDiaryToDoDataList(diaryToDoDataList);
                diaryAdapter.notifyDataSetChanged();

            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        headerCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();

            }
        });

        searchYoutube.setOnClickListener(new View.OnClickListener() {
            String sWord = "동기부여영상";

            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/search?q=/" + sWord));
                startActivity(searchIntent);


            }
        });


    }

    //기존의 내용들을 stop 과 destroy 사이에서 이 함수를 호출하여 저장한다. => 나는 가로세로 전환에의한 강제 종료될 때 사용..
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d("123", "my message : onSaveInstanceState 실행 타이밍 언젠지 확인하자 ");
        super.onSaveInstanceState(outState);

        //outState.putString("toDoList",toDoList.getText().toString());
        outState.putString("feedBack", feedBackText.getText().toString());
        outState.putString("headDate", headerDate.getText().toString());
    }

    //복원하는 함수인데, 액티비티가 강제 종료할 때만 호출한다 -> 시스템에 의해 종료 or 화면전환 할 때 호출한다 .//

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d("123", "my message : onRestoreInstanceState 실행 타이밍 언젠지 확인하자 ");
        super.onRestoreInstanceState(savedInstanceState);
        //toDoList.setText(savedInstanceState.getString("toDoList"));
        feedBackText.setText(savedInstanceState.getString("feedBack"));
        headerDate.setText(savedInstanceState.getString("headDate"));
    }

    @Override
    protected void onPause() {

        Log.d("123", "my message : 다이어리 액티비티 onPause ");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("123", "my message : 다이어리 액티비티 onResume ");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d("123", "my message : 다이어리 액티비티 onStop ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("123", "my message : 다이어리 액티비티 onDestroy ");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d("123", "my message : 다이어리 액티비티 onRestart ");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.d("123", "my message : 다이어리 액티비티 onStart ");
        super.onStart();
    }
}