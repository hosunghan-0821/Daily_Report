package com.example.daily_report;

import android.graphics.Bitmap;

import java.io.Serializable;

public class RecordData  {


    private String startTime,finishTime,actContent,concentrate,fileName;
    private int hour,minute;


    public RecordData(){}
    public RecordData(String fileName,String startTime, String finishTime, String actContent, String concentrate){

        this.fileName=fileName;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.actContent = actContent;
        this.concentrate = concentrate;

    }

    public String getStartTime() {
        return startTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getActContent() {
        return actContent;
    }

    public void setActContent(String actContent) {
        this.actContent = actContent;
    }

    public String getConcentrate() {
        return concentrate;
    }

    public void setConcentrate(String concentrate) {
        this.concentrate = concentrate;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

}
