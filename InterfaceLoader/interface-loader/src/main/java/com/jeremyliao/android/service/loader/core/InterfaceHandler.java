package com.jeremyliao.android.service.loader.core;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by liaohailiang on 2020-06-06.
 */
public class InterfaceHandler implements InvocationHandler {

    private final Class<?> service;
    private final IBinder binder;

    public InterfaceHandler(Class<?> service, IBinder binder) {
        this.service = service;
        this.binder = binder;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        int index = getIndex(method);
        if (index == -1) {
            return null;
        }
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        writeDescriptor(data);
        writeParams(data, objects);
        try {
            binder.transact(index, data, reply, 0);
            reply.readException();
            if (method.getReturnType().equals(Void.TYPE)) {
                return null;
            } else {
                return reply.readValue(getClass().getClassLoader());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            data.recycle();
            reply.recycle();
        }
        return null;
    }

    private int getIndex(Method method) {
        Method[] methods = service.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (method.equals(methods[i])) {
                return IBinder.FIRST_CALL_TRANSACTION + i;
            }
        }
        return -1;
    }

    private void writeParams(Parcel parcel, Object[] objects) {
        if (parcel == null) {
            return;
        }
        if (objects == null || objects.length == 0) {
            return;
        }
        for (Object value : objects) {
            parcel.writeValue(value);
        }
    }

    private void writeDescriptor(Parcel parcel) {
        if (parcel == null) {
            return;
        }
        parcel.writeInterfaceToken(service.getName());
    }
}
