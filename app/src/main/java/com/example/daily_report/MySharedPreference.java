package com.example.daily_report;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MySharedPreference {


    //MySharedPreference 라는 Class를 따로 만듦으로써, 재사용을 가능하게 하려고 함수를 만들었다.
    public MySharedPreference() {
    }

    //context(참조용)와 파일이름을 갖고, Sharedpreference를 활용할 수 있는 변수를 만들 때, 사용한다.
    //ex) SharedPreFerences sharedpreferences = MySharedPreFerence.getPreferecnes(context, fileName); 이런식으로
    //static으로 만든건 나쁘지 않은 선택인거 같은데, 다른 함수들까지 static으로 만들었어야 했나?에 대해서는 살짝 의문이 든다.. 비효율적 코딩인듯;

    public static SharedPreferences getPreferences(Context context, String fileName) {

        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    //RecordArrayList를 SharedPreference에 Gson을 활용하여 Gson 의 toJson이라는 함수를 활용해서 객체 -> String 화 시킨다.
    //그 후 SharedPreference 에 key value(객체-> 변환(Gson의 toJosn 함수 사용하여) ->  string 만든 것을 넣는다)
    //이 아래 코드가 위 설명에 해당한다. ** static을 선택한 것은 현명하지 못한것 같다. 인스턴스를 만들고 그 인스턴스에 context나 fileName을 생성자로 넣어서 사용하는 것이 더 좋았을 수도
    //ArrayList<?> 이걸 사용해서 어떤 타입이 오는지 변환이 되는 문법이 존재하고, 실제로 사용 할 수 있다면, set,get ArrayList 두 개의 함수로 모든 상황 커버 가능한대.. 문법찾아봐도 찾지 못했다.

    public static void setRecordArrayList(Context context, String fileName, ArrayList<RecordData> arrayList) {


        SharedPreferences sharedPreferences = getPreferences(context, fileName);            //제일 위에 정의한 getPreference 함수 사용하여, context가져오기
        Gson gson = new GsonBuilder().create();                                             //객체 -> String 바꿔주기 위한 Gson 선언.

        Type arraylistType = new TypeToken<ArrayList<RecordData>>() {                       // 내가 변환한 객체의 type을 얻어내는 코드 Type 와 TypeToken .getType() 메소드를 사용한다.
        }.getType();
        String objectToString = gson.toJson(arrayList, arraylistType);                      //이제 객체를 -> String 으로 바꿔보자, 바꿀 떄는 Gson instance의 toJson 함수사용
        //객체 변수, 객체 타입 (타입토큰으로부터)

        SharedPreferences.Editor editor = sharedPreferences.edit();                         //editor라는 것을 활용하여서, SharedPreferecnes 파일에 기입 할 수 있다.
        editor.putString("recordRecyclerView", objectToString);                          //이제 gson으로 변환된 object to string 을 value로 넣어준다
        editor.apply();                                                                     //editor.apply or commit()
    }


    //위에서 SharedPreference에 저장한 String을 이제 다시 object로 바꿔서 사용자가 원하는대로 쓸 수 있게 해주는 작업이 필요하다.
    //여기서는 그작업이 진행될 것이다.

    public static ArrayList<RecordData> getRecordArrayList(Context context, String fileName) {

        ArrayList<RecordData> savedList;                                                        // String에서 Object로 변환될 변수 선언
        savedList = new ArrayList<RecordData>();
        Gson gson = new GsonBuilder().create();                                                 //String -> 변환(Gson의 .fromJson 함수 사용) -> 객체 하기위해 선언

        SharedPreferences sharedPreferences = getPreferences(context, fileName);                //사용할 sharedPreference 선언
        String stringToObject = sharedPreferences.getString("recordRecyclerView", "");    //Shared 에 저장되어 있는 ,value (String)을 얻어오는과정
        Type arraylistType = new TypeToken<ArrayList<RecordData>>() {                           //Type, TypeToken을 이용하여서 변환시킨 객체 타입을 얻어낸다.
        }.getType();

        try {
            savedList = gson.fromJson(stringToObject, arraylistType);                           //gson을 이용하여서, 저장된 String을 객체로 변환
            Log.e("123", "tryCatch 중 try : " + savedList);
            if (savedList == null) {
                savedList = new ArrayList<RecordData>();                                        //key 값이 없을 때, savedList에 ==null이 들어가있을수 있기떄문에 예외처리
            }
            return savedList;                                                                   //저장된 savedList 반환
        } catch (Exception e) {
            e.printStackTrace();

            return savedList;
        }
    }

    public static void setWeekRecordArrayList(Context context, String fileName,ArrayList<RecordData> arrayList,String key){

        SharedPreferences sharedPreferences = getPreferences(context, fileName);            //제일 위에 정의한 getPreference 함수 사용하여, context가져오기
        Gson gson = new GsonBuilder().create();                                             //객체 -> String 바꿔주기 위한 Gson 선언.

        Type arraylistType = new TypeToken<ArrayList<RecordData>>() {                       // 내가 변환한 객체의 type을 얻어내는 코드 Type 와 TypeToken .getType() 메소드를 사용한다.
        }.getType();
        String objectToString = gson.toJson(arrayList, arraylistType);                      //이제 객체를 -> String 으로 바꿔보자, 바꿀 떄는 Gson instance의 toJson 함수사용
        //객체 변수, 객체 타입 (타입토큰으로부터)

        SharedPreferences.Editor editor = sharedPreferences.edit();                         //editor라는 것을 활용하여서, SharedPreferecnes 파일에 기입 할 수 있다.
        editor.putString(key, objectToString);                          //이제 gson으로 변환된 object to string 을 value로 넣어준다
        editor.apply();


    }
    public static ArrayList<RecordData> getWeekRecordArrayList(Context context, String fileName,String key){

        ArrayList<RecordData> savedList;                                                        // String에서 Object로 변환될 변수 선언
        savedList = new ArrayList<RecordData>();
        Gson gson = new GsonBuilder().create();                                                 //String -> 변환(Gson의 .fromJson 함수 사용) -> 객체 하기위해 선언


        SharedPreferences sharedPreferences = getPreferences(context, fileName);                //사용할 sharedPreference 선언
        String stringToObject = sharedPreferences.getString(key, "");    //Shared 에 저장되어 있는 ,value (String)을 얻어오는과정
        Type arraylistType = new TypeToken<ArrayList<RecordData>>() {                           //Type, TypeToken을 이용하여서 변환시킨 객체 타입을 얻어낸다.
        }.getType();

        try {
            savedList = gson.fromJson(stringToObject, arraylistType);                           //gson을 이용하여서, 저장된 String을 객체로 변환
            Log.e("123", "tryCatch 중 try : " + savedList);
            if (savedList == null) {
                savedList = new ArrayList<RecordData>();                                        //key 값이 없을 때, savedList에 ==null이 들어가있을수 있기떄문에 예외처리
            }
            return savedList;                                                                   //저장된 savedList 반환
        } catch (Exception e) {
            e.printStackTrace();

            return savedList;
        }

    }

    //아래 4개의 함수들은 위와 완전 똑같은 패턴이다.. 이걸 2개의 함수로 줄일 수 있는 방법(문법)이 있는지 알아보자
    //ArrayList<?>와 관련이 있을거 같은데.. 시간이 부족하다 찾아볼

    public static void setToDoList(Context context, String fileName, ArrayList<DiaryToDoData> arrayList) {

        SharedPreferences sharedPreferences = getPreferences(context, fileName);
        Gson gson = new GsonBuilder().create();

        Type arraylistType = new TypeToken<ArrayList<DiaryToDoData>>() {
        }.getType();
        String objectToString = gson.toJson(arrayList, arraylistType);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("toDoListRecyclerView", objectToString);
        editor.apply();
    }

    public static ArrayList<DiaryToDoData> getToDoListArrayList(Context context, String fileName) {

        ArrayList<DiaryToDoData> savedList;
        savedList = new ArrayList<DiaryToDoData>();
        Gson gson = new GsonBuilder().create();

        SharedPreferences sharedPreferences = getPreferences(context, fileName);
        String stringToObject = sharedPreferences.getString("toDoListRecyclerView", "");
        Type arraylistType = new TypeToken<ArrayList<DiaryToDoData>>() {
        }.getType();

        try {
            savedList = gson.fromJson(stringToObject, arraylistType);
            Log.e("123", "tryCatch 중 try : " + savedList);
            if (savedList == null) {
                savedList = new ArrayList<DiaryToDoData>();
            }
            return savedList;
        } catch (Exception e) {
            e.printStackTrace();

            return savedList;
        }
    }

    public static void setActContentArrayList(Context context, String fileName, ArrayList<RecordPlusActivityActData> arrayList) {
        SharedPreferences sharedPreferences = MySharedPreference.getPreferences(context, fileName);
        Gson gson = new Gson();
        Type arrayListType = new TypeToken<ArrayList<RecordPlusActivityActData>>() {
        }.getType();
        String stringToJson = gson.toJson(arrayList, arrayListType);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("actContent", stringToJson);
        editor.apply();
    }

    public static ArrayList<RecordPlusActivityActData> getActContentArrayList(Context context, String fileName) {

        ArrayList<RecordPlusActivityActData> savedList = new ArrayList<RecordPlusActivityActData>();
        Gson gson = new GsonBuilder().create();

        SharedPreferences sharedPreferences = getPreferences(context, fileName);
        String stringToObject = sharedPreferences.getString("actContent", null);
        Type arrayListType = new TypeToken<ArrayList<RecordPlusActivityActData>>() {
        }.getType();
        try {
            savedList = gson.fromJson(stringToObject, arrayListType);

            if (savedList == null) {
                savedList = new ArrayList<RecordPlusActivityActData>();
            }
            return savedList;
        } catch (Exception e) {
            e.printStackTrace();
            return savedList;
        }

    }

    public static void setRoutineArrayList(Context context, String fileName, ArrayList<DailyRoutineData> arrayList, String mode) {


        SharedPreferences sharedPreferences = getPreferences(context, fileName);
        Gson gson = new GsonBuilder().create();

        Type arraylistType = new TypeToken<ArrayList<DailyRoutineData>>() {
        }.getType();
        String objectToString = gson.toJson(arrayList, arraylistType);
        //객체 변수, 객체 타입 (타입토큰으로부터)

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (mode.equals("전체루틴")) {
            editor.putString("전체루틴", objectToString);
        } else if(mode.equals("아침루틴")){
            editor.putString("routineMorningRecyclerView", objectToString);
        }else{
            editor.putString("routineEveningRecyclerView",objectToString);
        }

        editor.apply();
    }


    //위에서 SharedPreference에 저장한 String을 이제 다시 object로 바꿔서 사용자가 원하는대로 쓸 수 있게 해주는 작업이 필요하다.
    //여기서는 그작업이 진행될 것이다.

    public static ArrayList<DailyRoutineData> getRoutineArrayList(Context context, String fileName, String mode) {

        ArrayList<DailyRoutineData> savedList;
        savedList = new ArrayList<DailyRoutineData>();
        Gson gson = new GsonBuilder().create();
        String stringToObject;


        SharedPreferences sharedPreferences = getPreferences(context, fileName);
        if (mode.equals("전체루틴")) {
            stringToObject = sharedPreferences.getString("전체루틴", "");
        } else if (mode.equals("아침루틴")){
            stringToObject = sharedPreferences.getString("routineMorningRecyclerView", "");
        }else{
            stringToObject = sharedPreferences.getString("routineEveningRecyclerView", "");
        }

        Type arraylistType = new TypeToken<ArrayList<DailyRoutineData>>() {
        }.getType();

        try {

            savedList = gson.fromJson(stringToObject, arraylistType);

            if (savedList == null) {
                savedList = new ArrayList<DailyRoutineData>();
            }
            return savedList;
        } catch (Exception e) {
            e.printStackTrace();

            return savedList;
        }
    }


}
