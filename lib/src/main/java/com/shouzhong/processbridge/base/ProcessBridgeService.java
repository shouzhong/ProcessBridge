package com.shouzhong.processbridge.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.shouzhong.processbridge.base.internal.IProcessBridgeService;
import com.shouzhong.processbridge.base.internal.IProcessBridgeServiceCallback;
import com.shouzhong.processbridge.base.internal.Mail;
import com.shouzhong.processbridge.base.internal.Reply;
import com.shouzhong.processbridge.base.receiver.Receiver;
import com.shouzhong.processbridge.base.receiver.ReceiverDesignator;
import com.shouzhong.processbridge.base.util.ObjectCenter;
import com.shouzhong.processbridge.base.util.ProcessBridgeException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ProcessBridgeService extends Service {

    private static final ObjectCenter OBJECT_CENTER = ObjectCenter.getInstance();

    private ConcurrentHashMap<Integer, IProcessBridgeServiceCallback> mCallbacks = new ConcurrentHashMap<>();

    private final IProcessBridgeService.Stub mBinder = new IProcessBridgeService.Stub() {
        @Override
        public Reply send(Mail mail) {
            try {
                Receiver receiver = ReceiverDesignator.getReceiver(mail.getObject());
                int pid = mail.getPid();
                IProcessBridgeServiceCallback callback = mCallbacks.get(pid);
                if (callback != null) {
                    receiver.setProcessBridgeServiceCallback(callback);
                }
                return receiver.action(mail.getTimeStamp(), mail.getMethod(), mail.getParameters());
            } catch (ProcessBridgeException e) {
                e.printStackTrace();
                return new Reply(e.getErrorCode(), e.getErrorMessage());
            }
        }

        @Override
        public void register(IProcessBridgeServiceCallback callback, int pid) throws RemoteException {
            mCallbacks.put(pid, callback);
        }

        @Override
        public void gc(List<Long> timeStamps) throws RemoteException {
            OBJECT_CENTER.deleteObjects(timeStamps);
        }
    };

    public ProcessBridgeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static class ProcessBridgeService0 extends ProcessBridgeService {}

    public static class ProcessBridgeService1 extends ProcessBridgeService {}

    public static class ProcessBridgeService2 extends ProcessBridgeService {}

    public static class ProcessBridgeService3 extends ProcessBridgeService {}

    public static class ProcessBridgeService4 extends ProcessBridgeService {}

    public static class ProcessBridgeService5 extends ProcessBridgeService {}

    public static class ProcessBridgeService6 extends ProcessBridgeService {}

    public static class ProcessBridgeService7 extends ProcessBridgeService {}

    public static class ProcessBridgeService8 extends ProcessBridgeService {}

    public static class ProcessBridgeService9 extends ProcessBridgeService {}

}
