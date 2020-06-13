package com.jeremyliao.android.service.loader.fetcher.rx;

import android.os.Looper;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by liaohailiang on 2020-06-13.
 */
public abstract class MainThreadDisposable implements Disposable {
    /**
     * Verify that the calling thread is the Android main thread.
     * <p>
     * Calls to this method are usually preconditions for subscription behavior which instances of
     * this class later undo. See the class documentation for an example.
     *
     * @throws IllegalStateException when called from any other thread.
     */
    public static void verifyMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException(
                    "Expected to be called on the main thread but was " + Thread.currentThread().getName());
        }
    }

    private final AtomicBoolean unsubscribed = new AtomicBoolean();

    @Override
    public final boolean isDisposed() {
        return unsubscribed.get();
    }

    @Override
    public final void dispose() {
        if (unsubscribed.compareAndSet(false, true)) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                onDispose();
            } else {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        onDispose();
                    }
                });
            }
        }
    }

    protected abstract void onDispose();
}