package com.shouzhong.processbridge.base.receiver;

import com.shouzhong.processbridge.base.util.ProcessBridgeException;
import com.shouzhong.processbridge.base.util.TypeUtils;
import com.shouzhong.processbridge.base.wrapper.MethodWrapper;
import com.shouzhong.processbridge.base.wrapper.ObjectWrapper;
import com.shouzhong.processbridge.base.wrapper.ParameterWrapper;

public class UtilityGettingReceiver extends Receiver {

    public UtilityGettingReceiver(ObjectWrapper objectWrapper) throws ProcessBridgeException {
        super(objectWrapper);
        Class<?> clazz = TYPE_CENTER.getClassType(objectWrapper);
        TypeUtils.validateAccessible(clazz);
    }

    @Override
    protected void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers) {

    }

    @Override
    protected Object invokeMethod() {
        return null;
    }

}
