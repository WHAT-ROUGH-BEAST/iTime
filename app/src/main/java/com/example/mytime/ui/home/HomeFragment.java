package com.example.mytime.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mytime.MainActivity;
import com.example.mytime.R;
import com.example.mytime.data.ItemPack;
import com.example.mytime.data.MainItemAdapter;
import com.example.mytime.data.model.MainItem;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ListView ItemList;
    private ImageView imageView;
    private ArrayList<MainItem> mainItems;


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = new HomeViewModel();

        //init
        if (isAdded()){
            //读数据
            try{
                assert getArguments() != null;
                ItemPack itemPack = (ItemPack) getArguments().getSerializable("items");
                assert itemPack != null;
                mainItems = itemPack.getMainItems();
                Log.d("itemPack", "get fine Serializable " + mainItems.size());
            } catch (Exception e){
                defaultItem();
                Log.d("getArguments", e.getMessage());
                Log.d("itemPack", e.getMessage());
            }
        }
        initModel(R.drawable.default_img, mainItems);

        //img
        imageView = view.findViewById(R.id.img_home);
        imageView.setImageResource(homeViewModel.getResource());

        //list // TODO-->solved 在create界面返回之后更新fragment
        ItemList = (ListView) view.findViewById(R.id.list_home);
        MainItemAdapter mainItemAdapter = new MainItemAdapter(getContext(), R.layout.item_main,
                homeViewModel.getMainItems());
        ItemList.setAdapter(mainItemAdapter); //adapter == null

        //return
        return view;
    }


    private void initModel(int id, ArrayList<MainItem> m){
        homeViewModel.setMainItems(m);
        homeViewModel.setResourceId(id);
    }

    private void defaultItem(){
        mainItems = new ArrayList<>();
        mainItems.add(new MainItem(R.drawable.default_img, "title", "date", "tip"));
        mainItems.add(new MainItem(R.drawable.default_img, "title1", "date1", "tip1"));
    }
}