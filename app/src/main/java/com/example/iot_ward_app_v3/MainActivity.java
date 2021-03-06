package com.example.iot_ward_app_v3;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//https://you2be.pixnet.net/blog/post/41235995 -> imageview_onclick

public class MainActivity extends AppCompatActivity {

    private TextView tv_ei,conclude,detail;
    private Spinner  sp_esp32_choice,beacon_spinner,beacon_id_num_spinner;
    private Button btMap,btStatus,esp32_switch,find_major;
    private ImageView To_adminster_page;

    private Button BT_FMC;//回選單

    int room_choice,beacon_number_choice;
    int number_decided; //1.用來丟入下一頁使用 2.防呆
    int rssi_1,rssi_2,rssi_3,rssi_sup; //存放rssi
    int To_adminster_page_TapCount = 0; //如同成為開發者一般

    String sw_number; //放esp32切換
    String String_rssi_1,String_rssi_2,String_rssi_3,String_rssi_sup; //存放rssi，用於顯示在esp32切換
    String String_distance_1,String_distance_2,String_distance_3; //存放距離，用於顯示儀器測距
    String String_displaytime_1 , String_displaytime_2 , String_displaytime_3; //存放時間，用來顯示時間
    Long unixtime_check1,unixtime_check2,unixtime_check3; //存放unix時間，用來判定時間
    String Major_1,Major_2,Major_3; //存放Major，用來顯示Major
    String Minor_1,Minor_2,Minor_3; //存放Minor，用來顯示Minor
    String room_place,select_room; //存放房間的選擇，前：隨選單控制，後：隨按鈕控制

    String beacon_name; //設備名稱
    String esp32_switch_unlock = "No"; //beacon選擇的spinner使用

    //下拉式選單
    String[] esp32_num = new String[]{ "1.門前牆角","2.斜對牆角","3.門平行牆角"}; //esp32切換

    String[] environment_choice = new String[]{ "1.大型空間","2.樂得兒產房","3.ICU" }; //環境選擇

    String[] beacon_id_spinner_choice = new String[]{ //設備編號
            "1.暫無使用之設備",
            "2.高層次胎兒監視器(FM20)","3.雙胞胎胎兒監視器(FC1400)", "4.嬰兒推車","5.嬰兒處理台(YD-IC-SCC)",
            "6.暫無使用之設備","7.暫無使用之設備","8.點滴架(有幫浦點滴輸液)" ,"9.暫無使用之設備","10.暫無使用之設備",
            "11.E化行動護理工作車","12.E化行動護理工作車(有條碼印表機)","13.急救車","14.生理監視器(PHILIPS)","15.生理監視器(M9000A)",
            "16.點滴架","17.調試用編號，僅供測試使用"
    };

    String firebase_environment_sp[];
    String firebase_device_real_sp[];
    String firebase_device_sp[];

    Toast txt;

    String str_Estrength,strength_choice,str_Door,door_choice;
    int Button_lock = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            int len_Enumber = fis_Enumber.read(E_number );
            String str_Enumber = new String(E_number , 0,len_Enumber);

            byte[] E_name = new byte[100000];
            int len_Ename = fis_Ename.read(E_name);
            String str_Ename = new String(E_name , 0, len_Ename);
            String str_Emultiname [] = str_Ename.split(" ");

            byte[] E_list = new byte[1024];
            int len_Elist = fis_Elist.read(E_list);
            String str_Elist = new String(E_list , 0, len_Elist);
            String str_Emultilist [] = str_Elist.split(" ");

            firebase_environment_sp = new String[Integer.parseInt(str_Enumber)];

            for (int i = 0; i < Integer.parseInt(str_Enumber); i++) {

                firebase_environment_sp[i] = str_Emultilist[i]+". "+str_Emultiname [i];

                //Toast txt = Toast.makeText(MainActivity.this,firebase_environment_sp[i]+"",Toast.LENGTH_SHORT);
                //txt.show();
            }

            byte[] E_strength = new byte[1024];
            int len_Estrength = fis_Estrength.read(E_strength);
            str_Estrength = new String(E_strength , 0, len_Estrength);

            byte[] E_door = new byte[1024];
            int len_Edoor = fis_Edoor.read(E_door);
            str_Door = new String(E_door , 0, len_Edoor); //門的動態陣列，型態為字串

            //-------------------------------------------------------

            FileInputStream fis_Dnumber = new FileInputStream(device_txt_number);
            FileInputStream fis_Dname = new FileInputStream(device_txt_name);
            FileInputStream fis_Dlist = new FileInputStream(device_txt_list);

            byte[] D_number = new byte[1024];
            int len_Dnumber = fis_Dnumber.read(D_number);
            String str_Dnumber = new String(D_number , 0,len_Dnumber);

            byte[] D_name = new byte[100000];
            int len_Dname = fis_Dname.read(D_name);
            String str_Dname = new String(D_name , 0, len_Dname);
            String str_Dmultiname [] = str_Dname.split(" ");

            byte[] D_list = new byte[1024];
            int len_Dlist = fis_Dlist.read(D_list);
            String str_Dlist = new String(D_list , 0, len_Dlist);
            String str_Dmultilist [] = str_Dlist.split(" ");

            firebase_device_sp = new String[Integer.parseInt(str_Dnumber)];

            for (int i = 0; i < Integer.parseInt(str_Dnumber); i++) {

                firebase_device_sp[i] = str_Dmultilist[i]+". "+str_Dmultiname [i];

                //Toast txt = Toast.makeText(MainActivity.this,firebase_device_sp[i]+"",Toast.LENGTH_SHORT);
                //txt.show();
            }

            //把 No.100000 號隱藏
            firebase_device_real_sp = new String[Integer.parseInt(str_Dnumber)-1];

            for(int i = 0; i < firebase_device_real_sp.length ; i++)
            {

                firebase_device_real_sp[i] = firebase_device_sp[i]+"";
            }


        } catch (Exception e) {
            e.printStackTrace();
            Toast txt = Toast.makeText(MainActivity.this,"錯誤或是安裝後第一次開啟，請完全關閉後再啟動一次",Toast.LENGTH_SHORT);
            txt.show();
        }

        //圖片的imageview_onclick，沒錯! imageview可以onclick
        To_adminster_page = (ImageView)findViewById(R.id.To_adminster_page);
        To_adminster_page.setOnClickListener(To_adminster_page_L);

        //下拉式選單
        sp_esp32_choice = (Spinner)findViewById(R.id.sp_esp32_choice); //選擇該esp32的哪一個
        beacon_spinner  = (Spinner)findViewById(R.id.environment_choice); //選擇環境
        beacon_id_num_spinner = (Spinner)findViewById(R.id.beacon_id_spinner); //用選擇編號替代寫入編號

        //細節(外觀文字)
        detail = (TextView)findViewById(R.id.detail);

        //顯示要尋找的beacon的uuid值
        tv_ei = (TextView)findViewById(R.id.search_hint);

        //文字的位置判定
        conclude = (TextView)findViewById(R.id.conclude);

        //spinner相關，你需要一個xml來調整大小(把android拿掉
        //Spinner(sp_esp32_choice)
        ArrayAdapter<String> adapternumber2 =
                new ArrayAdapter<String>(this,R.layout.spinner_value_choice_color,esp32_num);
        adapternumber2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_esp32_choice.setAdapter(adapternumber2);    //設定資料來源
        sp_esp32_choice.setOnItemSelectedListener(sp_esp32_choice_Listener);

        try {

            //Spinner(environment_choice)
            ArrayAdapter<String> adapternumber_environment_choice =
                    new ArrayAdapter<String>(this,R.layout.spinner_value_choice_color,firebase_environment_sp);
            adapternumber_environment_choice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            beacon_spinner.setAdapter(adapternumber_environment_choice);    //設定資料來源
            beacon_spinner.setOnItemSelectedListener(environment_choice_Listener);

            //Spinner(beacon_id_spinner)
            ArrayAdapter<String> adapternumber_beacon_id_spinner_choice =
                    new ArrayAdapter<String>(this ,R.layout.spinner_value_choice_color,firebase_device_real_sp);
            adapternumber_beacon_id_spinner_choice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            beacon_id_num_spinner.setAdapter(adapternumber_beacon_id_spinner_choice);    //設定資料來源
            beacon_id_num_spinner.setOnItemSelectedListener(beacon_id_spinner_choice_Listener);

        }catch (Exception e)
        {

        }

        //button
        btStatus = (Button)findViewById(R.id.btStatus);
        btStatus.setOnClickListener(btStatusListener);
        btMap = (Button)findViewById(R.id.BT_Map_Open);
        btMap.setOnClickListener(btMapListener);
        esp32_switch = (Button)findViewById(R.id.esp32_switch);
        esp32_switch.setOnClickListener(esp32_switchListener);
        find_major = (Button)findViewById(R.id.BT_find);
        find_major.setOnClickListener(find_major_L);

        BT_FMC = findViewById(R.id.BT_FMC);
        BT_FMC.setOnClickListener(BT_FMC_L);
        }

        //按下「查找按鈕」
        private  View.OnClickListener find_major_L = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database_get = FirebaseDatabase.getInstance();
                try {
                    int select_major = beacon_number_choice;
                    number_decided = beacon_number_choice;
                    select_room = room_place; //房間號碼

                    //Toast error = Toast.makeText(MainActivity.this,room_choice+"",Toast.LENGTH_SHORT);
                    //error.show();

                    //當我選擇環境時，他們的room_choice會被選項跟著改動(0：大型空間、1：產房、2：ICU)
                    int firebase_number_1 = room_choice*3 + 1;
                    int firebase_number_2 = room_choice*3 + 2;
                    int firebase_number_3 = room_choice*3 + 3;

                    if( (select_major > 0) & (select_major < 50) ){
                        //搜尋有沒有該major，沒有就換找下一個
                            DatabaseReference major1 = database_get.getReference("esp32 no_" + firebase_number_1)
                                    .child(String.valueOf(select_major)).child("Major");
                            major1.addValueEventListener(new ValueEventListener() {
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Integer major = dataSnapshot.getValue(Integer.class);
                                    if(major == null){

                                        try { throw new Exception(); }
                                        catch (Exception major_esp1_notfound) {

                                            DatabaseReference major2 = database_get.getReference("esp32 no_" + firebase_number_2)
                                                    .child(String.valueOf(select_major)).child("Major");
                                            major2.addValueEventListener(new ValueEventListener() {
                                                public void onDataChange(DataSnapshot dataSnapshot){
                                                    Integer major = dataSnapshot.getValue(Integer.class);
                                                    if(major == null){

                                                        try { throw new Exception(); }
                                                        catch (Exception major_esp2_notfound) {

                                                            DatabaseReference major3 = database_get.getReference("esp32 no_" + firebase_number_3)
                                                                    .child(String.valueOf(select_major)).child("Major");
                                                            major3.addValueEventListener(new ValueEventListener() {
                                                                public void onDataChange(DataSnapshot dataSnapshot){
                                                                    Integer major = dataSnapshot.getValue(Integer.class);
                                                                    if(major == null){
                                                                        //tv_ei.setText(firebase_number_1+"號、"+firebase_number_2+"號、"+firebase_number_3+"號、"+"至少有一個esp32有 "+select_major+"號的資料，請接續後續步驟");
                                                                        tv_ei.setText("三個esp32裝置都沒有上傳過 "+ select_major +"號 "+ beacon_name + " 的資料，請換編號查詢");
                                                                        detail.setText("請重新選擇要查詢的設備(beacon)編號");
                                                                        Button_lock = 0;
                                                                    }
                                                                    else{
                                                                        //tv_ei.setText(firebase_number_1+"號、"+firebase_number_2+"號、"+firebase_number_3+"號、"+"至少有一個esp32有 "+select_major+"號的資料，請接續後續步驟");
                                                                        tv_ei.setText("至少有一個esp32裝置有上傳過 "+select_major+"號 " + beacon_name + " 的資料，請接續後續步驟");
                                                                        detail.setText("查找完畢");
                                                                        Button_lock = 1;
                                                                    }
                                                            }
                                                                public void onCancelled(DatabaseError error) { }});}
                                                        }else{
                                                        //tv_ei.setText(firebase_number_1+"號、"+firebase_number_2+"號、"+firebase_number_3+"號、"+"至少有一個esp32有 "+select_major+"號的資料，請接續後續步驟");
                                                        tv_ei.setText("至少有一個esp32裝置有上傳過 "+select_major+"號 " + beacon_name + " 的資料，請接續後續步驟");
                                                        detail.setText("查找完畢");
                                                        Button_lock = 1;
                                                    }}
                                                public void onCancelled(DatabaseError error) { }});
                                                }}else{
                                        tv_ei.setText("至少有一個esp32裝置有上傳過 "+select_major+"號 " + beacon_name + " 的資料，請接續後續步驟");
                                        detail.setText("查找完畢");
                                        Button_lock = 1;
                                    }}
                                public void onCancelled(DatabaseError error) { }});

                        FirebaseDatabase database_sw = FirebaseDatabase.getInstance();

                        int sup = room_choice +1;
                        DatabaseReference esp32_sup_setcheck = database_sw.getReference("esp32_sup" + sup).child("time").child("time");
                        DatabaseReference esp32_sup_RSSIcheck = database_sw.getReference("esp32_sup" + sup).child(String.valueOf(select_major)).child("RSSI");
                        DatabaseReference esp32_sup_timecheck = database_sw.getReference("esp32_sup" + sup).child(String.valueOf(select_major)).child("time");
                        esp32_sup_setcheck.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                try {
                                    long time = dataSnapshot1.getValue(Integer.class);

                                    long time_now = System.currentTimeMillis() / 1000;

                                    if((time_now - time) > 600)
                                    {
                                        esp32_sup_timecheck.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot3) {
                                                try {
                                                    long time_ing = dataSnapshot3.getValue(Integer.class);

                                                    if ((time_now - (time + time_ing)) > 600)
                                                    {
                                                        rssi_sup = -150;
                                                        //Toast txt = Toast.makeText(MainActivity.this,"門口沒啟用或未找到此設備",Toast.LENGTH_SHORT);
                                                        //txt.show();
                                                    }else{

                                                        esp32_sup_RSSIcheck.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                                                try {
                                                                    Integer rssisup = dataSnapshot2.getValue(Integer.class);

                                                                    rssi_sup = rssisup; //把這個區域(無_) 丟給 全域(有_)
                                                                    String_rssi_sup = rssisup.toString();

                                                                } catch (Exception device_not_found) {

                                                                    rssi_sup = -150;
                                                                    //Toast txt = Toast.makeText(MainActivity.this,"門口沒找到此設備",Toast.LENGTH_SHORT);
                                                                    //txt.show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) { }
                                                        });
                                                    }

                                                } catch (Exception device_not_found) {

                                                    rssi_sup = -150;
                                                    //Toast txt = Toast.makeText(MainActivity.this,"門口沒啟用或未找到此設備",Toast.LENGTH_SHORT);
                                                    //txt.show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) { }
                                        });

                                        //Toast txt = Toast.makeText(MainActivity.this,"注意，未在門口設置或啟動esp32，可能使部分結果準確度下降",Toast.LENGTH_SHORT);
                                        //txt.show();

                                        //rssi_sup = -150;
                                    }else{

                                        esp32_sup_RSSIcheck.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                                try {
                                                    Integer rssisup = dataSnapshot2.getValue(Integer.class);

                                                    rssi_sup = rssisup; //把這個區域(無_) 丟給 全域(有_)
                                                    String_rssi_sup = rssisup.toString();

                                                } catch (Exception device_not_found) {

                                                    rssi_sup = -150;
                                                    //Toast txt = Toast.makeText(MainActivity.this,"門口沒找到此設備",Toast.LENGTH_SHORT);
                                                    //txt.show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) { }
                                        });

                                    }
                                } catch (Exception time_not_found) {
                                    //Toast txt = Toast.makeText(MainActivity.this,"注意，未在門口設置或啟動esp32，可能使部分結果準確度下降",Toast.LENGTH_SHORT);
                                    //txt.show();

                                    rssi_sup = -150;
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                        DatabaseReference esp32_no1_RSSI = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(select_major)).child("RSSI");
                        DatabaseReference esp32_no2_RSSI = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(select_major)).child("RSSI");
                        DatabaseReference esp32_no3_RSSI = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(select_major)).child("RSSI");

                        esp32_no1_RSSI.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer rssi1 = dataSnapshot.getValue(Integer.class);

                                    rssi_1 = rssi1; //把這個區域(無_) 丟給 全域(有_)
                                    String_rssi_1 = rssi1.toString();

                                    double A = 0, n = 0;

                                    if(Integer.parseInt(strength_choice) == 1) { A = 59.00; n = 3.00; } //大型
                                    if(Integer.parseInt(strength_choice) == 2) { A = 65.00; n = 4.00; } //產房
                                    if(Integer.parseInt(strength_choice) == 3) { A = 70.00; n = 3.60; } //ICU

                                    double M_1 = pow(10, ((abs(rssi1) - A) / (10 * n)));

                                    NumberFormat nf = NumberFormat.getInstance();
                                    nf.setMaximumFractionDigits(2);						// 若小數點超過四位，則第五位~四捨五入
                                    nf.setMinimumFractionDigits(1);
                                    String_distance_1 = nf.format(M_1);

                                } catch (Exception RSSI_not_found) {
                                    String_rssi_1 = "資料錯誤";
                                    String_distance_1 = "格式不符";
                                    rssi_1 = -150;
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                        esp32_no2_RSSI.addValueEventListener(new ValueEventListener() {
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer rssi2 = dataSnapshot.getValue(Integer.class);

                                    rssi_2 = rssi2; //把這個區域(無_) 丟給 全域(有_)
                                    String_rssi_2 = rssi2.toString();

                                    double A = 0, n = 0;

                                    if(Integer.parseInt(strength_choice) == 1) { A = 59.00; n = 3.00; } //大型
                                    if(Integer.parseInt(strength_choice) == 2) { A = 65.00; n = 4.00; } //產房
                                    if(Integer.parseInt(strength_choice) == 3) { A = 70.00; n = 3.60; } //ICU

                                    double M_2 = pow(10, ((abs(rssi2) - A) / (10 * n)));

                                    NumberFormat nf = NumberFormat.getInstance();
                                    nf.setMaximumFractionDigits(2);						// 若小數點超過四位，則第五位~四捨五入
                                    nf.setMinimumFractionDigits(1);
                                    String_distance_2 = nf.format(M_2);

                                } catch (Exception RSSI_not_found) {
                                    String_rssi_2 = "資料錯誤";
                                    String_distance_2 = "格式不符";
                                    rssi_2 = -150;
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                        esp32_no3_RSSI.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer rssi3 = dataSnapshot.getValue(Integer.class);

                                    rssi_3 = rssi3; //把這個區域(無_) 丟給 全域(有_)
                                    String_rssi_3 = rssi3.toString();

                                    double A = 0, n = 0;

                                    if(Integer.parseInt(strength_choice) == 1) { A = 59.00; n = 3.00; } //大型
                                    if(Integer.parseInt(strength_choice) == 2) { A = 65.00; n = 4.00; } //產房
                                    if(Integer.parseInt(strength_choice) == 3) { A = 70.00; n = 3.60; } //ICU

                                    double M_3 = pow(10, ((abs(rssi3) - A) / (10 * n)));

                                    NumberFormat nf = NumberFormat.getInstance();
                                    nf.setMaximumFractionDigits(2);						// 若小數點超過四位，則第五位~四捨五入
                                    nf.setMinimumFractionDigits(1);
                                    String_distance_3 = nf.format(M_3);

                                } catch (Exception RSSI_not_found) {
                                    String_rssi_3 = "資料錯誤";
                                    String_distance_3 = "格式不符";
                                    rssi_3 = -150;
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }});

                        DatabaseReference esp32_no1_unix = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(select_major)).child("epochTime_temp");
                        DatabaseReference esp32_no2_unix = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(select_major)).child("epochTime_temp");
                        DatabaseReference esp32_no3_unix = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(select_major)).child("epochTime_temp");
                        DatabaseReference esp32_no1_unix_fix = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(select_major)).child("time");
                        DatabaseReference esp32_no2_unix_fix = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(select_major)).child("time");
                        DatabaseReference esp32_no3_unix_fix = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(select_major)).child("time");

                        esp32_no1_unix.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    int time = dataSnapshot.getValue(Integer.class); //網路時間
                                    esp32_no1_unix_fix.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) { //注意DataSnapshot的名字
                                            long time_switch = Long.valueOf(time) * 1000;// its need to be in milisecond

                                            long fix = snapshot.getValue(Integer.class); //校準時間
                                            long fix_switch = Long.valueOf(fix) * 1000;
                                            long fix_add = Long.valueOf(time_switch + fix_switch);
                                            Date day_month_year = new Date(fix_add);
                                            String format = new SimpleDateFormat("yyyy/MM/dd ahh:mm").format(day_month_year);

                                            String_displaytime_1 = "\nesp之1偵測時間: " + format;
                                            unixtime_check1 = fix + time;
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError error) {}});
                                } catch (Exception time_not_found) {
                                    String_displaytime_1 = "\nesp之1時間找不到，請再試一次";
                                    unixtime_check1 = Long.valueOf(0);
                                }}
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                        esp32_no2_unix.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    //網路時間
                                    long time = dataSnapshot.getValue(Integer.class);
                                    esp32_no2_unix_fix.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) { //注意DataSnapshot的名字
                                            long time_switch = Long.valueOf(time) * 1000;// its need to be in milisecond
                                            //校準時間
                                            long fix = snapshot.getValue(Integer.class);
                                            long fix_switch = Long.valueOf(fix) * 1000;
                                            long fix_add = Long.valueOf(time_switch + fix_switch);
                                            Date day_month_year = new Date(fix_add);
                                            String format = new SimpleDateFormat("yyyy/MM/dd ahh:mm").format(day_month_year);

                                            String_displaytime_2 = "\nesp之2偵測時間: " + format;
                                            unixtime_check2 = fix + time;
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError error) {}
                                    });

                                } catch (Exception time_not_found) {

                                    String_displaytime_2 = "\nesp之2時間找不到，請再試一次";
                                    unixtime_check2 =  Long.valueOf(0);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                        esp32_no3_unix.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    //網路時間
                                    int time = dataSnapshot.getValue(Integer.class);
                                    esp32_no3_unix_fix.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) { //注意DataSnapshot的名字
                                            long time_switch = Long.valueOf(time) * 1000;// its need to be in milisecond
                                            //校準時間
                                            long fix = snapshot.getValue(Integer.class);
                                            long fix_switch = Long.valueOf(fix) * 1000;
                                            long fix_add = Long.valueOf(time_switch + fix_switch);
                                            Date day_month_year = new Date(fix_add);
                                            String format = new SimpleDateFormat("yyyy/MM/dd ahh:mm").format(day_month_year);

                                            String_displaytime_3 = "\nesp之3偵測時間: " + format;
                                            unixtime_check3 = fix + time;
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError error) {}
                                    });

                                } catch (Exception time_not_found) {

                                    String_displaytime_3 = "\nesp之3時間找不到，請再試一次";
                                    unixtime_check3 =  Long.valueOf(0);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                        DatabaseReference esp32_no1_minor = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(select_major)).child("Minor");
                        DatabaseReference esp32_no2_minor = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(select_major)).child("Minor");
                        DatabaseReference esp32_no3_minor = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(select_major)).child("Minor");
                        DatabaseReference esp32_no1_major = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(select_major)).child("Major");
                        DatabaseReference esp32_no2_major = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(select_major)).child("Major");
                        DatabaseReference esp32_no3_major = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(select_major)).child("Major");

                        //major
                        esp32_no1_major.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer major = dataSnapshot.getValue(Integer.class);
                                    Major_1 = "Major : " + major.toString();
                                } catch (Exception RSSI_not_found) {
                                    Major_1 = "Major找不到";
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                        esp32_no2_major.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer major = dataSnapshot.getValue(Integer.class);
                                    Major_2 = "Major : " + major.toString();
                                } catch (Exception RSSI_not_found) {
                                    Major_2 = "Major找不到";
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                        esp32_no3_major.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer major = dataSnapshot.getValue(Integer.class);
                                    Major_3 = "Major : " + major.toString();
                                } catch (Exception RSSI_not_found) {
                                    Major_3 = "Major找不到";
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                        //minor
                        esp32_no1_minor.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer minor = dataSnapshot.getValue(Integer.class);
                                    Minor_1 = "Minor : " + minor.toString();
                                } catch (Exception RSSI_not_found) {
                                    Minor_1 = "Minor找不到";
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                        esp32_no2_minor.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer minor = dataSnapshot.getValue(Integer.class);
                                    Minor_2 = "Minor : " + minor.toString();
                                } catch (Exception RSSI_not_found) {
                                    Minor_2 = "Minor找不到";
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                        esp32_no3_minor.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer minor = dataSnapshot.getValue(Integer.class);
                                    Minor_3 = "Minor : " + minor.toString();
                                } catch (Exception RSSI_not_found) {
                                    Minor_3 = "Minor找不到";
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                    }else{ tv_ei.setText("範圍不對"); }
                }catch(Exception E){ tv_ei.setText("格式錯誤"); }

                conclude.setText(""); //清除文字說明的文字
                esp32_switch_unlock = "Yes";
            }
    };

        //按下「文字說明」
        private  View.OnClickListener btStatusListener = new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            public void onClick(View v){

                if(Button_lock == 0)
                {
                    Toast hint = Toast.makeText(MainActivity.this, "請先找到一個有資料的設備",Toast.LENGTH_SHORT);
                    hint.show();

                    return;
                }

                //條件一：三個同時10分內，或其中兩個10分內
                //過：
                //規則一：純粹的比rssi哪個為最小，它就是最靠近的
                //規則二：延伸規則1，但出現兩者rssi相同之情形(兩者相同距為遠方)
                //規則三：出現兩者rssi相同之情形(兩者相同距為近方)，如果不符合一二，執行之
                //未過：
                //兩者比大小，或以唯一為主
                try{
                    long time_now = System.currentTimeMillis() / 1000; //獲取app系統時間

                    int gap1_2 = abs(rssi_1) - abs(rssi_2); //12之距離
                    int gap1_3 = abs(rssi_1) - abs(rssi_3); //13之距離
                    int gap2_3 = abs(rssi_2) - abs(rssi_3); //23之距離

                    //特定條件下，啟用第二個三角形
                    int gapsup_1 = abs(rssi_sup) - abs(rssi_1);
                    int gapsup_3 = abs(rssi_sup) - abs(rssi_3);

                    //esp32：1、門口前方牆角(第一個esp) 2、門口斜對牆角(第二個esp) 3、門口平行牆角(第三個esp)
                    //三個同時超過10分鐘，就是沒有
                    if((time_now - unixtime_check1 > 600) & (time_now - unixtime_check2 > 600) & (time_now - unixtime_check3 > 600)){
                        conclude.setText("你要找的設備，可能不在此範圍一段時間，或著三個esp同時一段時間未啟動");

                    }else if ((time_now - unixtime_check1 > 600) & (time_now - unixtime_check2 > 600)) { //1.2同時超過10分鐘
                        conclude.setText("esp裝置一(門口前方牆角) 和 esp裝置二(門口斜對牆角) 未啟動或未偵測到一段時間" +
                                "\n因此可能位於 \"門口平行牆角(第三個esp)\" 附近");

                    }else if ((time_now - unixtime_check2 > 600) & (time_now - unixtime_check3 > 600)) { //2.3同時超過10分鐘
                        conclude.setText("esp裝置二(門口斜對牆角) 和 esp裝置三(門口平行牆角) 未啟動或未偵測到一段時間" +
                                "\n因此可能位於 \"門口前方牆角(第一個esp)\" 附近");

                    }else if ((time_now - unixtime_check1 > 600) & (time_now - unixtime_check3 > 600)) { //1.3同時超過10分鐘
                        conclude.setText("esp裝置一(門口前方牆角) 和 esp裝置三(門口平行牆角) 未啟動或未偵測到一段時間" +
                                "\n因此可能位於 \"門口斜對牆角(第二個esp)\" 附近");

                    }else if (time_now - unixtime_check1 > 600)  { //只有1未偵測超過10分鐘
                        if((rssi_2 > -140) & (rssi_3 > -140) & (gap2_3 < 4) & (gap2_3 > -4)){ //判定：2和3 RSSI接近，相似距離
                            conclude.setText("esp裝置一(門口前方牆角)未啟動或未偵測到一段時間 " +
                                    "\n可能位於 \"門口斜對牆角(第二個esp) 和 門口平行牆角(第三個esp)\" 之間" +
                                    "\n或可能位於兩者之間的牆外"); }

                        else if((rssi_2 > -140) & (rssi_3 > -140) & (rssi_2 > rssi_3)){ //判定：2 > 3 ，2比較近
                            conclude.setText("esp裝置一(門口前方牆角)未啟動或未偵測到一段時間 " +
                                    "\n可能位於 \"門口斜對牆角(第二個esp)\" 附近" +
                                    "\n或可能位於 \"門口斜對牆角(第二個esp) 和 門口平行牆角(第三個esp)\" 之間的牆外"); }

                        else if((rssi_2 > -140) & (rssi_3 > -140) & (rssi_2 < rssi_3)){ //判定：2 < 3 ，3比較近
                            conclude.setText("esp裝置一(門口前方牆角)未啟動或未偵測到一段時間 " +
                                    "\n可能位於 \"門口平行牆角(第三個esp)\" 附近" +
                                    "\n或可能位於 \"門口斜對牆角(第二個esp) 和 門口平行牆角(第三個esp)\" 之間的牆外"); }

                    }else if (time_now - unixtime_check2 > 600)  { //只有2未偵測超過10分鐘
                        if((rssi_1 > -140) & (rssi_3 > -140) & (gap1_3 < 4) & (gap1_3 > -4)){ //判定：1和3 RSSI接近，相似距離
                            conclude.setText("esp裝置二(門口斜對牆角)未啟動或未偵測到一段時間 " +
                                    "\n可能位於 \"門口前方牆角(第一個esp) 和 門口平行牆角(第三個esp)\" 之間" +
                                    "\n或可能位於兩者之間的牆外"); }

                        else if((rssi_1 > -140) & (rssi_3 > -140) & (rssi_1 > rssi_3)){  //判定：1 > 3，1比較近
                            conclude.setText("esp裝置二(門口斜對牆角)未啟動或未偵測到一段時間 " +
                                    "\n可能位於 \"門口前方牆角(第一個esp)\" 附近" +
                                    "\n或可能位於 \"門口前方牆角(第一個esp) 和 門口平行牆角(第三個esp)\" 之間的牆外"); }

                        else if((rssi_1 > -140) & (rssi_3 > -140) & (rssi_1 < rssi_3)){ //判定：1 < 3，3比較近
                            conclude.setText("esp裝置二(門口斜對牆角)未啟動或未偵測到一段時間 " +
                                    "\n可能位於 \"門口斜對牆角(第二個esp)\" 附近" +
                                    "\n或可能位於 \"門口斜對牆角(第二個esp) 和 門口平行牆角(第三個esp)\" 之間的牆外"); }

                    }else if (time_now - unixtime_check3 > 600)  { //只有3未偵測超過10分鐘
                        if((rssi_1 > -140) & (rssi_2 > -140) & (gap1_2 < 4) & (gap1_2 > -4)){ //判定：1和2 RSSI接近，相似距離
                            conclude.setText("esp裝置三(門口平行牆角)未啟動或未偵測到一段時間 " +
                                    "\n可能位於 \"門口前方牆角(第一個esp) 或 門口斜對牆角(第二個esp)\" 之間，" +
                                    "\n或可能位於兩者之間的牆外"); }

                        else if((rssi_1 > -140) & (rssi_2 > -140) & (rssi_1 > rssi_2)){ //判定：1 > 2，1比較近
                            conclude.setText("esp裝置三(門口平行牆角)未啟動或未偵測到一段時間 " +
                                    "\n可能位於 \"門口前方牆角(第一個esp)\" 附近" +
                                    "\n或可能位於 \"門口前方牆角(第一個esp) 或 門口斜對牆角(第二個esp)\" 之間的牆外"); }

                        else if((rssi_1 > -140) & (rssi_2 > -140) & (rssi_1 < rssi_2)){ //判定：1 < 2，2比較近
                            conclude.setText("esp裝置三(門口平行牆角)未啟動或未偵測到一段時間 " +
                                    "\n可能位於 \"門口斜對牆角(第二個esp)\" 附近" +
                                    "\n或可能位於 \"門口前方牆角(第一個esp) 或 門口斜對牆角(第二個esp)\" 之間的牆外"); }

                    }else {
                        //20220119
                        if ((gap2_3 < 4) & (gap2_3 > -4) & (gap1_3 < 4) & (gap1_3 > -4) & (gap1_2 < 4) & (gap1_2 > -4)
                                & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                            //conclude.setText("你要找的設備可能位於該空間的中心");

                            //啟用第二個三角形
                            if ((gapsup_1 < 4) & (gapsup_1 > -4) & (gapsup_3 < 4) & (gapsup_3 > -4) & (rssi_sup > -140))
                            {
                                conclude.setText("你要找的設備可能位於該空間的中心");
                            }
                            else if((rssi_1 < rssi_sup) & (rssi_3 < rssi_sup) & (rssi_sup > -140))
                            {
                                conclude.setText("你要找的設備可能靠近門口");
                            }
                            else //if(rssi_sup < -140)
                            {
                                conclude.setText("因為門口esp32未啟動或設置，你要找的設備可能在門口或空間中心");
                            }
                        }

                        else if((rssi_1 > rssi_2) & (rssi_1 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ // 1最近
                            if((gap2_3 < 4) & (gap2_3 > -4)){ // 2,3 相似
                                conclude.setText("該設備靠近 \"門口前方牆角(第一個esp)\" " +
                                        "\n但離 \"門口斜對牆角(第二個esp) 與 門口平行牆角(第三個esp)\" 的距離相似"); }
                            else{
                                if (rssi_1 < rssi_sup) { //是門口較近，還是第一個較近?
                                    conclude.setText("該設備靠近門口，稍微接近 \"門口前方牆角(第一個esp)\" ");
                                } else {
                                    conclude.setText("該設備靠近 \"門口前方牆角(第一個esp)\" ");
                                }
                            }
                        }
                        else if((rssi_2 > rssi_1) & (rssi_2 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ // 2最近
                            if((gap1_3 < 4) & (gap1_3 > -4)){ // 1,3 相似
                                    conclude.setText("該設備靠近 \"門口斜對牆角(第二個esp)\" " +
                                            "\n但離 \"門口前方牆角(第一個esp) 與 門口平行牆角(第三個esp)\" 的距離相似");
                            }else{
                                conclude.setText("該設備靠近 \"門口斜對牆角(第二個esp)\" "); }
                        }
                        else if((rssi_3 > rssi_1) & (rssi_3 > rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { // 3最近
                            if ((gap1_2 < 4) & (gap1_2 > -4)) { // 1,2 相似
                                conclude.setText("該設備靠近 \"門口平行牆角(第三個esp)\" " +
                                        "\n但離 \"門口前方牆角(第一個esp) 與 門口斜對牆角(第二個esp)\" 的距離相似");
                            }else{ //如果1,2不相似，開啟第二個三角形
                                if (rssi_3 < rssi_sup) { //是門口較近，還是第三個較近?
                                    conclude.setText("該設備靠近門口，稍微接近 \"門口平行牆角(第三個esp)\" ");
                                } else {
                                    conclude.setText("該設備靠近 \"門口平行牆角(第三個esp)\" ");
                                }
                            }
                        }//此時檢查完畢
                        else if((rssi_1 < rssi_2) & (rssi_1 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                                & (gap2_3 < 4) & (gap2_3 > -4)){ //2,3 相似，1最遠
                            conclude.setText("該設備遠離 \"門口前方牆角(第一個esp)\" " +
                                    "\n但離 \"門口斜對牆角(第二個esp) 與 門口平行牆角(第三個esp)\" 的距離相似"); }

                        else if((rssi_2 < rssi_1) & (rssi_2 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                                & (gap1_3 < 4) & (gap1_3 > -4)){ //1,3 相似，2最遠
                            if (rssi_sup > rssi_2)
                            {
                                conclude.setText("該設備可能靠近門口");
                            }else
                            {
                                conclude.setText("該設備遠離 \"門口斜對牆角(第二個esp)\" " +
                                        "\n但離 \"門口前方牆角(第一個esp) 與 門口平行牆角(第三個esp)\" 的距離相似");
                            }
                        }
                        else if((rssi_3 < rssi_2) & (rssi_3 < rssi_1) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                                & (gap1_2 < 4) & (gap1_2 > -4)){ //1,2 相似，3最遠
                            conclude.setText("該設備遠離 \"門口平行牆角(第三個esp)\" " +
                                    "\n但離 \"門口前方牆角(第一個esp) 與 門口斜對牆角(第二個esp)\" 的距離相似"); }

                        else{
                            conclude.setText("資料有誤，或是未建構這項規則");
                        }
                    }
                }catch(Exception RSSI_not_found){
                    conclude.setText("請先查找設備(beacon)");

                }}};

    //按下按鈕，跳轉至第二頁
    private  View.OnClickListener btMapListener = new View.OnClickListener() {
        public void onClick(View v){

            if (Button_lock == 0)
            {
                Toast hint = Toast.makeText(MainActivity.this, "請先找到一個有資料的設備再傳送",Toast.LENGTH_SHORT);
                hint.show();

                return;
            }

            try{
                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                bundle.putInt("select_number", number_decided);
                bundle.putString("select_room", select_room);
                bundle.putInt("select_door", Integer.parseInt(door_choice));

                bundle.putInt("rssi_1",rssi_1);
                bundle.putInt("rssi_2",rssi_2);
                bundle.putInt("rssi_3",rssi_3);
                bundle.putInt("rssi_sup",rssi_sup);

                bundle.putLong("check_time1",unixtime_check1);
                bundle.putLong("check_time2",unixtime_check2);
                bundle.putLong("check_time3",unixtime_check3);

                bundle.putString("beacon_name",beacon_name);

                intent.putExtras(bundle);
                intent.setClass(MainActivity.this,Map.class);
                startActivity(intent);
                finish();

            }catch(Exception RSSI_not_found){
                Toast error = Toast.makeText(MainActivity.this,"請先在上方選擇編號",Toast.LENGTH_SHORT);
                error.show();
            }
        }
    };

    //選擇第幾個esp32的資料
    Spinner.OnItemSelectedListener sp_esp32_choice_Listener = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            sw_number = String.valueOf(position);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    //選擇哪個環境
    Spinner.OnItemSelectedListener environment_choice_Listener = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int position2, long id2) {

            room_place = parent.getItemAtPosition(position2).toString(); //取得文字
            //將前面阿拉伯數字和點去掉，例如：1.大型空間 => 大型空間

            int room_place_number_dot = room_place.indexOf('.'); //第一次的.在第幾個位置

            room_choice = Integer.parseInt(room_place.substring(0,room_place_number_dot))-1; //選項1時，使他輸出為0
            room_place = room_place.substring(room_place_number_dot+1);

            try {
                strength_choice = str_Estrength.substring(position2,position2+1); //環境
                door_choice = str_Door.substring(position2,position2+1); //門
                //Toast error = Toast.makeText(MainActivity.this,strength_choice+"",Toast.LENGTH_SHORT);
                //error.show();
            }catch (Exception e){

            };
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    //選擇哪個beacon
    Spinner.OnItemSelectedListener beacon_id_spinner_choice_Listener = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int beacon_num, long id3) {

            beacon_name = parent.getItemAtPosition(beacon_num).toString(); //擷取選項的文字
            int device_number_dot = beacon_name.indexOf('.'); //第一次的.在第幾個位置
            beacon_name = beacon_name.substring(device_number_dot+1); //處理文字

            beacon_number_choice = Integer.parseInt(parent.getItemAtPosition(beacon_num).toString().substring(0,device_number_dot));

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    //按下切換「esp32」
    private View.OnClickListener esp32_switchListener = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        public void onClick(View v) {
            try {
                switch (sw_number){
                    case "0":   //rssi、距離、時間
                        String display_distance_1;
                        if(String_distance_1 != "格式不符"){
                            display_distance_1 = String_distance_1+"m";
                        }else{
                            display_distance_1 = String_distance_1;
                        }
                        detail.setText("RSSI："+String_rssi_1 + "，儀器測距："+ display_distance_1 + " " + "\n" + Major_1 + "，" + Minor_1  + String_displaytime_1 );
                        tv_ei.setText("以下是您的結果");
                        break;

                    case "1":   //rssi、距離、時間
                        String display_distance_2;
                        if(String_distance_2 != "格式不符"){
                            display_distance_2 = String_distance_2+"m";
                        }else{
                            display_distance_2 = String_distance_2;
                        }
                        detail.setText("RSSI："+String_rssi_2 + "，儀器測距："+ display_distance_2 + " " + "\n" + Major_2 + "，" + Minor_2  + String_displaytime_2  );
                        tv_ei.setText("以下是您的結果");
                        break;

                    case "2":   //rssi、距離、時間
                        String display_distance_3;
                        if(String_distance_3 != "格式不符"){
                            display_distance_3 = String_distance_3+"m";
                        }else{
                            display_distance_3 = String_distance_3;
                        }
                        detail.setText("RSSI：" + String_rssi_3 + "，儀器測距："+ display_distance_3 + " " + "\n" + Major_3 + "，" + Minor_3  + String_displaytime_3 );
                        tv_ei.setText("以下是您的結果");
                        break;
                }
            } catch (Exception RSSI_not_found) {
                detail.setText("資料有誤");
            }}};

    //imageview的onclick：切到管理者頁面
    private View.OnClickListener To_adminster_page_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            To_adminster_page_TapCount++;

            if(To_adminster_page_TapCount == 3){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,adminster_page.class);
                startActivity(intent);
                finish();
            }
            if(To_adminster_page_TapCount != 3)
            {
                Toast hint = Toast.makeText(MainActivity.this,"你已點擊"+ To_adminster_page_TapCount +"次該圖，進入管理者頁面需要再點擊" + (3-To_adminster_page_TapCount) + "次！",Toast.LENGTH_SHORT);
                hint.show();
            }
        }
    };

    //挑轉到那個葉面
    private  View.OnClickListener BT_FMC_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,find_mode_choose.class);
            startActivity(intent);
            finish();
        }
    };
}
//firebase讀取參考來源：
//https://mnya.tw/cc/word/1495.html
//https://kk665403.pixnet.net/blog/post/403284134-android-firebaseact