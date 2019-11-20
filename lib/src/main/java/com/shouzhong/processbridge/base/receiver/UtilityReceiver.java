package com.shouzhong.processbridge.base.receiver;

import com.shouzhong.processbridge.base.util.ErrorCodes;
import com.shouzhong.processbridge.base.util.ProcessBridgeException;
import com.shouzhong.processbridge.base.util.TypeUtils;
import com.shouzhong.processbridge.base.wrapper.MethodWrapper;
import com.shouzhong.processbridge.base.wrapper.ObjectWrapper;
import com.shouzhong.processbridge.base.wrapper.ParameterWrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class UtilityReceiver extends Receiver {

    private Method mMethod;

    private Class<?> mClass;

    public UtilityReceiver(ObjectWrapper objectWrapper) throws ProcessBridgeException {
        super(objectWrapper);
        Class<?> clazz = TYPE_CENTER.getClassType(objectWrapper);
        TypeUtils.validateAccessible(clazz);
        mClass = clazz;
    }

    @Override
    protected void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers)
            throws ProcessBridgeException {
        Method method = TYPE_CENTER.getMethod(mClass, methodWrapper);
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new ProcessBridgeException(ErrorCodes.ACCESS_DENIED,
                    "Only static methods can be invoked on the utility class " + mClass.getName()
                            + ". Please modify the method: " + mMethod);
        }
        TypeUtils.validateAccessible(method);
        mMethod = method;
    }

    @Override
    protected Object invokeMethod() throws ProcessBridgeException {
        Exception exception;
        try {
            return mMethod.invoke(null, getParameters());
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        }
        exception.printStackTrace();
        throw new ProcessBridgeException(ErrorCodes.METHOD_INVOCATION_EXCEPTION,
                "Error occurs when invoking method " + mMethod + ".", exception);
    }

}
