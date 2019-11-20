package com.shouzhong.processbridge.base.sender;

import com.shouzhong.processbridge.base.ProcessBridgeService;
import com.shouzhong.processbridge.base.util.CodeUtils;
import com.shouzhong.processbridge.base.util.ErrorCodes;
import com.shouzhong.processbridge.base.util.ProcessBridgeException;
import com.shouzhong.processbridge.base.wrapper.MethodWrapper;
import com.shouzhong.processbridge.base.wrapper.ObjectWrapper;
import com.shouzhong.processbridge.base.wrapper.ParameterWrapper;

import java.lang.reflect.Method;

public class InstanceGettingSender extends Sender {

    public InstanceGettingSender(Class<? extends ProcessBridgeService> service, ObjectWrapper object) {
        super(service, object);
    }

    @Override
    protected void setParameterWrappers(ParameterWrapper[] parameterWrappers) {
        int length = parameterWrappers.length;
        ParameterWrapper[] tmp = new ParameterWrapper[length - 1];
        for (int i = 1; i < length; ++i) {
            tmp[i - 1] = parameterWrappers[i];
        }
        super.setParameterWrappers(tmp);
    }

    @Override
    protected MethodWrapper getMethodWrapper(Method method, ParameterWrapper[] parameterWrappers) throws ProcessBridgeException {
        ParameterWrapper parameterWrapper = parameterWrappers[0];
        String methodName;
        try {
            methodName = CodeUtils.decode(parameterWrapper.getData(), String.class);
        } catch (ProcessBridgeException e) {
            e.printStackTrace();
            throw new ProcessBridgeException(ErrorCodes.GSON_DECODE_EXCEPTION,
                    "Error occurs when decoding the method name.");
        }
        int length = parameterWrappers.length;
        Class<?>[] parameterTypes = new Class[length - 1];
        for (int i = 1; i < length; ++i) {
            parameterWrapper = parameterWrappers[i];
            parameterTypes[i - 1] = parameterWrapper == null ? null : parameterWrapper.getClassType();
        }
        return new MethodWrapper(methodName, parameterTypes);
    }
}
