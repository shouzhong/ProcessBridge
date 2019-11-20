# ProcessBridge
## 说明
我们在开发过程中不管是有意还是无意，难免会涉及到多进程，而进程之间的通信更是一个大问题，虽然有诸如activity，broadcast，aidl等这样现成的工具，但是总是存在着局限性或者技术实现上的不友好。
这个框架就是为了解决这个问题而诞生的，封装了你熟悉的EventBus，还有管理Activity的，以及SharedPreferences。如果这些不满足你的业务需求，可以自定义。
## 使用
### 依赖
```
implementation 'com.shouzhong:ProcessBridge:1.0.0'
```
### 代码
#### 初始化
在application的onCreate方法中
```
ProcessBridgeUtils.init(List<IProcessBridge>);
```
#### 获取实例
```
ProcessBridgeUtils.getEventBus()
ProcessBridgeUtils.getActivityManager()
ProcessBridgeUtils.getSP()
```
具体使用请[参考demo](https://github.com/shouzhong/ProcessBridge/tree/master/app/src/main/java/com/shouzhong/processbridge/demo)
#### 自定义
请参考源码
[EventBus](https://github.com/shouzhong/ProcessBridge/tree/master/lib/src/main/java/com/shouzhong/processbridge/eventbus)
[SharedPreferences](https://github.com/shouzhong/ProcessBridge/tree/master/lib/src/main/java/com/shouzhong/processbridge/sp)
[ActivityManager](https://github.com/shouzhong/ProcessBridge/tree/master/lib/src/main/java/com/shouzhong/processbridge/activity)
### 方法说明

ProcessBridgeUtils.getEventBus()

方法名 | 说明
------------ | -------------
register | 注册
isRegistered | 是否注册
unregister | 反注册
post | 发送事件
cancelEventDelivery | 取消事件传递
postSticky | 发送粘滞事件
getStickyEvent | 获取某个粘滞事件
removeStickyEvent | 移除某个粘滞事件
removeAllStickyEvents | 移除所有粘滞事件
hasSubscriberForEvent | 是否订阅某个类型的事件

ProcessBridgeUtils.getActivityManager()

方法名 | 说明
------------ | -------------
size | 获取所有进程所有activity数量
size(int) | 获取某个进程所有activity数量
contains | 在所有进程中是否有匹配cls的activity
contains(int) | 在某个进程中是否有匹配cls的activity
get(Class) | 获取最近的匹配cls的activity，只能获取当前进程的activity，否则会抛异常
get(int) | 获取activity，只能获取当前进程的activity，否则会抛异常
finish | finish所有进程的cls匹配activity
exit(int) | finish某个进程所有的activity
exit | finish所有进程的所有activity

ProcessBridgeUtils.getSP()

方法名 | 说明
------------ | -------------
getAll | 获取所有
getString | 获取String
getStringSet | 获取Set<String>
getInt | 获取int
getLong | 获取long
getFloat | 获取float
getBoolean | 获取Boolean
contains | 是否包含
putString | 保存String
putStringSet | 保存Set<String>
putInt | 保存int
putLong | 保存long
putFloat | 保存float
putBoolean | 保存boolean
remove | 删除
clear | 清空

## 注意事项
1. 如果在子进程刚启动时在主线程调有返回值的方法，主线程会锁住卡死，建议有这种操作的在子线程中调用
2. 对于EventBus的粘滞事件，请重写equals，确保调用removeStickyEvent(Object)时能真正移除，当然如果不想重写equals，那建议调用removeStickyEvent(Class)移除
3. 对于EventBus建议尽量少用或者不用带泛型事件，可能会有bug
4. 数据传输是基于json的，所以请保证你定义的事件可序列化

## 混淆
```
-keep class com.shouzhong.** {*;}
-dontwarn com.shouzhong.**
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
```

## 感谢
[Xiaofei-it/HermesEventBus](https://github.com/Xiaofei-it/HermesEventBus)
