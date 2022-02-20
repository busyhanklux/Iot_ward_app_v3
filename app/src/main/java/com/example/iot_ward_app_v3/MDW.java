package com.example.iot_ward_app_v3;

import static java.lang.Math.abs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MDW extends AppCompatActivity {

    TextView check,tvMap;
    Button BT_Map_Open,BT_FMC;
    int RSSI1 , RSSI2 , RSSI3 , RSSIs;
    long time_now = System.currentTimeMillis() / 1000; //現在時間

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdw);

        dialog();

        check = findViewById(R.id.check);
        tvMap = findViewById(R.id.tvMap);

        BT_Map_Open = findViewById(R.id.BT_Map_Open);
        BT_Map_Open.setOnClickListener(BT_Map_Open_L);

        BT_FMC = findViewById(R.id.BT_FMC);
        BT_FMC.setOnClickListener(BT_FMC_L);

    }

    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示訊息");  //設置標題
        builder.setIcon(R.drawable.logo4); //標題前面那個小圖示
        builder.setMessage("搜尋完成"); //提示訊息

        builder.setPositiveButton("確定",((dialog, which) -> {}));

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v -> {

            try {

                File f = new File("/data/data/com.example.iot_ward_app_v3/files/deal_number.txt");
                FileReader fr = new FileReader(f);
                char [] fc = new char [(int)f.length()];
                fr.read(fc);
                fr.close();

                String text = new String(fc);
                String text_split[] = text.split(" ");

                String text_display = new String();

                //你選擇的房間
                Intent intent = this.getIntent();
                Bundle bundle = intent.getExtras();

                int sup_adjust = bundle.getInt("sup_adjust");
                int room_choice = bundle.getInt("room_choice");
                int room_choice_display = room_choice +1;
                String door_choice = bundle.getString("door_choice");
                String env_name = bundle.getString("env_name");

                tvMap.setText( "環境：" + env_name  +"\n存在的號碼資訊" + door_choice);

                for (int i = 0; i < text_split.length; i++) {

                    if (text_split[i].equals("100000"))
                    {
                        continue;
                    }

                    text_display = text_display + "\n" + text_split[i] + "號";
                }

                //Toast txt = Toast.makeText(MDW.this,"以下是存在的號碼："+text_display+"", Toast.LENGTH_SHORT);
                //txt.show();

                //存號碼
                check.setText(text_display + "\n\n" + "按下「開啟地圖」，觀看它們在地圖上的位置\n\n");

                if (text_split.length == 1)
                {

                    text_display = "\n沒有符合條件的號碼";
                    check.setText(text_display + "\n\n" + "請回上頁搜尋其他環境，或是等待一段時間再搜尋\n\n");
                    BT_Map_Open.setText("回上頁重新搜尋");

                }

                if (text_split.length != 1)
                {

                    for (int i = 0; i < text_split.length; i++) {

                        //Toast txt = Toast.makeText(MDW.this,"sup_adjust："+sup_adjust+"", Toast.LENGTH_SHORT);
                        //txt.show();

                        if (text_split[i].equals("100000"))
                        {
                            continue;
                        }

                        int firebase_number_1 = room_choice * 3 + 1;
                        int firebase_number_2 = room_choice * 3 + 2;
                        int firebase_number_3 = room_choice * 3 + 3;
                        int sup = room_choice + 1;

                        FirebaseDatabase database_sw = FirebaseDatabase.getInstance();

                        DatabaseReference beacon_RSSI_1 = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(text_split[i])).child("RSSI");
                        DatabaseReference beacon_RSSI_2 = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(text_split[i])).child("RSSI");
                        DatabaseReference beacon_RSSI_3 = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(text_split[i])).child("RSSI");
                        DatabaseReference beacon_RSSI_sup = database_sw.getReference("esp32_sup" + sup).child(String.valueOf(text_split[i])).child("RSSI");

                        int finalI = i;
                        beacon_RSSI_1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                int rssi1 = snapshot1.getValue(Integer.class);
                                RSSI1 = rssi1;

                                //Toast txt = Toast.makeText(MDW.this,"RSSI1："+RSSI1+"", Toast.LENGTH_SHORT);
                                //txt.show();

                                try {

                                    File deal_number = new File(getFilesDir(), "RSSI_inf.txt");
                                    FileWriter fw = new FileWriter(deal_number, true);

                                    fw.write(text_split[finalI]);
                                    fw.write(' ');
                                    fw.write(String.valueOf(rssi1));
                                    fw.write(' ');
                                    fw.close();

                                }catch (Exception e){

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        beacon_RSSI_2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {

                                int rssi2 = snapshot2.getValue(Integer.class);
                                RSSI2 = rssi2;

                                //Toast txt = Toast.makeText(MDW.this,"RSSI2："+RSSI2+"", Toast.LENGTH_SHORT);
                                //txt.show();

                                try {

                                    File deal_number = new File(getFilesDir(), "RSSI_inf.txt");
                                    FileWriter fw = new FileWriter(deal_number, true);

                                    fw.write(String.valueOf(rssi2));
                                    fw.write(' ');
                                    fw.close();

                                }catch (Exception e){

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        beacon_RSSI_3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot3) {

                                int rssi3 = snapshot3.getValue(Integer.class);
                                RSSI3 = rssi3;

                                //Toast txt = Toast.makeText(MDW.this,"RSSI3："+RSSI3+"", Toast.LENGTH_SHORT);
                                //txt.show();

                                try {

                                    File deal_number = new File(getFilesDir(), "RSSI_inf.txt");
                                    FileWriter fw = new FileWriter(deal_number, true);

                                    fw.write(String.valueOf(rssi3));

                                    if (sup_adjust == 1)
                                    {
                                        fw.write(' ');

                                    }else {

                                        fw.write('\n');

                                    }
                                    fw.close();


                                }catch (Exception e){

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        if (sup_adjust == 1) {

                            beacon_RSSI_sup.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshots) {

                                    int rssis = snapshots.getValue(Integer.class);
                                    RSSIs = rssis;

                                    //Toast txt = Toast.makeText(MDW.this,"RSSIs："+RSSIs+"", Toast.LENGTH_SHORT);
                                    //txt.show();

                                    try {

                                        File deal_number = new File(getFilesDir(), "RSSI_inf.txt");
                                        FileWriter fw = new FileWriter(deal_number, true);

                                        fw.write(String.valueOf(rssis));
                                        fw.write('\n');
                                        fw.close();

                                    }catch (Exception e){

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }
                }

                dialog.dismiss();


            }catch (Exception e) {

            }

        }));

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


    }

    private View.OnClickListener BT_Map_Open_L = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            if (BT_Map_Open.getText().equals("回上頁重新搜尋"))
            {
                Intent intent = new Intent();

                intent.setClass(MDW.this, Multi_main.class);
                startActivity(intent);
                finish();

            }else{

                long time_now_this = System.currentTimeMillis() / 1000; //現在時間

                if ((time_now_this - time_now ) > 90)
                {
                    Toast txt = Toast.makeText(MDW.this,"已超過90秒，為了準確度，將跳回首頁，請重新查詢", Toast.LENGTH_SHORT);
                    txt.show();

                    Intent intent = new Intent();

                    intent.setClass(MDW.this, Multi_main.class);
                    startActivity(intent);
                    finish();

                }else{

                    try {
                        File f = new File("/data/data/com.example.iot_ward_app_v3/files/RSSI_inf.txt");
                        FileReader fr = new FileReader(f);
                        char [] fc = new char [(int)f.length()];
                        fr.read(fc);
                        fr.close();

                        String text = new String(fc);
                        String text_split[] = text.split(" |\n");

                        String text_display = new String();

                        /*
                        for (int i = 0; i < text_split.length; i++) {

                            Toast txt = Toast.makeText(MDW.this,text_split[i]+"", Toast.LENGTH_SHORT);
                            txt.show();
                        }

                         */

                        //你選擇的房間
                        Intent intent = getIntent();
                        Bundle bundle = intent.getExtras();

                        int sup_adjust = bundle.getInt("sup_adjust");
                        int room_choice = bundle.getInt("room_choice");
                        int a = 0;

                        //根據你sup的結果，跟改執行方法
                        if (sup_adjust == 0)
                        {

                            int rssi_1 = 0,rssi_2 = 0,rssi_3 = 0,rssi_sup = 0;

                            for (int i = 0; i < text_split.length; i++) {

                                String device_name;

                                if((i % 4) == 0)
                                {
                                    device_name = text_split[i];
                                    rssi_1 = Integer.parseInt(text_split[i+1]);
                                    rssi_2 = Integer.parseInt(text_split[i+2]);
                                    rssi_3 = Integer.parseInt(text_split[i+3]);

                                    int gap1_2 = abs(rssi_1) - abs(rssi_2); //12之距離
                                    int gap1_3 = abs(rssi_1) - abs(rssi_3); //13之距離
                                    int gap2_3 = abs(rssi_2) - abs(rssi_3); //23之距離

                                    String description = "";
                                    int rule = 0;

                                    if ((gap2_3 < 4) & (gap2_3 > -4) & (gap1_3 < 4) & (gap1_3 > -4) & (gap1_2 < 4) & (gap1_2 > -4)
                                            & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) {

                                        //20220119
                                        description = "因為門口esp32未啟動或設置，你要找的設備可能在門口或空間中心";
                                        rule = 661;
                                        //rule_keep.setText("661");

                                    } else if ((rssi_1 > rssi_2) & (rssi_1 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { // 1最近

                                        if ((gap2_3 < 4) & (gap2_3 > -4)) { // 2,3 相似
                                            //conclude.setText("你要找的beacon靠近第一個esp32，但離第二與第三的距離相似");
                                            description = "該設備靠近 \"門口前方牆角(第一個esp)\" " +
                                                    "\n但離 \"門口斜對牆角(第二個esp) 與 門口平行牆角(第三個esp)\" 的距離相似";
                                            rule = 11;
                                            //rule_keep.setText("11");

                                        } else {

                                            //conclude.setText("該設備靠近 \"門口前方牆角(第一個esp)\" ");
                                            description = "該設備靠近 \"門口前方牆角(第一個esp)\" ";
                                            rule = 1;
                                            //rule_keep.setText("1");

                                        }
                                    } else if ((rssi_2 > rssi_1) & (rssi_2 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { // 2最近
                                        if ((gap1_3 < 4) & (gap1_3 > -4)) { // 1,3 相似
                                            //conclude.setText("你要找的beacon靠近第二個esp32，但離第一與第三的距離相似");
                                            description = "該設備靠近 \"門口斜對牆角(第二個esp)\" " +
                                                    "\n但離 \"門口前方牆角(第一個esp) 與 門口平行牆角(第三個esp)\" 的距離相似";
                                            rule = 21;
                                            //rule_keep.setText("21");
                                        } else {
                                            //conclude.setText("你要找的beacon靠近第二個esp32");
                                            description = "該設備靠近 \"門口斜對牆角(第二個esp)\" ";
                                            rule = 2;
                                            //rule_keep.setText("2");
                                        }
                                    } else if ((rssi_3 > rssi_1) & (rssi_3 > rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { // 3最近
                                        if ((gap1_2 < 4) & (gap1_2 > -4)) { // 1,2 相似
                                            //conclude.setText("你要找的beacon靠近第三個esp32，但離第一與第二的距離相似");
                                            description = "該設備靠近 \"門口平行牆角(第三個esp)\" " +
                                                    "\n但離 \"門口前方牆角(第一個esp) 與 門口斜對牆角(第二個esp)\" 的距離相似";
                                            rule = 31;
                                            //rule_keep.setText("31");

                                        } else {

                                            //conclude.setText("你要找的beacon靠近第三個esp32");
                                            description = "該設備靠近 \"門口平行牆角(第三個esp)\" ";
                                            rule = 3;
                                            //rule_keep.setText("3");

                                        }
                                        //此時1,2檢查完畢

                                    } else if ((rssi_1 < rssi_2) & (rssi_1 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                                            & (gap2_3 < 4) & (gap2_3 > -4)) { //2,3 相似，1最遠
                                        //conclude.setText("你要找的beacon遠離第一個esp32，離第二與第三的距離相似");
                                        description = "該設備遠離 \"門口前方牆角(第一個esp)\" " +
                                                "\n但離 \"門口斜對牆角(第二個esp) 與 門口平行牆角(第三個esp)\" 的距離相似";
                                        rule = 12;
                                        //rule_keep.setText("12");

                                    } else if ((rssi_2 < rssi_1) & (rssi_2 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                                            & (gap1_3 < 4) & (gap1_3 > -4)) { //1,3 相似，2最遠
                                        //conclude.setText("你要找的beacon遠離第二個esp32，離第一與第三的距離相似");

                                        description = "該設備遠離 \"門口斜對牆角(第二個esp)\" " +
                                                "\n但離 \"門口前方牆角(第一個esp) 與 門口平行牆角(第三個esp)\" 的距離相似";
                                        rule = 22;
                                        //rule_keep.setText("22");

                                    } else if ((rssi_3 < rssi_2) & (rssi_3 < rssi_1) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                                            & (gap1_2 < 4) & (gap1_2 > -4)) { //1,2 相似，3最遠
                                        //conclude.setText("你要找的beacon遠離第三個esp32，離第一與第二的距離相似");

                                        description = "該設備遠離 \"門口平行牆角(第三個esp)\" " +
                                                "\n但離 \"門口前方牆角(第一個esp) 與 門口斜對牆角(第二個esp)\" 的距離相似";
                                        rule = 32;
                                        //rule_keep.setText("32");

                                    }

                                    try {

                                        File rule_result = new File(getFilesDir(), "place_des.txt");
                                        FileWriter fw = new FileWriter(rule_result, true);

                                        fw.write(String.valueOf(rule));
                                        fw.write(' ');
                                        fw.close();

                                    }catch (Exception e){

                                    }

                                    //Toast txt = Toast.makeText(MDW.this,rssi1+"_"+rssi2+"_"+rssi3+"_", Toast.LENGTH_SHORT);
                                    //txt.show();


                                }
                            }

                        }else {

                            int rssi_1 = 0,rssi_2 = 0,rssi_3 = 0,rssi_sup = 0;

                            for (int i = 0; i < text_split.length; i++) {

                                String device_name;

                                if ((i % 5) == 0) {

                                    device_name = text_split[i];
                                    rssi_1 = Integer.parseInt(text_split[i + 1]);
                                    rssi_2 = Integer.parseInt(text_split[i + 2]);
                                    rssi_3 = Integer.parseInt(text_split[i + 3]);
                                    rssi_sup = Integer.parseInt(text_split[i + 4]);

                                    int gap1_2 = abs(rssi_1) - abs(rssi_2); //12之距離
                                    int gap1_3 = abs(rssi_1) - abs(rssi_3); //13之距離
                                    int gap2_3 = abs(rssi_2) - abs(rssi_3); //23之距離

                                    //特定條件下，啟用第二個三角形
                                    int gapsup_1 = abs(rssi_sup) - abs(rssi_1);
                                    int gapsup_3 = abs(rssi_sup) - abs(rssi_3);

                                    String description = "";
                                    int rule = 0;

                                    if ((gap2_3 < 4) & (gap2_3 > -4) & (gap1_3 < 4) & (gap1_3 > -4) & (gap1_2 < 4) & (gap1_2 > -4)
                                            & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) {

                                        //啟用第二個三角形
                                        if ((gapsup_1 < 4) & (gapsup_1 > -4) & (gapsup_3 < 4) & (gapsup_3 > -4) & (rssi_sup > -140)) {

                                            description = "你要找的設備可能位於該空間的中心";
                                            rule = 66;
                                            //rule_keep.setText("66");

                                        } else if ((rssi_1 < rssi_sup) & (rssi_3 < rssi_sup) & (rssi_sup > -140)) {
                                            //20220119
                                            description = "你要找的設備可能靠近門口";
                                            rule = 660;
                                            //rule_keep.setText("660");

                                        } else //if(rssi_sup < -140)
                                        {
                                            //20220119
                                            description = "因為門口esp32未啟動或設置，你要找的設備可能在門口或空間中心";
                                            rule = 661;
                                            //rule_keep.setText("661");
                                        }

                                    } else if ((rssi_1 > rssi_2) & (rssi_1 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { // 1最近
                                        if ((gap2_3 < 4) & (gap2_3 > -4)) { // 2,3 相似
                                            //conclude.setText("你要找的beacon靠近第一個esp32，但離第二與第三的距離相似");
                                            description = "該設備靠近 \"門口前方牆角(第一個esp)\" " +
                                                    "\n但離 \"門口斜對牆角(第二個esp) 與 門口平行牆角(第三個esp)\" 的距離相似";
                                            rule = 11;
                                            //rule_keep.setText("11");
                                        } else {
                                            if (rssi_1 < rssi_sup) { //是門口較近，還是第一個較近?
                                                //conclude.setText("該設備靠近門口，稍微接近 \"門口前方牆角(第一個esp)\" ");
                                                description = "該設備靠近門口，稍微接近 \"門口前方牆角(第一個esp)\" ";
                                                rule = 10;
                                                //rule_keep.setText("10");
                                            } else {
                                                //conclude.setText("該設備靠近 \"門口前方牆角(第一個esp)\" ");
                                                description = "該設備靠近 \"門口前方牆角(第一個esp)\" ";
                                                rule = 1;
                                                //rule_keep.setText("1");
                                            }
                                        }
                                    } else if ((rssi_2 > rssi_1) & (rssi_2 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { // 2最近
                                        if ((gap1_3 < 4) & (gap1_3 > -4)) { // 1,3 相似
                                            //conclude.setText("你要找的beacon靠近第二個esp32，但離第一與第三的距離相似");
                                            description = "該設備靠近 \"門口斜對牆角(第二個esp)\" " +
                                                    "\n但離 \"門口前方牆角(第一個esp) 與 門口平行牆角(第三個esp)\" 的距離相似";
                                            rule = 21;
                                            //rule_keep.setText("21");
                                        } else {
                                            //conclude.setText("你要找的beacon靠近第二個esp32");
                                            description = "該設備靠近 \"門口斜對牆角(第二個esp)\" ";
                                            rule = 2;
                                            //rule_keep.setText("2");
                                        }
                                    } else if ((rssi_3 > rssi_1) & (rssi_3 > rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { // 3最近
                                        if ((gap1_2 < 4) & (gap1_2 > -4)) { // 1,2 相似
                                            //conclude.setText("你要找的beacon靠近第三個esp32，但離第一與第二的距離相似");
                                            description = "該設備靠近 \"門口平行牆角(第三個esp)\" " +
                                                    "\n但離 \"門口前方牆角(第一個esp) 與 門口斜對牆角(第二個esp)\" 的距離相似";
                                            rule = 31;
                                            //rule_keep.setText("31");
                                        } else {
                                            if (rssi_3 < rssi_sup) {
                                                //conclude.setText("你要找的beacon靠近第三個esp32");
                                                description = "該設備靠近門口，稍微接近 \"門口平行牆角(第三個esp)\" ";
                                                rule = 30;
                                                //rule_keep.setText("30");
                                            } else {
                                                //conclude.setText("你要找的beacon靠近第三個esp32");
                                                description = "該設備靠近 \"門口平行牆角(第三個esp)\" ";
                                                rule = 3;
                                                //rule_keep.setText("3");
                                            }
                                        }
                                        //此時1,2檢查完畢
                                    } else if ((rssi_1 < rssi_2) & (rssi_1 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                                            & (gap2_3 < 4) & (gap2_3 > -4)) { //2,3 相似，1最遠
                                        //conclude.setText("你要找的beacon遠離第一個esp32，離第二與第三的距離相似");
                                        description = "該設備遠離 \"門口前方牆角(第一個esp)\" " +
                                                "\n但離 \"門口斜對牆角(第二個esp) 與 門口平行牆角(第三個esp)\" 的距離相似";
                                        rule = 12;
                                        //rule_keep.setText("12");

                                    } else if ((rssi_2 < rssi_1) & (rssi_2 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                                            & (gap1_3 < 4) & (gap1_3 > -4)) { //1,3 相似，2最遠
                                        //conclude.setText("你要找的beacon遠離第二個esp32，離第一與第三的距離相似");
                                        if (rssi_sup > rssi_2) {
                                            //20220119
                                            description = "該設備可能靠近門口";
                                            rule = 29;
                                            //rule_keep.setText("29");
                                        } else {
                                            description = "該設備遠離 \"門口斜對牆角(第二個esp)\" " +
                                                    "\n但離 \"門口前方牆角(第一個esp) 與 門口平行牆角(第三個esp)\" 的距離相似";
                                            rule = 22;
                                            //rule_keep.setText("22");
                                        }

                                    } else if ((rssi_3 < rssi_2) & (rssi_3 < rssi_1) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                                            & (gap1_2 < 4) & (gap1_2 > -4)) { //1,2 相似，3最遠
                                        //conclude.setText("你要找的beacon遠離第三個esp32，離第一與第二的距離相似");
                                        description = "該設備遠離 \"門口平行牆角(第三個esp)\" " +
                                                "\n但離 \"門口前方牆角(第一個esp) 與 門口斜對牆角(第二個esp)\" 的距離相似";
                                        rule = 32;
                                        //rule_keep.setText("32");

                                    }

                                    try {

                                        File rule_result = new File(getFilesDir(), "place_des.txt");
                                        FileWriter fw = new FileWriter(rule_result, true);

                                        fw.write(String.valueOf(rule));
                                        fw.write(' ');
                                        fw.close();

                                    }catch (Exception e){

                                    }

                                }
                            }

                        }

                        //Toast txt = Toast.makeText(MDW.this,a+"", Toast.LENGTH_SHORT);
                        //txt.show();



                    }catch (Exception e){

                    }

                }

                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();

                int sup_adjust = bundle.getInt("sup_adjust");
                int room_choice = bundle.getInt("room_choice");
                String door_choice = bundle.getString("door_choice");
                String env_name = bundle.getString("env_name");

                //你選擇的房間
                Bundle bundle2 = new Bundle();

                bundle2.putInt("room_choice", room_choice);
                bundle2.putInt("sup_adjust", sup_adjust);
                bundle2.putString("door_choice" , door_choice);
                bundle2.putString("env_name" , env_name);

                intent.putExtras(bundle2);

                intent.setClass(MDW.this, MDW2.class);
                startActivity(intent);
                finish();

            }
        }
    };

    private View.OnClickListener BT_FMC_L = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            Intent intent = new Intent();

            intent.setClass(MDW.this, Multi_main.class);
            startActivity(intent);
            finish();
        }
    };

    }

