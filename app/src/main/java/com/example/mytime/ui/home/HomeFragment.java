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

    private HomeViewModel homeViewModel;
    private ListView ItemList;
    private ImageView imageView;
    private ArrayList<MainItem> mainItems;
    private TextView title_home;
    private TextView date_home;
    private TextView downcount_home;
    private Handler handler = new Handler();
    private long leftTime;//一分钟
    private Runnable update_thread;
    private Handler handlerStop;

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
                //default
                mainItems = new ArrayList<>();
                Log.d("getArguments", e.getMessage());
                Log.d("itemPack", e.getMessage());
            }
        }
        initModel(R.drawable.default_img, mainItems);

        //img
        imageView = view.findViewById(R.id.img_home);
        imageView.setImageResource(homeViewModel.getResource());

        //texts on img
        title_home = (TextView)view.findViewById(R.id.title_home);
        date_home = (TextView)view.findViewById(R.id.date_home);
        downcount_home = (TextView)view.findViewById(R.id.downcount_home);

        title_home.setText(mainItems.get(mainItems.size()-1).getTitle());
        date_home.setText(mainItems.get(mainItems.size()-1).getDate());
            //得到现在时间
        setLeftTime();
        update_thread = new RunUpdate();
        handlerStop = new HandlerStop();
        update_thread.run();

        //list
        ItemList = (ListView) view.findViewById(R.id.list_home);
        MainItemAdapter mainItemAdapter = new MainItemAdapter(getContext(), R.layout.item_main,
                homeViewModel.getMainItems());
        ItemList.setAdapter(mainItemAdapter);
        ItemList.setOnItemClickListener(new MainItemListener());

        //return
        return view;
    }//: end of onCreatView

    private void initModel(int id, ArrayList<MainItem> m){
        homeViewModel.setMainItems(m);
        homeViewModel.setResourceId(id);
    }

    private void setLeftTime(){
        String[] ddltime_str = mainItems.get(mainItems.size()-1).getDate().split("\\.");
        Calendar calendar_now = Calendar.getInstance();
        Calendar calendar_ddl = Calendar.getInstance();
        try{
            calendar_ddl.set(Integer.parseInt(ddltime_str[0]),
                    Integer.parseInt(ddltime_str[1]) - 1,
                    Integer.parseInt(ddltime_str[2]));
            calendar_ddl.getTimeInMillis();
            leftTime = (calendar_ddl.getTimeInMillis() - calendar_now.getTimeInMillis())/1000;
            Log.d("getLeftTime", leftTime+"");
        }catch (Exception e){
            leftTime = 0;
        }
    }

    class MainItemListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //TODO
            //打开新fragment
            //传相应的数据
            //收回数据以便更改本mainItems
            //通知mainactivity改动mainItems

            //传信息传到mainactivity
            Intent intent = new Intent(getContext(), ItemDetailActivity.class);
            intent.putExtra("mainItem", (Serializable) mainItems.get(i));
            intent.putExtra("leftTime", leftTime);
            startActivityForResult(intent, ITEM_DETAIL);
        }
    }

    class RunUpdate implements Runnable {
        @Override
        public void run() {
            leftTime--;
            if (leftTime > 0) {
                //倒计时效果展示
                String formatLongToTimeStr = formatLongToTimeStr(leftTime);
                downcount_home.setText(formatLongToTimeStr);
                //每一秒执行一次
                handler.postDelayed(this, 1000);
            } else {//倒计时结束
                //处理业务流程

                //发送消息，结束倒计时
                Message message = new Message();
                message.what = 1;
                handlerStop.sendMessage(message);
            }
        }

        public String formatLongToTimeStr(Long date) {
            long day = date / (60 * 60 * 24);
            long hour = (date / (60 * 60) - day * 24);
            long min = ((date / 60) - day * 24 * 60 - hour * 60);
            long s = (date - day*24*60*60 - hour*60*60 - min*60);
            String strtime = day+"天"+hour+"小时"+min+"分"+s+"秒";
            return strtime;
        }
    }

    class HandlerStop extends Handler{
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                leftTime = 0;
                handler.removeCallbacks(update_thread);
            } else {
                throw new IllegalStateException("Unexpected value: " + msg.what);
            }
            super.handleMessage(msg);
        }
    }
}