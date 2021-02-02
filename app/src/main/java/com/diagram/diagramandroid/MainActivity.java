package com.diagram.diagramandroid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.diagram.diagramandroid.handler.SyncBarrierTestActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListener();
    }


    private void initListener() {
        findViewById(R.id.btn_syncBarrier).setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_syncBarrier) {
            startActivity(new Intent(this, SyncBarrierTestActivity.class));
        }
    }
}