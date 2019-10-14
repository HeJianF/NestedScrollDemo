package com.taohuahua.nestedviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView mRecyclerView = findViewById(R.id.recyler_view);
        String[] items = getResources().getStringArray(R.array.tab_B);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(adapter);
        MyDialogFragment.instance().show(getSupportFragmentManager());
    }
}
