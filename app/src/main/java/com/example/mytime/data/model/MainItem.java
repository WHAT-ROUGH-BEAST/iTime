package com.example.mytime.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MainItem implements Serializable {
    private String title, date, tip, textOnImg;
    private int imgId;
//    private ArrayList<String> label;
//    private String repeat;

    public MainItem(int imgId, String title, String tip, String date){
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
