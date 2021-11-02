package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Map extends AppCompatActivity {

    //畫板參考https://lowren.pixnet.net/blog/post/92267045
    Button bt_back;
    int rule,door;
    private TextView test1,rule_keep,door_keep;
    private RadioButton left_door,right_door;
    private RadioGroup  select_door;
    private Button pre_display,display;
    private ImageView pre_place;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        test1 = (TextView) findViewById(R.id.choice_place);
        rule_keep = (TextView) findViewById(R.id.rule_keep);
        door_keep = (TextView) findViewById(R.id.door_keep);

        //單選按鈕
        left_door  = (RadioButton)findViewById(R.id.left_door);
        right_door = (RadioButton)findViewById(R.id.right_door);
        select_door = (RadioGroup)findViewById(R.id.select_door);

        //設定 RadioGroup
        select_door.setOnCheckedChangeListener(select_door_L);

        //設定按鈕
        pre_display = (Button)findViewById(R.id.check_door);
        pre_display.setOnClickListener(pre_display_L);
        display = (Button)findViewById(R.id.display);
        display.setOnClickListener(display_L);
        //圖片
        pre_place = (ImageView)findViewById(R.id.pre_place);

        try {

            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            int rssi_1 = bundle.getInt("rssi_1");
            int rssi_2 = bundle.getInt("rssi_2");
            int rssi_3 = bundle.getInt("rssi_3");

            //利用判斷規則決定原點的位置
            //版本1：套用規則一
            //規則一：純粹的比rssi哪個為最小，它就是最靠近的
            //規則二：延伸規則一，套用但出現兩者rssi相同之情形
            if((rssi_1 > rssi_2) & (rssi_1 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                Toast test = Toast.makeText(Map.this,"第一個esp32",Toast.LENGTH_SHORT);
                test.show();
                rule_keep.setText("1");
                rule = 1;
            }else if((rssi_2 > rssi_1) & (rssi_2 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                Toast test = Toast.makeText(Map.this,"第二個esp32",Toast.LENGTH_SHORT);
                test.show();
                rule_keep.setText("2");
                rule = 2;
            }else if((rssi_3 > rssi_1) & (rssi_3 > rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                Toast test = Toast.makeText(Map.this,"第三個esp32",Toast.LENGTH_SHORT);
                test.show();
                rule_keep.setText("3");
                rule = 3;
            }else{
                //test1.setText("沒有這東西");
                rule_keep.setText("0");
                rule = 0;
            }


            Toast test2 = Toast.makeText(Map.this,rssi_1+" "+rssi_2+" "+rssi_3,Toast.LENGTH_SHORT);
            test2.show();

        }catch (Exception intent_error){
            Intent intent = new Intent();
            intent.setClass(Map.this,MainActivity.class);
        }

        //button
        bt_back = (Button)findViewById(R.id.back);
        bt_back.setOnClickListener(bt_backListener);
    }


    private RadioGroup.OnCheckedChangeListener select_door_L = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.left_door){ //左側門
                door = 1;
                door_keep.setText("1");
                Toast test = Toast.makeText(Map.this,door_keep.getText(),Toast.LENGTH_SHORT);
                test.show();
            }
            if (checkedId == R.id.right_door){ //右側門
                door = 2;
                door_keep.setText("2");
                Toast test = Toast.makeText(Map.this,door_keep.getText(),Toast.LENGTH_SHORT);
                test.show();
            }}};


    //預覽左門還是右門
    public View.OnClickListener pre_display_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast test3 = Toast.makeText(Map.this,String.valueOf(door),Toast.LENGTH_SHORT);
            test3.show();

            if(door == 1){//左門
                pre_place.setImageResource(R.drawable.place1);
                Toast test = Toast.makeText(Map.this,door_keep.getText(),Toast.LENGTH_SHORT);
                test.show();
            }
            if(door == 2){//右門
                pre_place.setImageResource(R.drawable.place2);
                Toast test = Toast.makeText(Map.this,door_keep.getText(),Toast.LENGTH_SHORT);
                test.show();
            } }};

    //回到首頁重新查詢
    private  View.OnClickListener bt_backListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Map.this,MainActivity.class);
            startActivity(intent);
        }
    };

    //切換展示地圖
    private  View.OnClickListener display_L = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {

            Intent intent_2 = new Intent();
            Bundle bundle = new Bundle();
            int rssi_1 = bundle.getInt("rssi_1");
            int rssi_2 = bundle.getInt("rssi_2");
            int rssi_3 = bundle.getInt("rssi_3");

            Bundle bundle2 = new Bundle();
            bundle2.putInt("rssi_1",rssi_1);
            bundle2.putInt("rssi_2",rssi_2);
            bundle2.putInt("rssi_3",rssi_3);

            int door2 = Integer.parseInt((String) door_keep.getText());
            bundle2.putInt("door2",door2);

            int rule2 = Integer.parseInt((String) rule_keep.getText());
            bundle2.putInt("rule2",rule2);

            intent_2.putExtras(bundle2);

            intent_2.setClass(Map.this,display_Map.class);
            startActivity(intent_2);
        }
    };
}