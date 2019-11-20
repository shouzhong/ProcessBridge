package com.shouzhong.processbridge.base.receiver;

import com.shouzhong.processbridge.base.util.ErrorCodes;
import com.shouzhong.processbridge.base.util.ProcessBridgeException;
import com.shouzhong.processbridge.base.util.TypeUtils;
import com.shouzhong.processbridge.base.wrapper.MethodWrapper;
import com.shouzhong.processbridge.base.wrapper.ObjectWrapper;
import com.shouzhong.processbridge.base.wrapper.ParameterWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class InstanceCreatingReceiver extends Receiver {

    private Class<?> mObjectClass;

    private Constructor<?> mConstructor;

    public InstanceCreatingReceiver(ObjectWrapper object) throws ProcessBridgeException {
        super(object);
        Class<?> clazz = TYPE_CENTER.getClassType(object);
        TypeUtils.validateAccessible(clazz);
        mObjectClass = clazz;
    }

    @Override
    public void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers) throws ProcessBridgeException {
        Constructor<?> constructor = TypeUtils.getConstructor(mObjectClass, TYPE_CENTER.getClassTypes(parameterWrappers));
        TypeUtils.validateAccessible(constructor);
        mConstructor = constructor;
    }

    @Override
    protected Object invokeMethod() throws ProcessBridgeException {
        Exception exception;
        try {
            Object object;
            Object[] parameters = getParameters();
            if (parameters == null) {
                object = mConstructor.newInstance();
            } else {
                object = mConstructor.newInstance(parameters);
            }
            OBJECT_CENTER.putObject(getObjectTimeStamp(), object);
            return null;
        } catch (InstantiationException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        }
        exception.printStackTrace();
        throw new ProcessBridgeException(ErrorCodes.METHOD_INVOCATION_EXCEPTION,
                "Error occurs when invoking constructor to create an instance of "
                        + mObjectClass.getName(), exception);
    }
}
