package com.example.iot_ward_app_v3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener; //OnClickListener的package
import android.widget.Button;
import android.widget.ImageView; //ImageView的package

import androidx.appcompat.app.AppCompatActivity;

public class change_page__environment extends AppCompatActivity {

    private Button BT_CPE_back;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_page__environment);

        BT_CPE_back = findViewById(R.id.BT_CPD_back);
        BT_CPE_back.setOnClickListener(BT_CPE_back_L);
    }

    public View.OnClickListener BT_CPE_back_L = view ->
    {
        Intent intent = new Intent();
        intent.setClass(change_page__environment.this,adminster_page.class);
        startActivity(intent);
        finish();
    };
}
