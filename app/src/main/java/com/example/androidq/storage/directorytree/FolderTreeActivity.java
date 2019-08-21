package com.example.androidq.storage.directorytree;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.TextView;

import com.example.androidq.LogUtil;
import com.example.androidq.R;
import com.example.androidq.SPUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

/**
 * 打开目录树
 * Google demo地址：
 * https://github.com/android/storage/tree/228c8e0aa19586bfcf36318ddb191719537a45a4/ActionOpenDocumentTree
 */
public class FolderTreeActivity extends AppCompatActivity {
    private static final int REQ_CODE = 334;
    private static final String SP_DOC_KEY = "DOC_KEY";
    private TextView tv;
    private Uri mUri;
    String fileName = "shuaiguo.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_tree);
        tv = findViewById(R.id.tv);
        findViewById(R.id.btn1).setOnClickListener(v -> openDirectory());
        findViewById(R.id.btn2).setOnClickListener(v -> createFile());
        findViewById(R.id.btn3).setOnClickListener(v -> deleteFile());
        findViewById(R.id.btn4).setOnClickListener(v -> writeFile());

        String value = SPUtil.getValue(this, SP_DOC_KEY);
        if (!value.isEmpty()) {
            // content://com.android.externalstorage.documents/tree/primary%3Atrtms
            LogUtil.log("sp saved uri: " + value);
            mUri = Uri.parse(value);
            showDirContents(mUri);
        }
    }

    /**
     * 写入数据
     */
    private void writeFile() {
        DocumentFile dir = DocumentFile.fromTreeUri(this, mUri);
        DocumentFile documentFile = null;
        for (DocumentFile file : dir.listFiles()) {
            if (file.isFile() && fileName.equals(file.getName())) {
                documentFile = file;
                break;
            }
        }
        if (documentFile != null && documentFile.isFile()) {
            writeFile(documentFile.getUri());
        } else {
            createFile();
        }
    }

    private void writeFile(Uri uri) {
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
            //这种方法只能覆盖原来文件内容
            OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(pfd.getFileDescriptor()));
            // 不能传uri.toString(),否则FileNotFoundException
            // OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(uri.toString(), true));
            output.write("这是一段文件写入测试\n");
            output.close();
            LogUtil.log("写入成功。");
        } catch (IOException e) {
            LogUtil.log(e);
        }
    }


    /**
     * 删除文件
     */
    private void deleteFile() {
        DocumentFile documentFile = DocumentFile.fromTreeUri(this, mUri);
        for (DocumentFile file : documentFile.listFiles()) {
            if (file.isFile() && fileName.equals(file.getName())) {
                boolean delete = file.delete();
                LogUtil.log("deleteFile: " + delete);
                break;
            }
        }
    }

    /**
     * mimeType:"
     * text/plain
     * text/html
     * image/jpeg
     * image/png
     * audio/mpeg
     * audio/ogg
     * audio/*
     * video/mp4
     * application/*
     * application/json
     * application/javascript
     * application/ecmascript
     * application/octet-stream
     * "
     * 创建文件
     */
    private void createFile() {
        DocumentFile documentFile = DocumentFile.fromTreeUri(this, mUri);
        DocumentFile file = documentFile.createFile("text/plain", fileName);
        if (file != null && file.exists()) {
            LogUtil.log(file.getName() + " created");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            LogUtil.log("result: failed.");
            return;
        }
        if (data == null || data.getData() == null) {
            LogUtil.log("result: data is null.");
            return;
        }

        Uri dirUri = data.getData();
        mUri = dirUri;
        // 持久化；应用重装后权限失效，即使知道这个uri也没用
        SPUtil.setValue(this, SP_DOC_KEY, dirUri.toString());
        //重要：少这行代码手机重启后会失去权限
        getContentResolver().takePersistableUriPermission(dirUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        showDirContents(dirUri);

    }

    private void showDirContents(Uri dirUri) {
        DocumentFile documentFile = DocumentFile.fromTreeUri(getApplication(), dirUri);
        DocumentFile[] files = documentFile.listFiles();
        StringBuilder tmp = new StringBuilder();
        for (DocumentFile file : files) {
            tmp.append(file.getName()).append("---").append(file.getType()).append("\n");
            tv.setText(tmp);
        }

    }

    /**
     * 申请目录访问权限
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQ_CODE);
    }
}
