package com.example.daily_report;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class RecordPlusActivity extends AppCompatActivity {

    private final int PERMISSIONS_REQUEST = 1001;
    private static final String TAG = "RecordPlusActivity";
    String[] items = {"상", "중", "하"};
    TextView startTime, finishTime;
    Button recordPlusButton, imagePlusButton;
    EditText actContent;
    ImageView contentImage;

    private boolean startCheck, finishCheck;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private TimePickerDialog timePickerDialogEx;


    // 암시적인텐트를 활용해서 증거사진 찍어서 미리보기 파일로 보여주기..
    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {

                        Intent intent = result.getData();
                        Bundle bundle = intent.getExtras();

                        Bitmap contentImageBitMap = (Bitmap) bundle.get("data");
                        contentImage.setImageBitmap(contentImageBitMap);
                    }
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        //화면 레이아웃에서 불러서 생성
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_plus);

        //  Spinner 관련 코드
        Spinner spinner = findViewById(R.id.concentrate_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //XML에서 추가한 id를 통해 각종 변수들 선언하는 곳
        contentImage = findViewById(R.id.content_image);
        recordPlusButton = findViewById(R.id.record_plus_button);
        imagePlusButton = findViewById(R.id.image_plus_button);
        startTime = findViewById(R.id.start_time);
        finishTime = findViewById(R.id.finish_time);
        actContent = findViewById(R.id.act_content);


        // 생성될 때 종료시간을 현재 시간으로 세팅해두기


        //기존에 있던 정보들 불러오기
        //SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //String text = sf.getString("startTime","");
        //startTime.setText(text);


        //  추가하기 버튼 눌렀을 때, 데이터 정보 옮기는 코드
        recordPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //xml에 존재하는 EditText 들로 부터 정보를 각 변수에 넣기

                String TimeS = startTime.getText().toString();
                String TimeF = finishTime.getText().toString();
                String actC = actContent.getText().toString();
                String concentrate = spinner.getSelectedItem().toString();

                //기존에 쓴 내용 저장하기  -> (실험용) 나쁘진 않다. 나중에 쓸만한 기능
                /*SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("startTime", TimeS);
                editor.apply();*/

                //정보를 변수에 넣고 나머지는 번들에 넣기.
                Bundle bundle = new Bundle();
                bundle.putString("startTime", TimeS);
                bundle.putString("finishTime", TimeF);
                bundle.putString("actContent", actC);
                bundle.putString("concentrate", concentrate);


                //contentimage에 있는 사진 정보를 drawble로 바꾸고 -> drawable 파일을 비트맵으로 만든후 -> 바이트로 만든다.
                try {
                    BitmapDrawable drawable = (BitmapDrawable) contentImage.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    bundle.putByteArray("image", byteArray);
                } catch (Exception e) {
                    System.out.println("사진 안찍어서 오류");
                }

                //인텐트에 번들을 넣고 전송
                Intent i = new Intent(RecordPlusActivity.this, RecordActivity.class);

                boolean change = true;

                //시간 비교하고 , 이걸 text에 있는걸 Int 로 변환하는 것보다 변수하나 만들어서 시간 저장할 때 체크하는게 더 나은 방법인거 같기도...
                if (Integer.parseInt((startTime.getText().toString().substring(0, 2))) > Integer.parseInt((finishTime.getText().toString().substring(0, 2)))) {
                    Toast.makeText(RecordPlusActivity.this, "시작시간 종료시간 재설정 하세요.", Toast.LENGTH_SHORT).show();
                    change = false;
                } else if (Integer.parseInt((startTime.getText().toString().substring(0, 2))) == Integer.parseInt((finishTime.getText().toString().substring(0, 2)))) {

                    if (Integer.parseInt((startTime.getText().toString().substring(3, 5))) == Integer.parseInt((finishTime.getText().toString().substring(3, 5)))) {
                        Toast.makeText(RecordPlusActivity.this, "시작시간 종료시간 재설정 하세요.", Toast.LENGTH_SHORT).show();
                        change = false;
                    }
                }
                if (change == true) {
                    i.putExtras(bundle);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }

                //Log.e(TAG, "일정을 추가하고 정상 종료합니다");

            }
        });

        //사진 찍고 저장하는 코드 암시적 인텐트 사용
        imagePlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OnCheckPermission();

                if (ActivityCompat.checkSelfPermission(RecordPlusActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    resultLauncher.launch(i);
                } else {
                    Toast.makeText(RecordPlusActivity.this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        //시작시간 입력할 때, TimePickerDialog 사용하기
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //타임피커 생성해보자 OnTimeSet 함수오버라이드하면서
                // 이전에 사용했던 타임피커 방법
                /*timePickerDialogEx = TimepickerSet(timePickerDialogEx, startTime);

                timePickerDialogEx.setTitle("시작시간을 입력하세요");
                timePickerDialogEx.show();
                timePickerDialogEx.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                //화면 띄우고 난후,
              timePickerDialogEx.getButton(TimePickerDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        timePickerDialogEx.dismiss();

                        String checkTime=startTime.getText().toString();
                        Log.d("123","my :"+startTime.getText());
                        String checkHour=checkTime.substring(0,checkTime.indexOf(":"));
                        String checkMinute=checkTime.substring(3,5);
                        Log.d(TAG, "my message__ checkHour : "+checkHour+" checkMinute : "+checkMinute);

                        Toast.makeText(RecordPlusActivity.this, "안꺼짐 확인", Toast.LENGTH_SHORT).show();
                        timePickerDialogEx.show();
                    }
                });*/

                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(RecordPlusActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        startCheck = true;

                        // 현재시간보다 늦은 시간은 선택 못하게 하기위해 검사
                        if (selectedHour > hour) {
                            startCheck = false;
                        } else if (selectedHour == hour) {
                            if (selectedMinute > minute) {
                                startCheck = false;
                            }
                        }
                        //시간 분 이 한자리 숫자일 때 ,0 추가해주는 코드
                        if (startCheck) {


                            if (selectedHour < 10 && selectedMinute < 10) {
                                startTime.setText("0" + selectedHour + ":0" + +selectedMinute);
                            } else if (selectedHour < 10 && selectedMinute >= 10) {
                                startTime.setText("0" + selectedHour + ":" + +selectedMinute);
                            } else if (selectedHour >= 10 && selectedMinute < 10) {
                                startTime.setText(+selectedHour + ":0" + +selectedMinute);
                            } else {
                                startTime.setText(selectedHour + ":" + selectedMinute);
                            }

                        } else {
                            Toast.makeText(RecordPlusActivity.this, "시작시간을 재설정 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, hour, minute, true);
                //여기까지 타임피커 다이얼로그 설정 + 시간 선택하면 + String 타입으로 변환

                timePickerDialog.setTitle("시작시간을 입력하세요");
                timePickerDialog.show();
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            }
        });

        //종료시간 입력할 때, TimePickerDialog 사용하기
        finishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //타임피커 생성해보자 OnTimeSet 함수오버라이드하면서  //이전에 사용했던 타임피커 방법
                /*
                timePickerDialogEx = TimepickerSet(timePickerDialogEx, finishTime);
                timePickerDialogEx.setTitle("종료시간을 입력하세요");
                timePickerDialogEx.show();
                timePickerDialogEx.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                */

                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(RecordPlusActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        finishCheck = true;

                        // 현재시간보다 늦은 시간은 선택 못하게 하기위해 검사
                        if (selectedHour > hour) {
                            finishCheck = false;
                        } else if (selectedHour == hour) {
                            if (selectedMinute > minute) {
                                finishCheck = false;
                            }
                        }

                        //시간 분 이 한자리 숫자일 때 ,0 추가해주는 코드
                        if (finishCheck) {
                            if (selectedHour < 10 && selectedMinute < 10) {
                                finishTime.setText("0" + selectedHour + ":0" + +selectedMinute);
                            } else if (selectedHour < 10 && selectedMinute >= 10) {
                                finishTime.setText("0" + selectedHour + ":" + +selectedMinute);
                            } else if (selectedHour >= 10 && selectedMinute < 10) {
                                finishTime.setText(+selectedHour + ":0" + +selectedMinute);
                            } else {
                                finishTime.setText(selectedHour + ":" + selectedMinute);
                            }
                        } else {
                            Toast.makeText(RecordPlusActivity.this, "종료시간을 재설정 주세요.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, hour, minute, true);
                //여기까지 타임피커 다이얼로그 설정 + 시간 선택하면 + String 타입으로 변환

                timePickerDialog.setTitle("종료시간을 입력하세요");
                timePickerDialog.show();
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.e(TAG, "my message : 액티비티 onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.e(TAG, "my message : 액티비티 onStop");
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "my message : 액티비티 onDestroy");
        super.onDestroy();
    }


    //TimePickerDialog 선언하고, (바꾸고싶은 텍스트view), 인자로 보내면서 변화시키는 것 까지 함수 오버라이드 한 메소드
    public TimePickerDialog TimepickerSet(TimePickerDialog timePickerDialog, TextView selectedTextView) {
        /*
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        int changedHour, changedMinute;

        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {

            }
        };

        timePickerDialog = new TimePickerDialog(RecordPlusActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, listener, hour, minute, true);


        timePickerDialog = new TimePickerDialog(RecordPlusActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {


                if (selectedMinute < 10) {
                    selectedTextView.setText(selectedHour + ":0" + selectedMinute);
                } else {
                    selectedTextView.setText(selectedHour + ":" + selectedMinute);
                }

            }
        }, hour, minute, true);

        */
        return timePickerDialog;
    }


    //권한과 관련된 메소드
    public void OnCheckPermission() {
        if (ActivityCompat.checkSelfPermission(RecordPlusActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RecordPlusActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST);
             /*
             if (ActivityCompat.shouldShowRequestPermissionRationale(RecordPlusActivity.this, Manifest.permission.CAMERA)) {
                //권한 처음 설정할 때
                Log.e(TAG, "OncheckPermission: 사진찍기위해서는 권한을 설정해야합니다.");
                Toast.makeText(this, "사진을 찍기 위해서는 권한을 설정해야 합니다.", Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(RecordPlusActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST);

            } else {
                ActivityCompat.requestPermissions(RecordPlusActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST);
            }
            */
        }
    }

    //권한과 관련된 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "앱 실행을 위한 권한이 설정 되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "앱 실행을 위한 권한이 취소 되었습니다", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
}