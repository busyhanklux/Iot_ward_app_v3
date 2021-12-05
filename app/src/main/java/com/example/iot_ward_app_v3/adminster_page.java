package com.example.iot_ward_app_v3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView; //ImageView的package

import androidx.appcompat.app.AppCompatActivity;

public class adminster_page extends AppCompatActivity {

    private ImageView Img_v_env,Img_v_device;
    private Button BT_adminster_back,button;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminster_page);

        Img_v_device = findViewById(R.id.Img_v_device);
        Img_v_device.setOnClickListener(Img_v_device_L);

        Img_v_env = findViewById(R.id.Img_v_env);
        Img_v_env.setOnClickListener(Img_v_env_L);

        BT_adminster_back = findViewById(R.id.BT_adminster_back);
        BT_adminster_back.setOnClickListener(BT_adminster_back_L);

        button = findViewById(R.id.button);
        button.setOnClickListener(button_L);

    }

    //跳至更改設備
    public View.OnClickListener Img_v_device_L = view ->
    {
        Intent intent = new Intent();
        intent.setClass(adminster_page.this,change_page__device.class);
        startActivity(intent);
        finish();
    };

    //跳到更改環境
    public View.OnClickListener Img_v_env_L = view ->
    {
        Intent intent = new Intent();
        intent.setClass(adminster_page.this,change_page__environment.class);
        startActivity(intent);
        finish();
    };

    //回到搜尋頁面
    public View.OnClickListener BT_adminster_back_L = view ->
    {
        Intent intent = new Intent();
        intent.setClass(adminster_page.this,MainActivity.class);
        startActivity(intent);
        finish();
    };

    //測試頁面
    public View.OnClickListener button_L = view ->
    {
        Intent intent = new Intent();
        intent.setClass(adminster_page.this,Text.class);
        startActivity(intent);
        finish();
    };

}