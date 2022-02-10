package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Multi_mapdisplay extends AppCompatActivity {

    int point_decide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_mapdisplay);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        //有幾個點達成需求
        point_decide = bundle.getInt("point_decide");

        Toast txt = Toast.makeText(Multi_mapdisplay.this, point_decide+"第三頁", Toast.LENGTH_SHORT);
        txt.show();
    }
}