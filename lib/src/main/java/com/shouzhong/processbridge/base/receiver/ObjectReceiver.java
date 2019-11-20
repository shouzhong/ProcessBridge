package com.shouzhong.processbridge.base.receiver;

import com.shouzhong.processbridge.base.util.ErrorCodes;
import com.shouzhong.processbridge.base.util.ProcessBridgeException;
import com.shouzhong.processbridge.base.util.TypeUtils;
import com.shouzhong.processbridge.base.wrapper.MethodWrapper;
import com.shouzhong.processbridge.base.wrapper.ObjectWrapper;
import com.shouzhong.processbridge.base.wrapper.ParameterWrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectReceiver extends Receiver {

    private Method mMethod;

    private Object mObject;

    public ObjectReceiver(ObjectWrapper objectWrapper) {
        super(objectWrapper);
        mObject = OBJECT_CENTER.getObject(getObjectTimeStamp());
    }

    @Override
    public void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers)
            throws ProcessBridgeException {
        Method method = TYPE_CENTER.getMethod(mObject.getClass(), methodWrapper);
        TypeUtils.validateAccessible(method);
        mMethod = method;
    }

    @Override
    protected Object invokeMethod() throws ProcessBridgeException {
        Exception exception;
        try {
            return mMethod.invoke(mObject, getParameters());
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        }
        exception.printStackTrace();
        throw new ProcessBridgeException(ErrorCodes.METHOD_INVOCATION_EXCEPTION,
                "Error occurs when invoking method " + mMethod + " on " + mObject, exception);
    }
}
