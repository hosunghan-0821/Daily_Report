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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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
        diaryRecyclerView.setHasFixedSize(true);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                diaryToDoDataList.remove(position);
                diaryAdapter.notifyDataSetChanged();

            }
        };

        ItemTouchHelper itemTouchHelper= new ItemTouchHelper(simpleItemTouchCallback);
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

        //화면전환할 때 현재 날짜 값 인텐트로 보낸거 받아서, 선언한곳에다가 표시해주기
        Intent diaryGetIntent = getIntent();
        headerDate.setText(diaryGetIntent.getStringExtra("date"));


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

                        DiaryToDoData data = new DiaryToDoData(text, "0", false);

                        diaryToDoDataList.add(data);
                        Log.e("123", "my message add(data)");
                        diaryAdapter.notifyItemInserted(diaryToDoDataList.size()-1);
                        Log.e("123", "my message notifyDataSet tododatalist size : "+diaryToDoDataList.size());
                        dialogInterface.dismiss();
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

        //TODOLIST 터치했을 때, 다이얼로그를 이용해서 값 입력받고 저장하는코드 기본적인 다이얼로그 만들기
        /*    toDoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder toDoListDialog= new AlertDialog.Builder(DiaryActivity.this);
                final EditText toDo= new EditText(DiaryActivity.this);

                //다이얼로그 안의 내용들 추가해주는 코드
                toDoListDialog.setMessage("오늘의 할일을 입력하세요");
                toDoListDialog.setView(toDo);

                //toDoList 클릭했을 때, 다이얼로그가 보이게 해주는 코드

                //확인 버튼을 눌렀을 때, 일어나는 행동들
                toDoListDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = toDo.getText().toString();
                        toDoList.setText(text);
                        //dialogInterface.dismiss();
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
        });*/

        //셀프피드백 기록하려고 할때
        selfFeedBackPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder feedBackDialog = new AlertDialog.Builder(DiaryActivity.this);
                final EditText feedBack = new EditText(DiaryActivity.this);

                //다이얼로그 안의 내용들 추가해주는 코드
                feedBackDialog.setMessage("오늘 스스로를 돌아보세요 ");
                feedBackDialog.setView(feedBack);

                //확인 버튼을 눌렀을 때, 일어나는 행동들
                feedBackDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = feedBack.getText().toString();
                        feedBackText.setText(text);
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
        /*
        feedBackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder feedBackDialog= new AlertDialog.Builder(DiaryActivity.this);
                final EditText feedBack= new EditText(DiaryActivity.this);

                //다이얼로그 안의 내용들 추가해주는 코드
                feedBackDialog.setMessage("오늘 스스로를 돌아보세요 ");
                feedBackDialog.setView(feedBack);

                //확인 버튼을 눌렀을 때, 일어나는 행동들
                feedBackDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = feedBack.getText().toString();
                        feedBackText.setText(text);
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

        */

        //상단의 달력 버튼 눌렀을 때, datePickerDialog 를 활용하여, 날짜를 가져오고, 그 날짜에 따라서 데이터 기록들을 저장해보는 기능을 수행해보자
        DatePickerDialog datePickerDialog = new DatePickerDialog(DiaryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                //기존에 저장했던 todolist 내용들을 불러오고, 날짜도 바꾸기
                monthOfYear += 1;
                headerDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
                String text = sf.getString(headerDate.getText().toString(), "");
                //toDoList.setText(text);
            }
        }, 2021, 10 - 1, 11);

        headerCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();

                //달력 클릭했을 때, 기존에 있던 내용들 저장하기 sharedPreferences 를 이용하여서
                SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //editor.putString(headerDate.getText().toString(), toDoList.getText().toString());
                //editor.clear();
                //editor.apply();
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