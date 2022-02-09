package com.example.iot_ward_app_v3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class Multi_deal_with extends AppCompatActivity {

    //畫板參考https://lowren.pixnet.net/blog/post/92267045
    int rule,door,select_number,room_choice;
    String select_room,beacon_name;

    TextView Messageeeeeeeeee;

    //你必須在這裡創立全域變數，這可能是最簡單的bundle方法，不然程式會誤認為 0 或 null
    Long check1,check2,check3;
    int rssi_1,rssi_2,rssi_3,rssi_sup;
    String description;
    String str_Estrength, strength_choice, str_Door, door_choice;

    String firebase_environment_sp[];
    String firebase_device_sp[];
    String firebase_deal_with_number_1[];
    //String firebase_deal_with_number_1[] = new String[10000];

    //寄放要處理的編號，使用動態
    ArrayList<String> deal_with_number_1 = new ArrayList<String>();
    ArrayList<String> deal_with_number_2 = new ArrayList<String>();
    ArrayList<String> deal_with_number_3 = new ArrayList<String>();
    ArrayList<String> deal_with_number_ALL = new ArrayList<String>();

    Button bt_back;
    private TextView rule_keep,door_keep,remind_text,remind_device_L,remind_device_R,remind_room_L,remind_room_R,dir;
    private RadioButton left_door,right_door;
    private RadioGroup select_door;
    private Button pre_display,display;
    private ImageView pre_place;

    Toast txt;

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
            //Messageeeeeeeeee.setText(room_choice+"");

            //說明：從下載資料完後的設定檔案
            //首先找環境 -> 先搜尋esp32的啟動時間 -> 之後查訪所有設備的時間 -> 如果符合，進行方位判定，反之，忽略

            File device_txt_name = new File(getFilesDir(), "device_name.txt");   //設備名稱
            File device_txt_number = new File(getFilesDir(), "device_number.txt"); //設備數量
            File device_txt_list = new File(getFilesDir(), "device_list.txt");   //設備的數字代碼

            File environment_txt_name = new File(getFilesDir(), "environment_name.txt");   //環境名稱
            File environment_txt_number = new File(getFilesDir(), "environment_number.txt"); //環境總數
            File environment_txt_list = new File(getFilesDir(), "environment_list.txt");   //環境的數字代碼
            File environment_txt_door = new File(getFilesDir(), "environment_door.txt");   //門口
            File environment_txt_strength = new File(getFilesDir(), "environment_strength.txt");   //

            FileInputStream fis_Enumber = new FileInputStream(environment_txt_number);
            FileInputStream fis_Ename = new FileInputStream(environment_txt_name);
            FileInputStream fis_Elist = new FileInputStream(environment_txt_list);
            FileInputStream fis_Edoor = new FileInputStream(environment_txt_door);
            FileInputStream fis_Estrength = new FileInputStream(environment_txt_strength);

            byte[] E_number = new byte[1024];
            int len_Enumber = fis_Enumber.read(E_number);
            String str_Enumber = new String(E_number, 0, len_Enumber);

            String firebase_deal_with_number_1[] = new String[Integer.parseInt(str_Enumber)];

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



            int firebase_number_1 = room_choice * 3 + 1;
            int firebase_number_2 = room_choice * 3 + 2;
            int firebase_number_3 = room_choice * 3 + 3;

            long time_now = System.currentTimeMillis() / 1000; //現在時間

            for (int i = 0; i < 4; i++) { //實際用：str_Dmultilist.length  測試用：4

                //抓編號，因為有三個esp32，所以一圈要做三次
                //Toast txt = Toast.makeText(Multi_deal_with.this,str_Dmultilist[i]+"",Toast.LENGTH_SHORT);
                //txt.show();

                FirebaseDatabase database_sw = FirebaseDatabase.getInstance();

                DatabaseReference beacon_time_check_1 = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(str_Dmultilist[i])).child("epochTime_temp");
                DatabaseReference beacon_time_check_2 = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(str_Dmultilist[i])).child("epochTime_temp");
                DatabaseReference beacon_time_check_3 = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(str_Dmultilist[i])).child("epochTime_temp");
                DatabaseReference beacon_time_check_second_1 = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(str_Dmultilist[i])).child("time");
                DatabaseReference beacon_time_check_second_2 = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(str_Dmultilist[i])).child("time");
                DatabaseReference beacon_time_check_second_3 = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(str_Dmultilist[i])).child("time");

                DatabaseReference beacon_RSSI_1 = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(str_Dmultilist[i])).child("time");
                DatabaseReference beacon_RSSI_2 = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(str_Dmultilist[i])).child("time");
                DatabaseReference beacon_RSSI_3 = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(str_Dmultilist[i])).child("time");


                int part_i = i;

                //第一次
                beacon_time_check_1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot B_time1) {
                        long time1 = B_time1.getValue(Integer.class);

                        beacon_time_check_second_1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot B_second1) {

                                long second_1 = B_second1.getValue(Integer.class);

                                Toast txt = Toast.makeText(Multi_deal_with.this, time1+"", Toast.LENGTH_SHORT);
                                //txt.show();
                                //txt = Toast.makeText(Multi_deal_with.this, second_1+"", Toast.LENGTH_SHORT);
                                //txt.show();

                                //第一個esp32偵測他的時間差小於120秒，開始第二個
                                if((time_now -(time1 + second_1)) < 120)
                                {
                                    deal_with_number_1.add(str_Dmultilist[part_i]);

                                    txt = Toast.makeText(Multi_deal_with.this, deal_with_number_1+"", Toast.LENGTH_SHORT);
                                    //txt.show();

                                    //第二次
                                    beacon_time_check_2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot B_time2) {
                                            long time2 = B_time2.getValue(Integer.class);

                                            beacon_time_check_second_2.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot B_second2) {

                                                    long second_2 = B_second2.getValue(Integer.class);

                                                    Toast txt = Toast.makeText(Multi_deal_with.this, time2+"", Toast.LENGTH_SHORT);
                                                    //txt.show();
                                                    //txt = Toast.makeText(Multi_deal_with.this, second_2+"", Toast.LENGTH_SHORT);
                                                    //txt.show();

                                                    //第二個esp32偵測他的時間差小於120秒，開始第三個
                                                    if((time_now -(time2 + second_2)) < 120)
                                                    {
                                                        deal_with_number_2.add(str_Dmultilist[part_i]);

                                                        txt = Toast.makeText(Multi_deal_with.this, deal_with_number_2+"", Toast.LENGTH_SHORT);
                                                        //txt.show();

                                                        //第三次
                                                        beacon_time_check_3.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot B_time3) {
                                                                long time3 = B_time3.getValue(Integer.class);

                                                                beacon_time_check_second_3.addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot B_second3) {

                                                                        long second_3 = B_second3.getValue(Integer.class);

                                                                        Toast txt = Toast.makeText(Multi_deal_with.this, time3+"", Toast.LENGTH_SHORT);
                                                                        //txt.show();
                                                                        //txt = Toast.makeText(Multi_deal_with.this, second_3+"", Toast.LENGTH_SHORT);
                                                                        //txt.show();

                                                                        //第三個esp32偵測他的時間差小於120秒，開始判斷
                                                                        if((time_now -(time3 + second_3)) < 120)
                                                                        {
                                                                            deal_with_number_3.add(str_Dmultilist[part_i]);

                                                                            txt = Toast.makeText(Multi_deal_with.this, deal_with_number_3+"", Toast.LENGTH_SHORT);
                                                                            txt.show();

                                                                            //RSSI判定(2/10)
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            /*
            txt = Toast.makeText(Multi_deal_with.this, firebase_deal_with_number_1+"", Toast.LENGTH_SHORT);
            txt.show();
            txt = Toast.makeText(Multi_deal_with.this, deal_with_number_2+"", Toast.LENGTH_SHORT);
            txt.show();
            txt = Toast.makeText(Multi_deal_with.this, deal_with_number_3+"", Toast.LENGTH_SHORT);
            txt.show();

            //編號的初選結束，再來是統整(防呆)
            //動態陣列使用size，非length
            for (int i = 0; i < deal_with_number_1.size(); i++) {

                for (int j = 0; j < deal_with_number_2.size(); j++) {

                    if (deal_with_number_1.get(i).equals(deal_with_number_2.get(j)))
                    {
                        deal_with_number_ALL.add(deal_with_number_1.get(i));
                        txt = Toast.makeText(Multi_deal_with.this, deal_with_number_ALL+"", Toast.LENGTH_SHORT);
                        txt.show();
                    }
                }
            }

             */


        }catch (Exception e) {

            Toast txt = Toast.makeText(Multi_deal_with.this, ".....", Toast.LENGTH_SHORT);
            txt.show();
        }

    }
}