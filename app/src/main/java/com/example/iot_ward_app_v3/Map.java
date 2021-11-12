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
    int rule,door,select_number;
    String select_room;

    //你必須在這裡創立全域變數，這可能是最簡單的bundle方法，不然程式會誤認為 0 或 null
    Long check1,check2,check3;
    int rssi_1,rssi_2,rssi_3;

    Button bt_back;
    private TextView test1,rule_keep,door_keep,remind_text;
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
        remind_text = (TextView) findViewById(R.id.remind_text); //提醒訊息

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
            rssi_1 = bundle.getInt("rssi_1");
            rssi_2 = bundle.getInt("rssi_2");
            rssi_3 = bundle.getInt("rssi_3");

            check1 = bundle.getLong("check_time1");
            check2 = bundle.getLong("check_time2");
            check3 = bundle.getLong("check_time3");

            //某某
            select_number = bundle.getInt("select_number");
            //在某某的情形
            select_room = bundle.getString("select_room");

            Bundle bundle2 = new Bundle();
            bundle2.putLong("check_time1",check1);
            bundle2.putLong("check_time2",check2);
            bundle2.putLong("check_time3",check3);
            bundle2.putInt("select_number",select_number);
            bundle2.putString("select_room",select_room);
            bundle2.putInt("rssi_1",rssi_1);
            bundle2.putInt("rssi_2",rssi_2);
            bundle2.putInt("rssi_3",rssi_3);

            long time_now=System.currentTimeMillis() / 1000; //獲取app系統時間

            //RSSI判定(版本2，規則一和二)
            //規則一：純粹的比rssi哪個為最小，它就是最靠近的
            //規則二：延伸規則1，但出現兩者rssi相同之情形(兩者相同距為遠方)
            //規則三：出現兩者rssi相同之情形(兩者相同距為近方)，如果不符合一二，執行之
            //條件一：三個同時7分內，或其中兩個5分內

            //三個同時超過7分鐘
            if((time_now - check1 > 420) & (time_now - check2 > 420) & (time_now - check3 > 420)){
                //conclude.setText("你要找的beacon，可能不在此範圍一段時間，或著三個esp32同時一段時間未啟動");
                rule = 0;
                rule_keep.setText("0");
            }else if ((time_now - check1 > 300) & (time_now - check2 > 300)) { //1.2同時超過五分鐘
                //conclude.setText("esp裝置一和二未啟動或未偵測到一段時間，因此可能位於第三個esp32附近");
                rule = -3;
                rule_keep.setText("-3");
            }else if ((time_now - check2 > 300) & (time_now - check3 > 300)) { //2.3同時超過五分鐘
                //conclude.setText("esp裝置二和三未啟動或未偵測到一段時間，因此可能位於第一個esp32附近");
                rule = -1;
                rule_keep.setText("-1");
            }else if ((time_now - check1 > 300) & (time_now - check3 > 300)) { //1.3同時超過五分鐘
                //conclude.setText("esp裝置一和三未啟動或未偵測到一段時間，因此可能位於第二個esp32附近");
                rule = -2;
                rule_keep.setText("-2");
            }else if (time_now - check1 > 300)  { //只有1超過五分鐘
                //conclude.setText("esp裝置一未啟動或未偵測到一段時間，因此可能位於第二個和第三個esp32附近");
                rule = -23;
                rule_keep.setText("-23");
            }else if (time_now - check2 > 300)  { //只有2超過五分鐘
                //conclude.setText("esp裝置二未啟動或未偵測到一段時間，因此可能位於第一個和第三個esp32附近");
                rule = -13;
                rule_keep.setText("-13");
            }else if (time_now - check3 > 300)  { //只有1超過五分鐘
                //conclude.setText("esp裝置三未啟動或未偵測到一段時間，因此可能位於第一個和第二個esp32附近");
                rule = -12;
                rule_keep.setText("-12");
            }else {
                if((rssi_1 > rssi_2) & (rssi_1 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ //1(1最近)
                    if((rssi_2 == rssi_3)  & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ //2
                        //conclude.setText("你要找的beacon靠近第一個esp32，但離第二與第三的距離相似");
                        rule = 11;
                        rule_keep.setText("11");
                    }else{
                        //conclude.setText("你要找的beacon靠近第一個esp32");
                        rule = 1;
                        rule_keep.setText("1");
                    }
                }else if((rssi_2 > rssi_1) & (rssi_2 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ //1(2最近)
                    if((rssi_1 == rssi_3)  & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ //2
                        //conclude.setText("你要找的beacon靠近第二個esp32，但離第一與第三的距離相似");
                        rule = 21;
                        rule_keep.setText("21");
                    }else {
                        //conclude.setText("你要找的beacon靠近第二個esp32");
                        rule = 2;
                        rule_keep.setText("2");
                    }
                }else if((rssi_3 > rssi_1) & (rssi_3 > rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { //1(3最近)
                    if ((rssi_1 == rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { //2
                        //conclude.setText("你要找的beacon靠近第三個esp32，但離第一與第二的距離相似");
                        rule = 31;
                        rule_keep.setText("31");
                    } else {
                        //conclude.setText("你要找的beacon靠近第三個esp32");
                        rule = 3;
                        rule_keep.setText("3");
                    }
                    //此時1,2檢查完畢
                }else if((rssi_1 < rssi_2) & (rssi_1 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140) & (rssi_2 == rssi_3)){ //3(1最遠)
                    //conclude.setText("你要找的beacon遠離第一個esp32，離第二與第三的距離相似");
                    rule = 12;
                    rule_keep.setText("12");
                }else if((rssi_2 < rssi_1) & (rssi_2 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140) & (rssi_1 == rssi_3)){ //3(2最遠)
                    //conclude.setText("你要找的beacon遠離第二個esp32，離第一與第三的距離相似");
                    rule = 22;
                    rule_keep.setText("22");
                }else if((rssi_3 < rssi_2) & (rssi_3 < rssi_1) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140) & (rssi_2 == rssi_1)){ //3(3最遠)
                    //conclude.setText("你要找的beacon遠離第三個esp32，離第一與第二的距離相似");
                    rule = 32;
                    rule_keep.setText("32");
                }else{
                    //conclude.setText("資料有誤，或是未建構這項規則");
                    rule = 0;
                    rule_keep.setText("0");
                }
            }
            //利用判斷規則決定原點的位置
            //版本1：套用規則一
            //規則一：純粹的比rssi哪個為最小，它就是最靠近的
            //規則二：延伸規則一，套用但出現兩者rssi相同之情形
            /*
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

             */


            //Toast test2 = Toast.makeText(Map.this,rssi_1+" "+rssi_2+" "+rssi_3,Toast.LENGTH_SHORT);
            //test2.show();

        }catch (Exception intent_error){
            Intent intent = new Intent();
            intent.setClass(Map.this,MainActivity.class);
            startActivity(intent);
            finish();
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
                Toast test = Toast.makeText(Map.this,check1+"",Toast.LENGTH_SHORT);
                test.show();
                remind_text.setText("提醒訊息：點擊「展示」按鈕，以觀看\n"+select_number+" 號在 "+select_room+" 的情形");
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
                //Toast test = Toast.makeText(Map.this,door_keep.getText(),Toast.LENGTH_SHORT);
                //test.show();
            }
            if(door == 2){//右門
                pre_place.setImageResource(R.drawable.place2);
                //Toast test = Toast.makeText(Map.this,door_keep.getText(),Toast.LENGTH_SHORT);
                //test.show();
            } }};

    //回到首頁重新查詢
    private  View.OnClickListener bt_backListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Map.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    //切換展示地圖
    public  View.OnClickListener display_L = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            Bundle bundle2 = new Bundle();

            //防止回上一頁出error
            //首先從1獲取，再丟入2

            bundle2.putInt("rssi_1",rssi_1);
            bundle2.putInt("rssi_2",rssi_2);
            bundle2.putInt("rssi_3",rssi_3);

            //這個是給回上一頁準備資料用的
            //首先從1獲取，再丟入2

            bundle2.putLong("check_time1",check1);
            bundle2.putLong("check_time2",check2);
            bundle2.putLong("check_time3",check3);

            bundle2.putInt("select_number",select_number);
            bundle2.putString("select_room",select_room);
            Toast test2 = Toast.makeText(Map.this,rssi_1+" "+rssi_2+" "+rssi_3,Toast.LENGTH_SHORT);
            test2.show();

            //傳給下一頁：門的方向資料
            int door2 = Integer.parseInt((String) door_keep.getText());
            bundle2.putInt("door2",door2);

            //傳給下一頁：點的放置資料
            int rule2 = Integer.parseInt((String) rule_keep.getText());
            bundle2.putInt("rule2",rule2);

            intent.putExtras(bundle2);
            intent.setClass(Map.this,display_Map.class);
            startActivity(intent);
            finish();
        }
    };
}