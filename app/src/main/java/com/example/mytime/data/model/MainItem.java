package com.example.mytime.data.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class MainItem implements Serializable {
    private String title, date, tip, textOnImg;
    private int imgId;
    private ArrayList<String> label;
    private String repeat;
    private long leftTime;

    public MainItem(int imgId, String title, String tip, String date, ArrayList<String> label, String repeat, String textOnImg){
        this.imgId = imgId;
        this.title = title;
        this.tip = tip;
        this.date = date;
//        textOnImg = date;//后期修改为剩余时间
        this.textOnImg = textOnImg;
        this.label = label;
        this.repeat = repeat;
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

    public ArrayList<String> getLabel(){ return label; };

    public String getRepeat(){ return repeat; }

    public long getLeftTime(){
        return leftTime;
    }

    public void setLeftTime(long time){
        leftTime = time;
    }

    public void setTextOnImg(String s){
        textOnImg = s;
    }
}
