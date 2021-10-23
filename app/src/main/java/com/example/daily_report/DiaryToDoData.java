package com.example.daily_report;

public class DiaryToDoData {

    private String toDoListContent,toDoListNumber;
    private boolean checkBox;

    public DiaryToDoData(String toDoListContent, boolean checkBox) {
        this.toDoListContent = toDoListContent;
        this.toDoListNumber = toDoListNumber;
        this.checkBox = checkBox;
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
}
