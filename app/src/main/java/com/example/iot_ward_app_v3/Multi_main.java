package com.example.iot_ward_app_v3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;

public class Multi_main extends AppCompatActivity {

    private TextView tv_ei, conclude, detail;
    private Spinner sp_esp32_choice, beacon_spinner, beacon_id_num_spinner;
    private Button btMap, btStatus, esp32_switch, find_major;
    private ImageView To_adminster_page;

    private Button BT_FMC, BT_find, BT_Map_Open;
    private TextView search_hint, search_hint_map;

    int room_choice, beacon_number_choice;
    int number_decided; //1.用來丟入下一頁使用 2.防呆
    int rssi_1, rssi_2, rssi_3, rssi_sup; //存放rssi
    int To_adminster_page_TapCount = 0; //如同成為開發者一般
    Long Time1, Time2, Time3, Timesup;

    String sw_number; //放esp32切換
    String String_rssi_1, String_rssi_2, String_rssi_3, String_rssi_sup; //存放rssi，用於顯示在esp32切換
    String String_distance_1, String_distance_2, String_distance_3; //存放距離，用於顯示儀器測距
    String String_displaytime_1, String_displaytime_2, String_displaytime_3; //存放時間，用來顯示時間
    Long unixtime_check1, unixtime_check2, unixtime_check3; //存放unix時間，用來判定時間
    String Major_1, Major_2, Major_3; //存放Major，用來顯示Major
    String Minor_1, Minor_2, Minor_3; //存放Minor，用來顯示Minor
    String room_place, select_room; //存放房間的選擇，前：隨選單控制，後：隨按鈕控制
    String search_hint_map_Strength, search_hint_map_Door;

    String beacon_name; //設備名稱
    String esp32_switch_unlock = "No"; //beacon選擇的spinner使用

    String firebase_environment_sp[];
    String firebase_device_sp[];
    Toast txt;

    String str_Estrength, strength_choice, str_Door, door_choice;
    int Button_lock = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_main);
        //「回功能選單」
        BT_FMC = findViewById(R.id.BT_FMC);
        BT_FMC.setOnClickListener(BT_FMC_L);
        //「查找」
        BT_find = findViewById(R.id.BT_find);
        BT_find.setOnClickListener(BT_find_L);
        //「開啟地圖」
        BT_Map_Open = findViewById(R.id.BT_Map_Open);
        BT_Map_Open.setOnClickListener(BT_Map_Open_L);

        search_hint = findViewById(R.id.search_hint);
        search_hint_map = findViewById(R.id.search_hint_map);

        search_hint_map.setText("\n請先選擇一個環境");

        //說明：從下載資料完後的設定檔案
        //首先找環境 -> 先搜尋esp32的啟動時間 -> 之後查訪所有設備的時間 -> 如果符合，進行方位判定，反之，忽略

        File device_txt_name = new File(getFilesDir(), "device_name.txt");   //設備名稱
        File device_txt_number = new File(getFilesDir(), "device_number.txt"); //設備數量
        File device_txt_list = new File(getFilesDir(), "device_list.txt");   //設備的數字代碼

        File environment_txt_name = new File(getFilesDir(), "environment_name.txt");   //環境名稱
        File environment_txt_number = new File(getFilesDir(), "environment_number.txt"); //環境總數
        File environment_txt_list = new File(getFilesDir(), "environment_list.txt");   //環境的數字代碼
        File environment_txt_door = new File(getFilesDir(), "environment_door.txt");   //門口
        File environment_txt_strength = new File(getFilesDir(), "environment_strength.txt");   //環境的強度代碼

        //text檔
        try {
            FileInputStream fis_Enumber = new FileInputStream(environment_txt_number);
            FileInputStream fis_Ename = new FileInputStream(environment_txt_name);
            FileInputStream fis_Elist = new FileInputStream(environment_txt_list);
            FileInputStream fis_Edoor = new FileInputStream(environment_txt_door);
            FileInputStream fis_Estrength = new FileInputStream(environment_txt_strength);

            byte[] E_number = new byte[1024];
            int len_Enumber = fis_Enumber.read(E_number);
            String str_Enumber = new String(E_number, 0, len_Enumber);

            byte[] E_name = new byte[100000];
            int len_Ename = fis_Ename.read(E_name);
            String str_Ename = new String(E_name, 0, len_Ename);
            String str_Emultiname[] = str_Ename.split(" ");

            byte[] E_list = new byte[1024];
            int len_Elist = fis_Elist.read(E_list);
            String str_Elist = new String(E_list, 0, len_Elist);
            String str_Emultilist[] = str_Elist.split(" ");

            firebase_environment_sp = new String[Integer.parseInt(str_Enumber)];

            for (int i = 0; i < Integer.parseInt(str_Enumber); i++) {

                firebase_environment_sp[i] = str_Emultilist[i] + ". " + str_Emultiname[i];

                //Toast txt = Toast.makeText(MainActivity.this,firebase_environment_sp[i]+"",Toast.LENGTH_SHORT);
                //txt.show();
            }

            byte[] E_strength = new byte[1024];
            int len_Estrength = fis_Estrength.read(E_strength);
            str_Estrength = new String(E_strength, 0, len_Estrength);

            byte[] E_door = new byte[1024];
            int len_Edoor = fis_Edoor.read(E_door);
            str_Door = new String(E_door, 0, len_Edoor); //門的動態陣列，型態為字串

            //-------------------------------------------------------

            FileInputStream fis_Dnumber = new FileInputStream(device_txt_number);
            FileInputStream fis_Dname = new FileInputStream(device_txt_name);
            FileInputStream fis_Dlist = new FileInputStream(device_txt_list);

            //設備數量的陣列
            byte[] D_number = new byte[1024];
            int len_Dnumber = fis_Dnumber.read(D_number);
            String str_Dnumber = new String(D_number, 0, len_Dnumber);

            //設備名稱的陣列
            byte[] D_name = new byte[100000];
            int len_Dname = fis_Dname.read(D_name);
            String str_Dname = new String(D_name, 0, len_Dname);
            String str_Dmultiname[] = str_Dname.split(" ");

            //設備編號的陣列
            byte[] D_list = new byte[1024];
            int len_Dlist = fis_Dlist.read(D_list);
            String str_Dlist = new String(D_list, 0, len_Dlist);
            String str_Dmultilist[] = str_Dlist.split(" ");

            firebase_device_sp = new String[Integer.parseInt(str_Dnumber)];

            for (int i = 0; i < Integer.parseInt(str_Dnumber); i++) {

                firebase_device_sp[i] = str_Dmultilist[i] + ". " + str_Dmultiname[i];

                //Toast txt = Toast.makeText(MainActivity.this,firebase_device_sp[i]+"",Toast.LENGTH_SHORT);
                //txt.show();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Toast txt = Toast.makeText(Multi_main.this, "錯誤或是安裝後第一次開啟，請完全關閉後再啟動一次", Toast.LENGTH_SHORT);
            txt.show();
        }

        beacon_spinner = (Spinner) findViewById(R.id.environment_choice); //選擇環境

        try {

            //Spinner(environment_choice)
            ArrayAdapter<String> adapternumber_environment_choice =
                    new ArrayAdapter<String>(this, R.layout.spinner_value_choice_color, firebase_environment_sp);
            adapternumber_environment_choice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            beacon_spinner.setAdapter(adapternumber_environment_choice);    //設定資料來源
            beacon_spinner.setOnItemSelectedListener(environment_choice_Listener);

        } catch (Exception e) {

        }
    }

    //選擇哪個環境
    Spinner.OnItemSelectedListener environment_choice_Listener = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int position2, long id2) {

            room_place = parent.getItemAtPosition(position2).toString(); //取得文字
            //將前面阿拉伯數字和點去掉，例如：1.大型空間 => 大型空間

            int room_place_number_dot = room_place.indexOf('.'); //第一次的「.」在第幾個位置

            room_choice = Integer.parseInt(room_place.substring(0, room_place_number_dot)) - 1; //選項1時，使他輸出為0
            room_place = room_place.substring(room_place_number_dot + 1);

            try {
                strength_choice = str_Estrength.substring(position2, position2 + 1); //環境
                door_choice = str_Door.substring(position2, position2 + 1); //門
                //Toast error = Toast.makeText(MainActivity.this,strength_choice+"",Toast.LENGTH_SHORT);
                //error.show();
            } catch (Exception e) {

            }
            ;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    //按下查找
    private View.OnClickListener BT_find_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                select_room = room_place; //房間號碼
                //Toast txt = Toast.makeText(Multi_main.this, select_room+"", Toast.LENGTH_SHORT);
                //txt.show();

                //當我選擇環境時，他們的room_choice會被選項跟著改動(0：大型空間、1：產房、2：ICU)
                int firebase_number_1 = room_choice * 3 + 1;
                int firebase_number_2 = room_choice * 3 + 2;
                int firebase_number_3 = room_choice * 3 + 3;
                int sup = room_choice + 1;

                //檢查點1
                //search_hint.setText("良好，" + firebase_number_1);

                //查找三個一組的esp32，確認他們的時間(也就是檢查運行狀態)
                //當然，先檢查有沒有這東西

                FirebaseDatabase database_get = FirebaseDatabase.getInstance();
                DatabaseReference esp32_no1 = database_get.getReference("esp32 no_" + firebase_number_1).child("time").child("time");
                DatabaseReference esp32_no2 = database_get.getReference("esp32 no_" + firebase_number_2).child("time").child("time");
                DatabaseReference esp32_no3 = database_get.getReference("esp32 no_" + firebase_number_3).child("time").child("time");
                DatabaseReference esp32_sup = database_get.getReference("esp32_sup" + sup).child("time").child("time");
                DatabaseReference esp32_no1_second = database_get.getReference("esp32 no_" + firebase_number_1).child("time").child("second");
                DatabaseReference esp32_no2_second = database_get.getReference("esp32 no_" + firebase_number_2).child("time").child("second");
                DatabaseReference esp32_no3_second = database_get.getReference("esp32 no_" + firebase_number_3).child("time").child("second");
                DatabaseReference esp32_sup_second = database_get.getReference("esp32_sup" + sup).child("time").child("second");


                //第一個
                esp32_no1.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot time1_get) {

                        try {
                            long time1 = time1_get.getValue(Integer.class);
                            Time1 = time1;

                            //沒問題就第二個
                            esp32_no2.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot time2_get) {

                                    try {
                                        long time2 = time2_get.getValue(Integer.class);
                                        Time2 = time2;

                                        //沒問題就第三個
                                        esp32_no3.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot time3_get) {

                                                try {
                                                    long time3 = time3_get.getValue(Integer.class);
                                                    Time3 = time3;

                                                    //如果第三個沒問題了
                                                    //確認有了，第二步，看esp32有沒有開
                                                    //根據esp32的運作，掃描6秒上傳一次，他會在上傳時刷新時間
                                                    //如果他沒開或怎樣的，基本上三分鐘就沒了

                                                    long time_now = System.currentTimeMillis() / 1000; //現在時間

                                                    //步驟2-1：第一個esp32
                                                    esp32_no1_second.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot second_1_get) {
                                                            try {
                                                                long second_1 = second_1_get.getValue(Integer.class);

                                                                //超時一
                                                                if ((time_now - (Time1 + second_1)) > 180) {
                                                                    search_hint.setText("code2-1：沒有該環境的即時資料，\n請先運行該環境");
                                                                    search_hint_map.setText("\n請先執行「查詢狀態」之說明，\n之後再重新查找一次");
                                                                    Button_lock = 0;
                                                                    //long test1 = time_now - (Time1 + second_1);
                                                                    //search_hint.setText(test1+"中道1");

                                                                } else {

                                                                    //步驟2-2：第二個esp32
                                                                    esp32_no2_second.addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot second_2_get) {
                                                                            try {
                                                                                long second_2 = second_2_get.getValue(Integer.class);

                                                                                //超時二
                                                                                if ((time_now - (Time2 + second_2)) > 180) {
                                                                                    search_hint.setText("code2-2：沒有該環境的即時資料，\n請先運行該環境");
                                                                                    search_hint_map.setText("\n請先執行「查詢狀態」之說明，\n之後再重新查找一次");
                                                                                    Button_lock = 0;

                                                                                } else {

                                                                                    //步驟2-3：第三個esp32
                                                                                    esp32_no3_second.addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot second_3_get) {
                                                                                            try {
                                                                                                long second_3 = second_3_get.getValue(Integer.class);

                                                                                                //超時三
                                                                                                if ((time_now - (Time3 + second_3)) > 180) {
                                                                                                    search_hint.setText("code2-3：沒有該環境的即時資料，\n請先運行該環境");
                                                                                                    search_hint_map.setText("\n請先執行「查詢狀態」之說明，\n之後再重新查找一次");
                                                                                                    Button_lock = 0;

                                                                                                } else {

                                                                                                    //步驟2-4：sup在不在?
                                                                                                    esp32_sup.addValueEventListener(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot timesup_get) {

                                                                                                            try {

                                                                                                                long timesup = timesup_get.getValue(Integer.class);
                                                                                                                Timesup = timesup;

                                                                                                                esp32_sup_second.addValueEventListener(new ValueEventListener() {
                                                                                                                    @Override
                                                                                                                    public void onDataChange(@NonNull DataSnapshot second_sup_get) {

                                                                                                                        try {

                                                                                                                            long second_sup = second_sup_get.getValue(Integer.class);

                                                                                                                            //超時sup
                                                                                                                            if ((time_now - (Timesup + second_sup)) > 180)
                                                                                                                            {
                                                                                                                                search_hint.setText("code3-2：檢查完成，有該環境的即時資料");
                                                                                                                                //long A = time_now - (Time1 + second_1);
                                                                                                                                //search_hint.setText(Time1+"檢查點");

                                                                                                                                //可以開始尋找beacon了

                                                                                                                                if (strength_choice.equals("1")) {
                                                                                                                                    search_hint_map_Strength = "強";
                                                                                                                                }
                                                                                                                                if (strength_choice.equals("2")) {
                                                                                                                                    search_hint_map_Strength = "中";
                                                                                                                                }
                                                                                                                                if (strength_choice.equals("3")) {
                                                                                                                                    search_hint_map_Strength = "弱";
                                                                                                                                }

                                                                                                                                if (door_choice.equals("1")) {
                                                                                                                                    search_hint_map_Door = "左";
                                                                                                                                }
                                                                                                                                if (door_choice.equals("2")) {
                                                                                                                                    search_hint_map_Door = "右";
                                                                                                                                }

                                                                                                                                search_hint_map.setText("\n該環境規格如下：\n藍芽訊號：" + search_hint_map_Strength + "\n門：" + search_hint_map_Door + "\n門口無設置esp32可能導致部分結果有誤差\n準備就緒，你可以開啟地圖了");
                                                                                                                                Button_lock = 1;

                                                                                                                            }else{

                                                                                                                                search_hint.setText("code3-1：檢查完成，有該環境的即時資料");
                                                                                                                                //long A = time_now - (Time1 + second_1);
                                                                                                                                //search_hint.setText(Time1+"檢查點");

                                                                                                                                //可以開始尋找beacon了

                                                                                                                                if (strength_choice.equals("1")) {
                                                                                                                                    search_hint_map_Strength = "強";
                                                                                                                                }
                                                                                                                                if (strength_choice.equals("2")) {
                                                                                                                                    search_hint_map_Strength = "中";
                                                                                                                                }
                                                                                                                                if (strength_choice.equals("3")) {
                                                                                                                                    search_hint_map_Strength = "弱";
                                                                                                                                }

                                                                                                                                if (door_choice.equals("1")) {
                                                                                                                                    search_hint_map_Door = "左";
                                                                                                                                }
                                                                                                                                if (door_choice.equals("2")) {
                                                                                                                                    search_hint_map_Door = "右";
                                                                                                                                }

                                                                                                                                search_hint_map.setText("\n該環境規格如下：\n藍芽訊號：" + search_hint_map_Strength + "\n門：" + search_hint_map_Door + "\n門口有設置esp32\n準備就緒，你可以開啟地圖了");
                                                                                                                                Button_lock = 1;
                                                                                                                            }

                                                                                                                        }catch (Exception sup_404) {

                                                                                                                            search_hint.setText("Ecode3-2：檢查完成，有該環境的即時資料");
                                                                                                                            //long A = time_now - (Time1 + second_1);
                                                                                                                            //search_hint.setText(Time1+"檢查點");

                                                                                                                            //可以開始尋找beacon了

                                                                                                                            if (strength_choice.equals("1")) {
                                                                                                                                search_hint_map_Strength = "強";
                                                                                                                            }
                                                                                                                            if (strength_choice.equals("2")) {
                                                                                                                                search_hint_map_Strength = "中";
                                                                                                                            }
                                                                                                                            if (strength_choice.equals("3")) {
                                                                                                                                search_hint_map_Strength = "弱";
                                                                                                                            }

                                                                                                                            if (door_choice.equals("1")) {
                                                                                                                                search_hint_map_Door = "左";
                                                                                                                            }
                                                                                                                            if (door_choice.equals("2")) {
                                                                                                                                search_hint_map_Door = "右";
                                                                                                                            }

                                                                                                                            search_hint_map.setText("\n該環境規格如下：\n藍芽訊號：" + search_hint_map_Strength + "\n門：" + search_hint_map_Door + "\n門口無設置esp32可能導致部分結果有誤差\n準備就緒，你可以開啟地圖了");
                                                                                                                            Button_lock = 1;
                                                                                                                        }
                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                                                    }
                                                                                                                });

                                                                                                            } catch (Exception sup_404) {

                                                                                                                search_hint.setText("Ecode3-1：檢查完成，有該環境的即時資料");
                                                                                                                //long A = time_now - (Time1 + second_1);
                                                                                                                //search_hint.setText(Time1+"檢查點");

                                                                                                                //可以開始尋找beacon了

                                                                                                                if (strength_choice.equals("1")) {
                                                                                                                    search_hint_map_Strength = "強";
                                                                                                                }
                                                                                                                if (strength_choice.equals("2")) {
                                                                                                                    search_hint_map_Strength = "中";
                                                                                                                }
                                                                                                                if (strength_choice.equals("3")) {
                                                                                                                    search_hint_map_Strength = "弱";
                                                                                                                }

                                                                                                                if (door_choice.equals("1")) {
                                                                                                                    search_hint_map_Door = "左";
                                                                                                                }
                                                                                                                if (door_choice.equals("2")) {
                                                                                                                    search_hint_map_Door = "右";
                                                                                                                }

                                                                                                                search_hint_map.setText("\n該環境規格如下：\n藍芽訊號：" + search_hint_map_Strength + "\n門：" + search_hint_map_Door + "\n門口無設置esp32可能導致部分結果有誤差\n準備就緒，你可以開啟地圖了");
                                                                                                                Button_lock = 1;

                                                                                                            }

                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                                        }
                                                                                                    });


                                                                                                }

                                                                                            } catch (Exception second_3_404) {
                                                                                                //找不到第三個的second數值
                                                                                                search_hint.setText("Ecode2-3：沒有該環境的即時資料，\n請先運行該環境");
                                                                                                search_hint_map.setText("\n請先執行「查詢狀態」之說明，\n之後再重新查找一次");
                                                                                                Button_lock = 0;
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });
                                                                                }//二和三的else結束

                                                                            } catch (Exception second_2_404) {
                                                                                //找不到第二個的second數值
                                                                                search_hint.setText("Ecode2-2：沒有該環境的即時資料，\n請先運行該環境");
                                                                                search_hint_map.setText("\n請先執行「查詢狀態」之說明，\n之後再重新查找一次");
                                                                                Button_lock = 0;
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });
                                                                }//一和二的else結束


                                                            } catch (Exception second_1_404) {
                                                                //找不到第一個的second數值
                                                                search_hint.setText("Ecode2-1：沒有該環境的即時資料，\n請先運行該環境");
                                                                search_hint_map.setText("\n請先執行「查詢狀態」之說明，\n之後再重新查找一次");
                                                                Button_lock = 0;
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });


                                                } catch (Exception No_esp32_found_3) {

                                                    //第三個失敗
                                                    search_hint.setText("code1-3：沒有任何關於該環境的資料，\n請確認是否在該處架設過環境\n若無，請先架設環境");
                                                    search_hint_map.setText("\n請先執行「查詢狀態」之說明，\n之後再重新查找一次");
                                                    Button_lock = 0;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });

                                    } catch (Exception No_esp32_found_2) {

                                        //第二個失敗
                                        search_hint.setText("code1-2：沒有任何關於該環境的資料，\n請確認是否在該處架設過環境\n若無，請先架設環境");
                                        search_hint_map.setText("\n請先執行「查詢狀態」之說明，\n之後再重新查找一次");
                                        Button_lock = 0;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                        } catch (Exception No_esp32_found_1) {

                            //第一個失敗
                            search_hint.setText("code1-1：沒有任何關於該環境的資料，\n請確認是否在該處架設過環境\n若無，請先架設環境");
                            search_hint_map.setText("\n請先執行「查詢狀態」之說明，\n之後再重新查找一次");
                            Button_lock = 0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            } catch (Exception e) {

            }
        }
    };

    private View.OnClickListener BT_FMC_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent();
            intent.setClass(Multi_main.this, find_mode_choose.class);
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener BT_Map_Open_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (Button_lock == 1) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                //你選擇的房間
                int send_room = room_choice;
                bundle.putInt("room_choice", send_room);

                //打包，沒寫會出錯
                intent.putExtras(bundle);

                //跳轉到下一頁，處理資訊
                intent.setClass(Multi_main.this, Multi_deal_with.class);
                startActivity(intent);
                finish();
            }

            if (Button_lock == 0) {
                Toast txt = Toast.makeText(Multi_main.this, "使用者別急，請一步一步來", Toast.LENGTH_SHORT);
                txt.show();
            }
        }
    };
}