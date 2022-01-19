package com.example.iot_ward_app_v3;

import static java.lang.Math.abs;

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
    String select_room,beacon_name;

    //你必須在這裡創立全域變數，這可能是最簡單的bundle方法，不然程式會誤認為 0 或 null
    Long check1,check2,check3;
    int rssi_1,rssi_2,rssi_3,rssi_sup;
    String description;

    Button bt_back;
    private TextView rule_keep,door_keep,remind_text,remind_device_L,remind_device_R,remind_room_L,remind_room_R,dir;
    private RadioButton left_door,right_door;
    private RadioGroup  select_door;
    private Button pre_display,display;
    private ImageView pre_place;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        rule_keep = (TextView) findViewById(R.id.rule_keep);
        door_keep = (TextView) findViewById(R.id.door_keep);

        remind_text = (TextView) findViewById(R.id.remind_text); //提醒訊息
        remind_device_L = findViewById(R.id.remind_device_L);
        remind_device_R = findViewById(R.id.remind_device_R);
        remind_room_L = findViewById(R.id.remind_room_L);
        remind_room_R = findViewById(R.id.remind_room_R);
        dir = findViewById(R.id.dir);

        //設定按鈕
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
            rssi_sup = bundle.getInt("rssi_sup");

            check1 = bundle.getLong("check_time1");
            check2 = bundle.getLong("check_time2");
            check3 = bundle.getLong("check_time3");

            //某某號，設備名字嘎啦嘎拉
            select_number = bundle.getInt("select_number");
            beacon_name = bundle.getString("beacon_name");

            //在某某房間的情形
            select_room = bundle.getString("select_room");

            //門的方向
            door = bundle.getInt("select_door");

            if (door == 1)
            {
                remind_text.setText("提醒：點擊「展示」按鈕觀看地圖\n");
                remind_device_R.setText(select_number+"號，" + beacon_name );
                remind_room_R.setText(select_room);

                if(rssi_sup < -140)
                {
                    dir.setText("\n該環境的門在左側，門口未設置偵測裝置");
                }else
                {
                    dir.setText("\n該環境的門在左側");
                }
                pre_place.setImageResource(R.drawable.place1);
            }
            if (door == 2)
            {
                remind_text.setText("提醒：點擊「展示」按鈕觀看地圖\n");
                remind_device_R.setText(select_number+"號，" + beacon_name );
                remind_room_R.setText(select_room);

                if(rssi_sup < -140)
                {
                    dir.setText("\n該環境的門在右側，門口未設置偵測裝置");
                }else
                {
                    dir.setText("\n該環境的門在右側");
                }
                pre_place.setImageResource(R.drawable.place2);
            }


            Bundle bundle2 = new Bundle();
            bundle2.putLong("check_time1",check1);
            bundle2.putLong("check_time2",check2);
            bundle2.putLong("check_time3",check3);

            bundle2.putInt("select_number",select_number);
            bundle2.putString("select_room",select_room);
            bundle2.putInt("select_door",door);

            bundle2.putInt("rssi_1",rssi_1);
            bundle2.putInt("rssi_2",rssi_2);
            bundle2.putInt("rssi_3",rssi_3);
            bundle2.putInt("rssi_sup",rssi_sup);

            long time_now=System.currentTimeMillis() / 1000; //獲取app系統時間

            //RSSI判定：
            //條件一：三個同時7分內，或其中兩個5分內
            //過：
            //規則一：純粹的比rssi哪個為最小，它就是最靠近的
            //規則二：延伸規則1，但出現兩者rssi相同之情形(兩者相同距為遠方)
            //規則三：出現兩者rssi相同之情形(兩者相同距為近方)，如果不符合一二，執行之
            //未過：
            //兩者比大小，或以唯一為主

            int gap1_2 = abs(rssi_1) - abs(rssi_2); //12之距離
            int gap1_3 = abs(rssi_1) - abs(rssi_3); //13之距離
            int gap2_3 = abs(rssi_2) - abs(rssi_3); //23之距離

            //特定條件下，啟用第二個三角形
            int gapsup_1 = abs(rssi_sup) - abs(rssi_1);
            int gapsup_3 = abs(rssi_sup) - abs(rssi_3);

            //rule的數字：代碼，如果有負，其一不在，三者不在為0
            //第一：為第幾個附近
            //第二：(如果為負)兩位數一起看，第幾和第幾之間
            //第二：(如果為正)如果相似，個位為1(等近)或2(等遠)，十位為主角
            //第三：三位數，同二，再加上靠近第幾(中間為0)

            //esp32：1、門口前方牆角(第一個esp) 2、門口斜對牆角(第二個esp) 3、門口平行牆角(第三個esp)
            //三個同時超過10分鐘，就是沒有
            if((time_now - check1 > 600) & (time_now - check2 > 600) & (time_now - check3 > 600)){
                description = "你要找的設備，可能不在此範圍一段時間，或著三個esp同時一段時間未啟動";
                rule = 0;   rule_keep.setText("0");

            }else if ((time_now - check1 > 600) & (time_now - check2 > 600)) { //1.2同時超過10分鐘
                description = "esp裝置一(門口前方牆角) 和 esp裝置二(門口斜對牆角) 未啟動或未偵測到一段時間" +
                                "\n因此可能位於 \"門口平行牆角(第三個esp)\" 附近";
                rule = -3;  rule_keep.setText("-3");

            }else if ((time_now - check2 > 600) & (time_now - check3 > 600)) { //2.3同時超過10分鐘
                description = "esp裝置二(門口斜對牆角) 和 esp裝置三(門口平行牆角) 未啟動或未偵測到一段時間" +
                                "\n因此可能位於 \"門口前方牆角(第一個esp)\" 附近";
                rule = -1;  rule_keep.setText("-1");

            }else if ((time_now - check1 > 600) & (time_now - check3 > 600)) { //1.3同時超過10分鐘
                description = "esp裝置一(門口前方牆角) 和 esp裝置三(門口平行牆角) 未啟動或未偵測到一段時間" +
                                "\n因此可能位於 \"門口斜對牆角(第二個esp)\" 附近";
                rule = -2;  rule_keep.setText("-2");

            }else if (time_now - check1 > 600)  { //只有1超過10分鐘
                //conclude.setText("esp裝置一未啟動或未偵測到一段時間，因此可能位於第二個和第三個esp32附近");

                if((rssi_2 > -140) & (rssi_3 > -140) & (gap2_3 < 4) & (gap2_3 > -4)){ //判定：2和3 RSSI接近，相似距離
                    description = "esp裝置一(門口前方牆角)未啟動或未偵測到一段時間 " +
                            "\n可能位於 \"門口斜對牆角(第二個esp) 和 門口平行牆角(第三個esp)\" 之間" +
                            "\n或可能位於兩者之間的牆外";
                    rule = -230;    rule_keep.setText("-230");

                }else if((rssi_2 > -140) & (rssi_3 > -140) & (rssi_2 > rssi_3)){ //判定：2 > 3 ，2比較近
                    description = "esp裝置一(門口前方牆角)未啟動或未偵測到一段時間 " +
                            "\n可能位於 \"門口斜對牆角(第二個esp)\" 附近" +
                            "\n或可能位於 \"門口斜對牆角(第二個esp) 和 門口平行牆角(第三個esp)\" 之間的牆外";
                    rule = -232;    rule_keep.setText("-232");

                }else if((rssi_2 > -140) & (rssi_3 > -140) & (rssi_2 < rssi_3)){ //判定：2 < 3 ，3比較近
                    description = "esp裝置一(門口前方牆角)未啟動或未偵測到一段時間 " +
                            "\n可能位於 \"門口平行牆角(第三個esp)\" 附近" +
                            "\n或可能位於 \"門口斜對牆角(第二個esp) 和 門口平行牆角(第三個esp)\" 之間的牆外";
                    rule = -233;    rule_keep.setText("-233");
                }
            }else if (time_now - check2 > 600)  { //只有2超過10分鐘
                //conclude.setText("esp裝置二未啟動或未偵測到一段時間，因此可能位於第一個和第三個esp32附近");

                if((rssi_1 > -140) & (rssi_3 > -140) & (gap1_3 < 4) & (gap1_3 > -4)){ //判定：1和3 RSSI接近，相似距離
                    description = "esp裝置二(門口斜對牆角)未啟動或未偵測到一段時間 " +
                            "\n可能位於 \"門口前方牆角(第一個esp) 和 門口平行牆角(第三個esp)\" 之間" +
                            "\n或可能位於兩者之間的牆外";
                    rule = -130;    rule_keep.setText("-130");

                }else if((rssi_1 > -140) & (rssi_3 > -140) & (rssi_1 > rssi_3)){ //判定：1 > 3，1比較近
                    description = "esp裝置二(門口斜對牆角)未啟動或未偵測到一段時間 " +
                            "\n可能位於 \"門口前方牆角(第一個esp)\" 附近" +
                            "\n或可能位於 \"門口前方牆角(第一個esp) 和 門口平行牆角(第三個esp)\" 之間的牆外";
                    rule = -131;    rule_keep.setText("-131");

                }else if((rssi_1 > -140) & (rssi_3 > -140) & (rssi_1 < rssi_3)){ //判定：1 < 3，3比較近
                    description = "esp裝置二(門口斜對牆角)未啟動或未偵測到一段時間 " +
                            "\n可能位於 \"門口斜對牆角(第二個esp)\" 附近" +
                            "\n或可能位於 \"門口斜對牆角(第二個esp) 和 門口平行牆角(第三個esp)\" 之間的牆外";
                    rule = -133;    rule_keep.setText("-133");
                }
            }else if (time_now - check3 > 600)  { //只有3超過10分鐘
                //conclude.setText("esp裝置三未啟動或未偵測到一段時間，因此可能位於第一個和第二個esp32附近");

                if((rssi_1 > -140) & (rssi_2 > -140) & (gap1_2 < 4) & (gap1_2 > -4)){ //判定：1和2 RSSI接近，相似距離
                    description = "esp裝置三(門口平行牆角)未啟動或未偵測到一段時間 " +
                            "\n可能位於 \"門口前方牆角(第一個esp) 或 門口斜對牆角(第二個esp)\" 之間" +
                            "\n或可能位於兩者之間的牆外";
                    rule = -120;     rule_keep.setText("-120");

                }else if((rssi_1 > -140) & (rssi_2 > -140) & (rssi_1 > rssi_2)){ //判定：1 > 2，1比較近
                    description = "esp裝置三(門口平行牆角)未啟動或未偵測到一段時間 " +
                            "\n可能位於 \"門口前方牆角(第一個esp)\" 附近" +
                            "\n或可能位於 \"門口前方牆角(第一個esp) 或 門口斜對牆角(第二個esp)\" 之間的牆外";
                    rule = -121;     rule_keep.setText("-121");

                }else if((rssi_1 > -140) & (rssi_2 > -140) & (rssi_1 < rssi_2)){ //判定：1 < 2，2比較近
                    description = "esp裝置三(門口平行牆角)未啟動或未偵測到一段時間 " +
                            "\n可能位於 \"門口斜對牆角(第二個esp)\" 附近" +
                            "\n或可能位於 \"門口前方牆角(第一個esp) 或 門口斜對牆角(第二個esp)\" 之間的牆外";
                    rule = -122;     rule_keep.setText("-122");
                }
            }else {
                if ((gap2_3 < 4) & (gap2_3 > -4) & (gap1_3 < 4) & (gap1_3 > -4) & (gap1_2 < 4) & (gap1_2 > -4)
                        & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){

                    //啟用第二個三角形
                    if ((gapsup_1 < 4) & (gapsup_1 > -4) & (gapsup_3 < 4) & (gapsup_3 > -4) & (rssi_sup > -140))
                    {
                        description = "你要找的設備可能位於該空間的中心";
                        rule = 66;       rule_keep.setText("66");
                    }
                    else if((rssi_1 < rssi_sup) & (rssi_3 < rssi_sup) & (rssi_sup > -140))
                    {
                        //20220119
                        description = "你要找的設備可能靠近門口";
                        rule = 660;       rule_keep.setText("660");
                    }
                    else if(rssi_sup < -140)
                    {
                        //20220119
                        description = "因為門口esp32未啟動或設置，你要找的設備可能在門口或空間中心";
                        rule = 661;       rule_keep.setText("661");
                    }

                }else if((rssi_1 > rssi_2) & (rssi_1 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ // 1最近
                    if((gap2_3 < 4) & (gap2_3 > -4) ){ // 2,3 相似
                        //conclude.setText("你要找的beacon靠近第一個esp32，但離第二與第三的距離相似");
                        description = "該設備靠近 \"門口前方牆角(第一個esp)\" " +
                                "\n但離 \"門口斜對牆角(第二個esp) 與 門口平行牆角(第三個esp)\" 的距離相似";
                        rule = 11;      rule_keep.setText("11");
                    }else{
                        //conclude.setText("你要找的beacon靠近第一個esp32");
                        description = "該設備靠近 \"門口前方牆角(第一個esp)\" ";
                        rule = 1;       rule_keep.setText("1");
                    }
                }else if((rssi_2 > rssi_1) & (rssi_2 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ // 2最近
                    if((gap1_3 < 4) & (gap1_3 > -4) ){ // 1,3 相似
                        //conclude.setText("你要找的beacon靠近第二個esp32，但離第一與第三的距離相似");
                        description = "該設備靠近 \"門口斜對牆角(第二個esp)\" " +
                                "\n但離 \"門口前方牆角(第一個esp) 與 門口平行牆角(第三個esp)\" 的距離相似";
                        rule = 21;      rule_keep.setText("21");
                    }else {
                        //conclude.setText("你要找的beacon靠近第二個esp32");
                        description = "該設備靠近 \"門口斜對牆角(第二個esp)\" ";
                        rule = 2;       rule_keep.setText("2");
                    }
                }else if((rssi_3 > rssi_1) & (rssi_3 > rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { // 3最近
                    if ((gap1_2 < 4) & (gap1_2 > -4)) { // 1,2 相似
                        //conclude.setText("你要找的beacon靠近第三個esp32，但離第一與第二的距離相似");
                        description = "該設備靠近 \"門口平行牆角(第三個esp)\" " +
                                "\n但離 \"門口前方牆角(第一個esp) 與 門口斜對牆角(第二個esp)\" 的距離相似";
                        rule = 31;      rule_keep.setText("31");
                    } else {
                        //conclude.setText("你要找的beacon靠近第三個esp32");
                        description = "該設備靠近 \"門口平行牆角(第三個esp)\" ";
                        rule = 3;       rule_keep.setText("3");
                    }
                    //此時1,2檢查完畢
                }else if((rssi_1 < rssi_2) & (rssi_1 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                        & (gap2_3 < 4) & (gap2_3 > -4)){ //2,3 相似，1最遠
                    //conclude.setText("你要找的beacon遠離第一個esp32，離第二與第三的距離相似");
                    description = "該設備遠離 \"門口前方牆角(第一個esp)\" " +
                            "\n但離 \"門口斜對牆角(第二個esp) 與 門口平行牆角(第三個esp)\" 的距離相似";
                    rule = 12;      rule_keep.setText("12");

                }else if((rssi_2 < rssi_1) & (rssi_2 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                        & (gap1_3 < 4) & (gap1_3 > -4)){ //1,3 相似，2最遠
                    //conclude.setText("你要找的beacon遠離第二個esp32，離第一與第三的距離相似");
                    if (rssi_sup > rssi_2)
                    {
                        //20220119
                        description = "該設備可能靠近門口";
                        rule = 29;      rule_keep.setText("29");
                    }else
                    {
                        description = "該設備遠離 \"門口斜對牆角(第二個esp)\" " +
                                "\n但離 \"門口前方牆角(第一個esp) 與 門口平行牆角(第三個esp)\" 的距離相似";
                        rule = 22;      rule_keep.setText("22");
                    }

                }else if((rssi_3 < rssi_2) & (rssi_3 < rssi_1) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                        & (gap1_2 < 4) & (gap1_2 > -4)){ //1,2 相似，3最遠
                    //conclude.setText("你要找的beacon遠離第三個esp32，離第一與第二的距離相似");
                    description = "該設備遠離 \"門口平行牆角(第三個esp)\" " +
                            "\n但離 \"門口前方牆角(第一個esp) 與 門口斜對牆角(第二個esp)\" 的距離相似";
                    rule = 32;      rule_keep.setText("32");

                }else{
                    //conclude.setText("資料有誤，或是未建構這項規則");
                    description = "資料有誤，或是未建構這項規則";
                    rule = 0;       rule_keep.setText("0");
                }
            }

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

            //必須選擇其一才能觀看地圖，否則執行toast
            if ((door == 1) || (door == 2)){

                Intent intent = new Intent();
                Bundle bundle2 = new Bundle();

                //防止回上一頁出error
                //首先從1獲取，再丟入2

                bundle2.putInt("rssi_1",rssi_1);
                bundle2.putInt("rssi_2",rssi_2);
                bundle2.putInt("rssi_3",rssi_3);
                bundle2.putInt("rssi_sup",rssi_sup);

                //這個是給回上一頁準備資料用的
                //首先從1獲取，再丟入2

                bundle2.putLong("check_time1",check1);
                bundle2.putLong("check_time2",check2);
                bundle2.putLong("check_time3",check3);

                bundle2.putInt("select_number",select_number);
                bundle2.putString("select_room",select_room);
                bundle2.putString("beacon_name",beacon_name);
                //Toast test2 = Toast.makeText(Map.this,rssi_1+" "+rssi_2+" "+rssi_3,Toast.LENGTH_SHORT);
                //test2.show();

                //傳給下一頁：門的方向資料
                bundle2.putInt("door2",door);
                bundle2.putInt("select_door",door);

                //傳給下一頁：點的放置資料
                int rule2 = Integer.parseInt((String) rule_keep.getText());
                bundle2.putInt("rule2",rule2);

                bundle2.putString("description",description);

                intent.putExtras(bundle2);
                intent.setClass(Map.this,display_Map.class);
                startActivity(intent);
                finish();
            }else{
                Toast warning = Toast.makeText(Map.this,"請選擇門的位置，選擇完再按「展示」",Toast.LENGTH_SHORT);
                warning.show();
        }

    }

    };
}