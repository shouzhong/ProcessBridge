package com.shouzhong.processbridge.activity;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.shouzhong.processbridge.util.Utils;

class ActivityInfoBean {

    public int id;
    public String className;

    public ActivityInfoBean(Activity activity) {
        id = Utils.hashCode(activity);
        className = activity.getClass().getName();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof ActivityInfoBean)) return false;
        ActivityInfoBean b = (ActivityInfoBean) obj;
        return id == b.id && TextUtils.equals(className, b.className);
    }
}
