package com.example.androidq.clipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidq.R;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class ClipboardActivity extends AppCompatActivity {

    private TextView mTvPost;
    private TextView mTvCopy;
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTvCopy.setText(System.currentTimeMillis() + "");
                    mHandler.sendEmptyMessage(0);
                }
            }, 1000);
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipboard);
        findViewById(R.id.btn1).setOnClickListener(v -> copy());
        findViewById(R.id.btn2).setOnClickListener(v -> post());
        mTvCopy = findViewById(R.id.tv_copy);
        mTvPost = findViewById(R.id.tv_post);

        mHandler.sendEmptyMessage(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void post() {
        ClipboardManager clipboardManager = getSystemService(ClipboardManager.class);
        String text = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
        mTvPost.setText(text);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void copy() {
        ClipboardManager clipboardManager = getSystemService(ClipboardManager.class);
        ClipData clipData = ClipData.newPlainText("key", mTvCopy.getText() + "wang");
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
    }
}
