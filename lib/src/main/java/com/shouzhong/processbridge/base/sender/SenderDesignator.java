package com.shouzhong.processbridge.base.sender;

import com.shouzhong.processbridge.base.ProcessBridgeService;
import com.shouzhong.processbridge.base.wrapper.ObjectWrapper;

public class SenderDesignator {

    public static final int TYPE_NEW_INSTANCE = 0;

    public static final int TYPE_GET_INSTANCE = 1;

    public static final int TYPE_GET_UTILITY_CLASS = 2;

    public static final int TYPE_INVOKE_METHOD = 3;

    public static Sender getPostOffice(Class<? extends ProcessBridgeService> service, int type, ObjectWrapper object) {
        switch (type) {
            case TYPE_NEW_INSTANCE:
                return new InstanceCreatingSender(service, object);
            case TYPE_GET_INSTANCE:
                return new InstanceGettingSender(service, object);
            case TYPE_GET_UTILITY_CLASS:
                return new UtilityGettingSender(service, object);
            case TYPE_INVOKE_METHOD:
                return new ObjectSender(service, object);
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
    }

}
