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

    private Button BT_FMC, BT_find;
    private TextView search_hint;

    int room_choice, beacon_number_choice;
    int number_decided; //1.用來丟入下一頁使用 2.防呆
    int rssi_1, rssi_2, rssi_3, rssi_sup; //存放rssi
    int To_adminster_page_TapCount = 0; //如同成為開發者一般

    String sw_number; //放esp32切換
    String String_rssi_1, String_rssi_2, String_rssi_3, String_rssi_sup; //存放rssi，用於顯示在esp32切換
    String String_distance_1, String_distance_2, String_distance_3; //存放距離，用於顯示儀器測距
    String String_displaytime_1, String_displaytime_2, String_displaytime_3; //存放時間，用來顯示時間
    Long unixtime_check1, unixtime_check2, unixtime_check3; //存放unix時間，用來判定時間
    String Major_1, Major_2, Major_3; //存放Major，用來顯示Major
    String Minor_1, Minor_2, Minor_3; //存放Minor，用來顯示Minor
    String room_place, select_room; //存放房間的選擇，前：隨選單控制，後：隨按鈕控制

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

        BT_FMC = findViewById(R.id.BT_FMC);
        BT_FMC.setOnClickListener(BT_FMC_L);

        BT_find = findViewById(R.id.BT_find);
        BT_find.setOnClickListener(BT_find_L);

        search_hint = findViewById(R.id.search_hint);

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
            str_Door = new String(E_door, 0, len_Edoor);

            //-------------------------------------------------------

            FileInputStream fis_Dnumber = new FileInputStream(device_txt_number);
            FileInputStream fis_Dname = new FileInputStream(device_txt_name);
            FileInputStream fis_Dlist = new FileInputStream(device_txt_list);

            byte[] D_number = new byte[1024];
            int len_Dnumber = fis_Dnumber.read(D_number);
            String str_Dnumber = new String(D_number, 0, len_Dnumber);

            byte[] D_name = new byte[100000];
            int len_Dname = fis_Dname.read(D_name);
            String str_Dname = new String(D_name, 0, len_Dname);
            String str_Dmultiname[] = str_Dname.split(" ");

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

                //當我選擇環境時，他們的room_choice會被選項跟著改動(0：大型空間、1：產房、2：ICU)
                int firebase_number_1 = room_choice * 3 + 1;
                int firebase_number_2 = room_choice * 3 + 2;
                int firebase_number_3 = room_choice * 3 + 3;

                //檢查點1
                //search_hint.setText("良好，" + firebase_number_1);

                //查找三個一組的esp32，確認他們的時間(也就是檢查運行狀態)
                //當然，先檢查有沒有這東西

                FirebaseDatabase database_get = FirebaseDatabase.getInstance();
                DatabaseReference esp32_no1 = database_get.getReference("esp32 no_" + firebase_number_1).child("time").child("time");
                DatabaseReference esp32_no2 = database_get.getReference("esp32 no_" + firebase_number_2).child("time").child("time");
                DatabaseReference esp32_no3 = database_get.getReference("esp32 no_" + firebase_number_3).child("time").child("time");

                //第一個
                esp32_no1.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot time1_get) {

                        try {
                            int time1 = time1_get.getValue(Integer.class);

                            //沒問題就第二個
                            esp32_no2.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot time2_get) {

                                    try {
                                        int time2 = time2_get.getValue(Integer.class);

                                    } catch (Exception No_esp32_found) {
                                        //第二個失敗
                                        search_hint.setText("沒有任何關於該環境的資料，請確認是否在該處架設過環境\n若無，請先架設環境");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } catch (Exception No_esp32_found) {
                            //第一個失敗
                            search_hint.setText("沒有任何關於該環境的資料，請確認是否在該處架設過環境\n若無，請先架設環境");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            } catch (Exception e) {

            }
            //首先，先看esp32有沒有開
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
}