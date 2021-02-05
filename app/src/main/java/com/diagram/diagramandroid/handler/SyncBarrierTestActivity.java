package com.diagram.diagramandroid.handler;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.diagram.diagramandroid.R;
import com.diagram.diagramandroid.util.LogUtils;

import java.lang.reflect.Method;
import java.util.ArrayDeque;

/**
 * 同步屏障技术 兼容4.1及以上
 * 4.1以后Android重构了屏幕刷新机制，新增 Choreographer
 */
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
    public void sendSyncBarrier() {
        try {
            LogUtils.d("插入同步屏障");
            int token = addSyncBarrier(SystemClock.uptimeMillis());
            mSyncBarrierTokens.offer(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //往消息队列插入同步屏障
    public void sendSyncBarrier(long when) {
        try {
            LogUtils.d("插入同步屏障");
            int token = addSyncBarrier(when);
            mSyncBarrierTokens.offer(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int addSyncBarrier(long when) {
        try {
            MessageQueue queue;
            Method method;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                queue = mHandler.getLooper().getQueue();
                method = MessageQueue.class.getDeclaredMethod("postSyncBarrier");
            } else {
                queue = Looper.myQueue();
                // 6.0 以前需要使用 enqueueSyncBarrier 添加屏障消息
                method = MessageQueue.class.getDeclaredMethod("enqueueSyncBarrier");
            }
            method.setAccessible(true);
            return (int) method.invoke(queue, when);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    //移除屏障
    public void removeSyncBarrier() {
        Integer token = mSyncBarrierTokens.poll();
        if (token == null) return;
        LogUtils.d("移除同步屏障");
        removeSyncBarrier(token);
    }

    private void removeSyncBarrier(int token) {
        try {
            LogUtils.d("移除同步屏障");
            MessageQueue queue = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                // 6.0 以后新增
                queue = mHandler.getLooper().getQueue();
            } else {
                // 6.0 以前需要使用下面的方法获取消息队列
                queue = Looper.myQueue();
            }
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
    private void sendAsyncMessage() {
        LogUtils.d("插入异步消息");
        Message message = Message.obtain();
        message.what = MESSAGE_TYPE_ASYNC;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            // 5.1 开放了 setAsynchronous 为公有方法
            message.setAsynchronous(true);
        } else {
            try {
                Method method = Message.class.getDeclaredMethod("setAsynchronous", boolean.class);
                method.invoke(message, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mHandler.sendMessageDelayed(message, 1000);
    }

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
