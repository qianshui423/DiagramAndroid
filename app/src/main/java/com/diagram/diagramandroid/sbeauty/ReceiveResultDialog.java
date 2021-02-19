package com.diagram.diagramandroid.sbeauty;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.diagram.diagramandroid.R;

/**
 * Receive intent result for Dialog
 */
public class ReceiveResultDialog extends Dialog {

    private static final int REQUEST_CODE = 1;

    private Context mContext;
    private Button mBtnJump;
    private TextView mTvLog;

    public ReceiveResultDialog(@NonNull Context context) {
        super(context);
        mContext = context;

        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_receive_result, null);
        setContentView(contentView);

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            window.setGravity(Gravity.CENTER);
        }

        contentView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int height = contentView.findViewById(R.id.ll_layout_root).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        initView();
        initListener();
        testOnActivityForResult();
    }

    private void testOnActivityForResult() {
        GlobalPermissionCompatDelegate.getInstance().register((Activity) mContext, new ActivityCompat.PermissionCompatDelegate() {
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
    }

    private void initListener() {
        mBtnJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity ownerActivity = (Activity) mContext;
                ownerActivity.startActivityForResult(new Intent(ownerActivity, SetResultActivity.class), REQUEST_CODE);
            }
        });
    }
}
