package com.example.mytime.data;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mytime.R;
import com.example.mytime.data.model.Record;

import java.util.List;

public class RecordAdapter extends ArrayAdapter<Record> {
    private int resourceId;
    public RecordAdapter(@NonNull Context context, int resource, List<Record> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Record record = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.record_img);
        TextView title = (TextView) view.findViewById(R.id.record_title),
                tip = (TextView)view.findViewById(R.id.record_tip);
        imageView.setImageResource(record.getImgId());
        title.setText(record.getTitle());
        tip.setText(record.getTips());
        return view;
    }
}
