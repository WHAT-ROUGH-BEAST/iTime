package com.example.mytime;

import android.content.Intent;
import android.os.Bundle;

import com.example.mytime.data.ItemPack;
import com.example.mytime.data.model.MainItem;
import com.example.mytime.ui.CreateActivity;
import com.example.mytime.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int CREAT_RET = 1;
    private static final int CREAT_GET_RET = 1;
    private static final int ITEM_DETAIL = 100;
    private static final int ITEM_DETAIL_DEL = 101;
    private static final int ITEM_DETAIL_CHANGE = 102;

    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    protected Intent intentFab;
    private ArrayList<MainItem> mainItems;
    private NavController navController;

    private Handler handler = new Handler();
    private Runnable update_thread;
    private Handler handlerStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //维护一个ArrayList<MainItem>
        mainItems = new ArrayList<>();
        ArrayList<String> defaultlabel = new ArrayList<String>();
        defaultlabel.add("1");
        mainItems.add(new MainItem(R.drawable.default_img, "title", "tip", "2020.12.7",
                defaultlabel, "每天", "0"));//default

        //最上方的部分
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //右下方的按钮
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new FabListener());

        //侧滑选单
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        update();

        //倒计时线程
        for(MainItem item : mainItems){
            setLeftTime(item);
        }
        update_thread = new RunUpdate();
        handlerStop = new HandlerStop();
        update_thread.run();

        //测选单的不同选项Listener
//        navigationView.setNavigationItemSelectedListener(new NavSelectedListener());

        //为mainitem添加序列化//TODO
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * creat页面传数据的回调函数*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CREAT_RET:
            {
                if (CREAT_GET_RET == resultCode){
                    try{
                        assert data != null;
                        mainItems.add(new MainItem(
                                data.getIntExtra("resId", R.drawable.default_img),
                                data.getStringExtra("title"),
                                data.getStringExtra("tip"),
                                data.getStringExtra("date"),
                                data.getStringArrayListExtra("label"),
                                data.getStringExtra("repeat"),
                                "0"));
                        //label, repeat只对main有意义：判断标签以及倒计时间；

                        Log.d("creat_ret", "create_ret data != null " + mainItems.get(1).getLabel());//tags--String
                    }catch (Exception e){
                        Log.d("creat_ret", "create_ret data == null");
                        break;
                    }
                }
                break;
            }//END OF CASE
            case ITEM_DETAIL:
            {
                //TODO
                //返回的是一个mainitem
                if (resultCode == ITEM_DETAIL_DEL){
                    //删除
                }else if (resultCode == ITEM_DETAIL_CHANGE){
                    //更新
                }
            } //END OF CASE
            default:
                break;
        }
        //因为每次进入fragment必定会初始化一次 要么进行数据持久化 要么每次进入都刷新一次
        // （意味着main要维护一份ListMainItem）
        //考虑到有删除功能，如果一个标签下删除 意味着要通知其他的标签页也删除
        //因此选择每次点击进入fragment都刷新一次
        //public MainItem(int imgId, String title, String tip, String date)
        //更新数据
        update();
    }

    class FabListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            intentFab = new Intent(MainActivity.this, CreateActivity.class);
            startActivityForResult(intentFab, CREAT_RET);
        }
    }

    private void update(){
        Bundle bundle = new Bundle();
        ItemPack itemPack = new ItemPack();
        itemPack.setMainItems(mainItems);
        bundle.putSerializable("items", itemPack);
//        nav_home
        navController.navigate(R.id.nav_home, bundle);
    }

    //初始化倒计时
    private void setLeftTime(MainItem mainItem){
        String[] ddltime_str = mainItems.get(mainItems.size()-1).getDate().split("\\.");
        Calendar calendar_now = Calendar.getInstance();
        Calendar calendar_ddl = Calendar.getInstance();
        try{
            calendar_ddl.set(Integer.parseInt(ddltime_str[0]),
                    Integer.parseInt(ddltime_str[1]) - 1,
                    Integer.parseInt(ddltime_str[2]));
            calendar_ddl.getTimeInMillis();
            mainItem.setLeftTime((calendar_ddl.getTimeInMillis() - calendar_now.getTimeInMillis())/1000);
            Log.d("getLeftTime", mainItem.getLeftTime()+"");
        }catch (Exception e){
            mainItem.setLeftTime(0);
        }
    }
    //倒计时线程
    class RunUpdate implements Runnable {
        @Override
        public void run() {
            try{
                for(MainItem item : mainItems){
                    item.setLeftTime(item.getLeftTime()-1);
                }
                update();
                handler.postDelayed(this, 1000);
            } catch (Exception e){
                Message message = new Message();
                message.what = 1;
                handlerStop.sendMessage(message);
            }
        }
    }
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

    //    //侧边栏监听事件
//    class NavSelectedListener implements NavigationView.OnNavigationItemSelectedListener{
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.nav_home:
//                    Toast.makeText(MainActivity.this, "Home is clicked!", Toast.LENGTH_SHORT).show();
//                    break;
//                default:
//                    //后期实现label中的传值//TODO
//                    break;
//            }
//            drawer.closeDrawer(navigationView);
//            return false;
//        }
//    }

}
