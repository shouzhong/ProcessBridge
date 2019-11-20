package com.shouzhong.processbridge.base.sender;

import com.shouzhong.processbridge.base.ProcessBridgeService;
import com.shouzhong.processbridge.base.wrapper.MethodWrapper;
import com.shouzhong.processbridge.base.wrapper.ObjectWrapper;
import com.shouzhong.processbridge.base.wrapper.ParameterWrapper;

import java.lang.reflect.Method;

public class InstanceCreatingSender extends Sender {

    private Class<?>[] mConstructorParameterTypes;

    public InstanceCreatingSender(Class<? extends ProcessBridgeService> service, ObjectWrapper object) {
        super(service, object);
    }

    @Override
    protected MethodWrapper getMethodWrapper(Method method, ParameterWrapper[] parameterWrappers) {
        int length = parameterWrappers == null ? 0 : parameterWrappers.length;
        mConstructorParameterTypes = new Class<?>[length];
        for (int i = 0; i < length; ++i) {
            try {
                ParameterWrapper parameterWrapper = parameterWrappers[i];
                mConstructorParameterTypes[i] = parameterWrapper == null ? null : parameterWrapper.getClassType();
            } catch (Exception e) {

            }
        }
        return new MethodWrapper(mConstructorParameterTypes);
    }


}
