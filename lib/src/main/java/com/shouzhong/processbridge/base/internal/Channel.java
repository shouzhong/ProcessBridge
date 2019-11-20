package com.shouzhong.processbridge.base.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.util.Pair;

import com.shouzhong.processbridge.base.ProcessBridgeListener;
import com.shouzhong.processbridge.base.ProcessBridgeService;
import com.shouzhong.processbridge.base.util.CallbackManager;
import com.shouzhong.processbridge.base.util.CodeUtils;
import com.shouzhong.processbridge.base.util.ErrorCodes;
import com.shouzhong.processbridge.base.util.ProcessBridgeException;
import com.shouzhong.processbridge.base.util.TypeCenter;
import com.shouzhong.processbridge.base.wrapper.ParameterWrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Channel {

    private static final String TAG = "CHANNEL";

    private static volatile Channel sInstance = null;

    private final ConcurrentHashMap<Class<? extends ProcessBridgeService>, IProcessBridgeService> mProcessBridgeServices = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<? extends ProcessBridgeService>, ProcessBridgeServiceConnection> mProcessBridgeServiceConnections = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<? extends ProcessBridgeService>, Boolean> mBindings = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<? extends ProcessBridgeService>, Boolean> mBounds = new ConcurrentHashMap<>();

    private ProcessBridgeListener mListener = null;

    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    private static final CallbackManager CALLBACK_MANAGER = CallbackManager.getInstance();

    private static final TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    private IProcessBridgeServiceCallback mProcessBridgeServiceCallback = new IProcessBridgeServiceCallback.Stub() {

        private Object[] getParameters(ParameterWrapper[] parameterWrappers) throws ProcessBridgeException {
            if (parameterWrappers == null) {
                parameterWrappers = new ParameterWrapper[0];
            }
            int length = parameterWrappers.length;
            Object[] result = new Object[length];
            for (int i = 0; i < length; ++i) {
                ParameterWrapper parameterWrapper = parameterWrappers[i];
                if (parameterWrapper == null) {
                    result[i] = null;
                } else {
                    Class<?> clazz = TYPE_CENTER.getClassType(parameterWrapper);

                    String data = parameterWrapper.getData();
                    if (data == null) {
                        result[i] = null;
                    } else {
                        result[i] = CodeUtils.decode(data, clazz);
                    }
                }
            }
            return result;
        }

        public Reply callback(CallbackMail mail) {
            final Pair<Boolean, Object> pair = CALLBACK_MANAGER.getCallback(mail.getTimeStamp(), mail.getIndex());
            if (pair == null) {
                return null;
            }
            final Object callback = pair.second;
            if (callback == null) {
                return new Reply(ErrorCodes.CALLBACK_NOT_ALIVE, "");
            }
            boolean uiThread = pair.first;
            try {
                // TODO Currently, the callback should not be annotated!
                final Method method = TYPE_CENTER.getMethod(callback.getClass(), mail.getMethod());
                final Object[] parameters = getParameters(mail.getParameters());
                Object result = null;
                Exception exception = null;
                if (uiThread) {
                    boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
                    if (isMainThread) {
                        try {
                            result = method.invoke(callback, parameters);
                        } catch (IllegalAccessException e) {
                            exception = e;
                        } catch (InvocationTargetException e) {
                            exception = e;
                        }
                    } else {
                        mUiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    method.invoke(callback, parameters);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        return null;
                    }
                } else {
                    try {
                        result = method.invoke(callback, parameters);
                    } catch (IllegalAccessException e) {
                        exception = e;
                    } catch (InvocationTargetException e) {
                        exception = e;
                    }
                }
                if (exception != null) {
                    exception.printStackTrace();
                    throw new ProcessBridgeException(ErrorCodes.METHOD_INVOCATION_EXCEPTION,
                            "Error occurs when invoking method " + method + " on " + callback, exception);
                }
                if (result == null) {
                    return null;
                }
                return new Reply(new ParameterWrapper(result));
            } catch (ProcessBridgeException e) {
                e.printStackTrace();
                return new Reply(e.getErrorCode(), e.getErrorMessage());
            }
        }

        @Override
        public void gc(List<Long> timeStamps, List<Integer> indexes) throws RemoteException {
            int size = timeStamps.size();
            for (int i = 0; i < size; ++i) {
                CALLBACK_MANAGER.removeCallback(timeStamps.get(i), indexes.get(i));
            }
        }
    };

    private Channel() {

    }

    public static Channel getInstance() {
        if (sInstance == null) {
            synchronized (Channel.class) {
                if (sInstance == null) {
                    sInstance = new Channel();
                }
            }
        }
        return sInstance;
    }

    public void bind(Context context, String packageName, Class<? extends ProcessBridgeService> service) {
        ProcessBridgeServiceConnection connection;
        synchronized (this) {
            if (getBound(service)) {
                return;
            }
            Boolean binding = mBindings.get(service);
            if (binding != null && binding) {
                return;
            }
            mBindings.put(service, true);
            connection = new ProcessBridgeServiceConnection(service);
            mProcessBridgeServiceConnections.put(service, connection);
        }
        Intent intent;
        if (TextUtils.isEmpty(packageName)) {
            intent = new Intent(context, service);
        } else {
            intent = new Intent();
            intent.setClassName(packageName, service.getName());
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unbind(Context context, Class<? extends ProcessBridgeService> service) {
        synchronized (this) {
            Boolean bound = mBounds.get(service);
            if (bound != null && bound) {
                ProcessBridgeServiceConnection connection = mProcessBridgeServiceConnections.get(service);
                if (connection != null) {
                    context.unbindService(connection);
                }
                mBounds.put(service, false);
            }
        }
    }

    public Reply send(Class<? extends ProcessBridgeService> service, Mail mail) {
        IProcessBridgeService processBridgeService = mProcessBridgeServices.get(service);
        try {
            if (processBridgeService == null) {
                return new Reply(ErrorCodes.SERVICE_UNAVAILABLE,
                        "Service Unavailable: Check whether you have connected ProcessBridge.");
            }
            return processBridgeService.send(mail);
        } catch (RemoteException e) {
            return new Reply(ErrorCodes.REMOTE_EXCEPTION, "Remote Exception: Check whether "
                    + "the process you are communicating with is still alive.");
        }
    }

    public void gc(Class<? extends ProcessBridgeService> service, List<Long> timeStamps) {
        IProcessBridgeService processBridgeService = mProcessBridgeServices.get(service);
        if (processBridgeService == null) {
            Log.e(TAG, "Service Unavailable: Check whether you have disconnected the service before a process dies.");
        } else {
            try {
                processBridgeService.gc(timeStamps);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getBound(Class<? extends ProcessBridgeService> service) {
        Boolean bound = mBounds.get(service);
        return bound != null && bound;
    }

    public void setProcessBridgeListener(ProcessBridgeListener listener) {
        mListener = listener;
    }

    public boolean isConnected(Class<? extends ProcessBridgeService> service) {
        IProcessBridgeService processBridgeService = mProcessBridgeServices.get(service);
        return processBridgeService != null && processBridgeService.asBinder().pingBinder();
    }

    private class ProcessBridgeServiceConnection implements ServiceConnection {

        private Class<? extends ProcessBridgeService> mClass;

        ProcessBridgeServiceConnection(Class<? extends ProcessBridgeService> service) {
            mClass = service;
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            synchronized (Channel.this) {
                mBounds.put(mClass, true);
                mBindings.put(mClass, false);
                IProcessBridgeService processBridgeService = IProcessBridgeService.Stub.asInterface(service);
                mProcessBridgeServices.put(mClass, processBridgeService);
                try {
                    processBridgeService.register(mProcessBridgeServiceCallback, Process.myPid());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Remote Exception: Check whether "
                            + "the process you are communicating with is still alive.");
                    return;
                }
            }
            if (mListener != null) {
                mListener.onProcessBridgeConnected(mClass);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            synchronized (Channel.this) {
                mProcessBridgeServices.remove(mClass);
                mBounds.put(mClass, false);
                mBindings.put(mClass, false);
            }
            if (mListener != null) {
                mListener.onProcessBridgeDisconnected(mClass);
            }
        }
    }
}
