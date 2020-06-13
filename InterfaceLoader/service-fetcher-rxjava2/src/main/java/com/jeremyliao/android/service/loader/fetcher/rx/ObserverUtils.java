package com.jeremyliao.android.service.loader.fetcher.rx;

import android.os.Looper;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposables;

/**
 * Created by liaohailiang on 2020-06-13.
 */
public final class ObserverUtils {

    public static boolean checkMainThread(Observer<?> observer) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            observer.onSubscribe(Disposables.empty());
            observer.onError(new IllegalStateException(
                    "Expected to be called on the main thread but was " + Thread.currentThread().getName()));
            return false;
        }
        return true;
    }
}
