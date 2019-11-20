package com.shouzhong.processbridge.base.internal;

import android.os.RemoteException;
import android.util.Log;

import com.shouzhong.processbridge.base.util.ProcessBridgeException;
import com.shouzhong.processbridge.base.util.TypeUtils;
import com.shouzhong.processbridge.base.wrapper.MethodWrapper;
import com.shouzhong.processbridge.base.wrapper.ParameterWrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProcessBridgeCallbackInvocationHandler implements InvocationHandler {

    private static final String TAG = "ProcessBridge_CALLBACK";

    private long mTimeStamp;

    private int mIndex;

    private IProcessBridgeServiceCallback mCallback;

    public ProcessBridgeCallbackInvocationHandler(long timeStamp, int index, IProcessBridgeServiceCallback callback) {
        mTimeStamp = timeStamp;
        mIndex = index;
        mCallback = callback;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) {
        try {
            MethodWrapper methodWrapper = new MethodWrapper(method);
            ParameterWrapper[] parameterWrappers = TypeUtils.objectToWrapper(objects);
            CallbackMail callbackMail = new CallbackMail(mTimeStamp, mIndex, methodWrapper, parameterWrappers);
            Reply reply = mCallback.callback(callbackMail);
            if (reply == null) {
                return null;
            }
            if (reply.success()) {
                /**
                 * Note that the returned type should be registered in the remote process.
                 */
                return reply.getResult();
            } else {
                Log.e(TAG, "Error occurs: " + reply.getMessage());
                return null;
            }
        } catch (ProcessBridgeException e) {
            Log.e(TAG, "Error occurs but does not crash the app.", e);
        } catch (RemoteException e) {
            Log.e(TAG, "Error occurs but does not crash the app.", e);
        }
        return null;
    }
}
