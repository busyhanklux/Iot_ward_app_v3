package com.example.iot_ward_app_v3;

import static android.content.ContentValues.TAG;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

import androidx.appcompat.app.AppCompatActivity;

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
    private TextView detail,sw_number,sw_distance,sw_time,Input_major;
    private Spinner  sp_esp32_choice;
    private ImageView imgTitle;
    private Button btMap,btStatus,esp32_switch,find_major;
    private TextView time_check1,time_check2,time_check3;

    //下拉式選單
    String[] esp32_num = new String[]{
            "1","2","3"
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

        //細節(外觀文字)
        detail = (TextView)findViewById(R.id.detail);
        sw_number = (TextView)findViewById(R.id.sw_number); //放下拉sp_esp32_choice的選擇
        sw_distance = (TextView)findViewById(R.id.sw_distance);
        sw_time = (TextView)findViewById(R.id.sw_time);

        //設備編號(外觀文字)
        tvID = (TextView)findViewById(R.id.tvID);

        //設備資訊(外觀文字)
        tvResult = (TextView)findViewById(R.id.tvResult);

        //顯示要尋找的beacon的uuid值
        tv_ei = (TextView)findViewById(R.id.EI_tv);

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

        //Spinner
        ArrayAdapter<String> adapternumber2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,esp32_num);
        adapternumber2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_esp32_choice.setAdapter(adapternumber2);    //設定資料來源
        sp_esp32_choice.setOnItemSelectedListener(sp_esp32_choice_Listener);

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

        private  View.OnClickListener find_major_L = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database_get = FirebaseDatabase.getInstance();
                try {
                    int select_major = Integer.parseInt(String.valueOf(Input_major.getText()));
                    if((select_major > 0)&(select_major < 20)){
                        //搜尋有沒有該major，沒有就換找下一個
                            DatabaseReference major1 = database_get.getReference("esp32 no_1").child(String.valueOf(select_major)).child("Major");
                            major1.addValueEventListener(new ValueEventListener() {
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Integer major = dataSnapshot.getValue(Integer.class);
                                    if(major == null){

                                        try { throw new Exception(); }
                                        catch (Exception major_esp1_notfound) {

                                            DatabaseReference major2 = database_get.getReference("esp32 no_2").child(String.valueOf(select_major)).child("Major");
                                            major2.addValueEventListener(new ValueEventListener() {
                                                public void onDataChange(DataSnapshot dataSnapshot){
                                                    Integer major = dataSnapshot.getValue(Integer.class);
                                                    if(major == null){

                                                        try { throw new Exception(); }
                                                        catch (Exception major_esp2_notfound) {

                                                            DatabaseReference major3 = database_get.getReference("esp32 no_3").child(String.valueOf(select_major)).child("Major");
                                                            major3.addValueEventListener(new ValueEventListener() {
                                                                public void onDataChange(DataSnapshot dataSnapshot){
                                                                    Integer major = dataSnapshot.getValue(Integer.class);
                                                                    if(major == null){ tv_ei.setText("三個esp32都沒有"+ select_major +"號，請換編號查詢"); }
                                                                    else{ tv_ei.setText("至少有一個esp32有"+select_major+"號的資料，請接續後續步驟"); }
                                                            }
                                                                public void onCancelled(DatabaseError error) { }});}
                                                        }else{ tv_ei.setText("至少有一個esp32有"+select_major+"號的資料，請接續後續步驟");
                                                    }}
                                                public void onCancelled(DatabaseError error) { }});

                                                }}else{ tv_ei.setText("至少有一個esp32有"+select_major+"號的資料，請接續後續步驟"); }}
                                public void onCancelled(DatabaseError error) { }});

                        FirebaseDatabase database_sw = FirebaseDatabase.getInstance();
                        DatabaseReference esp32_no1_RSSI = database_sw.getReference("esp32 no_1").child((Input_major.getText()).toString()).child("RSSI");
                        DatabaseReference esp32_no2_RSSI = database_sw.getReference("esp32 no_2").child((Input_major.getText()).toString()).child("RSSI");
                        DatabaseReference esp32_no3_RSSI = database_sw.getReference("esp32 no_3").child((Input_major.getText()).toString()).child("RSSI");

                        esp32_no1_RSSI.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer rssi1 = dataSnapshot.getValue(Integer.class);
                                    invisible_rssi_1.setText(rssi1.toString());

                                    //tvrssi_1.setText("RSSI: " + rssi1.toString());
                                    tvrssi_1.setText(rssi1.toString());

                                    double A = 59.00;
                                    double n = 3.60;
                                    double M_1 = pow(10, ((abs(rssi1) - A) / (10 * n)));

                                    NumberFormat nf = NumberFormat.getInstance();
                                    nf.setMaximumFractionDigits(2);						// 若小數點超過四位，則第五位~四捨五入
                                    nf.setMinimumFractionDigits(1);
                                    distance_1.setText(String.valueOf(nf.format((M_1))));


                                } catch (Exception RSSI_not_found) {
                                    tvrssi_1.setText("資料錯誤");
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

                                    double A = 59.00;
                                    double n = 3.60;
                                    double M_1 = pow(10, ((abs(rssi2) - A) / (10 * n)));

                                    NumberFormat nf = NumberFormat.getInstance();
                                    nf.setMaximumFractionDigits(2);						// 若小數點超過四位，則第五位~四捨五入
                                    nf.setMinimumFractionDigits(1);
                                    distance_2.setText(String.valueOf(nf.format((M_1))));

                                } catch (Exception RSSI_not_found) {
                                    tvrssi_2.setText("資料錯誤");
                                    invisible_rssi_2.setText("-150");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
                        esp32_no3_RSSI.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Integer rssi3 = dataSnapshot.getValue(Integer.class);

                                    invisible_rssi_3.setText(rssi3.toString());
                                    //tvrssi_3.setText("RSSI: " + rssi3.toString());
                                    tvrssi_3.setText(rssi3.toString());

                                    double A = 59.00;
                                    double n = 3.60;
                                    double M_1 = pow(10, ((abs(rssi3) - A) / (10 * n)));

                                    NumberFormat nf = NumberFormat.getInstance();
                                    nf.setMaximumFractionDigits(2);						// 若小數點超過四位，則第五位~四捨五入
                                    nf.setMinimumFractionDigits(1);
                                    distance_3.setText(String.valueOf(nf.format((M_1))));

                                } catch (Exception RSSI_not_found) {
                                    tvrssi_3.setText("資料錯誤");
                                    invisible_rssi_3.setText("-150");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });

                        DatabaseReference esp32_no1_unix = database_sw.getReference("esp32 no_1").child(String.valueOf(Input_major.getText())).child("epochTime_temp");
                        DatabaseReference esp32_no2_unix = database_sw.getReference("esp32 no_2").child(String.valueOf(Input_major.getText())).child("epochTime_temp");
                        DatabaseReference esp32_no3_unix = database_sw.getReference("esp32 no_3").child(String.valueOf(Input_major.getText())).child("epochTime_temp");
                        DatabaseReference esp32_no1_unix_fix = database_sw.getReference("esp32 no_1").child(String.valueOf(Input_major.getText())).child("time");
                        DatabaseReference esp32_no2_unix_fix = database_sw.getReference("esp32 no_2").child(String.valueOf(Input_major.getText())).child("time");
                        DatabaseReference esp32_no3_unix_fix = database_sw.getReference("esp32 no_3").child(String.valueOf(Input_major.getText())).child("time");

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
                                }
                            }

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

                        DatabaseReference esp32_no1_minor = database_sw.getReference("esp32 no_1").child(String.valueOf(Input_major.getText())).child("Minor");
                        DatabaseReference esp32_no2_minor = database_sw.getReference("esp32 no_2").child(String.valueOf(Input_major.getText())).child("Minor");
                        DatabaseReference esp32_no3_minor = database_sw.getReference("esp32 no_3").child(String.valueOf(Input_major.getText())).child("Minor");
                        DatabaseReference esp32_no1_major = database_sw.getReference("esp32 no_1").child(String.valueOf(Input_major.getText())).child("Major");
                        DatabaseReference esp32_no2_major = database_sw.getReference("esp32 no_2").child(String.valueOf(Input_major.getText())).child("Major");
                        DatabaseReference esp32_no3_major = database_sw.getReference("esp32 no_3").child(String.valueOf(Input_major.getText())).child("Major");

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

        private  View.OnClickListener btStatusListener = new View.OnClickListener()
        {
            public void onClick(View v){
                //RSSI判定(版本2，規則一和二)
                //規則一：純粹的比rssi哪個為最小，它就是最靠近的
                //規則二：延伸規則1，但出現兩者rssi相同之情形(兩者相同距為遠方)
                //規則三：出現兩者rssi相同之情形(兩者相同距為近方)，如果不符合一二，執行之
                //條件一：設定10分鐘，如果其中一個時間超過該值，那就代表它不在該位或未啟動
                try{
                    int rssi_1 = Integer.parseInt(String.valueOf(invisible_rssi_1.getText()));
                    int rssi_2 = Integer.parseInt(String.valueOf(invisible_rssi_2.getText()));
                    int rssi_3 = Integer.parseInt(String.valueOf(invisible_rssi_3.getText()));
                    Long check1 = Long.parseLong(String.valueOf(time_check1.getText()));
                    Long check2 = Long.parseLong(String.valueOf(time_check2.getText()));
                    Long check3 = Long.parseLong(String.valueOf(time_check3.getText()));

                    long time_now=System.currentTimeMillis() / 1000; //獲取app系統時間
                    //Toast time = Toast.makeText(MainActivity.this, ""+time_now,Toast.LENGTH_SHORT);
                    //time.show();
                    if((time_now - check1 > 600) || (time_now - check2 > 600) || (time_now - check3 > 600)){
                        conclude.setText("你要找的beacon，可能不在該範圍一段時間，或著esp32一段時間未啟動");
                        //Toast overtime = Toast.makeText(MainActivity.this, "超時了",Toast.LENGTH_SHORT);
                        //overtime.show();
                    }else{
                        //Toast overtime = Toast.makeText(MainActivity.this, "還沒過",Toast.LENGTH_SHORT);
                        //overtime.show();
                        if((rssi_1 > rssi_2) & (rssi_1 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ //1(1最近)
                            if((rssi_2 == rssi_3)  & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ //2
                                conclude.setText("你要找的beacon靠近第一個esp32，但離第二與第三的距離相等");
                            }else{
                                conclude.setText("你要找的beacon靠近第一個esp32");
                            }
                        }else if((rssi_2 > rssi_1) & (rssi_2 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ //1(2最近)
                            if((rssi_1 == rssi_3)  & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){ //2
                                conclude.setText("你要找的beacon靠近第二個esp32，但離第一與第三的距離相等");
                            }else {
                                conclude.setText("你要找的beacon靠近第二個esp32");
                            }
                        }else if((rssi_3 > rssi_1) & (rssi_3 > rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { //1(3最近)
                            if ((rssi_1 == rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)) { //2
                                conclude.setText("你要找的beacon靠近第三個esp32，但離第一與第二的距離相等");
                            } else {
                                conclude.setText("你要找的beacon靠近第三個esp32");
                            }
                            //此時1,2檢查完畢
                        }else if((rssi_1 < rssi_2) & (rssi_1 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140) & (rssi_2 == rssi_3)){ //3(1最遠)
                            conclude.setText("你要找的beacon遠離第一個esp32，離第二與第三的距離相等");
                        }else if((rssi_2 < rssi_1) & (rssi_2 < rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140) & (rssi_1 == rssi_3)){ //3(2最遠)
                            conclude.setText("你要找的beacon遠離第二個esp32，離第一與第三的距離相等");
                        }else if((rssi_3 < rssi_2) & (rssi_3 < rssi_1) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140) & (rssi_2 == rssi_1)){ //3(3最遠)
                            conclude.setText("你要找的beacon遠離第三個esp32，離第一與第二的距離相等");
                        }else{
                            conclude.setText("資料有誤，或是未建構這項規則");
                        }
                    }
                    //conclude.setText("這是一條測試用訊息"+ rssi_1 + "\n" + rssi_2 + "\n" + rssi_3);
                }catch(Exception RSSI_not_found){
                    conclude.setText("這是一條找不到的測試用訊息");

                }
            }
        };

    private  View.OnClickListener btMapListener = new View.OnClickListener()
    {
        public void onClick(View v){

            try{

                int rssi_1 = Integer.parseInt(String.valueOf(invisible_rssi_1.getText()));
                int rssi_2 = Integer.parseInt(String.valueOf(invisible_rssi_2.getText()));
                int rssi_3 = Integer.parseInt(String.valueOf(invisible_rssi_3.getText()));
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
                bundle.putInt("rssi_1",rssi_1);
                bundle.putInt("rssi_2",rssi_2);
                bundle.putInt("rssi_3",rssi_3);

                intent.putExtras(bundle);
                intent.setClass(MainActivity.this,Map.class);
                startActivity(intent);

            }catch(Exception RSSI_not_found){
                Toast error = Toast.makeText(MainActivity.this,"資料有誤",Toast.LENGTH_SHORT);
                error.show();
            }
        }
    };
    Spinner.OnItemSelectedListener sp_esp32_choice_Listener = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String pos_A_2 = String.valueOf(position);
            sw_number.setText(pos_A_2);
            String pos_B = parent.getItemAtPosition(position).toString();
            String Input = (Input_major.getText()).toString();

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }};

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