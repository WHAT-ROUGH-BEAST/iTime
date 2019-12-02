package com.example.mytime.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mytime.R;
import com.example.mytime.data.model.MainItem;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<Integer> resourceId;
    private MutableLiveData<ArrayList<MainItem>> mainItems;

    public HomeViewModel() {
        resourceId = new MutableLiveData<>();
        mainItems = new MutableLiveData<>();
//        defaultData();
    }

    public LiveData<Integer> getResource() {
        return resourceId;
    }
    public LiveData<ArrayList<MainItem>> getMainItems() {
        return mainItems;
    }
    public void setResourceId(int id){
        resourceId.setValue(id);
    }
    public void setMainItems(ArrayList<MainItem> m){
        mainItems.setValue(m);
    }

//    private void defaultData(){
//        resourceId.setValue(R.drawable.default_img);
//
//        ArrayList<MainItem> m = mainItems.getValue();
//        assert m != null;
//        m.add(new MainItem(R.drawable.default_img,
//                "title", "date", "tip"));
//        mainItems.setValue(mainItems.getValue());
//    }
}