package com.jeremyliao.android.service.loader.fetcher;

import android.os.IBinder;

import com.jeremyliao.android.service.loader.fetcher.rx.MainThreadDisposable;
import com.jeremyliao.android.service.loader.fetcher.rx.ObserverUtils;

import io.reactivex.Observable;
import io.reactivex.Observer;

final class ServiceFetchObservable extends Observable<IBinder> {
    private final ServiceFetcher serviceFetcher;

    public ServiceFetchObservable(ServiceFetcher serviceFetcher) {
        this.serviceFetcher = serviceFetcher;
    }

    @Override
    protected void subscribeActual(Observer<? super IBinder> observer) {
        if (!ObserverUtils.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(serviceFetcher, observer);
        observer.onSubscribe(listener);
        serviceFetcher.addServiceConnectionListener(listener);
    }

    static final class Listener extends MainThreadDisposable implements ServiceFetcher.OnServiceConnectionListener {
        private final ServiceFetcher serviceFetcher;
        private final Observer<? super IBinder> observer;

        public Listener(ServiceFetcher serviceFetcher, Observer<? super IBinder> observer) {
            this.serviceFetcher = serviceFetcher;
            this.observer = observer;
        }

        @Override
        public void onServiceConnected(IBinder service) {
            if (!isDisposed()) {
                observer.onNext(service);
            }
        }

        @Override
        protected void onDispose() {
            serviceFetcher.removeServiceConnectionListener(this);
        }
    }
}
