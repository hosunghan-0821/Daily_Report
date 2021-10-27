package com.example.daily_report;

public class DailyRoutineData {


    private String routineTime,routineContent,routineType,routineRepeat;
    private boolean checkBox;
    private int  serialNumber;


    public DailyRoutineData(String routineTime, String routineContent, String routineType, String routineRepeat, boolean checkBox) {
        this.routineTime = routineTime;
        this.routineContent = routineContent;
        this.routineType = routineType;
        this.routineRepeat = routineRepeat;
        this.checkBox = checkBox;
    }

    public DailyRoutineData(String routineTime, String routineContent, String routineType, String routineRepeat, boolean checkBox, int serialNumber) {
        this.routineTime = routineTime;
        this.routineContent = routineContent;
        this.routineType = routineType;
        this.routineRepeat = routineRepeat;
        this.checkBox = checkBox;
        this.serialNumber = serialNumber;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getRoutineType() {
        return routineType;
    }

    public void setRoutineType(String routineType) {
        this.routineType = routineType;
    }

    public String getRoutineRepeat() {
        return routineRepeat;
    }

    public void setRoutineRepeat(String routineRepeat) {
        this.routineRepeat = routineRepeat;
    }

    public String getRoutineTime() {
        return routineTime;
    }

    public void setRoutineTime(String routineType) {
        this.routineTime = routineType;
    }

    public String getRoutineContent() {
        return routineContent;
    }

    public void setRoutineContent(String routineContent) {
        this.routineContent = routineContent;
    }

    public boolean isCheckBox() {
        return checkBox;
    }

    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }
}
