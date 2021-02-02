package com.diagram.diagramandroid.handler;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.diagram.diagramandroid.R;
import com.diagram.diagramandroid.util.LogUtils;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Queue;

public class SyncBarrierTestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SyncBarrierTestActivity";

    public static final int MESSAGE_TYPE_SYNC = 1;
    public static final int MESSAGE_TYPE_ASYNC = 2;

    private Handler mHandler;
    private ArrayDeque<Integer> mSyncBarrierTokens = new ArrayDeque<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_barrier_test);
        initHandler();
        initListener();
    }

    private void initHandler() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == MESSAGE_TYPE_SYNC) {
                            LogUtils.d("收到普通消息");
                        } else if (msg.what == MESSAGE_TYPE_ASYNC) {
                            LogUtils.d("收到异步消息");
                        }
                    }
                };
                Looper.loop();
            }
        }).start();
    }

    private void initListener() {
        findViewById(R.id.btn_postSyncBarrier).setOnClickListener(this);
        findViewById(R.id.btn_removeSyncBarrier).setOnClickListener(this);
        findViewById(R.id.btn_postSyncMessage).setOnClickListener(this);
        findViewById(R.id.btn_postAsyncMessage).setOnClickListener(this);
    }

    //往消息队列插入同步屏障
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendSyncBarrier() {
        try {
            LogUtils.d("插入同步屏障");
            // getQueue api 需要系统版本为6.0及以上
            MessageQueue queue = mHandler.getLooper().getQueue();
            Method method = MessageQueue.class.getDeclaredMethod("postSyncBarrier");
            method.setAccessible(true);
            int token = (int) method.invoke(queue);
            mSyncBarrierTokens.offer(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //往消息队列插入同步屏障
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendSyncBarrier(long when) {
        try {
            LogUtils.d("插入同步屏障");
            // getQueue api 需要系统版本为6.0及以上
            MessageQueue queue = mHandler.getLooper().getQueue();
            Method method = MessageQueue.class.getDeclaredMethod("postSyncBarrier");
            method.setAccessible(true);
            int token = (int) method.invoke(queue, when);
            mSyncBarrierTokens.offer(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //移除屏障
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void removeSyncBarrier() {
        Integer token = mSyncBarrierTokens.poll();
        if (token == null) return;
        try {
            LogUtils.d("移除同步屏障");
            // getQueue api 需要系统版本为6.0及以上
            MessageQueue queue = mHandler.getLooper().getQueue();
            Method method = MessageQueue.class.getDeclaredMethod("removeSyncBarrier", int.class);
            method.setAccessible(true);
            method.invoke(queue, token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //往消息队列插入普通消息
    public void sendSyncMessage() {
        LogUtils.d("插入普通消息");
        Message message = Message.obtain();
        message.what = MESSAGE_TYPE_SYNC;
        mHandler.sendMessageDelayed(message, 1000);
    }

    //往消息队列插入异步消息
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void sendAsyncMessage() {
        LogUtils.d("插入异步消息");
        Message message = Message.obtain();
        message.what = MESSAGE_TYPE_ASYNC;
        // setAsynchronous api 需要系统版本为5.1及以上
        message.setAsynchronous(true);
        mHandler.sendMessageDelayed(message, 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_postSyncBarrier) {
            sendSyncBarrier();
        } else if (id == R.id.btn_removeSyncBarrier) {
            removeSyncBarrier();
        } else if (id == R.id.btn_postSyncMessage) {
            sendSyncMessage();
        } else if (id == R.id.btn_postAsyncMessage) {
            sendAsyncMessage();
        }
    }
}
