package com.example.androidq.storage;

import android.content.Intent;
import android.os.Bundle;

import com.example.androidq.R;
import com.example.androidq.storage.directorytree.FolderTreeActivity;
import com.example.androidq.storage.image.ImageActivity;

import androidx.appcompat.app.AppCompatActivity;

public class StorageMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_main);
        findViewById(R.id.btn1).setOnClickListener(v ->
                startActivity(new Intent(this, ScopedStorageActivity.class)));
        findViewById(R.id.btn2).setOnClickListener(v ->
                startActivity(new Intent(this, FolderTreeActivity.class)));
        findViewById(R.id.btn3).setOnClickListener(v ->
                startActivity(new Intent(this, ImageActivity.class)));
    }
}
