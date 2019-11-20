package com.shouzhong.processbridge.base.util;

public class ProcessBridgeException extends Exception {

    private int mErrorCode;

    private String mErrorMessage;

    public ProcessBridgeException(int errorCode, String errorMessage) {
        mErrorCode = errorCode;
        mErrorMessage = errorMessage;
    }

    public ProcessBridgeException(int errorCode, String errorMessage, Throwable t) {
        super(t);
        mErrorCode = errorCode;
        mErrorMessage = errorMessage;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
