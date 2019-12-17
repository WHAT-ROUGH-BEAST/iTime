package com.example.itime;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itime.data.model.MainItem;

import java.io.Serializable;

import static com.example.itime.MainActivity.formatLongToTimeStr;
import static com.example.itime.MainActivity.setLeftTime;
import static com.example.itime.MainActivity.updateTextOnImg;

public class ItemDetailActivity extends AppCompatActivity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
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
                Intent intent = new Intent();
                intent.putExtra("resId", data.getIntExtra("resId", R.drawable.default_img));
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("tip", data.getStringExtra("tip"));
                intent.putExtra("date", data.getStringExtra("date"));
                intent.putExtra("label", data.getStringArrayListExtra("label"));
                intent.putExtra("repeat", data.getStringExtra("repeat"));
                intent.putExtra("textOnImg", thismainItem.getTextOnImg());
                intent.putExtra("leftTime", thismainItem.getLeftTime());
                intent.putExtra("index", index);
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

        title.setText(thismainItem.getTitle());
        date.setText(thismainItem.getDate());

//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
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
        Intent intent = getIntent();
        thismainItem = new MainItem(intent.getIntExtra("resId", 0),
                intent.getStringExtra("title"),
                intent.getStringExtra("tip"),
                intent.getStringExtra("date"),
                intent.getStringArrayListExtra("lable"),
                intent.getStringExtra("repeat"),
                "0");
        setLeftTime(thismainItem);
        thismainItem.setTextOnImg(updateTextOnImg(thismainItem.getLeftTime()));
        index = intent.getIntExtra("index", 0);

    }

    @Override
    protected void onDestroy() {
        //发送消息，结束倒计时
        Message message = new Message();
        message.what = 1;
        handlerStop.sendMessage(message);
        super.onDestroy();
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
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
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
