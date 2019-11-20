package com.shouzhong.processbridge.sp;

import android.text.TextUtils;

import com.shouzhong.processbridge.base.BaseProcessBridge;
import com.shouzhong.processbridge.base.ProcessBridge;
import com.shouzhong.processbridge.base.ProcessBridgeService;
import com.shouzhong.processbridge.concurrentutils.util.Action;
import com.shouzhong.processbridge.concurrentutils.util.Function;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SPProcessBridge extends BaseProcessBridge<ISPService> {
    private static volatile SPProcessBridge instance;

    private SPProcessBridge() {
        super();
    }

    public static SPProcessBridge getDefault() {
        if (instance == null) {
            synchronized (SPProcessBridge.class) {
                if (instance == null) {
                    instance = new SPProcessBridge();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        super.init();
        if (mMainProcess) {
            ProcessBridge.register(SPService.class);
            api = SPService.getInstance();
        }
    }

    @Override
    public void onProcessBridgeConnected(Class<? extends ProcessBridgeService> service) {
        ISPService spService = ProcessBridge.getInstanceInService(service, ISPService.class);
        mRemoteApis.set(spService);
        mState = STATE_CONNECTED;
    }

    @Override
    public void onProcessBridgeDisconnected(Class<? extends ProcessBridgeService> service) {
        mState = STATE_DISCONNECTED;
    }

    public Map<String, Object> getAll() {
        try {
            String s = calculateInternal(new Function<ISPService, String>() {
                @Override
                public String call(ISPService o) {
                    return o.getAll();
                }
            });
            if (TextUtils.isEmpty(s)) return null;
            JSONObject jo = new JSONObject(s);
            Map<String, Object> map = new HashMap<>();
            if (jo.has("data")) {
                JSONObject joData = jo.getJSONObject("data");
                Iterator<String> it = joData.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    map.put(key, joData.get(key));
                }
            }
            if (jo.has("set")) {
                JSONObject joSet = jo.getJSONObject("set");
                Iterator<String> it = joSet.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    JSONArray jsonArray = new JSONArray(joSet.getString(key));
                    Set<String> temp = new HashSet<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        temp.add(jsonArray.getString(i));
                    }
                    map.put(key, temp);
                }
            }
            return map.size() == 0 ? null : map;
        } catch (Exception e) {}
        return null;
    }

    public String getString(final String key, final String defValue) {
        try {
            return calculateInternal(new Function<ISPService, String>() {
                @Override
                public String call(ISPService o) {
                    return o.getString(key, defValue);
                }
            });
        } catch (Exception e) {}
        return defValue;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public Set<String> getStringSet(final String key, final Set<String> defValue) {
        try {
            final StringBuffer sb = new StringBuffer();
            if (defValue != null && defValue.size() > 0) {
                sb.append(new JSONArray(defValue).toString());
            }
            String result = calculateInternal(new Function<ISPService, String>() {
                @Override
                public String call(ISPService o) {
                    return o.getStringSet(key, sb.length() > 0 ? sb.toString() : null);
                }
            });
            if (TextUtils.isEmpty(result)) return null;
            JSONArray jsonArray = new JSONArray(result);
            int len = jsonArray.length();
            if (len == 0) return null;
            Set<String> set = new HashSet<>();
            for (int i = 0; i < len; i++) {
                set.add(jsonArray.getString(i));
            }
            return set;
        } catch (Exception e) {}
        return defValue;
    }

    public Set<String> getStringSet(String key) {
        return getStringSet(key, null);
    }

    public int getInt(final String key, final int defValue) {
        try {
            return calculateInternal(new Function<ISPService, Integer>() {
                @Override
                public Integer call(ISPService o) {
                    return o.getInt(key, defValue);
                }
            });
        } catch (Exception e) {}
        return defValue;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public long getLong(final String key, final long defValue) {
        try {
            return calculateInternal(new Function<ISPService, Long>() {
                @Override
                public Long call(ISPService o) {
                    return o.getLong(key, defValue);
                }
            });
        } catch (Exception e) {}
        return defValue;
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public float getFloat(final String key, final float defValue) {
        try {
            return calculateInternal(new Function<ISPService, Float>() {
                @Override
                public Float call(ISPService o) {
                    return o.getFloat(key, defValue);
                }
            });
        } catch (Exception e) {}
        return defValue;
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public boolean getBoolean(final String key, final boolean defValue) {
        try {
            return calculateInternal(new Function<ISPService, Boolean>() {
                @Override
                public Boolean call(ISPService o) {
                    return o.getBoolean(key, defValue);
                }
            });
        } catch (Exception e) {}
        return defValue;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean contains(final String key) {
        try {
            return calculateInternal(new Function<ISPService, Boolean>() {
                @Override
                public Boolean call(ISPService o) {
                    return o.contains(key);
                }
            });
        } catch (Exception e) {}
        return false;
    }

    public void putString(final String key, final String value) {
        actionInternal(new Action<ISPService>() {
            @Override
            public void call(ISPService o) {
                o.putString(key, value);
            }
        });
    }

    public void putStringSet(final String key, final Set<String> value) {
        actionInternal(new Action<ISPService>() {
            @Override
            public void call(ISPService o) {
                try {
                    o.putStringSet(key, new JSONArray(value).toString());
                } catch (Exception e) {}
            }
        });
    }

    public void putInt(final String key, final int value) {
        actionInternal(new Action<ISPService>() {
            @Override
            public void call(ISPService o) {
                o.putInt(key, value);
            }
        });
    }

    public void putLong(final String key, final long value) {
        actionInternal(new Action<ISPService>() {
            @Override
            public void call(ISPService o) {
                o.putLong(key, value);
            }
        });
    }

    public void putFloat(final String key, final float value) {
        actionInternal(new Action<ISPService>() {
            @Override
            public void call(ISPService o) {
                o.putFloat(key, value);
            }
        });
    }

    public void putBoolean(final String key, final boolean value) {
        actionInternal(new Action<ISPService>() {
            @Override
            public void call(ISPService o) {
                o.putBoolean(key, value);
            }
        });
    }

    public void remove(final String key) {
        actionInternal(new Action<ISPService>() {
            @Override
            public void call(ISPService o) {
                o.remove(key);
            }
        });
    }

    public void clear() {
        actionInternal(new Action<ISPService>() {
            @Override
            public void call(ISPService o) {
                o.clear();
            }
        });
    }
}
