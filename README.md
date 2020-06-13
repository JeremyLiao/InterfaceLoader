# InterfaceLoader
![license](https://img.shields.io/github/license/JeremyLiao/InterfaceLoader.svg) [![version](https://img.shields.io/badge/JCenter-v0.0.1-blue.svg)](https://mvnrepository.com/artifact/com.jeremyliao/)

- InterfaceLoader是Android跨进程接口调用框架，可以完美替代Android AIDL
- 史上最好用的Android跨进程接口调用框架
- 新增service-fetcher，可以链式连接service和调用方法，并帮你处理重连、掉线等问题

## 为什么要用InterfaceLoader
1. 因为不想用AIDL！
2. 因为不想用AIDL！！
3. 因为不想用AIDL！！！

### 干掉难用的AIDL，InterfaceLoader就是这么爽:smirk:

## InterfaceLoader的原理和亮点
总的来说，分为两个库：interface-loader和service-fetcher，可单独使用interface-loader，也可一起使用
#### interface-loader
- 主要解决了跨进程方法调用的问题
- 封装了跨进程通信需要的打包和拆包，让跨进程接口调用更好用
- 相对AIDL：
    - AIDL的跨进程通信是一种典型的代理模式，由.aidl文件生成client端的proxy和server端的binder，但是我觉得生成的代码总觉得看起来很别扭，也很难用。
    - 所以interface-loader使用动态代理代替了AIDl的代码生成，非常巧妙。不仅减少了生成的代码，也让跨进程通信的使用体验更接近普通的接口调用，这就是interface-loader的奇妙之处。

#### service-fetcher
- 主要解决了连接远程服务的问题
- 链式处理服务连接和接口调用
- 封装和处理了服务掉线、重连等异常场景


## 在工程中引用
#### interface-loader
Via Gradle:
```
implementation 'com.jeremyliao:interface-loader:0.0.1'
```
#### service-fetcher
Via Gradle:
```
implementation 'com.jeremyliao:service-fetcher-rxjava2:0.0.1'
```
- 注意：service-fetcher依赖了rxjava2

## 快速开始
#### 定义接口

```
public interface IXXX {
    ...
}
```
#### 接口的实现和启动服务
1. 定义Android Service
2. 在onBind方法中调用InterfaceService.newService：

```
InterfaceService.newService()
```
#### client端接口调用
1. 定义Activity
2. 通过bindService获取IBinder
3. 通过InterfaceLoader.getService调用接口的方法

```
InterfaceLoader.getService()
```
## 完整示例
我们以一个加减乘法的接口调用来说明这个示例
#### 接口定义

```
public interface ILoaderDemo {

    int plus(int a, int b);

    int minus(int a, int b);

    int multi(int a, int b);
}
```
#### 接口实现和启动服务
定义一个service：

```
public class LoaderDemoService extends Service {

    public static final String ACTION = "intent.action.loader";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return InterfaceService.newService(ILoaderDemo.class, new ILoaderDemo() {
            @Override
            public int plus(int a, int b) {
                return a + b;
            }

            @Override
            public int minus(int a, int b) {
                return a - b;
            }

            @Override
            public int multi(int a, int b) {
                return a * b;
            }
        });
    }
}
```
1. 关键点是在onBind中调用InterfaceService.newService
2. 当然还需要在androidManifests中注册服务

#### 连接服务和接口调用
###### 用ServiceFetcher连接服务
```
Intent intent = new Intent(LoaderDemoService.ACTION);
intent.setPackage(getPackageName());
serviceFetcher = new ServiceFetcher(this, intent);
serviceFetcher.bindService();
```
###### 用ServiceFetcher链式的连接服务
###### 用InterfaceLoader调用接口
```
disposable.add(serviceFetcher.getService()
        .map(new Function<IBinder, Integer>() {
            @Override
            public Integer apply(IBinder binder) throws Exception {
                return InterfaceLoader.getService(ILoaderDemo.class, binder)
                        .plus(10, 20);
            }
        })
        .map(new Function<Integer, String>() {
            @Override
            public String apply(Integer result) throws Exception {
                return "result: " + result;
            }
        })
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String result) throws Exception {
                binding.tvContent.setText(result);
            }
        }));
```
###### 别忘了activity退出的时候断开连接

```
serviceFetcher.unbindService();
```
###### 完整的客服端代码

```
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ServiceFetcher serviceFetcher;
    final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        Intent intent = new Intent(LoaderDemoService.ACTION);
        intent.setPackage(getPackageName());
        serviceFetcher = new ServiceFetcher(this, intent);
        serviceFetcher.bindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serviceFetcher.unbindService();
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void remotePlus() {
        disposable.add(serviceFetcher.getService()
                .map(new Function<IBinder, Integer>() {
                    @Override
                    public Integer apply(IBinder binder) throws Exception {
                        return InterfaceLoader.getService(ILoaderDemo.class, binder)
                                .plus(10, 20);
                    }
                })
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer result) throws Exception {
                        return "result: " + result;
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        binding.tvContent.setText(result);
                    }
                }));
    }

    public void remoteMinus() {
        disposable.add(serviceFetcher.getService()
                .map(new Function<IBinder, Integer>() {
                    @Override
                    public Integer apply(IBinder binder) throws Exception {
                        return InterfaceLoader.getService(ILoaderDemo.class, binder)
                                .minus(10, 20);
                    }
                })
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer result) throws Exception {
                        return "result: " + result;
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        binding.tvContent.setText(result);
                    }
                }));
    }

    public void remoteMulti() {
        disposable.add(serviceFetcher.getService()
                .map(new Function<IBinder, Integer>() {
                    @Override
                    public Integer apply(IBinder binder) throws Exception {
                        return InterfaceLoader.getService(ILoaderDemo.class, binder)
                                .multi(10, 20);
                    }
                })
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer result) throws Exception {
                        return "result: " + result;
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        binding.tvContent.setText(result);
                    }
                }));
    }
}
```

[示例代码直达](InterfaceLoader/tree/master/InterfaceLoader/app)

## 妈妈再也不用担心我使用AIDL了:sunglasses::sunglasses:
