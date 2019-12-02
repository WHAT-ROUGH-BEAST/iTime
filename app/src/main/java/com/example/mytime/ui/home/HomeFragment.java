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
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //init
        if (isAdded()){
            //读数据
            try{
                assert getArguments() != null;
                ItemPack itemPack = (ItemPack) getArguments().getSerializable("items");
                assert itemPack != null;
                mainItems = itemPack.getMainItems();//以后将其修改为从activity中读数据// TODO
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
        homeViewModel.getResource().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer id) {
                imageView.setImageResource(id);
            }
        });

        //list
        ItemList = (ListView) view.findViewById(R.id.list_home);
            //当主页面当前的fragment是本fragment时 没有点击侧边栏 却依然需要传数据
            //此时就需要默认初始化
            //这个逻辑不对 因为默认初始化也应该与刚修改的内容一致 在create完之后也应该传一次数据//TODO
        MainItemAdapter mainItemAdapter = new MainItemAdapter(getContext(), R.layout.item_main,
                homeViewModel.getMainItems().getValue());
        ItemList.setAdapter(mainItemAdapter); //adapter == null

        //return
        return view;
    }

    private void initModel(int id, ArrayList<MainItem> m){
        homeViewModel.setMainItems(m);
        homeViewModel.setResourceId(id);
    }

    private ArrayList<MainItem> defaultItem(){
        mainItems = new ArrayList<>();
        mainItems.add(new MainItem(R.drawable.default_img, "title", "date", "tip"));
        mainItems.add(new MainItem(R.drawable.default_img, "title1", "date1", "tip1"));
        return mainItems;
    }
}