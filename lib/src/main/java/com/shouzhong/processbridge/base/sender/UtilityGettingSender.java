package com.shouzhong.processbridge.base.sender;

import com.shouzhong.processbridge.base.ProcessBridgeService;
import com.shouzhong.processbridge.base.wrapper.MethodWrapper;
import com.shouzhong.processbridge.base.wrapper.ObjectWrapper;
import com.shouzhong.processbridge.base.wrapper.ParameterWrapper;

import java.lang.reflect.Method;

public class UtilityGettingSender extends Sender {

    public UtilityGettingSender(Class<? extends ProcessBridgeService> service, ObjectWrapper object) {
        super(service, object);
    }

    @Override
    protected MethodWrapper getMethodWrapper(Method method, ParameterWrapper[] parameterWrappers) {
        return null;
    }
}
