package com.example.itime;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itime.data.RecordAdapter;
import com.example.itime.data.model.Record;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CreateActivity extends AppCompatActivity {

    private static final int CREAT_GET_RET = 1;

    private ImageButton btnNo, btnYes;
    private EditText title, tip;
    private ListView recordList;
    private ArrayList<Record> records = new ArrayList<>();
    private RecordAdapter recordAdapter;
    private ItemListener getData;

    private String[] date;
    private String tags;
    private String repeat;
    private int resourceId;

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

        date = new String[6];
        tags = new String();
        repeat = new String();
        resourceId = R.drawable.default_img;

        //ListView init
        initRecord();
        recordAdapter =
                new RecordAdapter(CreateActivity.this, R.layout.listview_component, records);
        recordList.setAdapter(recordAdapter);
            //监听事件 TODO: 选择图片
        getData = new ItemListener();
        recordList.setOnItemClickListener(getData);

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
                //TODO:后期改为自己选图片
                intent.putExtra("resId", R.drawable.default_img);
                intent.putExtra("title", title.getText().toString());
                intent.putExtra("tip", tip.getText().toString());
                intent.putExtra("date", date[0]
                        +"."+ date[1] +"."+ date[2] + "." + date[3] +
                        "." + date[4] +"."+ date[5]);
                intent.putExtra("label", tags);
                intent.putExtra("repeat", repeat);

                Log.d("repeat", "create_ret data != null " + repeat);
                Log.d("tags", "create_ret data != null " + tags);

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

    //TODO: 将图片功能补全
    class ItemListener implements AdapterView.OnItemClickListener{
        //onitemClick的所有操作必定比dialog内的操作要晚，所以只得到值，不要操作值
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i){
                case 0:
                    timeDialog1();
                    timeDialog2();
                    break;
                case 1:
                    repeatDialog();
                    break;
                case 2:
//                    resourceId = resourceDialog(resourceId);
                    break;
                case 3:
                    labelDialog();
                    break;
            }
        }
    }

    //timedialog 初版
//    private void timeDialog(){
//        View view = getLayoutInflater().inflate(R.layout.get_time_dialog, null);
//        final EditText edityear = (EditText)view.findViewById(R.id.edit_year),
//                editmonth = (EditText)view.findViewById(R.id.edit_month),
//                editday = (EditText)view.findViewById(R.id.edit_day);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
//        builder.setTitle("日历");
//        builder.setView(view);
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                date[0] = edityear.getText().toString();//'android.text.Editable android.widget.EditText.getText()' on a null object reference//TODO
//                date[1] = editmonth.getText().toString();
//                date[2] = editday.getText().toString();
//                //show
//                records.set(0,new Record(R.drawable.time, "日期",
//                        date[0]+"."+date[1]+"."+date[2]));
//                recordAdapter.notifyDataSetChanged();
//
//                dialog.dismiss();
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//////////////////////////////////////////////////////////

    private void timeDialog1() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GTM+08:00"), Locale.CHINA);
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        //monthOfYear 得到的月份会减1所以我们要加1
                        date[0] = String.valueOf(year);
                        date[1] = String.valueOf(monthOfYear + 1);
                        date[2] = Integer.toString(dayOfMonth);
                    }
                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
        //自动弹出键盘问题解决
        datePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void timeDialog2() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GTM+08:00"), Locale.CHINA);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                date[3] = String.valueOf(hourOfDay);
                date[4] = String.valueOf(minute);
                date[5] = "0";
            }
        }, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
        timePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    //labeldialog
    //get_label_dialog.xml目前弃用
    private void labelDialog(){
        final String items[] = {"学习", "玩"};
        final boolean checkedItems[] = {false, false};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择");
        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int i=0; i<2; i++){
                    if(true == checkedItems[i]){
                        tags += " "+items[i];
                    }
                }
                //show
                records.set(3, new Record(R.drawable.tag, "添加标签", tags));
                recordAdapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void repeatDialog(){
        final String items[] = {"每天", "每周", "每月", "每年"};
        final int[] checkedItem = {1};
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("单选列表对话框")//设置对话框的标题
                .setSingleChoiceItems(items, checkedItem[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem[0] = which;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        repeat = items[checkedItem[0]];

                        records.set(1,new Record(R.drawable.time, "重复设置", repeat));
                        recordAdapter.notifyDataSetChanged();

                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }
}
