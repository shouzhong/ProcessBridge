package com.shouzhong.processbridge.base.receiver;

import com.shouzhong.processbridge.base.util.ErrorCodes;
import com.shouzhong.processbridge.base.util.ProcessBridgeException;
import com.shouzhong.processbridge.base.wrapper.ObjectWrapper;

public class ReceiverDesignator {
    public static Receiver getReceiver(ObjectWrapper objectWrapper) throws ProcessBridgeException {
        int type = objectWrapper.getType();
        switch (type) {
            case ObjectWrapper.TYPE_OBJECT_TO_NEW:
                return new InstanceCreatingReceiver(objectWrapper);
            case ObjectWrapper.TYPE_OBJECT_TO_GET:
                return new InstanceGettingReceiver(objectWrapper);
            case ObjectWrapper.TYPE_CLASS:
                return new UtilityReceiver(objectWrapper);
            case ObjectWrapper.TYPE_OBJECT:
                return new ObjectReceiver(objectWrapper);
            case ObjectWrapper.TYPE_CLASS_TO_GET:
                return new UtilityGettingReceiver(objectWrapper);
            default:
                throw new ProcessBridgeException(ErrorCodes.ILLEGAL_PARAMETER_EXCEPTION,
                        "Type " + type + " is not supported.");
        }
    }
}
