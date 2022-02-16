package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Multi_deal_with2 extends AppCompatActivity {

    int rule, door, select_number, room_choice, sup_adjust;
    int point_decide,count = 0; //有幾個點的資料
    String select_room, beacon_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_deal_with2);

        Intent intent2 = this.getIntent();
        Bundle bundle2 = intent2.getExtras();

        //你選擇的房間
        room_choice = bundle2.getInt("room_choice");
        sup_adjust = bundle2.getInt("sup_adjust");

        Toast txt = Toast.makeText(Multi_deal_with2.this,"room_choice："+room_choice+"，sup_adjust："+ sup_adjust, Toast.LENGTH_SHORT);
        txt.show();

    }
}