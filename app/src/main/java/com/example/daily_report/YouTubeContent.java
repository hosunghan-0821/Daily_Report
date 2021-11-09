package com.example.daily_report;

public class YouTubeContent {
    private String VideoId;
    private String youtubeName;
    private String date;
    private String channelId;
    private String likeNumber;
    private String viewCount;
    private String imgUrl;

    public YouTubeContent(String videoId) {
        VideoId = videoId;
    }

    public YouTubeContent(String videoId, String youtubeName, String date) {
        VideoId = videoId;
        this.youtubeName = youtubeName;
        this.date = date;
    }

    public YouTubeContent(String videoId, String youtubeName, String date, String channelId, String likeNumber) {
        VideoId = videoId;
        this.youtubeName = youtubeName;
        this.date = date;
        this.channelId = channelId;
        this.likeNumber = likeNumber;
    }

    public YouTubeContent(String videoId, String youtubeName, String date, String channelId, String likeNumber, String imgUrl) {
        VideoId = videoId;
        this.youtubeName = youtubeName;
        this.date = date;
        this.channelId = channelId;
        this.likeNumber = likeNumber;
        this.imgUrl = imgUrl;
    }

    public YouTubeContent(String videoId, String youtubeName, String date, String channelId, String likeNumber, String viewCount, String imgUrl) {
        VideoId = videoId;
        this.youtubeName = youtubeName;
        this.date = date;
        this.channelId = channelId;
        this.likeNumber = likeNumber;
        this.viewCount = viewCount;
        this.imgUrl = imgUrl;
    }

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    public String getYoutubeName() {
        return youtubeName;
    }

    public void setYoutubeName(String youtubeName) {
        this.youtubeName = youtubeName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(String likeNumber) {
        this.likeNumber = likeNumber;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }
}
