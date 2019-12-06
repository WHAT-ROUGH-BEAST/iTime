package com.example.mytime.ui.create;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import java.util.Arrays;

public class CreateActivity extends AppCompatActivity {

    private static final int CREAT_GET_RET = 1;
    private ImageButton btnNo, btnYes;
    private EditText title, tip;
    private ListView recordList;
    private ArrayList<Record> records = new ArrayList<>();
    private RecordAdapter recordAdapter;

    

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
        recordAdapter =
                new RecordAdapter(CreateActivity.this, R.layout.listview_component, records);
        recordList.setAdapter(recordAdapter);
            //监听事件 TODO: 选择图片
        ItemListener getData = new ItemListener();
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
                intent.putExtra("date", getData.retDate()[0]
                        +"."+ getData.retDate()[1] +"."+ getData.retDate()[2]);
                intent.putExtra("label", getData.retTag());
                intent.putExtra("repeat", getData.retRepeat());

                Log.d("repeat", "create_ret data != null " + getData.retRepeat());
                Log.d("tags", "create_ret data != null " + getData.retTag());

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
        String[] date = {"2020", "1", "1"};
        String tags;
        String repeat;
        int resourceId;

        //onitemClick的所有操作必定比dialog内的操作要晚，所以只得到值，不要操作值
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i){
                case 0:
                    date = timeDialog(date);
                    records.set(0,new Record(R.drawable.time, "日期",
                            date[0]+"."+date[1]+"."+date[2]));
                    recordAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    repeat = repeatDialog(repeat);
                    records.set(1,new Record(R.drawable.time, "重复设置", repeat));
                    recordAdapter.notifyDataSetChanged();
                    break;
                case 2:
//                    resourceId = resourceDialog(resourceId);
                    break;
                case 3:
                    //TODO:tags始终是null
                    tags = labelDialog(tags);
                    records.set(3, new Record(R.drawable.tag, "添加标签", tags));
                    recordAdapter.notifyDataSetChanged();
                    break;
            }
        }

        public String[] retDate(){
            return date;
        }
        public String retTag(){
            return tags;
        }
        public String retRepeat(){
            return repeat;
        }
    }

    //timedialog
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
        builder.setPositiveButton("确定", gettime);

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

    //labeldialog
    //get_label_dialog.xml目前弃用
    String labelDialog(String tags){
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
        LabelDialogClickListener retLabels = new LabelDialogClickListener(items, checkedItems);
        builder.setPositiveButton("确定", retLabels);

        AlertDialog dialog = builder.create();
        dialog.show();

        tags = retLabels.retLabels();
        return tags;
    }
    class LabelDialogClickListener implements DialogInterface.OnClickListener{
        String[] items;
        boolean[] checkedItems;
        String labels = "";

        public LabelDialogClickListener(String[] items, boolean[] checkedItems){
            this.items = items;
            this.checkedItems = checkedItems;
        }
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            for(int i=0; i<2; i++){
                if(true == checkedItems[i]){
                    labels += items[i];
                }
            }
            dialogInterface.dismiss();
        }

        //此处可以正常传出完整labels
        public String retLabels(){
            return labels;
        }
    }

//
//    String[] resourceDialog(String[] date){
//        View view = getLayoutInflater().inflate(R.layout.get_time_dialog, null);
//        EditText edityear = (EditText)view.findViewById(R.id.edit_year),
//                editmonth = (EditText)view.findViewById(R.id.edit_month),
//                editday = (EditText)view.findViewById(R.id.edit_day);
//        TimeDialogClickListener gettime;
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
//        gettime = new TimeDialogClickListener(edityear, editmonth, editday);
//        builder.setPositiveButton("确定", gettime);//设置对话框的标题
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        date = gettime.retDate();
//
//        return date;
//    }
//    class TimeDialogClickListener implements DialogInterface.OnClickListener{
//        private String[] date = new String[3];
//        EditText edityear, editmonth, editday;
//
//        public TimeDialogClickListener(EditText edityear, EditText editmonth, EditText editday){
//            this.editday = editday;
//            this.editmonth = editmonth;
//            this.edityear = edityear;
//        }
//
//        @Override
//        public void onClick(DialogInterface dialog, int i) {
//            date[0] = edityear.getText().toString();//'android.text.Editable android.widget.EditText.getText()' on a null object reference//TODO
//            date[1] = editmonth.getText().toString();
//            date[2] = editday.getText().toString();
//            dialog.dismiss();
//        }
//
//        public String[] retDate(){
//            return date;
//        }
//    }
//
    String repeatDialog(String repeat){
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
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        repeat = items[checkedItem[0]];
        return repeat;
    }
}
