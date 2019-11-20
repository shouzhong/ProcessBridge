package com.shouzhong.processbridge.base.receiver;

import android.content.Context;

import com.shouzhong.processbridge.base.ProcessBridge;
import com.shouzhong.processbridge.base.internal.IProcessBridgeServiceCallback;
import com.shouzhong.processbridge.base.internal.ProcessBridgeCallbackInvocationHandler;
import com.shouzhong.processbridge.base.internal.Reply;
import com.shouzhong.processbridge.base.util.CodeUtils;
import com.shouzhong.processbridge.base.util.ObjectCenter;
import com.shouzhong.processbridge.base.util.ProcessBridgeCallbackGc;
import com.shouzhong.processbridge.base.util.ProcessBridgeException;
import com.shouzhong.processbridge.base.util.TypeCenter;
import com.shouzhong.processbridge.base.wrapper.MethodWrapper;
import com.shouzhong.processbridge.base.wrapper.ObjectWrapper;
import com.shouzhong.processbridge.base.wrapper.ParameterWrapper;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class Receiver {

    protected static final ObjectCenter OBJECT_CENTER = ObjectCenter.getInstance();

    protected static final TypeCenter TYPE_CENTER = TypeCenter.getInstance();

    protected static final ProcessBridgeCallbackGc CALLBACK_GC = ProcessBridgeCallbackGc.getInstance();

    private long mObjectTimeStamp;

    private Object[] mParameters;

    private IProcessBridgeServiceCallback mCallback;

    public Receiver(ObjectWrapper objectWrapper) {
        mObjectTimeStamp = objectWrapper.getTimeStamp();
    }

    protected long getObjectTimeStamp() {
        return mObjectTimeStamp;
    }

    protected Object[] getParameters() {
        return mParameters;
    }

    public void setProcessBridgeServiceCallback(IProcessBridgeServiceCallback callback) {
        mCallback = callback;
    }

    private Object getProxy(Class<?> clazz, int index, long methodInvocationTimeStamp) {
        return Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                new ProcessBridgeCallbackInvocationHandler(methodInvocationTimeStamp, index, mCallback));
    }

    private static void registerCallbackReturnTypes(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            TYPE_CENTER.register(method.getReturnType());
        }
    }

    private void setParameters(long methodInvocationTimeStamp, ParameterWrapper[] parameterWrappers) throws ProcessBridgeException {
        if (parameterWrappers == null) {
            mParameters = null;
        } else {
            int length = parameterWrappers.length;
            mParameters = new Object[length];
            for (int i = 0; i < length; ++i) {
                ParameterWrapper parameterWrapper = parameterWrappers[i];
                if (parameterWrapper == null) {
                    mParameters[i] = null;
                } else {
                    Class<?> clazz = TYPE_CENTER.getClassType(parameterWrapper);
                    if (clazz != null && clazz.isInterface()) {
                        registerCallbackReturnTypes(clazz); //****
                        mParameters[i] = getProxy(clazz, i, methodInvocationTimeStamp);
                        CALLBACK_GC.register(mCallback, mParameters[i], methodInvocationTimeStamp, i);
                    } else if (clazz != null && Context.class.isAssignableFrom(clazz)) {
                        mParameters[i] = ProcessBridge.getContext();
                    } else {
                        String data = parameterWrapper.getData();
                        if (data == null) {
                            mParameters[i] = null;
                        } else {
                            mParameters[i] = CodeUtils.decode(data, clazz);
                        }
                    }
                }
            }
        }
    }

    protected abstract void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers)
            throws ProcessBridgeException;

    protected abstract Object invokeMethod() throws ProcessBridgeException;

    public final Reply action(long methodInvocationTimeStamp, MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers) throws ProcessBridgeException{
        setMethod(methodWrapper, parameterWrappers);
        setParameters(methodInvocationTimeStamp, parameterWrappers);
        Object result = invokeMethod();
        if (result == null) {
            return null;
        } else {
            return new Reply(new ParameterWrapper(result));
        }
    }

}
