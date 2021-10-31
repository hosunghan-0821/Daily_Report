package com.example.daily_report;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;

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
    private ArrayList<String> feedBackArrayList;
    private RatingBar selfRating;

    private int hour, minute;

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        selfRating = (RatingBar) findViewById(R.id.self_rating);


        //recyclerview 선언하고, recyclerView 에 장착할 것들 => layoutManger + Adapter

        diaryRecyclerView = findViewById(R.id.recyclerview_todo_list);
        linearLayoutManager = new LinearLayoutManager(this);
        diaryToDoDataList = new ArrayList<DiaryToDoData>();

        diaryAdapter = new DiaryAdapter(diaryToDoDataList);

        diaryRecyclerView.setLayoutManager(linearLayoutManager);
        diaryRecyclerView.setAdapter(diaryAdapter);
        //diaryRecyclerView.setHasFixedSize(true);

        //화면전환할 때 현재 날짜 값 인텐트로 보낸거 받아서, header에 날짜 관련 내용 표시

        // 시스템으로부터 날짜 받아오는 코드
        long now =System.currentTimeMillis();                   //현재 시스템으로 부터 현재 정보를 받아온다.
        Date date= new Date(now);                               //현재 시스템 시간 날짜 정보로부터, 날짜를 얻어온다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-E");
        String getDay = dateFormat.format(date);

        headerDate.setText(getDay);
        MainActivity.dateControl = headerDate.getText().toString();

        //onCreate 할 떄 기본 날짜에 맞춰서, recyclerView 보여주기.
        diaryToDoDataList = MySharedPreference.getToDoListArrayList(DiaryActivity.this, MainActivity.dateControl);
        diaryAdapter.setDiaryToDoDataList(diaryToDoDataList);
        diaryAdapter.notifyDataSetChanged();

        //onCreate 할 때, 오늘 한줄 평 저장되어 있다면 RatingBar,SharedPreferecne 로부터 불러오기
        feedBackArrayList = new ArrayList<String>();
        feedBackArrayList = getStringArrayListFromSharedPreferences("selfFeedBack");
        try {
            feedBackText.setText(feedBackArrayList.get(2).toString());
        } catch (Exception e) {

            Log.e("123", "저장된게 없을 떄, 공백으로 설정");
            feedBackText.setText("");
        }

        //ratingBar 점수 변화 할 떄 하는 행동시킨 코드들
        selfRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (b) {
                    float rating;
                    rating = ratingBar.getRating();
                    SharedPreferences sharedPreferences = MySharedPreference.getPreferences(DiaryActivity.this, MainActivity.dateControl);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("dailyRating", Float.toString(rating));
                    editor.apply();
                }
            }
        });

        //초기 별점 데이터 저장하는
        SharedPreferences sharedPreferences = MySharedPreference.getPreferences(DiaryActivity.this, MainActivity.dateControl);
        String strRating = sharedPreferences.getString("dailyRating", "3");
        Float getRating = Float.parseFloat(strRating);
        Log.e("123", "getRating : " + getRating);
        selfRating.setRating(getRating);


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
                MySharedPreference.setToDoList(DiaryActivity.this, headerDate.getText().toString(), diaryToDoDataList);

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(diaryRecyclerView);


        //아이템 클릭 리스너 인터페이스 설정 하는 부분
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
                        MySharedPreference.setToDoList(DiaryActivity.this, headerDate.getText().toString(), diaryToDoDataList);
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

            //알람 버튼 눌렀을 때, 이제 알람 설정하는 곳 만들어놨다. 자리
            @Override
            public void onAlarmClick(DiaryAdapter.DiaryViewHolder diaryViewHolder, View itemView, int position) {

                Calendar c = Calendar.getInstance();
                //사용할 타임피커 정의하는 부분
                TimePickerDialog timePickerDialog = new TimePickerDialog(DiaryActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        calendar.set(Calendar.SECOND, 0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Log.e("123", "알림 시작 0");
                            Log.e("123", "calender " + calendar.getTime());
                            startAlarm(calendar,position);

                        }


                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);


                if (!diaryToDoDataList.get(position).isAlarm()) {
                    timePickerDialog.setTitle("알람시간을 입력하세요");
                    timePickerDialog.show();
                    timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                } else {
                    cancelAlarm(position);


                }


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
                toDo.requestFocus();

                //toDoList 클릭했을 때, 다이얼로그가 보이게 해주는 코드

                //확인 버튼을 눌렀을 때, 일어나는 행동들
                toDoListDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = toDo.getText().toString();

                        int serialNumber;
                        DiaryToDoData data;
                        if(diaryToDoDataList.isEmpty()){
                            serialNumber=0;
                             data = new DiaryToDoData(text,false,false,serialNumber);
                        }
                        else{
                            serialNumber = diaryToDoDataList.get(diaryToDoDataList.size()-1).getSerialNumber()+1;
                            data = new DiaryToDoData(text, false, false,serialNumber);
                        }
                        diaryToDoDataList.add(data);
                        diaryAdapter.notifyItemInserted(diaryToDoDataList.size() - 1);

                        MySharedPreference.setToDoList(DiaryActivity.this, headerDate.getText().toString(), diaryToDoDataList);
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
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
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialogInterface.dismiss();
                    }
                });

                toDoListDialog.show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


            }
        });

        //셀프피드백 기록,수정 하려고 할때
        selfFeedBackPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // selfFeedBackDialog 기록추가 할 때 사용
                selfFeedBackDialogShow(view, "기록추가");

            }
        });
        feedBackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // selfFeedBackDialog 기록수정 할 때 사용
                selfFeedBackDialogShow(view, "기록수정");
            }
        });


        //상단의 달력 버튼 눌렀을 때, datePickerDialog 를 활용하여, 날짜를 가져오고, 그 날짜에 따라서 데이터 기록들을 저장해보는 기능을 수행해보자
        DatePickerDialog datePickerDialog = new DatePickerDialog(DiaryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                //기존에 저장했던 todolist 내용들을 불러오고, 날짜도 바꾸기
                monthOfYear += 1;

                //ArrayList<String> feedBackArrayList= new ArrayList<String>();

                //datePickerDialog에서 선택한 날짜를 -> 내가 사용할 수 있게끔 static변수에 넣어서 날짜 기준을 정해준다.
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear - 1, dayOfMonth);
                Date date = calendar.getTime();
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd-E");

                //datePickerDialog에서 선택한 날짜를 -> 내가 사용할 수 있게끔 static변수에 넣어서 날짜 기준을 정해준다.
                MainActivity.dateControl = dateFormat.format(date);
                headerDate.setText(MainActivity.dateControl);

                //저장되어 있던. JsonArray를 다시 내가 사용할 수 있는 ArrayList<String>으로 바꾸는 함수. 셀프 피드백
                feedBackArrayList = getStringArrayListFromSharedPreferences("selfFeedBack");

                //저장되어 있던 toDoList를 불러오는 sharedPreference로부터
                diaryToDoDataList = MySharedPreference.getToDoListArrayList(DiaryActivity.this, MainActivity.dateControl);

                //feedBackArrayList를 이용하여  저장된 정보 불러오기, 기존에 저장된 내용이 없을 때, 불러올 때, index참조 오류때문에 try~catch사용
                try {
                    feedBackText.setText(feedBackArrayList.get(2).toString());
                } catch (Exception e) {
                    feedBackText.setText("");
                }

                //저장되어 있는 RATING점수 불러오기 날짜별로
                SharedPreferences sharedPreferences = MySharedPreference.getPreferences(DiaryActivity.this, MainActivity.dateControl);
                String strRating = sharedPreferences.getString("dailyRating", "3");
                Float rating = Float.parseFloat(strRating);
                selfRating.setRating(rating);

                //recyclerView diaryToDOlIST Adapter 참조로 연결시키고 다시 내용 뿌려주는 역할
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

    /// OnCreate() 함수 여기까지 ;;
    //-------------------------------------------------------

    public void startAlarm(Calendar calendar,int position) {


        if (calendar.before((Calendar.getInstance()))) {
            Toast.makeText(DiaryActivity.this, "현재 시간 이후로 알람 다시 설정해주세요", Toast.LENGTH_SHORT).show();
        } else {

            //알람설정에 성공하면 shared에 arrayList 정보들 기입하고 . adapter가  실제로 보여지는 것도 다르게 나오게 해야함.
            diaryToDoDataList.get(position).setAlarm(true);
            diaryAdapter.notifyItemChanged(position);
            MySharedPreference.setToDoList(DiaryActivity.this, headerDate.getText().toString(), diaryToDoDataList);


            //알람 매니저를 사용하여 알람 등록
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this, AlertReceiver.class);

            intent.putExtra("content",diaryToDoDataList.get(position).getToDoListContent());

            //이 request 코드를 어떻게 활용할 것인가가 포인트인데.. 어떻게 쓸것인가?
            //int requestCode =>> diary의 일련번호를 활용해서 사용
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, diaryToDoDataList.get(position).getSerialNumber(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.e("123", "알림 시작 1");
            Log.e("123","보낸 것 : "+diaryToDoDataList.get(position).getToDoListContent());
        }

    }

    public void cancelAlarm(int position) {

        //취소하려고 한번더 버튼을 누르면, arrayList 정보 바꾸고  , shared에 바뀐 내용 언급.
        diaryToDoDataList.get(position).setAlarm(false);
        diaryAdapter.notifyItemChanged(position);
        MySharedPreference.setToDoList(DiaryActivity.this, headerDate.getText().toString(), diaryToDoDataList);


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(DiaryActivity.this, "알람을 취소합니다", Toast.LENGTH_SHORT).show();
    }


    public void selfFeedBackDialogShow(View view, String state) {
        ArrayList<String> arrayList = new ArrayList<String>();

        view = LayoutInflater.from(DiaryActivity.this).inflate(R.layout.dialog_self_feddback_layout, null, false);

        EditText selfFeedbackFinal = view.findViewById(R.id.self_feedback_final);
        EditText selfFeedbackGood = view.findViewById(R.id.self_feedback_good_point);
        EditText selfFeedbackBad = view.findViewById(R.id.self_feedback_bad_point);
        AlertDialog.Builder feedBackDialog = new AlertDialog.Builder(DiaryActivity.this);
        feedBackDialog.setView(view);

        //기록 수정할 때, Shared에 저장되어 있는 내용 불러오기
        if (state.equals("기록수정")) {

            ArrayList<String> updateArrayList = new ArrayList<>();

            updateArrayList = getStringArrayListFromSharedPreferences("selfFeedBack");
            try {
                selfFeedbackGood.setText(updateArrayList.get(0));
                selfFeedbackBad.setText(updateArrayList.get(1));
                selfFeedbackFinal.setText(updateArrayList.get(2));
            } catch (Exception e) {
                selfFeedbackGood.setText("");
                selfFeedbackBad.setText("");
                selfFeedbackFinal.setText("");
            }
        }


        //확인 버튼을 눌렀을 때, 일어나는 행동들
        feedBackDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                arrayList.add(selfFeedbackGood.getText().toString());
                arrayList.add(selfFeedbackBad.getText().toString());
                arrayList.add(selfFeedbackFinal.getText().toString());
                setStringArrayListToSharedPreferences(arrayList, "selfFeedBack");
                // 여기서 각종 Rating bar 점수들이랑 이런 것들 저장하는곳에 저장하고, 평가를 어떻게 할 것인지는 생각해보자
                feedBackText.setText(selfFeedbackFinal.getText().toString());
                //Toast.makeText(DiaryActivity.this,"별점  : "+ratingBar.getRating(),Toast.LENGTH_SHORT).show();
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


    //JSONArray 함수를 사용하여서 arrayList<String> 의 내용 전부를 Shared에 저장하는 함수
    //JsonArray를 사용하여서 기존의 ArrayList<String>을 한줄의 String으로 만들어주는 함수
    public void setStringArrayListToSharedPreferences(ArrayList<String> arrayList, String key) {


        SharedPreferences sharedPreferences = MySharedPreference.getPreferences(DiaryActivity.this, MainActivity.dateControl);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        JSONArray jsonArray = new JSONArray();                      //JSONArray 선언하여서
        for (int i = 0; i < arrayList.size(); i++) {
            jsonArray.put(arrayList.get(i));                        //JsonArray에 arrayList<String> 한 list씩 삽입
        }
        if (!arrayList.isEmpty()) {
            editor.putString(key, jsonArray.toString());            //arrayList가 비어있지 않다면, JSONArray에 들어가 있는 arrayList를 .toString으로 만들어서 넣어준다
        } else {
            editor.putString(key, null);                        //arrayList가 비어있다면, null값을 넣는다.
        }
        editor.apply();
    }


    //이제 변환되었던, String을 다시 arrayList<String> 으로 복구하는 작업.
    public ArrayList<String> getStringArrayListFromSharedPreferences(String key) {

        //JsonArray로 사용하여 일렬로 만들어 놓은 arrayList를 다시 내가 사용할 수 있는 arrayList<String>로 바꿔주는 역할
        SharedPreferences sharedPreferences = MySharedPreference.getPreferences(DiaryActivity.this, MainActivity.dateControl);
        String stringFromJsonArray = sharedPreferences.getString(key, null);     // key값을 이용하여서 String 값을 얻어낸다.


        ArrayList<String> arrayList = new ArrayList<>();                             //arrayList에 string을 저장하기위해 선언
        if (stringFromJsonArray != null) {

            try {
                JSONArray jsonArray = new JSONArray(stringFromJsonArray);       //      JSONArray를 (String) 값을 이용해서 생성하고.
                for (int i = 0; i < jsonArray.length(); i++) {
                    String data = jsonArray.optString(i);                       //      JSONArray를 한줄 한줄 뽑아내서
                    arrayList.add(data);                                        //      arrayList에 넣어준다 .add(data) 한다
                }
            } catch (Exception e) {

            }

        }
        return arrayList;                                                         // 저장된게 없을 경우에는 빈 arrayList를 retrun, 저장된게 있을 경우에는 add된 데이터 arrayList를 리턴
    }

    //기존의 내용들을 stop 과 destroy 사이에서 이 함수를 호출하여 저장한다. => 나는 가로세로 전환에의한 강제 종료될 때 사용..
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d("123", "my message : onSaveInstanceState 실행 타이밍 언젠지 확인하자 ");
        super.onSaveInstanceState(outState);

        //outState.putString("toDoList",toDoList.getText().toString());
        //outState.putString("feedBack", feedBackText.getText().toString());
        //outState.putString("headDate", headerDate.getText().toString());
    }

    //복원하는 함수인데, 액티비티가 강제 종료할 때만 호출한다 -> 시스템에 의해 종료 or 화면전환 할 때 호출한다 .//

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d("123", "my message : onRestoreInstanceState 실행 타이밍 언젠지 확인하자 ");
        super.onRestoreInstanceState(savedInstanceState);
        //toDoList.setText(savedInstanceState.getString("toDoList"));
        //feedBackText.setText(savedInstanceState.getString("feedBack"));
        //headerDate.setText(savedInstanceState.getString("headDate"));
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