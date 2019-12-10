package com.example.mytime.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mytime.R;
import com.example.mytime.data.ItemPack;
import com.example.mytime.data.MainItemAdapter;
import com.example.mytime.data.model.MainItem;
import com.example.mytime.ui.ItemDetailActivity;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private static final int ITEM_DETAIL = 100;
    private static final int ITEM_DETAIL_DEL = 101;
    private static final int ITEM_DETAIL_CHANGE = 102;
    private static final int ITEM_DETAIL_BACK = 103;

    private HomeViewModel homeViewModel;
    private ListView ItemList;
    private ImageView imageView;
    private ArrayList<MainItem> mainItems;
    private TextView title_home;
    private TextView date_home;
    private TextView downcount_home;
    private Handler Updatehandler = new Handler();
    private Runnable update_thread;
    private Handler handlerStop;
    private Intent intentItemDetailAc;

    //ItemDetailAc的回调函数
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = new HomeViewModel();
        //init
        readFromMain();
        setModel(R.drawable.default_img, mainItems);

        //img
        imageView = view.findViewById(R.id.img_home);
        imageView.setImageResource(homeViewModel.getResource());

        //texts on img
        title_home = (TextView)view.findViewById(R.id.title_home);
        date_home = (TextView)view.findViewById(R.id.date_home);
        downcount_home = (TextView)view.findViewById(R.id.downcount_home);

        title_home.setText(mainItems.get(mainItems.size()-1).getTitle());
        date_home.setText(mainItems.get(mainItems.size()-1).getDate());
            //downcount_home更新线程
        update_thread = new RunUpdate();
//        detail_thread = new
        handlerStop = new HandlerStop();
        update_thread.run();

        //list
        ItemList = (ListView) view.findViewById(R.id.list_home);
        MainItemAdapter mainItemAdapter = new MainItemAdapter(getContext(), R.layout.item_main,
                homeViewModel.getMainItems());
        ItemList.setAdapter(mainItemAdapter);
        ItemList.setOnItemClickListener(new MainItemListener());
        intentItemDetailAc = new Intent(getContext(), ItemDetailActivity.class);

        //return
        return view;
    }//: end of onCreatView

    @Override
    public void onDestroy() {
        //发送消息，结束子线程
        Message message = new Message();
        message.what = 1;
        handlerStop.sendMessage(message);
        super.onDestroy();
    }//: end of onDestroy

    private void setModel(int id, ArrayList<MainItem> m){
        homeViewModel.setMainItems(m);
        homeViewModel.setResourceId(id);
    }

    private void readFromMain(){
        if (isAdded()){
            //读数据
            try{
                assert getArguments() != null;
                ItemPack itemPack = (ItemPack) getArguments().getSerializable("items");
                assert itemPack != null;
                mainItems = itemPack.getMainItems();
                Log.d("itemPack", "get fine Serializable " + mainItems.size());
            } catch (Exception e){
                //default
                mainItems = new ArrayList<>();
                Log.d("getArguments", e.getMessage());
                Log.d("itemPack", e.getMessage());
            }
        }
    }

    //TODO
    class MainItemListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //新的activity的回调函数在mainactivity以及此fragment中都能得到
            intentItemDetailAc.putExtra("mainItem", (Serializable) mainItems.get(i));
            intentItemDetailAc.putExtra("index", i);
            startActivityForResult(intentItemDetailAc, ITEM_DETAIL);

        }
    }

    //downcount
    class RunUpdate implements Runnable {
        @Override
        public void run() {
            readFromMain();
            long leftTime = mainItems.get(mainItems.size()-1).getLeftTime();
            //倒计时效果展示
            String formatLongToTimeStr = formatLongToTimeStr(leftTime);
            downcount_home.setText(formatLongToTimeStr);

            //半秒执行一次
            Updatehandler.postDelayed(this, 500);
        }

        public String formatLongToTimeStr(Long date) {
            long day, hour, min, s;
            String strtime;
            if (date > 0){
                day = date / (60 * 60 * 24);
                hour = (date / (60 * 60) - day * 24);
                min = ((date / 60) - day * 24 * 60 - hour * 60);
                s = (date - day*24*60*60 - hour*60*60 - min*60);
                strtime = "剩余"+day+"天"+hour+"小时"+min+"分"+s+"秒";
            }else{
                date = -date;
                day = date / (60 * 60 * 24);
                hour = (date / (60 * 60) - day * 24);
                min = ((date / 60) - day * 24 * 60 - hour * 60);
                s = (date - day*24*60*60 - hour*60*60 - min*60);
                strtime = "已过"+day+"天"+hour+"小时"+min+"分"+s+"秒";
            }

            return strtime;
        }
    }
    //handler stop
    class HandlerStop extends Handler{
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Updatehandler.removeCallbacks(update_thread);
            } else {
                throw new IllegalStateException("Unexpected value: " + msg.what);
            }
            super.handleMessage(msg);
        }
    }
}