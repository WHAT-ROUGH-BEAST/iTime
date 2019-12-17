package com.example.itime.data;

import com.example.itime.data.model.MainItem;

import java.util.ArrayList;

public class KeepData {
    private ArrayList<MainItem> mainItems;

    public KeepData(ArrayList<MainItem> mainItems){
        this.mainItems = mainItems;
    }

    public void setMainItems(ArrayList<MainItem> mainItems){
        this.mainItems = mainItems;
    }

    public ArrayList<MainItem> getMainItems(){
        return mainItems;
    }

    public void updateTime(){
        for (MainItem m : mainItems){
            m.setLeftTime(m.getLeftTime()-1);
        }
    }
}
