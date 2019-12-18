package com.example.itime.data;

import android.content.Context;

import com.example.itime.data.model.MainItem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainItemSaver {

    private Context context;

    public MainItemSaver(Context context) {
        this.context = context;
    }

    public void save(ArrayList<MainItem> mainItems)
    {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    context.openFileOutput("Serializable.txt",Context.MODE_PRIVATE)
            );
            outputStream.writeObject(mainItems);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<MainItem> load()
    {
        ArrayList<MainItem> m = new ArrayList<>();
        try{
            ObjectInputStream inputStream = new ObjectInputStream(
                    context.openFileInput("Serializable.txt")
            );
            m = (ArrayList<MainItem>) inputStream.readObject();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }
}
