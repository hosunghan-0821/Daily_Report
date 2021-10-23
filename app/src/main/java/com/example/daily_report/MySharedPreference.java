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



    public MySharedPreference() {
    }

    public static SharedPreferences getPreferences(Context context, String fileName) {

        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public static void setRecordArrayList(Context context, String fileName, ArrayList<RecordData> arrayList) {


        SharedPreferences sharedPreferences = getPreferences(context, fileName);
        Gson gson = new GsonBuilder().create();

        Type arraylistType = new TypeToken<ArrayList<RecordData>>() {
        }.getType();
        String objectToString = gson.toJson(arrayList, arraylistType);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("recordRecyclerView", objectToString);
        editor.apply();
    }

    public static ArrayList<RecordData> getRecordArrayList(Context context, String fileName) {

        ArrayList<RecordData> savedList;
        savedList= new ArrayList<RecordData>();
        Gson gson = new GsonBuilder().create();

        SharedPreferences sharedPreferences = getPreferences(context, fileName);
        String stringToObject = sharedPreferences.getString("recordRecyclerView", "");
        Type arraylistType = new TypeToken<ArrayList<RecordData>>() {
        }.getType();

        try {
            savedList = gson.fromJson(stringToObject, arraylistType);
            Log.e("123","tryCatch 중 try : "+savedList);
            if(savedList==null){
                savedList=new ArrayList<RecordData>();
            }
            return savedList;
        } catch (Exception e) {
            e.printStackTrace();

            return savedList;
        }
    }

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
        savedList= new ArrayList<DiaryToDoData>();
        Gson gson = new GsonBuilder().create();

        SharedPreferences sharedPreferences = getPreferences(context, fileName);
        String stringToObject = sharedPreferences.getString("toDoListRecyclerView", "");
        Type arraylistType = new TypeToken<ArrayList<DiaryToDoData>>() {
        }.getType();

        try {
            savedList = gson.fromJson(stringToObject, arraylistType);
            Log.e("123","tryCatch 중 try : "+savedList);
            if(savedList==null){
                savedList=new ArrayList<DiaryToDoData>();
            }
            return savedList;
        } catch (Exception e) {
            e.printStackTrace();

            return savedList;
        }
    }

}
