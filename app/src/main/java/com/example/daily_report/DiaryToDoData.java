package com.example.daily_report;

public class DiaryToDoData {

    private String toDoListContent,toDoListNumber;
    private boolean checkBox,alarm;
    private int serialNumber;



    public DiaryToDoData(String toDoListContent, boolean checkBox, boolean alarm) {
        this.toDoListContent = toDoListContent;
        this.checkBox = checkBox;
        this.alarm = alarm;
    }

    public DiaryToDoData(String toDoListContent, boolean checkBox, boolean alarm, int serialNumber) {
        this.toDoListContent = toDoListContent;
        this.checkBox = checkBox;
        this.alarm = alarm;
        this.serialNumber = serialNumber;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public DiaryToDoData(String toDoListContent) {
        this.toDoListContent = toDoListContent;
    }

    public String getToDoListContent() {
        return toDoListContent;
    }

    public void setToDoListContent(String toDoListContent) {
        this.toDoListContent = toDoListContent;
    }

    public String getToDoListNumber() {
        return toDoListNumber;
    }

    public void setToDoListNumber(String toDoListNumber) {
        this.toDoListNumber = toDoListNumber;
    }

    public boolean isCheckBox() {
        return checkBox;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }
}
