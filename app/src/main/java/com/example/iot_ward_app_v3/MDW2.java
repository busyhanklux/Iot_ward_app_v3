package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MDW2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdw2);

        Intent intent = getIntent();
        Bundle bundle2 = intent.getExtras();

        int sup_adjust = bundle2.getInt("sup_adjust");
        int room_choice = bundle2.getInt("room_choice");

        Toast txt = Toast.makeText(MDW2.this,"sup_adjust："+sup_adjust+"room_choice："+ room_choice, Toast.LENGTH_SHORT);
        txt.show();
    }
}