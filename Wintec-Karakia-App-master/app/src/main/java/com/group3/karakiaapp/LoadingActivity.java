package com.group3.karakiaapp;

import com.group3.karakiaapp.R;

import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.appcompat.app.*;
import androidx.fragment.app.*;
import androidx.navigation.*;
import com.group3.karakiaapp.*;
import com.group3.karakiaapp.fragments.HomeFragment;

public class LoadingActivity extends AppCompatActivity {

    Runnable update;
    View root;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        root = findViewById(R.id.loadingRoot);
        bar = findViewById(R.id.loadingDisplay);
        if (ResourceManager.Instance() == null) {
            ResourceManager.OnLoaded = () -> {
                finish();
                startActivity(new Intent(this, MainActivity.class));
            };
            new ResourceManager(this);

        }
        update = () -> {
            OnUpdate();
            root.postDelayed(update,1);
        };
        root.postDelayed(update,1);
    }
    static int updateCount = 0;
    void OnUpdate() {
        updateCount++;
    }
}