package com.shouzhong.processbridge.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shouzhong.processbridge.ProcessBridgeUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SecondActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        tv = findViewById(R.id.tv);
        ProcessBridgeUtils.getEventBus().register(this);
    }

    public void onClickSize(View v) {
        try {
            int size = ProcessBridgeUtils.getActivityManager().size();
            tv.setText("size=" + size);
        } catch (Exception e) {}
    }

    public void onClickSizeInt(View v) {
        try {
            int size = ProcessBridgeUtils.getActivityManager().size(Process.myPid());
            tv.setText("pid=" + Process.myPid() + ";size=" + size);
        } catch (Exception e) {}
    }

    public void onClickContains(View v) {
        try {
            boolean b = ProcessBridgeUtils.getActivityManager().contains(MainActivity.class);
            tv.setText("class=" + MainActivity.class.getName() + ";contains=" + b);
        } catch (Exception e) {}
    }

    public void onClickContainsInt(View v) {
        try {
            boolean b = ProcessBridgeUtils.getActivityManager().contains(Process.myPid(), SecondActivity.class);
            tv.setText("pid=" + Process.myPid() + ";class=" + SecondActivity.class.getName() + ";contains=" + b);
        } catch (Exception e) {}
    }

    public void onClickGetClass(View v) {
        Activity activity = ProcessBridgeUtils.getActivityManager().get(SecondActivity.class);
        tv.setText(activity.getClass().getName());
    }

    public void onClickGetInt(View v) {
        Activity activity = ProcessBridgeUtils.getActivityManager().get(0);
        tv.setText(activity.getClass().getName());
    }

    public void onClickFinish(View v) {
        ProcessBridgeUtils.getActivityManager().finish(MainActivity.class);
    }

    public void onClickExitInt(View v) {
        ProcessBridgeUtils.getActivityManager().exit(Process.myPid());
    }

    public void onClickExit(View v) {
        ProcessBridgeUtils.getActivityManager().exit();
    }

    public void onClickNewPage(View v) {
        Intent intent = new Intent(this, SPActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void a(String s) {
        try {
            boolean b = ProcessBridgeUtils.getEventBus().removeStickyEvent(s);
            tv.setText("removeStickyEvent=" + b + ";data=" + s);
        } catch (Exception e) {}

    }
}
