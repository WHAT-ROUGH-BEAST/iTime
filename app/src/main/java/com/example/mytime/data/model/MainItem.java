package com.example.mytime.data.model;

public class MainItem {
    private String title, date, tip, textOnImg;
    private int imgId;

    public MainItem(int imgId, String title, String date, String tip){
        this.imgId = imgId;
        this.title = title;
        this.tip = tip;
        this.date = date;
        textOnImg = date;//后期修改为剩余时间
    }

    public int getImgId() {
        return imgId;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getTip() {
        return tip;
    }

    public String getTextOnImg() { return textOnImg; }
}
