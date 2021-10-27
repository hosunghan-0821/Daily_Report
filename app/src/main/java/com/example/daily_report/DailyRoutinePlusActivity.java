package com.example.daily_report;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DailyRoutinePlusActivity extends AppCompatActivity {

    public static final int INPUT_TYPE_PERSON = 0x00000060;

    private String[] items = {"아침", "저녁"};
    private ImageView routinePlusImage;
    private TextView routineRepeat, routineStartTime, headerTitle;
    private Spinner routineTypeSpinner;
    private EditText routineName;
    private Bundle bundle;
    private boolean isPlusImage;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_routine_plus);
        headerTitle = findViewById(R.id.header_title);
        routineTypeSpinner = findViewById(R.id.routine_type);
        routineName = findViewById(R.id.routine_name);
        routinePlusImage = findViewById(R.id.routine_plus_image);
        routineRepeat = findViewById(R.id.routine_repeat_checkbox);
        routineStartTime = findViewById(R.id.routine_time);


        //Spinner에 arrayAdapter 간단하게 장착 하는 코드
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routineTypeSpinner.setAdapter(adapter);

        //resume 생명주기를 활용하는데 뒤로가기 했을 떄, 번들에 대한 전 내용이 계속 남아있어서
        bundle = new Bundle();

        //update 화면으로 이 화면이 켜졌을 경우 intent를 받고 안 내용들이 null 이 아닌지 체크하고 수정진행

        Bundle bundleCheck = getIntent().getExtras();


        //수정할 경우 받은 인텐트의 번들의 내용이 null 이 아닐거야
        if (bundleCheck != null) {
            position = bundleCheck.getInt("position");
            routineName.setText(bundleCheck.getString("routineName"));
            routineStartTime.setText(bundleCheck.getString("routineTime"));

            routineRepeat.setText(bundleCheck.getString("routineRepeat"));
            headerTitle.setText("루틴 수정하기");

            for (int j = 1; j < 2; j++) {
                if (routineTypeSpinner.getSelectedItem().toString().equals(bundleCheck.getString("routineType"))) {
                    break;
                } else {
                    routineTypeSpinner.setSelection(j);
                }
            }

        }


        //루틴 추가하기 버튼을 누르면 인텐트들을 만들어서 정보를 result 런쳐로 보내주고 거기서 해결한다.
        routinePlusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //새로 추가하는 경우
                Intent resultIntent = new Intent();
                Intent updateIntent = new Intent();
                Intent updateEveningIntent = new Intent();
                String nameCheck = routineName.getText().toString();
                String routineType = routineTypeSpinner.getSelectedItem().toString();
                String routineTime = routineStartTime.getText().toString();
                String routineRepeatText = routineRepeat.getText().toString();


                if (nameCheck.equals("")||routineRepeatText.equals("")) {
                    Toast.makeText(view.getContext(), "루틴이름 혹은 루틴주기를  확인하여 입력해 주세요", Toast.LENGTH_SHORT).show();
                    isPlusImage = false;
                    nameCheck = "false";
                    routineRepeatText="false";
                }

                bundle.putString("routineType", routineType); //아침 or 저녁
                bundle.putString("routineName", nameCheck);  // 루틴 제목
                bundle.putString("routineTime", routineTime); // 구체적 상황 or 시간
                bundle.putString("routineRepeat", routineRepeatText); // 요일 선택
                if (bundleCheck != null) {
                    bundle.putInt("position", position);
                }


                if (nameCheck.equals("false")||routineRepeatText.equals("false")) {

                } else {
                    //새로 추가할때,
                    if (bundleCheck == null) {
                        bundle.putString("create","create");

                        resultIntent.putExtras(bundle);
                        setResult(1000, resultIntent);
                        Log.e("123", "수정할 떄, 이거 setresult 되면 문제다");

                        finish();
                    }

                    //수정 할때,
                    else {
                        if (routineType.equals("아침")) {


                            bundle.putString("update","update");
                            updateIntent.putExtras(bundle);
                            setResult(100, updateIntent);

                            finish();

                        } else if (routineType.equals("저녁")) {

                            bundle.putString("update","update");
                            updateEveningIntent.putExtras(bundle);
                            setResult(101, updateEveningIntent);

                            finish();

                        }


                    }
                }
            }
        });
        //루틴 repeat 을 눌렀을 떄 다이얼로그 띄우게 하기
        routineRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder repeatDialog = new AlertDialog.Builder(view.getContext());

                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_routine_checkbox, null, false);
                repeatDialog.setView(dialogView);

                CheckBox mon = dialogView.findViewById(R.id.checkbox_mon);
                CheckBox tue = dialogView.findViewById(R.id.checkbox_tue);
                CheckBox wed = dialogView.findViewById(R.id.checkbox_wed);
                CheckBox thu = dialogView.findViewById(R.id.checkbox_thu);
                CheckBox fri = dialogView.findViewById(R.id.checkbox_fri);
                CheckBox sat = dialogView.findViewById(R.id.checkbox_sat);
                CheckBox sun = dialogView.findViewById(R.id.checkbox_sun);


                //확인버튼 눌렀을 때,
                repeatDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String checkDay = "";

                        if (mon.isChecked()) {
                            checkDay += "월 ";
                        }
                        if (tue.isChecked()) {
                            checkDay += "화 ";
                        }
                        if (wed.isChecked()) {
                            checkDay += "수 ";
                        }
                        if (thu.isChecked()) {
                            checkDay += "목 ";
                        }
                        if (fri.isChecked()) {
                            checkDay += "금 ";
                        }
                        if (sat.isChecked()) {
                            checkDay += "토 ";
                        }
                        if (sun.isChecked()) {
                            checkDay += "일 ";
                        }

                        routineRepeat.setText(checkDay);
                        dialogInterface.dismiss();
                    }
                });
                //취소버튼 눌렀을 때,
                repeatDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        dialogInterface.dismiss();
                    }
                });


                repeatDialog.show();

            }
        });

        routineStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder timeDialog = new AlertDialog.Builder(view.getContext());
                timeDialog.setTitle("구체적인 상황이나 시간을 입력하세요");

                //edit text 속성 자바로 설정하기
                final EditText routineTime = new EditText(view.getContext());
                routineTime.requestFocus();
                routineTime.setInputType(INPUT_TYPE_PERSON);
                timeDialog.setView(routineTime);
                //확인버튼 눌렀을 때,
                timeDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        routineStartTime.setText(routineTime.getText().toString());
                        //키보드 자동으로 사라지게 하는것
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialogInterface.dismiss();
                    }
                });
                //취소버튼 눌렀을 때,
                timeDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //키보드 자동으로 사라지게 하는것
                        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                        dialogInterface.dismiss();
                    }
                });

                timeDialog.show();
                //키보드 자동으로 띄우는 것!
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
/*        if(isPlusImage==false){
            Intent resultIntent =new Intent(DailyRoutinePlusActivity.this,DailyRoutineActivity.class);
            bundle=new Bundle();
            resultIntent.putExtras(bundle);
            setResult(1000,resultIntent);

        }*/
    }
}