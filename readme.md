文档说明

1. 本文档基于[谷歌AndroidQ官方文档](https://developer.android.google.cn/preview)和[华为Q版本应用兼容性整改指导](https://developer.huawei.com/consumer/cn/devservice/doc/50127)(华为的有点过时)

2. 所用测试机：Google初代Pixel，AndroidQ-beta6-190730.005

3. 版本号对应关系

   > Android-Q = Android-10 = Api29
   >
   > Android-P = Android-9.0 = Api28

4. 

## 设备硬件标识符访问限制

限制应用访问不可重设的设备识别码，如 IMEI、序列号等，系统应用不受影响。

#### 原来的做法

```java
// 返回：866976045261713; 
TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
tm.getDeviceId();
//返回：66J0218B19000977; 
Build.getSerial();
```

1. 在低于AndroidQ的系统上没问题

2. 在AndroidQ及以上的系统上运行时：

   * 如`targetSdkVersion<Q`，返回null或“unknown”；

   * 如`targetSdkVersion>=Q`，抛异常：

     > ```
     > SecurityException: getDeviceId: The user 10196 does not meet the requirements to access device identifiers.
     > ```




#### 替代方案

1. 方案一：

   使用AndroidId代替，缺点是应用签署密钥或用户（如系统恢复出产设置）不同返回的Id不同。与实际测试结果相符。

   经实际测试：相同签名密钥的不同应用androidId相同，不同签名的应用androidId不同。恢复出产设置或升级系统没测。

   ```java
   // 返回：496836e3a48d2d9d
   String androidId = Settings.System.getString(context.getContentResolver(),
           Settings.Secure.ANDROID_ID);
   ```

   

2. 方案二：

   通过硬件信息拼接，缺点是还是不能保证唯一。
   经测试：似乎与方案一比更稳定，不受密钥影响，但非官方建议，没安全感。
   
   ```java
   private static String makeDeviceId(Context context) {
       String  deviceInfo = new StringBuilder()
               .append(Build.BOARD).append("#")
               .append(Build.BRAND).append("#")
           	   //CPU_ABI，这个值和appp使用的so库是arm64-v8a还是armeabi-v7a有关，舍弃
               //.append(Build.CPU_ABI).append("#")
               .append(Build.DEVICE).append("#")
               .append(Build.DISPLAY).append("#")
               .append(Build.HOST).append("#")
               .append(Build.ID).append("#")
               .append(Build.MANUFACTURER).append("#")
               .append(Build.MODEL).append("#")
               .append(Build.PRODUCT).append("#")
               .append(Build.TAGS).append("#")
               .append(Build.TYPE).append("#")
               .append(Build.USER).append("#")
               .toString();
       try {
          //22a49a46-b39e-36d1-b75f-a0d0b9c72d6c
          return UUID.nameUUIDFromBytes(deviceInfo.getBytes("utf8")).toString();
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       }
       String androidId = Settings.System.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
       return androidId;
   }
   ```



## 禁止后台启动Activity

[官方文档](https://developer.android.google.cn/preview/privacy/background-activity-starts)

#### 情况描述

1. AndroidQ上，后台启动Activity会被系统忽略，不管targetSdkVersion多少；
2. AndroidQ上，即返回应用有前台服务也不行；
3. AndroidQ以下版本没影响。

#### 解决方法

发送全屏通知: 

```java
//AndroidManifest 声明新权限，不用动态申请
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT"/>

Intent intent = new Intent(this, ScopedStorageActivity.class);
PendingIntent pendingIntent = PendingIntent.getActivity(this,
        REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
Notification notification = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Incoming call")
        .setContentText("(919) 555-1234")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        //设置全屏通知后，发送通知直接启动Activity
        .setFullScreenIntent(pendingIntent, true)
        .build();
NotificationManager manager = getSystemService(NotificationManager.class);
manager.notify(445456, notification);
```

但是：在华为mate20（Api-28）上需要到设置中打开横幅通知；原生AndroidQ（beta6）上有效。



## 后台应用增加定位限制

[官方文档](https://developer.android.google.cn/preview/privacy/device-location)

#### 情况描述

1. 后台应用要获取位置信息需要动态申请权限，

   `<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>`

2. 在AndroidQ上运行：

   - `targetSdkVersion<Q`，没影响，申请权限时系统默认会加上后台位置权限
   - `targetSdkVersion>=Q`，需申请；
   - 应用变为后台应用90s后开始定位失败（Pixel AndroidQ-beta6）

3. `ACCESS_BACKGROUND_LOCATION`不能单独申请，需要和`ACCESS_COARSE_LOCATION/ACCESS_FINE_LOCATION`一起申请

#### 解决方法

1. 动态申请即可；

2. 启动前台服务

   ```java
   <!-需要设置foregroundServiceType为“location” ->
   <service 
    android:name=".permission.LocationService"
       android:foregroundServiceType="location"/>
   ```

   

## 分区存储

[官方文档](https://developer.android.google.cn/preview/privacy/scoped-storage)

应用只能访问自己过滤视图下的文件或XXXX，无权访问其他文件目录。

#### 情况描述

1. 在AndroidQ上运行：
   *　`targetSdkVersion<Q`，没影响；
   *　`targetSdkVersion>=Q`，默认启用过滤视图，应用以外的文件需要通过[存储访问框架](https://developer.android.google.cn/guide/topics/providers/document-provider)（SAF，StorageAccessFramework）读写。
2. 

#### 解决方法

##### 方法一、停用过滤视图，使用旧版存储模式

   ```xml
   <manifest ... >
     <!-- This attribute is "false" by default on apps targeting Android Q. -->
     <application android:requestLegacyExternalStorage="true" ... >
       ...
     </application>
   </manifest>
   ```

##### 方法二、将文件存储到过滤视图中

   ```java
   // /Android/data/com.example.androidq/files/Documents
   File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
   ```

   **优点：**不用申请读写权限；

   **缺点：**随应用卸载而删除；

​     

##### 方法三、使用存储访问框架（SAF），由用户指定要读写的文件。

​	这个功能Android 4.4（API: 19）就有，[官方文档在此](https://developer.android.google.cn/guide/topics/providers/document-provider)。



##### 方法四、获取用户指定的某个目录的读写权限

​	这种方式从Android5.0（Api 21）开始就有了。[官方文档](https://developer.android.google.cn/about/versions/android-5.0#Storage)

1. 申请目录的访问权限

   会打开系统的文件目录，由用户自己选择允许访问的目录，不用申请`WRITE/READ_EXTERNAL_STORAGE`权限。

   ```java
   Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
   intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | 
           Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);				
   startActivityForResult(intent, REQ_CODE);
   ```

   执行上述代码后会出现类似如下图界面，点击‘允许访问“DuoKan”’按钮，

   <img src="image/允许访问的目录.png" height="440"/>

   允许了之后通过`onActivityResult()`的`intent.getData()`得到该目录的Uri，通过Uri可获取子目录和文件。这种方式的<b>缺点</b>是应用重装后权限失效，即使可以保存了这个Uri也没用。

   ```java
   Uri dirUri = intent.getData();
   // 持久化；应用重装后权限失效，即使知道这个uri也没用
   SPUtil.setValue(this, SP_DOC_KEY, dirUri.toString());
   //重要：少这行代码手机重启后会失去权限
   getContentResolver().takePersistableUriPermission(dirUri, 
           Intent.FLAG_GRANT_READ_URI_PERMISSION);
   ```

   

2. 通过Uri读写文件

   - 创建文件

     ```java
     // 在mUri目录（‘DuoKan’目录）下创建'test.txt'文件
     private void createFile() {
         DocumentFile documentFile = DocumentFile.fromTreeUri(this, mUri);
         DocumentFile file = documentFile.createFile("text/plain", "test.txt");
         if (file != null && file.exists()) {
             LogUtil.log(file.getName() + " created");
         }
     }
     ```

     主要用到DocumentFile类，和File类的方法类似，有isFile、isDirectory、exists、listFiles等方法

   - 删除文件

     ```java
     //删除"test.txt"
     private void deleteFile() {
         DocumentFile documentFile = DocumentFile.fromTreeUri(this, mUri);
         // listFiles()，列出所有的子文件和文件夹
         for (DocumentFile file : documentFile.listFiles()) {
             if (file.isFile() && "test.txt".equals(file.getName())) {
                 boolean delete = file.delete();
                 LogUtil.log("deleteFile: " + delete);
                 break;
             }
         }
     }
     ```

     

   - 写入数据

     ```java
     private void writeFile(Uri uri) {
         try {
             ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
             //这种方法会覆盖原来文件内容
             OutputStreamWriter output = 
                     new OutputStreamWriter(new FileOutputStream(pfd.getFileDescriptor()));
             // 不能传uri.toString(),否则FileNotFoundException
             // OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(uri.toString(), true));
             output.write("这是一段文件写入测试\n");
             output.close();
             LogUtil.log("写入成功。");
         } catch (IOException e) {
             LogUtil.log(e);
         }
     }
     ```

3. 

