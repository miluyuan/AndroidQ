## 通过FileProvider分享文件

### 分享方
1. 指定应用的FileProvider
    ```xml
    <provider
        android:name="android.support.v4.content.FileProvider"
        android:authorities="com.huawei.qappcompatissues.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths"/>
    </provider>
    ```

2. 指定应用分享的文件路径，在res/xml/目录增加文件file_paths.xml文件：

   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <paths xmlns:android="http://schemas.android.com/apk/res/android">
       <external-path name="external" path="" />
   </paths>
   ```

3. Java代码：

   ```java
   //获得分享文件的Content Uri：
   Uri fileUri = FileProvider.getUriForFile(
           this,
           "com.huawei.qappcompatissues.fileprovider",
           picFile);
   Log.e(TAG, "fileUri:" + fileUri);
   if (fileUri != null) {
       Intent intent = new Intent(Intent.ACTION_SEND);
       // Grant temporary read permission to the content URI
       //临时授予文件接收方的文件读写权限：
       intent.addFlags(
               Intent.FLAG_GRANT_READ_URI_PERMISSION);
       // Put the Uri and MIME type in the result Intent
       intent.setDataAndType(
               fileUri,
               getContentResolver().getType(fileUri));
       startActivity(Intent.createChooser(intent, "test file share"));
   } else {
       Toast.makeText(this, "share file error", Toast.LENGTH_SHORT).show();
   }
   ```

### 接收方

**接收图片示例**

1. AndroidManifest.xml文件中添加intent过滤器：

   ```xml
   <intent-filter>
       <action android:name="android.intent.action.SEND" />
       <category android:name="android.intent.category.DEFAULT" />
       <data android:mimeType="image/*" />
   </intent-filter>
   ```

2. 通过Intent读取图片，content uri：content://com.huawei.qappcompatissues.fileprovider/external/test.jpg

   ```java
   ImageView imageView = findViewById(R.id.imageView);
   Intent intent = getIntent();
   String action = intent.getAction();
   String type = intent.getType();
   if (Intent.ACTION_SEND.equals(action) && type != null) {
       // Get the file's content URI from the incoming Intent
       Uri returnUri = intent.getData();
       if (type.startsWith("image/")) {
           Log.e(TAG, "open image file:" + returnUri);
           try {
               Bitmap bmp = getBitmapFromUri(returnUri);
               imageView.setImageBitmap(bmp);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   }
   ```

   ```java
   //通过Content Uri读取图片
   public static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
       ParcelFileDescriptor parcelFileDescriptor =
               context.getContentResolver().openFileDescriptor(uri, "r");
       FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
       Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
       parcelFileDescriptor.close();
       return image;
   }
   ```



## 权限申请

1. Manifest中声明

   ```xml
   <uses-permission android:name="android.permission.READ_MEDIA_AUDIO"/>
   <uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>
   <uses-permission android:name="android.permission.READ_MEDIA_VIDEO"/>
   ```

   

2. 动态申请

   ```java
   private void requestPermission() {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
           String[] permissions = new String[]{
                   Manifest.permission.READ_MEDIA_IMAGES,
                   Manifest.permission.READ_MEDIA_AUDIO,
                   Manifest.permission.READ_MEDIA_VIDEO};
           if (ContextCompat.checkSelfPermission(this,
                   Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(this,
                       new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                       MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES);
           }
       } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           // request old storage permission
       }
   }
   ```

   

## 读写文件

### 读取文件

1. 获取文件Uri

   ```java
   //目前cursor.getCount为0，由于还没有AndroidQ源码，不知具体原因
   public static List<Uri> loadPhotoFiles(Context context) {
       Log.e(TAG, "loadPhotoFiles");
       List<Uri> photoUris = new ArrayList<>();
       Cursor cursor = context.getContentResolver().query(
               MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
               new String[]{MediaStore.Images.Media._ID}, 
               null, null, null);
       if (cursor == null) {
           return photoUris;
       }
       Log.e(TAG, "cursor size:" + cursor.getCount());
       while (cursor.moveToNext()) {
           int id = cursor.getInt(cursor
                   .getColumnIndex(MediaStore.Images.Media._ID));
           Uri photoUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()
                   + File.separator + id);
           Log.e(TAG, "photoUri:" + photoUri);
           photoUris.add(photoUri);
       }
       cursor.close();
       return photoUris;
   }
   ```

   

2. MediaProvider变更适配指导：

   > MediaProvider中的“__data”字段已经废弃掉了，开发者不能再认为该字段保存的是文件的真实路径，Q版本因为存储空间限制的变更，应用已经无法直接通过文件路径读取文件，需要使用文件的Content URI读取文件，目前发现有很多应用通过“_data”值作为文件的真实路径在加载显示图片之前判断文件是否存在，这样的做法在Q版本是有问题的，应用需要整改。

3. 

### 写入文件

1. 通过`Context.getExternalFilesDir()`过滤视图访问自己的文件不需要权限

```java
// 0/Android/data/com.example.androidq/files/Documents
//这个目录下不需要申请权限
File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
// 0/Documents
// 不能直接访问，Permission Denied，需要FileProvider
File dir2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
```

2. `MediaStore`

3. 过滤视图还施加了以下媒体相关数据限制：

   - 除非您的应用已获得 [`ACCESS_MEDIA_LOCATION`](https://developer.android.google.cn/reference/android/Manifest.permission.html#ACCESS_MEDIA_LOCATION) 权限，否则图片文件中的 Exif 元数据会被遮盖。如需了解详情，请参阅关于如何访问[照片中的位置信息](https://developer.android.google.cn/preview/privacy/scoped-storage#photos-location-info)的部分。
   - 媒体存储器中每个文件的 `DATA` 列都会被遮盖。
   - `MediaStore.Files` 表格会自行过滤，仅显示照片、视频和音频文件。例如，该表格不会再显示 PDF 文件。

4. 临时取消“过滤视图”，

   ```java
   <manifest ... >
         <!-- This attribute is "false" by default on apps targeting Android Q. -->
         <application android:requestLegacyExternalStorage="true" ... >
           ...
         </application>
       </manifest>
   ```

   

5. 明年，所有应用的主要平台版本都需要分区存储，无论其采用哪种目标 SDK 级别。因此，您应该提前确保您的应用支持分区存储。为此，请确保在运行您应用的 Android Q 设备上启用该行为。



















## 分区存储

对*targetSdkVersion<=28*(Android9及以下版本)的应用没有影响

