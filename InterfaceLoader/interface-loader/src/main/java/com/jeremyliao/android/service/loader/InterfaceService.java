package com.jeremyliao.android.service.loader;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.jeremyliao.android.service.loader.core.Utils;
import com.jeremyliao.android.service.loader.core.exception.InterfaceLoadException;

import java.lang.reflect.Method;

/**
 * Created by liaohailiang on 2020-06-06.
 */
public class InterfaceService<T> extends Binder {

    public static <R> InterfaceService<R> newService(Class<R> interfaceType, R implement) {
        Utils.validateServiceInterface(interfaceType);
        return new InterfaceService<>(interfaceType, implement);
    }

    private final Class<T> interfaceType;
    private final T implement;
    private String descriptor;

    private InterfaceService(Class<T> interfaceType, T implement) {
        this.interfaceType = interfaceType;
        this.implement = implement;
        this.descriptor = interfaceType.getName();
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        data.enforceInterface(descriptor);
        Method method = getMethod(code);
        Object[] params = getParams(method, data);
        try {
            Object result = method.invoke(implement, params);
            if (reply != null) {
                reply.writeNoException();
                if (!(method.getReturnType().equals(Void.TYPE))) {
                    reply.writeValue(result);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterfaceLoadException(e);
        }
    }

    private Method getMethod(int code) {
        int index = code - IBinder.FIRST_CALL_TRANSACTION;
        Method[] methods = interfaceType.getDeclaredMethods();
        if (index < 0 || index >= methods.length) {
            throw new InterfaceLoadException("Get method, index out of bounds!");
        }
        return methods[index];
    }

    private Object[] getParams(Method method, Parcel data) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        int length = parameterTypes.length;
        if (length == 0) {
            return new Object[]{};
        }
        ClassLoader classLoader = data.getClass().getClassLoader();
        Object[] params = new Object[length];
        for (int i = 0; i < length; i++) {
            params[i] = data.readValue(classLoader);
        }
        return params;
    }
}
