package com.example.mytime.ui.home;

import android.os.Bundle;
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
import com.example.mytime.data.MainItemAdapter;
import com.example.mytime.data.model.MainItem;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ListView ItemList;
    private ImageView imageView;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //init
        ArrayList<MainItem> mainItems = new ArrayList<>();
        mainItems.add(new MainItem(R.drawable.default_img, "title", "date", "tip"));
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
        MainItemAdapter mainItemAdapter = new MainItemAdapter(getContext(), R.layout.item_main,
                homeViewModel.getMainItems().getValue());
        ItemList.setAdapter(mainItemAdapter);

        return view;
    }

    private void initModel(int id, ArrayList<MainItem> m){
        homeViewModel.setMainItems(m);
        homeViewModel.setResourceId(id);
    }
}