package com.jeremyliao.android.service.loader;

import android.os.IBinder;

import com.jeremyliao.android.service.loader.core.InterfaceHandler;
import com.jeremyliao.android.service.loader.core.Utils;

import java.lang.reflect.Proxy;

/**
 * Created by liaohailiang on 2020-06-06.
 */
public final class InterfaceLoader {

    private InterfaceLoader() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(final Class<T> service, final IBinder binder) {
        Utils.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service},
                new InterfaceHandler(service, binder));
    }
}
