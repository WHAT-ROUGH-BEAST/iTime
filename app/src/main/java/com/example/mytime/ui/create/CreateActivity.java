package com.example.mytime.ui.create;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mytime.R;
import com.example.mytime.data.RecordAdapter;
import com.example.mytime.data.model.Record;

import java.util.ArrayList;

public class CreateActivity extends AppCompatActivity {

    private static final int CREAT_GET_RET = 1;
    private ImageButton btnNo, btnYes;
    private EditText title, tip;
    private ListView recordList;
    private ArrayList<Record> records = new ArrayList<>();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);
        recordList = (ListView)findViewById(R.id.list_record);
        btnNo = (ImageButton)findViewById(R.id.create_no);
        btnYes = (ImageButton)findViewById(R.id.create_yes);
        title = (EditText)findViewById(R.id.create_title_edit);
        tip = (EditText)findViewById(R.id.create_tip_edit);

        //ListView init
        initRecord();
        RecordAdapter recordAdapter =
                new RecordAdapter(CreateActivity.this, R.layout.listview_component, records);
        recordList.setAdapter(recordAdapter);
        //监听事件 TODO
        recordList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        int[] date = new int[]{2020, 1, 1};
                        timeDialog(date);
                        break;
                }
            }
        });

        //OnClick
        //no
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //yes
        btnYes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("resId", R.drawable.default_img);
                intent.putExtra("title", title.getText().toString());
                intent.putExtra("tip", tip.getText().toString());
                intent.putExtra("date", "date");

                setResult(CREAT_GET_RET, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }

    private void initRecord(){
        records.add(new Record(R.drawable.time, "日期", "长按使用日期计算器"));
        records.add(new Record(R.drawable.repeat, "重复设置", "无"));
        records.add(new Record(R.drawable.pic, "图片", ""));
        records.add(new Record(R.drawable.tag, "添加标签", ""));
    }

    private int[] timeDialog(int[] date){
        View view = getLayoutInflater().inflate(R.layout.get_time_dialog, null);
        final EditText edityear = (EditText)findViewById(R.id.edit_year),
                editmonth = (EditText)findViewById(R.id.edit_month),
                editday = (EditText)findViewById(R.id.edit_day);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("日历")//设置对话框的标题
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    int[] date = new int[3];
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        date[0] = Integer.parseInt(edityear.getText().toString());
                        date[1] = Integer.parseInt(editmonth.getText().toString());
                        date[2] = Integer.parseInt(editday.getText().toString());
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();

        return date;
    }
}
