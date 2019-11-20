package com.shouzhong.processbridge.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shouzhong.processbridge.ProcessBridgeUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SPActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp);
        tv = findViewById(R.id.tv);
    }

    public void onClickAll(View v) {
        Map<String, Object> map = ProcessBridgeUtils.getSP().getAll();
        tv.setText("All=" + (map == null ? 0 : map.size()));
    }

    public void onClickString(View v) {
        ProcessBridgeUtils.getSP().putString("String", "String");
        tv.setText("String=" + ProcessBridgeUtils.getSP().getString("String"));
    }

    public void onClickStringSet(View v) {
        Set<String> set = new HashSet<>();
        set.add("a");
        set.add("b");
        ProcessBridgeUtils.getSP().putStringSet("Set", set);
        Set<String> temp = ProcessBridgeUtils.getSP().getStringSet("Set");
        tv.setText("StringSet=" + (temp == null ? 0 : temp.size()));
    }

    public void onClickInt(View v) {
        ProcessBridgeUtils.getSP().putInt("Int", 1);
        tv.setText("Int=" + ProcessBridgeUtils.getSP().getInt("Int"));
    }

    public void onClickLong(View v) {
        ProcessBridgeUtils.getSP().putLong("Long", 1L);
        tv.setText("Long=" + ProcessBridgeUtils.getSP().getLong("Long"));
    }

    public void onClickFloat(View v) {
        ProcessBridgeUtils.getSP().putFloat("Float", 1.0f);
        tv.setText("Float=" + ProcessBridgeUtils.getSP().getFloat("Float"));
    }

    public void onClickBoolean(View v) {
        ProcessBridgeUtils.getSP().putBoolean("Boolean", true);
        tv.setText("Boolean=" + ProcessBridgeUtils.getSP().getBoolean("Boolean"));
    }

    public void onClickContains(View v) {
        boolean b = ProcessBridgeUtils.getSP().contains("String");
        tv.setText("Contains=" + b);
    }

    public void onClickRemove(View v) {
        ProcessBridgeUtils.getSP().remove("String");
        String s = ProcessBridgeUtils.getSP().getString("String");
        tv.setText("Remove=" + (s == null));
    }

    public void onClickClear(View v) {
        ProcessBridgeUtils.getSP().clear();
        Map<String, Object> map = ProcessBridgeUtils.getSP().getAll();
        tv.setText("Clear=" + (map == null));
    }

    public void onClickNewPage(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void a(String s) {
        tv.setText(s);
    }
}
