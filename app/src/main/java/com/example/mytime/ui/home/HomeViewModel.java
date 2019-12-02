package com.example.mytime.ui.home;

import com.example.mytime.data.model.MainItem;

import java.util.ArrayList;

public class HomeViewModel {

    private int resourceId;
    private ArrayList<MainItem> mainItems;

    public HomeViewModel() {
        mainItems = new ArrayList<>();
    }

    public int getResource() {
        return resourceId;
    }
    public ArrayList<MainItem> getMainItems() {
        return mainItems;
    }
    public void setResourceId(int id){
        this.resourceId = id;
    }
    public void setMainItems(ArrayList<MainItem> m){
        this.mainItems = m;
    }
}