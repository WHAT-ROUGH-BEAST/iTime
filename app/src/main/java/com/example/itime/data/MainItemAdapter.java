package com.example.itime.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.itime.R;
import com.example.itime.data.model.MainItem;

import java.util.List;

public class MainItemAdapter extends ArrayAdapter<MainItem> {

    private int resourceId;

    public MainItemAdapter(@NonNull Context context, int resource, @NonNull List<MainItem> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MainItem mainItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.item_img);
        TextView title = (TextView)view.findViewById(R.id.item_title),
                tip = (TextView)view.findViewById(R.id.item_tip),
                date = (TextView)view.findViewById(R.id.item_date),
                textOnImg = (TextView)view.findViewById(R.id.item_text_on_img);

        imageView.setImageResource(mainItem.getImgId());
        title.setText(mainItem.getTitle());
        tip.setText(mainItem.getTip());
        date.setText(mainItem.getDate());
        textOnImg.setText(mainItem.getTextOnImg());
        return view;
    }


}
