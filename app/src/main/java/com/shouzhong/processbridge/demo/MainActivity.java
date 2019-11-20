package com.shouzhong.processbridge.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.shouzhong.processbridge.ProcessBridgeUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        ProcessBridgeUtils.getEventBus().register(this);
    }

    public void onClickPost(View v) {
        ProcessBridgeUtils.getEventBus().post("123");
    }

    public void onClickPostSticky(View v) {
        ProcessBridgeUtils.getEventBus().postSticky("456");
    }

    public void onClickRemoveStickyEvent(View v) {
        try {
            String s = ProcessBridgeUtils.getEventBus().getStickyEvent(String.class);
            boolean b = ProcessBridgeUtils.getEventBus().removeStickyEvent(s);
            tv.setText("RemoveStickyEvent=" + b);
        } catch (Exception e) {}
    }

    public void onClickRemoveAllStickyEvents(View v) {
        ProcessBridgeUtils.getEventBus().removeAllStickyEvents();
    }

    public void onClickHasSubscriberForEvent(View v) {
        try {
            boolean b = ProcessBridgeUtils.getEventBus().hasSubscriberForEvent(String.class);
            tv.setText("HasSubscriberForEvent=" + b);
        } catch (Exception e) {}
    }

    public void onClickNewPage(View v) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void a(String s) {
        tv.setText(s);
    }
}
