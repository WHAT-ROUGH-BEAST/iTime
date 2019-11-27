package com.example.mytime.ui.create;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytime.R;
import com.example.mytime.data.RecordAdapter;
import com.example.mytime.data.model.Record;

import java.util.ArrayList;

public class CreateActitvity extends AppCompatActivity {

    private Button btnRet, btnYes;
    private EditText title, tip;
    private ListView recordList;
    private ArrayList<Record> records;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(findViewById(R.layout.create_activity));

        initRecord();
        RecordAdapter recordAdapter =
                new RecordAdapter(CreateActitvity.this, R.layout.listview_component, records);
        recordList = (ListView)findViewById(R.id.list_record);
        recordList.setAdapter(recordAdapter);
    }

    private void initRecord(){
        records.add(new Record(R.drawable.date, "日期", "长按使用日期计算器"));
    }
}
