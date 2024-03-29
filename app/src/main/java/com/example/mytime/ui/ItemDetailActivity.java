package com.example.mytime.ui;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mytime.R;
import com.example.mytime.data.model.MainItem;
import com.example.mytime.ui.home.HomeFragment;

import java.io.Serializable;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ItemDetailActivity extends AppCompatActivity {

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            mButtonsView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private View mButtonsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
            mButtonsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private static final int ITEM_DETAIL_DEL = 101;
    private static final int ITEM_DETAIL_CHANGE = 102;
//        private static final int ITEM_DETAIL_BACK = 103;

    private MainItem thismainItem;
    private Runnable update_thread;
    private Handler handlerStop;
    private Handler handler = new Handler();

    private ImageButton btn_back, btn_del, btn_change;
    private TextView title, date, downcount;
    private ImageView img;

    private int index;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case ITEM_DETAIL_CHANGE:
                MainItem changeItem = new MainItem(
                        data.getIntExtra("resId", R.drawable.default_img),
                        data.getStringExtra("title"),
                        data.getStringExtra("tip"),
                        data.getStringExtra("date"),
                        data.getStringArrayListExtra("label"),
                        data.getStringExtra("repeat"),
                        thismainItem.getTextOnImg());

                changeItem.setLeftTime(thismainItem.getLeftTime());
                Intent intent = new Intent();
                intent.putExtra("changeItem", (Serializable)changeItem);
                intent.putExtra("change_index", index);
                setResult(ITEM_DETAIL_CHANGE, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mButtonsView = findViewById(R.id.top_buttons);
        btn_back = (ImageButton)findViewById(R.id.detail_back);
        btn_change = (ImageButton)findViewById(R.id.detail_change);
        btn_del = (ImageButton)findViewById(R.id.detail_del);
        title = (TextView)findViewById(R.id.detail_title);
        date = (TextView)findViewById(R.id.detail_date);
        downcount = (TextView)findViewById(R.id.detail_downcount);
        img = (ImageView)findViewById(R.id.detail_img);

        //获得main的数据
        getMainItemAttr();
        update_thread = new RunUpdate();
        handlerStop = new HandlerStop();
        update_thread.run();

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("del_index", index);
                setResult(ITEM_DETAIL_DEL, intent);
                finish();
            }
        });
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                Intent intent = new Intent(ItemDetailActivity.this, CreateActivity.class);
                startActivityForResult(intent, ITEM_DETAIL_CHANGE);
            }
        });
    }//: END OF onCreate

    private void getMainItemAttr(){
        thismainItem = (MainItem) getIntent().getSerializableExtra("mainItem");
        title.setText(thismainItem.getTitle());
        date.setText(thismainItem.getDate());
        index = getIntent().getIntExtra("index", 0);

        Log.i("getMainItemAttr", ""+thismainItem.getLeftTime());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    protected void onDestroy() {
        //发送消息，结束倒计时
        Message message = new Message();
        message.what = 1;
        handlerStop.sendMessage(message);
        super.onDestroy();
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }
    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mButtonsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }
    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mButtonsView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }
    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    //perform downcount
    class RunUpdate implements Runnable {
        @Override
        public void run() {
            thismainItem.setLeftTime(thismainItem.getLeftTime()-1);
            String formatLongToTimeStr = formatLongToTimeStr(thismainItem.getLeftTime());
            downcount.setText(formatLongToTimeStr);
            handler.postDelayed(this, 1000);
        }

        private String formatLongToTimeStr(Long date) {
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
    @SuppressLint("HandlerLeak")
    class HandlerStop extends Handler{
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                handler.removeCallbacks(update_thread);
            } else {
                throw new IllegalStateException("Unexpected value: " + msg.what);
            }
            super.handleMessage(msg);
        }
    }
}
