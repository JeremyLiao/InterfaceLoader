package com.jeremyliao.android.service.loader.fetcher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by liaohailiang on 2020-06-09.
 */
public class ServiceFetcher {

    private final Context context;
    private final Intent intent;
    private final int flags;
    private final List<OnServiceConnectionListener> connectionListeners = new LinkedList<>();
    private ConnectStatus connectStatus = ConnectStatus.NOT_CONNECT;
    private IBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = service;
            for (OnServiceConnectionListener listener : connectionListeners) {
                listener.onServiceConnected(service);
            }
            connectStatus = ConnectStatus.CONNECTED;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
            connectStatus = ConnectStatus.NOT_CONNECT;
        }
    };

    public ServiceFetcher(Context context, Intent intent, int flags) {
        this.context = context;
        this.intent = intent;
        this.flags = flags;
    }

    public ServiceFetcher(Context context, Intent intent) {
        this(context, intent, Context.BIND_AUTO_CREATE);
    }

    public Observable<IBinder> getService() {
        if (connectStatus == ConnectStatus.NOT_CONNECT) {
            bindService();
            return new ServiceFetchObservable(ServiceFetcher.this);
        } else if (connectStatus == ConnectStatus.CONNECTING) {
            return new ServiceFetchObservable(ServiceFetcher.this);
        } else {
            return Observable.just(binder);
        }
    }

    public void bindService() {
        if (connectStatus == ConnectStatus.NOT_CONNECT) {
            context.bindService(intent, connection, flags);
            connectStatus = ConnectStatus.CONNECTING;
        }
    }

    public void unbindService() {
        if (connectStatus == ConnectStatus.NOT_CONNECT) {
            return;
        }
        context.unbindService(connection);
        connectStatus = ConnectStatus.NOT_CONNECT;
    }

    void addServiceConnectionListener(OnServiceConnectionListener listener) {
        if (listener == null) {
            return;
        }
        if (connectionListeners.contains(listener)) {
            return;
        }
        connectionListeners.add(listener);
    }

    boolean removeServiceConnectionListener(OnServiceConnectionListener listener) {
        if (listener == null) {
            return false;
        }
        return connectionListeners.remove(listener);
    }

    public interface OnServiceConnectionListener {
        void onServiceConnected(IBinder service);
    }

    public enum ConnectStatus {
        NOT_CONNECT(0),
        CONNECTING(1),
        CONNECTED(2),
        ;
        private final int status;

        ConnectStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}
