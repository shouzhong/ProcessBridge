package com.shouzhong.processbridge.sp;

import com.shouzhong.processbridge.base.annotation.ClassId;
import com.shouzhong.processbridge.base.annotation.MethodId;

@ClassId("SPService")
interface ISPService {

    @MethodId("getAll")
    String getAll();

    @MethodId("getString")
    String getString(String key, String defValue);

    @MethodId("getStringSet")
    String getStringSet(String key, String defValue);

    @MethodId("getInt")
    int getInt(String key, int defValue);

    @MethodId("getLong")
    long getLong(String key, long defValue);

    @MethodId("getFloat")
    float getFloat(String key, float defValue);

    @MethodId("getBoolean")
    boolean getBoolean(String key, boolean defValue);

    @MethodId("contains")
    boolean contains(String key);

    @MethodId("putString")
    void putString(String key, String value);

    @MethodId("putStringSet")
    void putStringSet(String key, String value);

    @MethodId("putInt")
    void putInt(String key, int value);

    @MethodId("putLong")
    void putLong(String key, long value);

    @MethodId("putFloat")
    void putFloat(String key, float value);

    @MethodId("putBoolean")
    void putBoolean(String key, boolean value);

    @MethodId("remove")
    void remove(String key);

    @MethodId("clear")
    void clear();
}
