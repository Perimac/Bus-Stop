package com.example.busstop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initApp();
    }

    private void initApp(){
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
            finish();
        },500);
    }


}