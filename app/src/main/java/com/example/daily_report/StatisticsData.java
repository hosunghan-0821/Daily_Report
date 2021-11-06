package com.example.daily_report;

public class StatisticsData  {
    private String content,contentTime;

    public StatisticsData(String content, String contentTime) {
        this.content = content;
        this.contentTime = contentTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentTime() {
        return contentTime;
    }

    public void setContentTime(String contentTime) {
        this.contentTime = contentTime;
    }



}
