package com.shouzhong.processbridge.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.os.Process;

import java.lang.reflect.Method;
import java.util.List;

public class Utils {

    public static Application getApp() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) throw new NullPointerException("error");
            return (Application) app;
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }
    }

    public static boolean isProcessExist(int pid) {
        ActivityManager am = (ActivityManager) getApp().getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return false;
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : lists) {
            if (appProcess.pid == pid) return true;
        }
        return false;
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

    public static boolean isMainProcess() {
        return getApp().getPackageName().equals(getCurrentProcessName());
    }

    public static String getCurrentProcessName() {
        ActivityManager activityManager = (ActivityManager) getApp().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : activityManager.getRunningAppProcesses()) {
            if (processInfo.pid == Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    public static int hashCode(Object obj) {
        if (obj == null) throw new NullPointerException();
        try {
            Method m = Object.class.getDeclaredMethod("identityHashCode", Object.class);
            m.setAccessible(true);
            int hashCode = (int) m.invoke(null, obj);
            return hashCode;
        } catch (Exception e) {}
        return obj.hashCode();
    }
}
