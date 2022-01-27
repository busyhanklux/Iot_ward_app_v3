package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class find_mode_choose extends AppCompatActivity {

    private ImageView One_device_I,Multi_device_I,adminster_page_I;
    private TextView One_device_T,Multi_device_T,adminster_page_T;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_mode_choose);

        One_device_I = findViewById(R.id.One_device_I);
        Multi_device_I = findViewById(R.id.Multi_device_I);
        adminster_page_I = findViewById(R.id.adminster_page_I);

        One_device_I.setOnClickListener(One_device_I_L);
        Multi_device_I.setOnClickListener(Multi_device_I_L);
        adminster_page_I.setOnClickListener(adminster_page_I_L);

        One_device_T = findViewById(R.id.One_device_T);
        Multi_device_T = findViewById(R.id.Multi_device_T);
        adminster_page_T = findViewById(R.id.adminster_page_T);

        One_device_T.setOnClickListener(One_device_I_L);
        Multi_device_T.setOnClickListener(Multi_device_I_L);
        adminster_page_T.setOnClickListener(adminster_page_I_L);

    }

    //跳轉至一開始製作的首頁
    private  View.OnClickListener One_device_I_L = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent();
            intent.setClass(find_mode_choose.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private  View.OnClickListener Multi_device_I_L = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

        }
    };

    private  View.OnClickListener adminster_page_I_L = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

        }
    };
}