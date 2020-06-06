package com.jeremyliao.android.service.loader.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.jeremyliao.android.service.loader.InterfaceLoader;
import com.jeremyliao.android.service.loader.app.databinding.ActivityMainBinding;
import com.jeremyliao.android.service.loader.app.service.LoaderDemoService;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    IBinder binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        bindService();
    }

    public void remotePlus() {
        int result = InterfaceLoader.getService(ILoaderDemo.class, binder)
                .plus(10, 20);
        Toast.makeText(this, "result: " + result, Toast.LENGTH_SHORT).show();
    }

    public void remoteMinus() {
        int result = InterfaceLoader.getService(ILoaderDemo.class, binder)
                .minus(10, 20);
        Toast.makeText(this, "result: " + result, Toast.LENGTH_SHORT).show();
    }

    public void remoteMulti() {
        int result = InterfaceLoader.getService(ILoaderDemo.class, binder)
                .multi(10, 20);
        Toast.makeText(this, "result: " + result, Toast.LENGTH_SHORT).show();
    }

    private void bindService() {
        Intent intent = new Intent(LoaderDemoService.ACTION);
        intent.setPackage(getPackageName());
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }
}