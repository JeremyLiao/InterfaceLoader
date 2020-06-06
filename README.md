# InterfaceLoader
![license](https://img.shields.io/github/license/JeremyLiao/InterfaceLoader.svg) [![version](https://img.shields.io/badge/JCenter-v0.0.1-blue.svg)](https://mvnrepository.com/artifact/com.jeremyliao/)

- InterfaceLoader是一款Android跨进程接口调用框架，可以替代Android AIDL
- 史上最好用的Android跨进程接口调用框架

## 为什么要用InterfaceLoader
1. 因为不想用AIDL！
2. 因为不想用AIDL！！
3. 因为不想用AIDL！！！

## 干掉难用的AIDL，InterfaceLoader就是这么爽:smirk:

## 在工程中引用
Via Gradle:
```
implementation 'com.jeremyliao:interface-loader:0.0.1'
```

## 快速开始
#### 定义接口
- 例如：

```
public interface ILoaderDemo {

    int plus(int a, int b);

    int minus(int a, int b);

    int multi(int a, int b);
}
```
#### 定义接口的实现
1. 定义Android Service
2. 在onBind方法中调用InterfaceService.newService：

```
InterfaceService.newService(ILoaderDemo.class, new ILoaderDemo()
```
#### 接口调用
1. 定义Activity
2. 通过bindService获取IBinder
3. 通过InterfaceLoader.getService调用接口的方法

```
InterfaceLoader.getService(ILoaderDemo.class, binder)
                .plus(10, 20);
```
#### 完整示例
[Demo](InterfaceLoader/tree/master/InterfaceLoader/app)

## 妈妈再也不用担心我使用AIDL了:sunglasses::sunglasses:
