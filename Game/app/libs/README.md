#数字广告监测及验证OTT SDK (Android) 部署指南



##适用范围

Android OTT SDK适用于 **Android 4.0.3（API Level 15）**及以上的设备。

## 集成SDK

### 步骤1: 导入SDK

请根据所用IDE选择导入方式：

#### Eclipse ADT 

将 SDK 中的  `jar` 文件拷贝到工程的 `libs` 文件夹中：右键选择jar包 **-->** **Build Path --> Add to Build Path**。

#### Android Studio

1. 在 Android Studio 项目的 `app` 文件夹中，新建 `libs` 文件夹。
2. 将 SDK `jar` 文件拷贝到新建的 `libs` 文件夹中。 
3. 修改 `app` 文件夹中的 `build.gradle` 文件，添加 `dependencies` 依赖项：

```
dependencies {
      …
      implementation files('libs/mmachina_ott_sdk.jar') 
      …
  }
```

####导入签名库

将SDK中的 `libMMASignature.so`  文件拷贝到工程 `libs/armeabi` 目录下， SDK `lib_abi` 目录下对应不同CPU架构下的库文件，建议拷贝该目录下所有到工程 **app/libs **文件夹下，如果考虑APK大小，至少要包含主流架构：`armeabi`、`armeabi-v7a`、`arm64-v8a`。

#### 导入离线配置

将 `sdkconfig.xml` 配置文件拷贝到工程的 `assets` 目录下，在网络不好或者离线状态下读取缺省配置可以正常监测。同时 **配置文件** 上传到  web 服务器，使其可以通过 web 方式访问，便于后期灵活远程变更配置。



#### 配置权限

##### 常规权限

满足基本监测需要如下权限：

| 权限                   | 用途                                 |
| -------------------- | ---------------------------------- |
| INTERNET             | 允许程序联网和上报监测数据。                     |
| ACCESS_NETWORK_STATE | 允许检测网络连接状态，在网络异常状态下避免数据发送，节省流量和电量。 |
| READ_PHONE_STATE     | 允许访问手机设备的信息，通过获取的信息来唯一标识用户。        |
| ACCESS_WIFI_STATE    | 允许读取WiFi相关信息，在合适的网络环境下更新配置。        |

##### 扩展权限

在基本监测的基础上，如果想要回传**位置相关信息**，除了监测代码对应**sdkconfig**内**Company**标签的<isTrackLocation>项设置为**true**，还额外需要如下权限：

| 权限                     | 用途                    |
| ---------------------- | --------------------- |
| ACCESS_FINE_LOCATION   | 通过GPS方式获取位置信息。        |
| ACCESS_COARSE_LOCATION | 通过WiFi或移动基站的方式获取位置信息。 |

示例代码

```
<!--?xml version="1.0" encoding="utf-8"?-->
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="cn.com.mma.ott.tracking.demo">
    <!-- SDK 所需常规权限 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 	<!-- 如果获取位置信息，需要声明以下权限 -->
 	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
 	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<application ......>
  <activity ......>
  ......
  </activity>
    </application>
</manifest>
```

### 步骤2:使用方法

#### 引入类

在您需要使用 SDK 监测功能的类中，**import** 相关类：

```
import cn.com.mma.ott.tracking.api.Countly;
```

#### SDK初始化

接口定义：

```
public void init(Context context, String configURL) 
```

参数说明：

| 参数        | 类型      | 说明                  |
| --------- | ------- | ------------------- |
| context   | Context | APP or Activity 上下文 |
| configURL | String  | 更新sdkconfig配置的远程地址  |

代码示例：在您的工程中的 Application 或者 Activity 中的 **onCreate** 中添加如下代码：

```
Countly.sharedInstance().init(this, "sdkconfig远程地址");
```



####曝光监测

SDK曝光监测接口现已升级为曝光/Track Ads接口， 支持曝光或Tracked Ads监测。

曝光的定义：只有广告物料已经加载在客户端并至少已经开始渲染（Begin to render，简称BtR）时，才应称之为“曝光”事件。
“渲染”指的是绘制物料的过程，或者指将物料添加到文档对象模型的过程。

接口定义：

```
public  void onExpose(String adURL,View adview,int type)
```

参数说明：

| 参数    | 类型     | 说明        |
| ----- | ------ | --------- |
| adURL | String | 广告位曝光监测代码 |
| adview | View | 广告展示视图对象 |
| type | int |0 代表Tracked Ads监测； 1 代表曝光 |


示例代码：

```
Countly.sharedInstance().onExpose("http://example.com/axxx,bxxxx,c2,i0,h",adview,1);
```

#### 广告曝光延时上报监测

接口定义：

```
public void onDelayExpose(String adURL, long delayTime)
```

参数说明：

| 参数    | 类型     | 说明        |
| ----- | ------ | --------- |
| adURL | String | 广告位曝光监测代码 |
| delayTime | long | SDK延时广告曝光监测的情况下，广告实际发生的时间 |

SDK提供延时监测的功能，需要传入**广告实际发生的时间**，延时上报用于满足特定情形下的需求，即，广告发生时，SDK可能尚未启动、设备也可能尚未联网。因此提供一个延时曝光上报接口，在调用该接口时需要传入广告实际发生时间，用来完成广告延时上报。
示例代码：

```

String adURL = "http://vxyz.miaozhen.com.cn/w/a86218,b1778712,c2343,i0,m202,8a2,8b2,2j,h";
long delayTime  = 1570798938;
//调用延时上报接口
Countly.sharedInstance().onDelayExpose(TRACKING_URL, delayTime);

        
```

#### 点击监测

接口定义：

```
public  void onClick(String adURL)
```

参数说明：

| 参数    | 类型     | 说明        |
| ----- | ------ | --------- |
| adURL | String | 广告位点击监测代码 |

示例代码：

```
Countly.sharedInstance().onClick("http://example.com/axxx,bxxxx,c3,i0,h");
```



#### 可见性曝光监测

描述：对广告进行可见性监测时，广告必须是满足开始渲染（Begin to render，简称BtR）条件的合法曝光，否则SDK不会执行可见监测。
在调用可见曝光监测接口时，SDK会查验传入的广告View对象是否已开始渲染，如果是，则SDK会向监测方发出曝光上报，并继续进行可见监测，直到满足可见/不可见条件，再结束可见监测流程；如果不是，则SDK会向监测方发出Tracked Ads上报，并结束可见监测流程。

接口定义：

```
public  void onExpose(String adURL, View adView) 
```

参数说明：

| 参数     | 类型     | 说明        |
| ------ | ------ | --------- |
| adURL  | String | 广告位曝光监测代码 |
| adView | View   | 广告展示视图对象  |

注意：对于需要可见性曝光的广告监测，第二个参数（**View**）为必选项，且需要传入当前广告展示的视图对象，否则可能造成SDK无法成功可见性曝光监测。

示例代码：

```
Countly.sharedInstance().onExpose("http://vxyz.miaozhen.com.cn/w/a86218,b1778712,c2343,i0,m202,8a2,8b2,2j,h",adview);
```



#### 可见性视频曝光监测

接口定义：

```
public void onVideoExpose(String adURL, View videoView, int videoPlayType)
```

参数说明：

| 参数            | 类型     | 说明                             |
| ------------- | ------ | ------------------------------ |
| adURL         | String | 广告位曝光监测代码                      |
| videoView     | View   | 视频广告展示视图对象                     |
| videoPlayType | int    | 视频播放类型<br>1-自动播放，2-手动播放，0-无法识别 |

注意：对于需要可见性视频曝光的广告监测，第二个参数（**View**）为必选项，需要传入当前广告展示的视图对象且监测代码需要配置**视频时长及进度监测点**，否则可能造成SDK无法成功可见性视频曝光监测。

示例代码：

```
        String adURL = "http://v.miaozhen.com.cn/i/a90981,b1899468,c2,i0,m202,8a2,8b2,2p,2u2,2g,2f=3,2v50,2w15,2x0010,va1,2a2p,2j,2d1234,h";
        Countly.sharedInstance().onVideoExpose(adURL, adView, 2);
```





#### 停止可见性监测

接口定义：

```
public void stop(String adURL)
```

参数说明：

| 参数    | 类型     | 说明        |
| ----- | ------ | --------- |
| adURL | String | 广告位曝光监测代码 |

SDK提供主动关闭可见性监测的功能，需要传入**已经开启可见性监测的广告位曝光监测代码**，如果传入错位的监测代码可能导致停止不生效。

示例代码：

```
        String adURL = "http://vxyz.miaozhen.com.cn/w/a86218,b1778712,c2343,i0,m202,8a2,8b2,2j,h";
        Countly.sharedInstance().onExpose(adURL, adView);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                //5s后停止监测
                Countly.sharedInstance().stop(adURL);
            }
        }, 5000);
```


#### 调试模式

调试模式下，SDK会有LOG输出，APP发布时建议不要开启。（请在**初始化之前设置Log开关**，默认为false）。

接口定义：

```
public void setLogState(boolean debugmode)
```

参数说明：

| 参数        | 类型      | 说明                              |
| --------- | ------- | ------------------------------- |
| debugmode | boolean | true为打开SDK Log， false为关闭SDK Log |

示例代码：

```
Countly.sharedInstance().setLogState(true);
```

#### 释放内存

SDK提供释放内存的接口，一般在应用即将退出时调用，或者等待系统内存管理自动释放。

接口定义：

```
public  void terminateSDK()
```

示例代码：

```
Countly.sharedInstance().terminateSDK();
```

#### 混淆配置

如果开发者的应用需要混淆，请在 `Proguard` 混淆配置文件中增加以下规则，以避免 SDK 不可用。

```
# SDK用到了v4包里的API，请确保v4相关support包不被混淆
-keep class android.support.v4.** { *; }
-dontwarn android.support.v4.**
```



### 步骤3:验证和调试

SDK 的测试有两个方面：

1. 参数是否齐全，URL 拼接方式是否正确。
2. 请求次数和第三方监测平台是否能对应上。

请联系第三方监测平台完成测试。