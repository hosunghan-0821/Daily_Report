package com.example.daily_report;

import android.graphics.Bitmap;

import java.io.Serializable;

public class RecordData  {

    private int recordImage;
    private String startTime,finishTime,actContent,concentrate;
    private Bitmap bitmapImage;

    public RecordData(){}
    public RecordData(Bitmap image,String startTime, String finishTime, String actContent, String concentrate){

        this.bitmapImage=image;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.actContent = actContent;
        this.concentrate = concentrate;
    }
    public RecordData(int recordImage, String startTime, String finishTime, String actContent, String concentrate) {
        this.recordImage = recordImage;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.actContent = actContent;
        this.concentrate = concentrate;
    }

    public int getRecordImage() {
        return recordImage;
    }

    public void setRecordImage(int recordImage) {
        this.recordImage = recordImage;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }

    public String getStartTime() {
        return startTime;
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
}
