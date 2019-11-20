package com.shouzhong.processbridge.base.util;

import com.google.gson.Gson;

public class CodeUtils {

    private static final Gson GSON = new Gson();

    private CodeUtils() {

    }

    public static String encode(Object object) throws ProcessBridgeException {
        if (object == null) {
            return null;
        } else {
            try {
                return GSON.toJson(object);
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new ProcessBridgeException(ErrorCodes.GSON_ENCODE_EXCEPTION,
                    "Error occurs when Gson encodes Object "
                    + object + " to Json.");
        }
    }

    public static <T> T decode(String data, Class<T> clazz) throws ProcessBridgeException {
        try {
            return GSON.fromJson(data, clazz);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ProcessBridgeException(ErrorCodes.GSON_DECODE_EXCEPTION,
                "Error occurs when Gson decodes data of the Class "
                + clazz.getName());
    }

}
