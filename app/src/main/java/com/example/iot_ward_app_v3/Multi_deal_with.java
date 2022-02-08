package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Multi_deal_with extends AppCompatActivity {

    //畫板參考https://lowren.pixnet.net/blog/post/92267045
    int rule,door,select_number,room_choice;
    String select_room,beacon_name;

    TextView Messageeeeeeeeee;

    //你必須在這裡創立全域變數，這可能是最簡單的bundle方法，不然程式會誤認為 0 或 null
    Long check1,check2,check3;
    int rssi_1,rssi_2,rssi_3,rssi_sup;
    String description;

    Button bt_back;
    private TextView rule_keep,door_keep,remind_text,remind_device_L,remind_device_R,remind_room_L,remind_room_R,dir;
    private RadioButton left_door,right_door;
    private RadioGroup select_door;
    private Button pre_display,display;
    private ImageView pre_place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_deal_with);

        try {

            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();

            //你選擇的房間
            room_choice = bundle.getInt("room_choice");

            //檢查點1
            Messageeeeeeeeee = findViewById(R.id.Messageeeeeeeeee);
            Messageeeeeeeeee.setText(room_choice+"");

        }catch (Exception e) {

            Toast txt = Toast.makeText(Multi_deal_with.this, ".....", Toast.LENGTH_SHORT);
            txt.show();
        }

    }
}