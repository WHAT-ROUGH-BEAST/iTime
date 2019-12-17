package com.example.itime.data;

import com.example.itime.data.model.MainItem;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemPack implements Serializable {
    private static final long serialVersionUID = 7382351359868556980L;
    private ArrayList<MainItem> mainItems = new ArrayList<>();

    public void setMainItems(ArrayList<MainItem> mainItems){
        this.mainItems = mainItems;
    }

    public ArrayList<MainItem> getMainItems(){
        return mainItems;
    }
}
