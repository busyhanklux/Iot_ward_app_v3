package com.example.iot_ward_app_v3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class change_page__device extends Activity {

    private Button BT_CPD_back;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_page__device);

        BT_CPD_back = findViewById(R.id.BT_CPD_back);
        BT_CPD_back.setOnClickListener(BT_CPD_back_L);
    }

    public View.OnClickListener BT_CPD_back_L = view ->
    {
        Intent intent = new Intent();
        intent.setClass(change_page__device.this,adminster_page.class);
        startActivity(intent);
        finish();
    };
}
