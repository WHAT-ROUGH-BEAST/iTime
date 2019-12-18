package com.example.itime;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.itime.data.MainItemAdapter;
import com.example.itime.data.MainItemSaver;
import com.example.itime.data.model.MainItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

//TODO: 改颜色 bar上的label标志 序列化
public class MainActivity extends AppCompatActivity {

    private static final int CREAT_RET = 1;
    private static final int CREAT_GET_RET = 1;
    private static final int ITEM_DETAIL = 100;
    private static final int ITEM_DETAIL_DEL = 101;
    private static final int ITEM_DETAIL_CHANGE = 102;
    private static final int COLOR= 201;
    private static final int COLORBCK = 200;
    private String DEFAULT_PIC;

    //部件
    protected Intent intentFab;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ListView drawerList;
    private ArrayList<String> drawerItem;
    private ArrayAdapter<String> drawerItemAdapter;

    //页面
    private ImageView mainImg;
    private TextView title, date, downcount;
    private ListView mainlistView;
    private ArrayList<MainItem> mainItemDisplay;
    private MainItemAdapter mainItemAdapter;

    //数据
    private ArrayList<MainItem> mainItems;

    //刷新
    private Handler handler = new Handler();
    private Runnable update_thread;
    private Handler handlerStop;

    //保存
    private MainItemSaver saver;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CREAT_RET:
                if (CREAT_GET_RET == resultCode){
                    getMainItemChanges(data);
                }
                break;
                //END OF CASE
            case ITEM_DETAIL:
                if(ITEM_DETAIL_DEL == resultCode){
                    int index = data.getIntExtra("index", 0);
                    mainItems.remove(index);
                    mainItemAdapter.notifyDataSetChanged();
                }
                else if (ITEM_DETAIL_CHANGE == resultCode){
                    getMainItemChanges(data);
                }
                break;
            case COLOR:
                if (resultCode == COLORBCK){
                    int color = data.getIntExtra("color", 0);
                    this.getWindow().setColorMode(color);
                }
            default:
                Log.i("strange", "someone else pass back");
                break;
        }
    }
    private void getMainItemChanges(Intent data){
        if (null != data.getStringExtra("textOnImg")){
            MainItem m = new MainItem(
                    data.getStringExtra("resId"),
                    data.getStringExtra("title"),
                    data.getStringExtra("tip"),
                    data.getStringExtra("date"),
                    data.getStringArrayListExtra("label"),
                    data.getStringExtra("repeat"),
                    data.getStringExtra("textOnImg"));
            m.setLeftTime(data.getLongExtra("leftTime", 0));

            mainItems.set(data.getIntExtra("index", 0), m);
            mainItemAdapter.notifyDataSetChanged();
        }else{
            try{
                assert data != null;
                MainItem m = new MainItem(
                        data.getStringExtra("resId"),
                        data.getStringExtra("title"),
                        data.getStringExtra("tip"),
                        data.getStringExtra("date"),
                        data.getStringArrayListExtra("label"),
                        data.getStringExtra("repeat"),
                        "0");
                setLeftTime(m);
                m.setTextOnImg(updateTextOnImg(m.getLeftTime()));
                mainItems.add(m);
                mainItemAdapter.notifyDataSetChanged();
                //label, repeat只对main有意义：判断标签以及倒计时间；
            }catch (Exception e){
                Log.d("creat_ret", "create_ret data == null");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DEFAULT_PIC = "android.resource://"+this.getPackageName()+"/"+R.drawable.default_img;

        //工具栏
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //按钮
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new FabListener());

        //侧滑单
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.drawer_list);
        drawerItem = new ArrayList<>();
        drawerItem.add("主页");
        drawerItem.add("主题色");
        drawerItem.add("学习");
        drawerItem.add("玩");
        drawerItemAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, drawerItem);
        drawerList.setAdapter(drawerItemAdapter);
        drawerList.setOnItemClickListener(new ItemListener());

        //data
        initMainItems();

        //页面
        mainImg = findViewById(R.id.img_home);
        title = findViewById(R.id.title_home);
        date = findViewById(R.id.date_home);
        downcount = findViewById(R.id.downcount_home);
        mainlistView = findViewById(R.id.list_home);
        mainItemDisplay = new ArrayList<>(mainItems);//不能直接给引用

        mainItemAdapter = new MainItemAdapter(MainActivity.this,
                R.layout.item_main, mainItemDisplay);
        mainlistView.setAdapter(mainItemAdapter);
        mainlistView.setOnItemClickListener(new MainItemListener());

        //downcount
        update_thread = new RunUpdate();
        handlerStop = new HandlerStop();
        update_thread.run();

        //save
        saver = new MainItemSaver(this);

    }//END OF ONCREATE

    @Override
    public void onStart() {
        //默认每次返回后进入home页面
        //设置bar上文字
        mainItemDisplay.clear();mainItemDisplay.addAll(mainItems);
        initPage();
        this.getWindow().setTitle("home");

        super.onStart();
    }//: end of onStart

    @Override
    public void onDestroy() {
        //发送消息，结束子线程
        Message message = new Message();
        message.what = 1;
        handlerStop.sendMessage(message);
        saver.save(mainItems);
        super.onDestroy();
    }//: end of onDestroy

    //倒计时线程
    class RunUpdate implements Runnable {
        @Override
        public void run() {
            //data
            try{
                for(MainItem item : mainItems){
                    item.setLeftTime(item.getLeftTime()-1);
                    item.setTextOnImg(updateTextOnImg(item.getLeftTime()));

                    //时间到的提示
                    if (0 == item.getLeftTime()){
                        Toast.makeText(getApplicationContext(), item.getTitle()+" RING!",
                                Toast.LENGTH_LONG).show();
                    }
                }
                //更新主页
                if (0 == mainItemDisplay.size()){
                    downcount.setText("nope");
                }else{
                    long leftTime = mainItemDisplay.get(mainItemDisplay.size()-1).getLeftTime();
                    String formatLongToTimeStr = formatLongToTimeStr(leftTime);
                    downcount.setText(formatLongToTimeStr);
                }

                handler.postDelayed(this, 1000);
            } catch (Exception e){
                Message message = new Message();
                message.what = 1;
                handlerStop.sendMessage(message);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class HandlerStop extends Handler {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                handler.removeCallbacks(update_thread);
            } else {
                throw new IllegalStateException("Unexpected value: " + msg.what);
            }
            super.handleMessage(msg);
        }
    }

    //按钮点击事件
    class FabListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            intentFab = new Intent(MainActivity.this, CreateActivity.class);
            startActivityForResult(intentFab, CREAT_RET);
        }
    }

    //drawer点击事件
    //每一次drawer都会重新打开ac
    class ItemListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i){
                case 0:
                    //home
                    //设置bar上文字
                    MainActivity.this.getWindow().setTitle("home");
                    mainItemDisplay.clear();mainItemDisplay.addAll(mainItems);
                    mainItemAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    //color
                    Intent intent = new Intent(MainActivity.this, ColorActivity.class);
                    startActivityForResult(intent, COLOR);
                    break;
                default:
                    //label
                    //设置bar上文字
                    MainActivity.this.getWindow().setTitle(drawerItemAdapter.getItem(i));

                    for (MainItem m : mainItems){ //mainTiems size == 0
                        if (!m.getLabel().contains(drawerItemAdapter.getItem(i))){
                            mainItemDisplay.remove(m);
                        }
                    }
                    mainItemAdapter.notifyDataSetChanged();
                    initPage();
                    break;
            }
        }
    }

    //mainlistView点击事件
    class MainItemListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
            intent.putExtra("index", i);
            intent.putExtra("resId", mainItemDisplay.get(i).getImgId());
            intent.putExtra("title", mainItemDisplay.get(i).getTitle());
            intent.putExtra("tip", mainItemDisplay.get(i).getTip());
            intent.putExtra("date", mainItemDisplay.get(i).getDate());
            intent.putExtra("lable", mainItemDisplay.get(i).getLabel());
            intent.putExtra("repeat", mainItemDisplay.get(i).getRepeat());
//            intent.putExtra("leftTime", mainItemDisplay.get(i).getLeftTime());
            startActivityForResult(intent, ITEM_DETAIL);
        }
    }

    private void initMainItems(){
        try{
            assert mainItems!=null;
            mainItems = saver.load();
        }catch (Exception e){
            mainItems = new ArrayList<>();
            ArrayList<String> defaultlabel = new ArrayList<String>();
            defaultlabel.add("学习");
            mainItems.add(new MainItem(DEFAULT_PIC, "TITLE", "TIP", "2019.12.17.0.0.0",
                    defaultlabel, "每天", "0"));//default
            for(MainItem item : mainItems){
                setLeftTime(item);
                item.setTextOnImg(updateTextOnImg(item.getLeftTime()));
            }
        }
    }
    private void initPage(){
        if (0 == mainItemDisplay.size()) {
            title.setText("nope");
            date.setText("nope");
            downcount.setText("nope");
            mainImg.setImageResource(R.drawable.default_img);
        } else {
            MainItem m = mainItemDisplay.get(mainItemDisplay.size()-1);
            title.setText(m.getTitle());
            date.setText(m.getDate());
            mainImg.setImageURI(Uri.parse(m.getImgId()));
        }
    }

    //剩余时间
    public static void setLeftTime(MainItem mainItem){
        String[] ddltime_str = mainItem.getDate().split("\\.");
        Calendar calendar_now = Calendar.getInstance();
        Calendar calendar_ddl = Calendar.getInstance();
        try{
            calendar_ddl.set(Integer.parseInt(ddltime_str[0]),
                    Integer.parseInt(ddltime_str[1]) - 1,
                    Integer.parseInt(ddltime_str[2]),
                    Integer.parseInt(ddltime_str[3]) - 8,
                    Integer.parseInt(ddltime_str[4]),
                    Integer.parseInt(ddltime_str[5]));
            mainItem.setLeftTime((calendar_ddl.getTimeInMillis() - calendar_now.getTimeInMillis())/1000);
        }catch (Exception e){
            mainItem.setLeftTime(0);
        }
    }
    //更新每一项显示的剩余时间
    public static String updateTextOnImg(Long date) {
        long[] left = new long[]{0, 0, 0, 0};
        String[] s = {"天", "小时", "分", "秒"};
//            long day, hour, min, s;
        String strtime = "";
        if (date > 0){
            left[0] = date / (60 * 60 * 24);
            left[1] = (date / (60 * 60) - left[0] * 24);
            left[2] = ((date / 60) - left[0] * 24 * 60 - left[1] * 60);
            left[3] = (date - left[0]*24*60*60 - left[1]*60*60 - left[2]*60);
            for (int i = 0; i < left.length; i++){
                if(left[i] != 0){
                    strtime = "剩余"+left[i]+s[i];
                    break;
                }
            }
        }else{
            date = -date;
            left[0] = date / (60 * 60 * 24);
            left[1] = (date / (60 * 60) - left[0] * 24);
            left[2] = ((date / 60) - left[0] * 24 * 60 - left[1] * 60);
            left[3] = (date - left[0]*24*60*60 - left[1]*60*60 - left[2]*60);
            strtime = "已过"+left[0]+"天"+left[1]+"小时"+left[2]+"分"+left[3]+"秒";
            for (int i = 0; i < left.length; i++){
                if(left[i] != 0){
                    strtime = "已过"+left[i]+s[i];
                    break;
                }
            }
        }

        return strtime;
    }

    public static String formatLongToTimeStr(Long date) {
        long[] left = new long[]{0, 0, 0, 0};
        String strtime;
        if (date > 0){
            left[0] = date / (60 * 60 * 24);
            left[1] = (date / (60 * 60) - left[0] * 24);
            left[2] = ((date / 60) - left[0] * 24 * 60 - left[1] * 60);
            left[3] = (date - left[0]*24*60*60 - left[1]*60*60 - left[2]*60);
            strtime = "剩余"+left[0]+"天"+left[1]+"小时"+left[2]+"分"+left[3]+"秒";
        }else{
            date = -date;
            left[0] = date / (60 * 60 * 24);
            left[1] = (date / (60 * 60) - left[0] * 24);
            left[2] = ((date / 60) - left[0] * 24 * 60 - left[1] * 60);
            left[3] = (date - left[0]*24*60*60 - left[1]*60*60 - left[2]*60);
            strtime = "已过"+left[0]+"天"+left[1]+"小时"+left[2]+"分"+left[3]+"秒";
        }

        return strtime;
    }
}
