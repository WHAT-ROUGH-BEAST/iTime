package com.example.mytime;

import android.content.Intent;
import android.os.Bundle;

import com.example.mytime.data.ItemPack;
import com.example.mytime.data.MainItemAdapter;
import com.example.mytime.data.model.MainItem;
import com.example.mytime.ui.create.CreateActivity;
import com.example.mytime.ui.home.HomeFragment;
import com.example.mytime.ui.home.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int CREAT_RET = 1;
    private static final int CREAT_GET_RET = 1;
    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    protected Intent intentFab;
    private ArrayList<MainItem> mainItems;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //维护一个ArrayList<MainItem>
        mainItems = new ArrayList<>();
        mainItems.add(new MainItem(R.drawable.default_img, "title", "tip", "date"));//default

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
                        //因为每次进入fragment必定会初始化一次 要么进行数据持久化 要么每次进入都刷新一次
                        // （意味着main要维护一份ListMainItem）
                        //考虑到有删除功能，如果一个标签下删除 意味着要通知其他的标签页也删除
                        //因此选择每次点击进入fragment都刷新一次
                        //public MainItem(int imgId, String title, String tip, String date)
                        mainItems.add(new MainItem(
                                data.getIntExtra("resId", R.drawable.default_img),
                                data.getStringExtra("title"),
                                data.getStringExtra("tip"),
                                data.getStringExtra("date")));

                        Log.d("data.get", data.getStringExtra("title"));
                        Log.d("creat_ret", "create_ret data != null " + mainItems.size());
                    }catch (Exception e){
                        Log.d("creat_ret", "create_ret data == null");
                        break;
                    }
                }
                break;
            }
            default:
                break;
        }

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

    private void update(){
        Bundle bundle = new Bundle();
        ItemPack itemPack = new ItemPack();
        itemPack.setMainItems(mainItems);
        bundle.putSerializable("items", itemPack);
//        nav_home
        navController.navigate(R.id.nav_home, bundle);
    }
}
