package com.example.mytime.ui.create;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
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
    private ItemListener getDate;

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
        getDate = new ItemListener();
        recordList.setOnItemClickListener(getDate);

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
                intent.putExtra("date",
                        getDate.retDate()[0] +"."+ getDate.retDate()[1] +"."+ getDate.retDate()[2]);

                setResult(CREAT_GET_RET, intent);
                finish();
            }
        });
    }

    private void initRecord(){
        records.add(new Record(R.drawable.time, "日期", "长按使用日期计算器"));
        records.add(new Record(R.drawable.repeat, "重复设置", "无"));
        records.add(new Record(R.drawable.pic, "图片", ""));
        records.add(new Record(R.drawable.tag, "添加标签", ""));
    }

    class ItemListener implements AdapterView.OnItemClickListener{
        String[] date = {"2020", "1", "1"};
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i){
                case 0:
                    date = timeDialog(date);
                    break;
                case 1:

                    break;
            }
        }

        public String[] retDate(){
            return date;
        }
    }

    //后期改成日历//TODO
    String[] timeDialog(String[] date){
        View view = getLayoutInflater().inflate(R.layout.get_time_dialog, null);
        EditText edityear = (EditText)view.findViewById(R.id.edit_year),
                editmonth = (EditText)view.findViewById(R.id.edit_month),
                editday = (EditText)view.findViewById(R.id.edit_day);
        TimeDialogClickListener gettime;

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
        builder.setTitle("日历");
        builder.setView(view);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        gettime = new TimeDialogClickListener(edityear, editmonth, editday);
        builder.setPositiveButton("确定", gettime);//设置对话框的标题

        AlertDialog dialog = builder.create();
        dialog.show();

        date = gettime.retDate();

        return date;
    }
    class TimeDialogClickListener implements DialogInterface.OnClickListener{
        private String[] date = new String[3];
        EditText edityear, editmonth, editday;

        public TimeDialogClickListener(EditText edityear, EditText editmonth, EditText editday){
            this.editday = editday;
            this.editmonth = editmonth;
            this.edityear = edityear;
        }

        @Override
        public void onClick(DialogInterface dialog, int i) {
            date[0] = edityear.getText().toString();//'android.text.Editable android.widget.EditText.getText()' on a null object reference//TODO
            date[1] = editmonth.getText().toString();
            date[2] = editday.getText().toString();
            dialog.dismiss();
        }

        public String[] retDate(){
            return date;
        }
    }
}
