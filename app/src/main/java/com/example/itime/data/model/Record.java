package com.example.itime.data.model;

public class Record {
    private String title, tip;
    private int imgId;

    public Record(int imgId, String title, String tip){
        this.imgId = imgId;
        this.title = title;
        this.tip = tip;
    }

    public String getTitle(){
        return title;
    }

    public String getTips(){
        return tip;
    }

    public int getImgId(){
        return imgId;
    }
}