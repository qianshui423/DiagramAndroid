package com.diagram.diagramandroid.sbeauty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.diagram.diagramandroid.R;

/**
 * Receive intent result for Activity
 */
public class ReceiveResultActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    private Button mBtnJump;
    private Button mBtnShowDialog;
    private TextView mTvLog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_result);
        initView();
        initListener();
        testOnActivityForResult();
    }

    private void testOnActivityForResult() {
        GlobalPermissionCompatDelegate.getInstance().register(this, new ActivityCompat.PermissionCompatDelegate() {
            @Override
            public boolean requestPermissions(@NonNull Activity activity, @NonNull String[] permissions, int requestCode) {
                return false;
            }

            @Override
            public boolean onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
                if (data != null) {
                    String text = data.getStringExtra("text");
                    mTvLog.setText(text);
                }
                return false;
            }
        });
    }

    private void initView() {
        mBtnJump = findViewById(R.id.btn_jump);
        mTvLog = findViewById(R.id.tv_log);
        mBtnShowDialog = findViewById(R.id.btn_show_dialog);
    }

    private void initListener() {
        mBtnJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ReceiveResultActivity.this, SetResultActivity.class), REQUEST_CODE);
            }
        });
        mBtnShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReceiveResultDialog(ReceiveResultActivity.this).show();
            }
        });
    }
}
