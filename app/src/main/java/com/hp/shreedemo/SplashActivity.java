package com.hp.shreedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //TODO: here we are calling the MainActivity
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}
