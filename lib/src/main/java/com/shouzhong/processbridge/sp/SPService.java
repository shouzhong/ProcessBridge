package com.shouzhong.processbridge.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.shouzhong.processbridge.base.annotation.ClassId;
import com.shouzhong.processbridge.base.annotation.GetInstance;
import com.shouzhong.processbridge.base.annotation.MethodId;
import com.shouzhong.processbridge.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ClassId("SPService")
class SPService implements ISPService {

    private static volatile SPService instance;

    private SharedPreferences sp;

    private SPService() {
        sp = Utils.getApp().getSharedPreferences("SPProcessBridge", Context.MODE_PRIVATE);
    }

    @GetInstance
    public static SPService getInstance() {
        if (instance == null) {
            synchronized (SPService.class) {
                if (instance == null) {
                    instance = new SPService();
                }
            }
        }
        return instance;
    }

    @MethodId("getAll")
    @Override
    public String getAll() {
        Map<String, ?> map = sp.getAll();
        if (map == null || map.size() == 0) return null;
        try {
            JSONObject joData = new JSONObject();
//            JSONObject joSet = new JSONObject();
            Map<String, JSONArray> mapSet = new HashMap<>();
            for (String key : map.keySet()) {
                Object obj = map.get(key);
                if (obj == null) continue;
                if (obj instanceof Set) {
                    mapSet.put(key, new JSONArray((Set<String>) obj));
                } else if (obj instanceof String) {
                    joData.put(key, (String) obj);
                } else if (obj instanceof Integer) {
                    joData.put(key, (int) obj);
                } else if (obj instanceof Long) {
                    joData.put(key, (long) obj);
                } else if (obj instanceof Float) {
                    joData.put(key, (float) obj);
                } else if (obj instanceof Boolean) {
                    joData.put(key, (boolean) obj);
                }
            }
            if (joData.length() == 0 && mapSet.size() == 0) return null;
            StringBuffer sb = new StringBuffer("{");
            if (joData.length() > 0) sb.append("\"data\":").append(joData.toString());
            if (joData.length() > 0 &&  mapSet.size() > 0) sb.append(',');
            if (mapSet.size() > 0) {
                sb.append("\"set\":{");
                for (String key : mapSet.keySet()) {
                    sb.append('\"').append(key).append("\":").append(mapSet.get(key).toString()).append(',');
                }
                sb.setCharAt(sb.length() - 1, '}');
            }
            sb.append('}');
            Log.e("SPService", sb.toString());
            return sb.toString();
        } catch (Exception e) {
            Log.e("SPService", e.getMessage());
        }
        return null;
    }

    @MethodId("getString")
    @Override
    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    @MethodId("getStringSet")
    @Override
    public String getStringSet(String key, String defValue) {
        Set<String> set = null;
        if (!TextUtils.isEmpty(defValue)) {
            try {
                JSONArray jsonArray = new JSONArray(defValue);
                int len = jsonArray.length();
                if (len > 0) {
                    set = new HashSet<>();
                    for (int i = 0; i < len; i++) {
                        set.add(jsonArray.getString(i));
                    }
                }
            } catch (Exception e) {}
        }
        try {
            Set<String> result = sp.getStringSet(key, set);
            if (result == null || result.size() == 0) return null;
            return new JSONArray(result).toString();
        } catch (Exception e) {
        }
        return defValue;
    }

    @MethodId("getInt")
    @Override
    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    @MethodId("getLong")
    @Override
    public long getLong(String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    @MethodId("getFloat")
    @Override
    public float getFloat(String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

    @MethodId("getBoolean")
    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    @MethodId("contains")
    @Override
    public boolean contains(String key) {
        return sp.contains(key);
    }

    @MethodId("putString")
    @Override
    public void putString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    @MethodId("putStringSet")
    @Override
    public void putStringSet(String key, String value) {
        Set<String> set = null;
        if (!TextUtils.isEmpty(value)) {
            try {
                JSONArray jsonArray = new JSONArray(value);
                int len = jsonArray.length();
                if (len > 0) {
                    set = new HashSet<>();
                    for (int i = 0; i < len; i++) {
                        set.add(jsonArray.getString(i));
                    }
                }
            } catch (Exception e) {}
        }
        sp.edit().putStringSet(key, set).apply();
    }

    @MethodId("putInt")
    @Override
    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    @MethodId("putLong")
    @Override
    public void putLong(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    @MethodId("putFloat")
    @Override
    public void putFloat(String key, float value) {
        sp.edit().putFloat(key, value).apply();
    }

    @MethodId("putBoolean")
    @Override
    public void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    @MethodId("remove")
    @Override
    public void remove(String key) {
        sp.edit().remove(key).apply();
    }

    @MethodId("clear")
    @Override
    public void clear() {
        sp.edit().clear().apply();
    }
}
