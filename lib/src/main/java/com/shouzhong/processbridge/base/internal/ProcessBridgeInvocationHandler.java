package com.shouzhong.processbridge.base.internal;

import android.util.Log;

import com.shouzhong.processbridge.base.ProcessBridgeService;
import com.shouzhong.processbridge.base.sender.Sender;
import com.shouzhong.processbridge.base.sender.SenderDesignator;
import com.shouzhong.processbridge.base.util.ProcessBridgeException;
import com.shouzhong.processbridge.base.wrapper.ObjectWrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProcessBridgeInvocationHandler implements InvocationHandler {

    private static final String TAG = "ProcessBridgeInvocation";

    private Sender mSender;

    public ProcessBridgeInvocationHandler(Class<? extends ProcessBridgeService> service, ObjectWrapper object) {
        mSender = SenderDesignator.getPostOffice(service, SenderDesignator.TYPE_INVOKE_METHOD, object);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) {
        try {
            Reply reply = mSender.send(method, objects);
            if (reply == null) {
                return null;
            }
            if (reply.success()) {
                return reply.getResult();
            } else {
                Log.e(TAG, "Error occurs. Error " + reply.getErrorCode() + ": " + reply.getMessage());
                return null;
            }
        } catch (ProcessBridgeException e) {
            e.printStackTrace();
            Log.e(TAG, "Error occurs. Error " + e.getErrorCode() + ": " + e.getErrorMessage());
            return null;
        }
    }
}
