package com.example.mytime.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.mytime.R;
import com.example.mytime.data.MainItemAdapter;
import com.example.mytime.data.model.MainItem;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ArrayList<MainItem> mainItems;
    private ListView ItemList;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);

        //添加list失败？？？？？？？？？？？？？？？
//            //MainItem的ListView
//            mainItems.add(new MainItem(R.drawable.date, "title", "date", "tip"));//initMainItem
//
//            ItemList = (ListView)getView().findViewById(R.id.list_home);
//            MainItemAdapter mainItemAdapter = new MainItemAdapter(getContext(), R.layout.item_main, mainItems);
//            ItemList.setAdapter(mainItemAdapter);
//            //realtime update list

        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }
}