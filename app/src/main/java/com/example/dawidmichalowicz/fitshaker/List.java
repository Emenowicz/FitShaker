package com.example.dawidmichalowicz.fitshaker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Dawid Michałowicz on 01.06.2017.
 */

public class List extends Activity{
    ListView list;
    ArrayAdapter<String> adapter;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        preferences = getSharedPreferences("trainings", Activity.MODE_PRIVATE);
        list = (ListView) findViewById(R.id.list);
        Map<String,?> keys =  preferences.getAll();
        Log.d("map", String.valueOf(keys.isEmpty()));
        ArrayList<String> trainings = new ArrayList<>();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values", entry.getKey() + ": "+ entry.getValue().toString());
            trainings.add(entry.getValue().toString());
        }
        Log.d("trainings",String.valueOf(trainings.isEmpty()));
        if(trainings.isEmpty()){
            trainings.add("Brak treningów");
        }
        Collections.sort(trainings);
        Collections.reverse(trainings);
        adapter = new ArrayAdapter<>(this,R.layout.row,trainings);

        list.setAdapter(adapter);
    }
}
