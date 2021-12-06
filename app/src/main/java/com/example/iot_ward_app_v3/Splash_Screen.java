package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Splash_Screen extends AppCompatActivity {

    //把資料預載放這
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Toast hint = Toast.makeText(Splash_Screen.this,"歡迎",Toast.LENGTH_SHORT);
        hint.show();

        Intent intent = new Intent();
        intent.setClass(Splash_Screen.this,MainActivity.class);
        startActivity(intent);
        finish();

    }
}