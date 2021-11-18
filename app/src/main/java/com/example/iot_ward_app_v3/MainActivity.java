package com.example.iot_ward_app_v3;

import static android.content.ContentValues.TAG;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView tvID,tvResult,tv_ei,tvrssi_1,tvrssi_2,tvrssi_3,distance_1,distance_2,distance_3;
    private TextView tvmajor1,tvmajor2,tvmajor3,tvminor1,tvminor2,tvminor3;
    private TextView tv_time_1,tv_time_2,tv_time_3,invisible_rssi_1,invisible_rssi_2,invisible_rssi_3,conclude;
    private TextView detail,sw_number,sw_distance,sw_time,sw_room,Input_major,number_decided;
    private Spinner  sp_esp32_choice,beacon_spinner,beacon_idnum_spinner;
    private ImageView imgTitle;
    private Button btMap,btStatus,esp32_switch,find_major;
    private TextView time_check1,time_check2,time_check3;
    int room_choice,beacon_number_choice;

    //下拉式選單
    String[] esp32_num = new String[]{
            "1","2","3"
    };

    String[] environment_choice = new String[]{
      "1.大型空間","2.樂得兒產房","3.ICU"
    };

    String[] beacon_id_spinner_choice = new String[]{
            "1.暫無使用之設備",
            "2.高層次胎兒監視器(FM20)","3.雙胞胎胎兒監視器(FC1400)", "4.嬰兒推車","5.嬰兒處理台(YD-IC-SCC)",
            "6.暫無使用之設備","7.暫無使用之設備","8.點滴架(有幫浦點滴輸液)" ,"9.暫無使用之設備","10.暫無使用之設備",
            "11.E化行動護理工作車","12.E化行動護理工作車(有條碼印表機)","13.急救車","14.生理監視器(PHILIPS)","15.生理監視器(M9000A)",
            "16.點滴架","17.調試用編號，僅供測試使用"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //輸入major編號(輸入文字)
        Input_major = (TextView) findViewById(R.id.Input_major);

        //圖片(外觀圖示)
        imgTitle = (ImageView)findViewById(R.id.imgTitle);

        //下拉式選單
        sp_esp32_choice = (Spinner)findViewById(R.id.sp_esp32_choice); //選擇該esp32的哪一個
        beacon_spinner  = (Spinner)findViewById(R.id.environment_choice); //選擇環境
        beacon_idnum_spinner = (Spinner)findViewById(R.id.beacon_id_spinner); //用選擇編號替代寫入編號

        //細節(外觀文字)
        detail = (TextView)findViewById(R.id.detail);
        sw_number = (TextView)findViewById(R.id.sw_number); //放下拉sp_esp32_choice的選擇
        sw_room = (TextView)findViewById(R.id.sw_room);     //放下拉sp_room_choice的選擇
        sw_distance = (TextView)findViewById(R.id.sw_distance);
        sw_time = (TextView)findViewById(R.id.sw_time);
        number_decided = (TextView)findViewById(R.id.number_decided); //1.用來丟入下一頁使用 2.防呆

        //設備編號(外觀文字)
        tvID = (TextView)findViewById(R.id.tvID);

        //設備資訊(外觀文字)
        tvResult = (TextView)findViewById(R.id.tvResult);

        //顯示要尋找的beacon的uuid值
        tv_ei = (TextView)findViewById(R.id.equipment_information_tv);

        //顯示三個esp32的RSSI值
        tvrssi_1 = (TextView)findViewById(R.id.tvrssi_1);
        tvrssi_2 = (TextView)findViewById(R.id.tvrssi_2);
        tvrssi_3 = (TextView)findViewById(R.id.tvrssi_3);

        //顯示三個esp32的距離值
        distance_1 = (TextView)findViewById(R.id.distance_1);
        distance_2 = (TextView)findViewById(R.id.distance_2);
        distance_3 = (TextView)findViewById(R.id.distance_3);

        //顯示該uuid的major、minor
        tvmajor1 = (TextView)findViewById(R.id.tvmajor1);
        tvmajor2 = (TextView)findViewById(R.id.tvmajor2);
        tvmajor3 = (TextView)findViewById(R.id.tvmajor3);
        tvminor1 = (TextView)findViewById(R.id.tvminor1);
        tvminor2 = (TextView)findViewById(R.id.tvminor2);
        tvminor3 = (TextView)findViewById(R.id.tvminor3);

        //顯示time
        tv_time_1 = (TextView)findViewById(R.id.time_1);
        tv_time_2 = (TextView)findViewById(R.id.time_2);
        tv_time_3 = (TextView)findViewById(R.id.time_3);
        time_check1 = (TextView)findViewById(R.id.time_check_1);
        time_check2 = (TextView)findViewById(R.id.time_check_2);
        time_check3 = (TextView)findViewById(R.id.time_check_3);

        //文字的位置判定
        conclude = (TextView)findViewById(R.id.conclude);

        //純粹放數字用的，別理他
        invisible_rssi_1 = (TextView)findViewById(R.id.invisible_rssi_1);
        invisible_rssi_2 = (TextView)findViewById(R.id.invisible_rssi_2);
        invisible_rssi_3 = (TextView)findViewById(R.id.invisible_rssi_3);

        //Spinner(sp_esp32_choice)
        ArrayAdapter<String> adapternumber2 =
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,esp32_num);
        adapternumber2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_esp32_choice.setAdapter(adapternumber2);    //設定資料來源
        sp_esp32_choice.setOnItemSelectedListener(sp_esp32_choice_Listener);
        //Spinner(environment_choice)
        ArrayAdapter<String> adapternumber_environment_choice =
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,environment_choice);
        adapternumber_environment_choice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        beacon_spinner.setAdapter(adapternumber_environment_choice);    //設定資料來源
        beacon_spinner.setOnItemSelectedListener(environment_choice_Listener);
        //Spinner(beacon_id_spinner)
        ArrayAdapter<String> adapternumber_beacon_id_spinner_choice =
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,beacon_id_spinner_choice);
        adapternumber_beacon_id_spinner_choice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        beacon_idnum_spinner.setAdapter(adapternumber_beacon_id_spinner_choice);    //設定資料來源
        beacon_idnum_spinner.setOnItemSelectedListener(beacon_id_spinner_choice_Listener);

        //button
        btStatus = (Button)findViewById(R.id.btStatus);
        btStatus.setOnClickListener(btStatusListener);
        btMap = (Button)findViewById(R.id.btMap);
        btMap.setOnClickListener(btMapListener);
        esp32_switch = (Button)findViewById(R.id.esp32_switch);
        esp32_switch.setOnClickListener(esp32_switchListener);
        find_major = (Button)findViewById(R.id.find_major);
        find_major.setOnClickListener(find_major_L);
        }

        //按下「查找按鈕」
        private  View.OnClickListener find_major_L = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database_get = FirebaseDatabase.getInstance();
                try {
                    int select_major = Integer.parseInt(String.valueOf(Input_major.getText()));
                    number_decided.setText(Input_major.getText());
                    //當我選擇環境時，他們的room_choice會被選項跟著改動
                    int firebase_number_1 = room_choice*3 + 1;
                    int firebase_number_2 = room_choice*3 + 2;
                    int firebase_number_3 = room_choice*3 + 3;

                    if((select_major > 0)&(select_major < 20)){
                        //搜尋有沒有該major，沒有就換找下一個
                            DatabaseReference major1 = database_get.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(select_major)).child("Major");
                            major1.addValueEventListener(new ValueEventListener() {
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Integer major = dataSnapshot.getValue(Integer.class);
                                    if(major == null){

                                        try { throw new Exception(); }
                                        catch (Exception major_esp1_notfound) {

                                            DatabaseReference major2 = database_get.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(select_major)).child("Major");
                                            major2.addValueEventListener(new ValueEventListener() {
                                                public void onDataChange(DataSnapshot dataSnapshot){
                                                    Integer major = dataSnapshot.getValue(Integer.class);
                                                    if(major == null){

                                                        try { throw new Exception(); }
                                                        catch (Exception major_esp2_notfound) {

                                                            DatabaseReference major3 = database_get.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(select_major)).child("Major");
                                                            major3.addValueEventListener(new ValueEventListener() {
                                                                public void onDataChange(DataSnapshot dataSnapshot){
                                                                    Integer major = dataSnapshot.getValue(Integer.class);
                                                                    if(major == null){
                                                                        tv_ei.setText(firebase_number_1+"號、"+firebase_number_2+"號、"+firebase_number_3+"號、"+"這三個esp32都沒有"+ select_major +"號，請換編號查詢");
                                                                        detail.setText("請重新輸入要查詢的beacon編號");}
                                                                    else{
                                                                        tv_ei.setText(firebase_number_1+"號、"+firebase_number_2+"號、"+firebase_number_3+"號、"+"至少有一個esp32有"+select_major+"號的資料，請接續後續步驟");
                                                                        detail.setText("查找完畢");}
                                                            }
                                                                public void onCancelled(DatabaseError error) { }});}
                                                        }else{
                                                        tv_ei.setText(firebase_number_1+"號、"+firebase_number_2+"號、"+firebase_number_3+"號、"+"至少有一個esp32有"+select_major+"號的資料，請接續後續步驟");
                                                        detail.setText("查找完畢");
                                                    }}
                                                public void onCancelled(DatabaseError error) { }});
                                                }}else{
                                        tv_ei.setText(firebase_number_1+"號、"+firebase_number_2+"號、"+firebase_number_3+"號、"+"至少有一個esp32有"+select_major+"號的資料，請接續後續步驟");
                                        detail.setText("查找完畢");
                                    }}
                                public void onCancelled(DatabaseError error) { }});

                        FirebaseDatabase database_sw = FirebaseDatabase.getInstance();
                        DatabaseReference esp32_no1_RSSI = database_sw.getReference("esp32 no_" + firebase_number_1).child((Input_major.getText()).toString()).child("RSSI");
                        DatabaseReference esp32_no2_RSSI = database_sw.getReference("esp32 no_" + firebase_number_2).child((Input_major.getText()).toString()).child("RSSI");
                        DatabaseReference esp32_no3_RSSI = database_sw.getReference("esp32 no_" + firebase_number_3).child((Input_major.getText()).toString()).child("RSSI");

                        esp32_no1_RSSI.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer rssi1 = dataSnapshot.getValue(Integer.class);
                                    invisible_rssi_1.setText(rssi1.toString());

                                    //tvrssi_1.setText("RSSI: " + rssi1.toString());
                                    tvrssi_1.setText(rssi1.toString());

                                    double A = 0, n = 0;
                                    if(room_choice == 0) {
                                        A = 59.00; n = 3.40;
                                    }
                                    if(room_choice == 1) {
                                        A = 65.00; n = 3.40;
                                    }
                                    if(room_choice == 2) {
                                        A = 75.00; n = 3.40;
                                    }
                                    //Toast test = Toast.makeText(MainActivity.this,A +""+n,Toast.LENGTH_SHORT);
                                    //test.show();

                                    //這個蠻有趣的，這邊0/0 = 無限大
                                    double M_1 = pow(10, ((abs(rssi1) - A) / (10 * n)));

                                    NumberFormat nf = NumberFormat.getInstance();
                                    nf.setMaximumFractionDigits(2);						// 若小數點超過四位，則第五位~四捨五入
                                    nf.setMinimumFractionDigits(1);
                                    distance_1.setText(String.valueOf(nf.format((M_1))));


                                } catch (Exception RSSI_not_found) {
                                    tvrssi_1.setText("資料錯誤");
                                    distance_1.setText("格式不符");
                                    invisible_rssi_1.setText("-150");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
                        esp32_no2_RSSI.addValueEventListener(new ValueEventListener() {
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer rssi2 = dataSnapshot.getValue(Integer.class);

                                    invisible_rssi_2.setText(rssi2.toString());
                                    //tvrssi_2.setText("RSSI: " + rssi2.toString());
                                    tvrssi_2.setText(rssi2.toString());

                                    double A = 0, n = 0;
                                    if(room_choice == 0) {
                                        A = 59.00; n = 3.40;
                                    }
                                    if(room_choice == 1) {
                                        A = 65.00; n = 3.40;
                                    }
                                    if(room_choice == 2) {
                                        A = 75.00; n = 3.40;
                                    }
                                    double M_1 = pow(10, ((abs(rssi2) - A) / (10 * n)));

                                    NumberFormat nf = NumberFormat.getInstance();
                                    nf.setMaximumFractionDigits(2);						// 若小數點超過四位，則第五位~四捨五入
                                    nf.setMinimumFractionDigits(1);
                                    distance_2.setText(String.valueOf(nf.format((M_1))));

                                } catch (Exception RSSI_not_found) {
                                    tvrssi_2.setText("資料錯誤");
                                    distance_2.setText("格式不符");
                                    invisible_rssi_2.setText("-150");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) { Log.w(TAG, "Failed to read value.", error.toException()); }
                        });
                        esp32_no3_RSSI.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer rssi3 = dataSnapshot.getValue(Integer.class);

                                    invisible_rssi_3.setText(rssi3.toString());
                                    //tvrssi_3.setText("RSSI: " + rssi3.toString());
                                    tvrssi_3.setText(rssi3.toString());

                                    double A = 0, n = 0;
                                    if(room_choice == 0) {
                                        A = 59.00; n = 3.40;
                                    }
                                    if(room_choice == 1) {
                                        A = 65.00; n = 3.40;
                                    }
                                    if(room_choice == 2) {
                                        A = 75.00; n = 3.40;
                                    }
                                    double M_1 = pow(10, ((abs(rssi3) - A) / (10 * n)));

                                    NumberFormat nf = NumberFormat.getInstance();
                                    nf.setMaximumFractionDigits(2);						// 若小數點超過四位，則第五位~四捨五入
                                    nf.setMinimumFractionDigits(1);
                                    distance_3.setText(String.valueOf(nf.format((M_1))));

                                } catch (Exception RSSI_not_found) {
                                    tvrssi_3.setText("資料錯誤");
                                    distance_3.setText("格式不符");
                                    invisible_rssi_3.setText("-150");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) { Log.w(TAG, "Failed to read value.", error.toException()); }});

                        DatabaseReference esp32_no1_unix = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(Input_major.getText())).child("epochTime_temp");
                        DatabaseReference esp32_no2_unix = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(Input_major.getText())).child("epochTime_temp");
                        DatabaseReference esp32_no3_unix = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(Input_major.getText())).child("epochTime_temp");
                        DatabaseReference esp32_no1_unix_fix = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(Input_major.getText())).child("time");
                        DatabaseReference esp32_no2_unix_fix = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(Input_major.getText())).child("time");
                        DatabaseReference esp32_no3_unix_fix = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(Input_major.getText())).child("time");

                        esp32_no1_unix.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    //網路時間
                                    int time = dataSnapshot.getValue(Integer.class);
                                    esp32_no1_unix_fix.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) { //注意DataSnapshot的名字
                                            long time_switch = Long.valueOf(time) * 1000;// its need to be in milisecond
                                            //校準時間
                                            long fix = snapshot.getValue(Integer.class);
                                            long fix_switch = Long.valueOf(fix) * 1000;
                                            long fix_add = Long.valueOf(time_switch + fix_switch);
                                            Date day_month_year = new Date(fix_add);
                                            String format = new SimpleDateFormat("MM月dd日, yyyy年 hh:mma").format(day_month_year);
                                            tv_time_1.setText("\n組1偵測時間: " + format);
                                            //tv_time_1.setText("\n組1偵測時間: " + fix_add);
                                            long check = fix + time;
                                            time_check1.setText(Long.toString(check));
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError error) {}
                                    });

                                } catch (Exception time_not_found) {
                                    tv_time_1.setText("\n組1時間找不到，請再試一次");
                                    time_check1.setText(Long.toString(0));
                                }}

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
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
                                            String format = new SimpleDateFormat("MM月dd日, yyyy年 hh:mma").format(day_month_year);
                                            tv_time_2.setText("\n組1偵測時間: " + format);
                                            //tv_time_2.setText("\n組1偵測時間: " + fix_add);
                                            long check = fix + time;
                                            time_check2.setText(Long.toString(check));
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError error) {}
                                    });

                                } catch (Exception time_not_found) {
                                    tv_time_2.setText("\n組2時間找不到，請再試一次");
                                    time_check2.setText(Long.toString(0));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
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
                                            String format = new SimpleDateFormat("MM月dd日, yyyy年 hh:mma").format(day_month_year);
                                            tv_time_3.setText("\n組3偵測時間: " + format);
                                            //tv_time_3.setText("\n組3偵測時間: " + fix_add);
                                            long check = fix + time;
                                            time_check3.setText(Long.toString(check));
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError error) {}
                                    });

                                } catch (Exception time_not_found) {
                                    tv_time_3.setText("\n組3時間找不到，請再試一次");
                                    time_check3.setText(Long.toString(0));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });

                        DatabaseReference esp32_no1_minor = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(Input_major.getText())).child("Minor");
                        DatabaseReference esp32_no2_minor = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(Input_major.getText())).child("Minor");
                        DatabaseReference esp32_no3_minor = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(Input_major.getText())).child("Minor");
                        DatabaseReference esp32_no1_major = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(Input_major.getText())).child("Major");
                        DatabaseReference esp32_no2_major = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(Input_major.getText())).child("Major");
                        DatabaseReference esp32_no3_major = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(Input_major.getText())).child("Major");

                        //major
                        esp32_no1_major.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer major = dataSnapshot.getValue(Integer.class);
                                    tvmajor1.setText("Major : " + major.toString());
                                } catch (Exception RSSI_not_found) {
                                    tvmajor1.setText("Major找不到");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
                        esp32_no2_major.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer major = dataSnapshot.getValue(Integer.class);
                                    tvmajor2.setText("Major : " + major.toString());
                                } catch (Exception RSSI_not_found) {
                                    tvmajor2.setText("Major找不到");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
                        esp32_no3_major.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer major = dataSnapshot.getValue(Integer.class);
                                    tvmajor3.setText("Major : " + major.toString());
                                } catch (Exception RSSI_not_found) {
                                    tvmajor3.setText("Major找不到");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });

                        //minor
                        esp32_no1_minor.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer minor = dataSnapshot.getValue(Integer.class);
                                    tvminor1.setText("Minor : " + minor.toString());
                                } catch (Exception RSSI_not_found) {
                                    tvminor1.setText("Minor找不到");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
                        esp32_no2_minor.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer minor = dataSnapshot.getValue(Integer.class);
                                    tvminor2.setText("Minor : " + minor.toString());
                                } catch (Exception RSSI_not_found) {
                                    tvminor2.setText("Minor找不到");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
                        esp32_no3_minor.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer minor = dataSnapshot.getValue(Integer.class);
                                    tvminor3.setText("Minor : " + minor.toString());
                                } catch (Exception RSSI_not_found) {
                                    tvminor3.setText("Minor找不到");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
                    } else{ tv_ei.setText("範圍不對"); }
                }catch(Exception E){
                    tv_ei.setText("格式錯誤");
                }


            }

    };
        //按下「文字說明」
        private  View.OnClickListener btStatusListener = new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            public void onClick(View v){
                //條件一：三個同時10分內，或其中兩個10分內
                //過：
                //規則一：純粹的比rssi哪個為最小，它就是最靠近的
                //規則二：延伸規則1，但出現兩者rssi相同之情形(兩者相同距為遠方)
                //規則三：出現兩者rssi相同之情形(兩者相同距為近方)，如果不符合一二，執行之
                //未過：
                //兩者比大小，或以唯一為主
                try{
                    int rssi_1 = Integer.parseInt(String.valueOf(invisible_rssi_1.getText()));
                    int rssi_2 = Integer.parseInt(String.valueOf(invisible_rssi_2.getText()));
                    int rssi_3 = Integer.parseInt(String.valueOf(invisible_rssi_3.getText()));
                    Long check1 = Long.parseLong(String.valueOf(time_check1.getText()));
                    Long check2 = Long.parseLong(String.valueOf(time_check2.getText()));
                    Long check3 = Long.parseLong(String.valueOf(time_check3.getText()));

                    long time_now=System.currentTimeMillis() / 1000; //獲取app系統時間

                    int gap1_2 = abs(rssi_1) - abs(rssi_2); //12之距離
                    int gap1_3 = abs(rssi_1) - abs(rssi_3); //13之距離
                    int gap2_3 = abs(rssi_2) - abs(rssi_3); //23之距離

                    //esp32：1、門口前方牆角(第一個esp) 2、門口斜對牆角(第二個esp) 3、門口平行牆角(第三個esp)
                    //三個同時超過10分鐘，就是沒有
                    if((time_now - check1 > 600) & (time_now - check2 > 600) & (time_now - check3 > 600)){
                        conclude.setText("你要找的beacon，可能不在此範圍一段時間，或著三個esp同時一段時間未啟動");

                    }else if ((time_now - check1 > 600) & (time_now - check2 > 600)) { //1.2同時超過10分鐘
                        conclude.setText("esp裝置一(門口前方牆角) 和 esp裝置二(門口斜對牆角) 未啟動或未偵測到一段時間" +
                                "\n因此可能位於 \"門口平行牆角(第三個esp)\" 附近");

                    }else if ((time_now - check2 > 600) & (time_now - check3 > 600)) { //2.3同時超過10分鐘
                        conclude.setText("esp裝置二(門口斜對牆角) 和 esp裝置三(門口平行牆角) 未啟動或未偵測到一段時間" +
                                "\n因此可能位於 \"門口前方牆角(第一個esp)\" 附近");

                    }else if ((time_now - check1 > 600) & (time_now - check3 > 600)) { //1.3同時超過10分鐘
                        conclude.setText("esp裝置一(門口前方牆角) 和 esp裝置三(門口平行牆角) 未啟動或未偵測到一段時間" +
                                "\n因此可能位於 \"門口斜對牆角(第二個esp)\" 附近");

                    }else if (time_now - check1 > 600)  { //只有1未偵測超過10分鐘
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

                    }else if (time_now - check2 > 600)  { //只有2未偵測超過10分鐘
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

                    }else if (time_now - check3 > 600)  { //只有3未偵測超過10分鐘
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
                        if ((gap2_3 < 4) & (gap2_3 > -4) & (gap1_3 < 4) & (gap1_3 > -4) & (gap1_2 < 4) & (gap1_2 > -4)
                                & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                            conclude.setText("你要找的beacon可能位於該空間的中心"); }

                        else if((rssi_1 > rssi_2) & (rssi_1 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ // 1最近
                            if((gap2_3 < 4) & (gap2_3 > -4)  & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ // 2,3 相似
                                conclude.setText("該設備靠近 \"門口前方牆角(第一個esp)\" " +
                                        "\n但離 \"門口斜對牆角(第二個esp) 與 門口平行牆角(第三個esp)\" 的距離相似"); }
                            else{
                                conclude.setText("該設備靠近 \"門口前方牆角(第一個esp)\" "); }
                        }
                        else if((rssi_2 > rssi_1) & (rssi_2 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ // 2最近
                            if((gap1_3 < 4) & (gap1_3 > -4)  & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ // 1,3 相似
                                conclude.setText("該設備靠近 \"門口斜對牆角(第二個esp)\" " +
                                        "\n但離 \"門口前方牆角(第一個esp) 與 門口平行牆角(第三個esp)\" 的距離相似");
                            }else{
                                conclude.setText("該設備靠近 \"門口斜對牆角(第二個esp)\" "); }
                        }
                        else if((rssi_3 > rssi_1) & (rssi_3 > rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { // 3最近
                            if ((gap1_2 < 4) & (gap1_2 > -4) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { // 1,2 相似
                                conclude.setText("該設備靠近 \"門口平行牆角(第三個esp)\" " +
                                        "\n但離 \"門口前方牆角(第一個esp) 與 門口斜對牆角(第二個esp)\" 的距離相似");
                            }else{
                                conclude.setText("該設備靠近 \"門口平行牆角(第三個esp)\" "); }
                        }//此時檢查完畢
                        else if((rssi_1 < rssi_2) & (rssi_1 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                                & (gap2_3 < 4) & (gap2_3 > -4)){ //2,3 相似，1最遠
                            conclude.setText("該設備遠離 \"門口前方牆角(第一個esp)\" " +
                                    "\n但離 \"門口斜對牆角(第二個esp) 與 門口平行牆角(第三個esp)\" 的距離相似"); }

                        else if((rssi_2 < rssi_1) & (rssi_2 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                                & (gap1_3 < 4) & (gap1_3 > -4)){ //1,3 相似，2最遠
                            conclude.setText("該設備遠離 \"門口斜對牆角(第二個esp)\" " +
                                    "\n但離 \"門口前方牆角(第一個esp) 與 門口平行牆角(第三個esp)\" 的距離相似"); }

                        else if((rssi_3 < rssi_2) & (rssi_3 < rssi_1) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)
                                & (gap1_2 < 4) & (gap1_2 > -4)){ //1,2 相似，3最遠
                            conclude.setText("該設備遠離 \"門口平行牆角(第三個esp)\" " +
                                    "\n但離 \"門口前方牆角(第一個esp) 與 門口斜對牆角(第二個esp)\" 的距離相似"); }

                        else{
                            conclude.setText("資料有誤，或是未建構這項規則");
                        }
                    }
                    //conclude.setText("這是一條測試用訊息"+ rssi_1 + "\n" + rssi_2 + "\n" + rssi_3);
                }catch(Exception RSSI_not_found){
                    conclude.setText("請先查找beacon");

                }}};

    //按下按鈕，跳轉至第二頁
    private  View.OnClickListener btMapListener = new View.OnClickListener() {
        public void onClick(View v){
            try{
                int rssi_1 = Integer.parseInt(String.valueOf(invisible_rssi_1.getText()));
                int rssi_2 = Integer.parseInt(String.valueOf(invisible_rssi_2.getText()));
                int rssi_3 = Integer.parseInt(String.valueOf(invisible_rssi_3.getText()));

                Long check1 = Long.parseLong(String.valueOf(time_check1.getText()));
                Long check2 = Long.parseLong(String.valueOf(time_check2.getText()));
                Long check3 = Long.parseLong(String.valueOf(time_check3.getText()));

                /*
                //conclude.setText("這是一條測試用訊息"+ rssi_1 + "\n" + rssi_2 + "\n" + rssi_3);
                if((rssi_1 > rssi_2) & (rssi_1 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                    conclude.setText("你要找的beacon靠近第一個esp32");
                }else if((rssi_2 > rssi_1) & (rssi_2 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                    conclude.setText("你要找的beacon靠近第二個esp32");
                }else if((rssi_3 > rssi_1) & (rssi_3 > rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                    conclude.setText("你要找的beacon靠近第三個esp32");
                }else{
                    conclude.setText("資料有誤，或是未建構這項規則");
                }*/

                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                int select_number = Integer.parseInt(String.valueOf(number_decided.getText()));
                bundle.putInt("select_number",select_number);

                String select_room = String.valueOf(sw_room.getText());
                bundle.putString("select_room", select_room);

                bundle.putInt("rssi_1",rssi_1);
                bundle.putInt("rssi_2",rssi_2);
                bundle.putInt("rssi_3",rssi_3);

                bundle.putLong("check_time1",check1);
                bundle.putLong("check_time2",check2);
                bundle.putLong("check_time3",check3);

                intent.putExtras(bundle);
                intent.setClass(MainActivity.this,Map.class);
                startActivity(intent);
                finish();

            }catch(Exception RSSI_not_found){
                Toast error = Toast.makeText(MainActivity.this,"請先在上方輸入編號",Toast.LENGTH_SHORT);
                error.show();
            }
        }
    };

    //選擇第幾個esp32的資料
    Spinner.OnItemSelectedListener sp_esp32_choice_Listener = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String pos_A_2 = String.valueOf(position);
            sw_number.setText(pos_A_2);
            //String pos_B = parent.getItemAtPosition(position).toString();
            //String Input = (Input_major.getText()).toString();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    //選擇哪個環境
    Spinner.OnItemSelectedListener environment_choice_Listener = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int position2, long id2) {
            room_choice = position2 ; //選項1時，使他輸出為0
            String room_place = parent.getItemAtPosition(position2).toString();
            //將前面阿拉伯數字和點去掉，例如：1.大型空間 => 大型空間
            int Position_string = 1;
            room_place = room_place.substring(Position_string+1);
            sw_room.setText(room_place);
            //Toast test = Toast.makeText(MainActivity.this,room_choice+"",Toast.LENGTH_SHORT);
            //test.show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};

    //選擇哪個beacon：用下拉式選單，替代輸入文字(因應11/10的老師建議)
    Spinner.OnItemSelectedListener beacon_id_spinner_choice_Listener = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int beacon_num, long id3) {

            beacon_number_choice = beacon_num + 1; //當我選16號時，beacon_num = 15，+1是為了textview方便設定
            //Toast beacon_number_Toast = Toast.makeText(MainActivity.this,beacon_number_choice+"",Toast.LENGTH_SHORT);
            //beacon_number_Toast.show();
            Input_major.setText(String.valueOf(beacon_number_choice)); //由此控制原本的輸入文字
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};


    private View.OnClickListener esp32_switchListener = new View.OnClickListener() {
        public void onClick(View v) {
            FirebaseDatabase database2 = FirebaseDatabase.getInstance();

            String pos_A = (String) sw_number.getText();
            String pos_B = (String) tv_ei.getText();

            try {
                String condition = (String) sw_number.getText();
                switch (condition){
                    case "0":
                        String S_tvrssi_1 = (String) tvrssi_1.getText(); //rssi
                        String S_distance_1 = (String) distance_1.getText(); //距離
                        String S_time_1 = (String)  tv_time_1.getText(); //時間

                        detail.setText("RSSI："+S_tvrssi_1 + "，距離："+ S_distance_1 + " " + "\n" + tvmajor1.getText() + "，" + tvminor1.getText()  + S_time_1  );
                        tv_ei.setText("以下是您的結果");
                        break;

                    case "1":
                        String S_tvrssi_2 = (String) tvrssi_2.getText(); //rssi
                        String S_distance_2 = (String) distance_2.getText(); //距離
                        String S_time_2 = (String)  tv_time_2.getText(); //時間

                        detail.setText("RSSI："+S_tvrssi_2 + "，距離："+ S_distance_2 + " " + "\n" + tvmajor2.getText() + "，" + tvminor2.getText()  + S_time_2  );
                        tv_ei.setText("以下是您的結果");
                        break;

                    case "2":
                        String S_tvrssi_3 = (String) tvrssi_3.getText(); //rssi
                        String S_distance_3 = (String) distance_3.getText(); //距離
                        String S_time_3 = (String)  tv_time_3.getText(); //時間

                        detail.setText("RSSI："+S_tvrssi_3 + "，距離："+ S_distance_3 + " " + "\n" + tvmajor3.getText() + "，" + tvminor3.getText()  + S_time_3  );
                        tv_ei.setText("以下是您的結果");
                        break;
                }
            } catch (Exception RSSI_not_found) {
                detail.setText("資料有誤");
            }}};

}


//firebase讀取來源https://mnya.tw/cc/word/1495.html