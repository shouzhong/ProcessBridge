package com.shouzhong.processbridge.base;

import android.content.Context;
import android.util.Log;

import com.shouzhong.processbridge.base.internal.Channel;
import com.shouzhong.processbridge.base.internal.ProcessBridgeInvocationHandler;
import com.shouzhong.processbridge.base.internal.Reply;
import com.shouzhong.processbridge.base.sender.Sender;
import com.shouzhong.processbridge.base.sender.SenderDesignator;
import com.shouzhong.processbridge.base.util.ProcessBridgeException;
import com.shouzhong.processbridge.base.util.ProcessBridgeGc;
import com.shouzhong.processbridge.base.util.TypeCenter;
import com.shouzhong.processbridge.base.util.TypeUtils;
import com.shouzhong.processbridge.base.wrapper.ObjectWrapper;

import java.lang.reflect.Proxy;

public class ProcessBridge {

    private static final String TAG = "ProcessBridge";

    private static final TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    private static final Channel CHANNEL = Channel.getInstance();

    private static final ProcessBridgeGc GC = ProcessBridgeGc.getInstance();

    private static Context sContext = null;

    public static void register(Object object) {
        register(object.getClass());
    }

    private static void checkInit() {
        if (sContext == null) {
            throw new IllegalStateException("ProcessBridge has not been initialized.");
        }
    }

    /**
     * There is no need to register class in local process!
     *
     * But if the returned type of a method is not exactly the same with the return type of the method, it should be registered.
     * @param clazz
     */
    public static void register(Class<?> clazz) {
        checkInit();
        TYPE_CENTER.register(clazz);
    }

    public static Context getContext() {
        return sContext;
    }

    public static void init(Context context) {
        if (sContext != null) {
            return;
        }
        sContext = context.getApplicationContext();
    }

    private static void checkBound(Class<? extends ProcessBridgeService> service) {
        if (!CHANNEL.getBound(service)) {
            throw new IllegalStateException("Service Unavailable: You have not connected the service "
                    + "or the connection is not completed. You can set ProcessBridgeListener to receive a callback "
                    + "when the connection is completed.");
        }
    }

    private static <T> T getProxy(Class<? extends ProcessBridgeService> service, ObjectWrapper object) {
        Class<?> clazz = object.getObjectClass();
        T proxy =  (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},
                    new ProcessBridgeInvocationHandler(service, object));
        GC.register(service, proxy, object.getTimeStamp());
        return proxy;
    }

    public static <T> T newInstance(Class<T> clazz, Object... parameters) {
        return newInstanceInService(ProcessBridgeService.ProcessBridgeService0.class, clazz, parameters);
    }

    public static <T> T newInstanceInService(Class<? extends ProcessBridgeService> service, Class<T> clazz, Object... parameters) {
        TypeUtils.validateServiceInterface(clazz);
        checkBound(service);
        ObjectWrapper object = new ObjectWrapper(clazz, ObjectWrapper.TYPE_OBJECT_TO_NEW);
        Sender sender = SenderDesignator.getPostOffice(service, SenderDesignator.TYPE_NEW_INSTANCE, object);
        try {
            Reply reply = sender.send(null, parameters);
            if (reply != null && !reply.success()) {
                Log.e(TAG, "Error occurs during creating instance. Error code: " + reply.getErrorCode());
                Log.e(TAG, "Error message: " + reply.getMessage());
                return null;
            }
        } catch (ProcessBridgeException e) {
            e.printStackTrace();
            return null;
        }
        object.setType(ObjectWrapper.TYPE_OBJECT);
        return getProxy(service, object);
    }

    public static <T> T getInstanceInService(Class<? extends ProcessBridgeService> service, Class<T> clazz, Object... parameters) {
        return getInstanceWithMethodNameInService(service, clazz, "", parameters);
    }

    public static <T> T getInstance(Class<T> clazz, Object... parameters) {
        return getInstanceInService(ProcessBridgeService.ProcessBridgeService0.class, clazz, parameters);
    }

    public static <T> T getInstanceWithMethodName(Class<T> clazz, String methodName, Object... parameters) {
        return getInstanceWithMethodNameInService(ProcessBridgeService.ProcessBridgeService0.class, clazz, methodName, parameters);
    }

    public static <T> T getInstanceWithMethodNameInService(Class<? extends ProcessBridgeService> service, Class<T> clazz, String methodName, Object... parameters) {
        TypeUtils.validateServiceInterface(clazz);
        checkBound(service);
        ObjectWrapper object = new ObjectWrapper(clazz, ObjectWrapper.TYPE_OBJECT_TO_GET);
        Sender sender = SenderDesignator.getPostOffice(service, SenderDesignator.TYPE_GET_INSTANCE, object);
        if (parameters == null) {
            parameters = new Object[0];
        }
        int length = parameters.length;
        Object[] tmp = new Object[length + 1];
        tmp[0] = methodName;
        for (int i = 0; i < length; ++i) {
            tmp[i + 1] = parameters[i];
        }
        try {
            Reply reply = sender.send(null, tmp);
            if (reply != null && !reply.success()) {
                Log.e(TAG, "Error occurs during getting instance. Error code: " + reply.getErrorCode());
                Log.e(TAG, "Error message: " + reply.getMessage());
                return null;
            }
        } catch (ProcessBridgeException e) {
            e.printStackTrace();
            return null;
        }
        object.setType(ObjectWrapper.TYPE_OBJECT);
        return getProxy(service, object);
    }

    public static <T> T getUtilityClass(Class<T> clazz) {
        return getUtilityClassInService(ProcessBridgeService.ProcessBridgeService0.class, clazz);
    }

    public static <T> T getUtilityClassInService(Class<? extends ProcessBridgeService> service, Class<T> clazz) {
        TypeUtils.validateServiceInterface(clazz);
        checkBound(service);
        ObjectWrapper object = new ObjectWrapper(clazz, ObjectWrapper.TYPE_CLASS_TO_GET);
        Sender sender = SenderDesignator.getPostOffice(service, SenderDesignator.TYPE_GET_UTILITY_CLASS, object);
        try {
            Reply reply = sender.send(null, null);
            if (reply != null && !reply.success()) {
                Log.e(TAG, "Error occurs during getting utility class. Error code: " + reply.getErrorCode());
                Log.e(TAG, "Error message: " + reply.getMessage());
                return null;
            }
        } catch (ProcessBridgeException e) {
            e.printStackTrace();
            return null;
        }
        object.setType(ObjectWrapper.TYPE_CLASS);
        return getProxy(service, object);
    }

    public static void connect(Context context) {
        connectApp(context, null, ProcessBridgeService.ProcessBridgeService0.class);
    }

    public static void connect(Context context, Class<? extends ProcessBridgeService> service) {
        // It seems that callbacks may not be registered.
        connectApp(context, null, service);
    }

    public static void connectApp(Context context, String packageName) {
        connectApp(context, packageName, ProcessBridgeService.ProcessBridgeService0.class);
    }

    public static void connectApp(Context context, String packageName, Class<? extends ProcessBridgeService> service) {
        init(context);
        CHANNEL.bind(context.getApplicationContext(), packageName, service);
    }

    public static void disconnect(Context context) {
        disconnect(context, ProcessBridgeService.ProcessBridgeService0.class);
    }

    public static void disconnect(Context context, Class<? extends ProcessBridgeService> service) {
        CHANNEL.unbind(context.getApplicationContext(), service);
    }

    public static boolean isConnected() {
        return isConnected(ProcessBridgeService.ProcessBridgeService0.class);
    }

    public static boolean isConnected(Class<? extends ProcessBridgeService> service) {
        return CHANNEL.isConnected(service);
    }

    public static void setProcessBridgeListener(ProcessBridgeListener listener) {
        CHANNEL.setProcessBridgeListener(listener);
    }

}
