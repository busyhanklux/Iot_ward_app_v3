package com.example.iot_ward_app_v3;

import static android.content.ContentValues.TAG;

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

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView tvID,tvResult,tvuuid,tvrssi_1,tvrssi_2,tvrssi_3,distance_1,distance_2,distance_3,tvmajor,tvminor;
    private TextView tv_time_1,tv_time_2,tv_time_3,invisible_rssi_1,invisible_rssi_2,invisible_rssi_3,conclude;
    private Spinner spID;
    private ImageView imgTitle;
    private Button btMap,btStatus;

    //將來下拉式選單在之後需要改掉
    String[] number = new String[]{
            "000000a00000-00b0-0000-00c0-00000000",
            "000000a00000-00b0-0000-00c0-00000010",
            "000000a00000-00b0-0000-00c0-00000020",
            "000000a00000-00b0-0000-00c0-00000030",
            "000000a00000-00b0-0000-00c0-00000040",
            "000000a00000-00b0-0000-00c0-00000050",
            "000000a00000-00b0-0000-00c0-00000060",
            "000000a00000-00b0-0000-00c0-00000070",
            "000000a00000-00b0-0000-00c0-00000080"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //圖片(外觀圖示)
        imgTitle = (ImageView)findViewById(R.id.imgTitle);

        //下拉式選單
        spID = (Spinner)findViewById(R.id.spID);

        //設備編號(外觀文字)
        tvID = (TextView)findViewById(R.id.tvID);

        //設備資訊(外觀文字)
        tvResult = (TextView)findViewById(R.id.tvResult);

        //顯示要尋找的beacon的uuid值
        tvuuid = (TextView)findViewById(R.id.tvuuid);

        //顯示三個esp32的RSSI值
        tvrssi_1 = (TextView)findViewById(R.id.tvrssi_1);
        tvrssi_2 = (TextView)findViewById(R.id.tvrssi_2);
        tvrssi_3 = (TextView)findViewById(R.id.tvrssi_3);

        //顯示三個esp32的距離值
        distance_1 = (TextView)findViewById(R.id.distance_1);
        distance_2 = (TextView)findViewById(R.id.distance_2);
        distance_3 = (TextView)findViewById(R.id.distance_3);

        //顯示該uuid的major、minor
        tvmajor = (TextView)findViewById(R.id.tvmajor);
        tvminor = (TextView)findViewById(R.id.tvminor);

        //顯示time
        tv_time_1 = (TextView)findViewById(R.id.time_1);
        tv_time_2 = (TextView)findViewById(R.id.time_2);
        tv_time_3 = (TextView)findViewById(R.id.time_3);

        //文字的位置判定
        conclude = (TextView)findViewById(R.id.conclude);

        //純粹放數字用的，別理他
        invisible_rssi_1 = (TextView)findViewById(R.id.invisible_rssi_1);
        invisible_rssi_2 = (TextView)findViewById(R.id.invisible_rssi_2);
        invisible_rssi_3 = (TextView)findViewById(R.id.invisible_rssi_3);

        //Spinner
        ArrayAdapter<String> adapternumber = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,number);
        adapternumber.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);   //設定顯示格式
        spID.setAdapter(adapternumber);    //設定資料來源
        spID.setOnItemSelectedListener(spIDListener);

        //button
        btStatus = (Button)findViewById(R.id.btStatus);
        btStatus.setOnClickListener(btStatusListener);
        btMap = (Button)findViewById(R.id.btMap);
        btMap.setOnClickListener(btMapListener);
        }

        private  View.OnClickListener btStatusListener = new View.OnClickListener()
        {
            public void onClick(View v){
                //RSSI判定(版本2，規則一和二)
                //規則一：純粹的比rssi哪個為最小，它就是最靠近的
                //規則二：延伸規則一，但出現兩者rssi相同之情形(兩者相同距為遠方)
                //規則三：延伸規則二，但出現兩者rssi相同之情形(兩者相同距為近方)
                try{
                    int rssi_1 = Integer.parseInt(String.valueOf(invisible_rssi_1.getText()));
                    int rssi_2 = Integer.parseInt(String.valueOf(invisible_rssi_2.getText()));
                    int rssi_3 = Integer.parseInt(String.valueOf(invisible_rssi_3.getText()));
                    //conclude.setText("這是一條測試用訊息"+ rssi_1 + "\n" + rssi_2 + "\n" + rssi_3);
                    if((rssi_1 > rssi_2) & (rssi_1 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                        if((rssi_2 == rssi_3)  & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                            conclude.setText("你要找的beacon靠近第一個esp32，但離第二與第三的距離相等");
                        }else{
                            conclude.setText("你要找的beacon靠近第一個esp32");
                        }
                    }else if((rssi_2 > rssi_1) & (rssi_2 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                        if((rssi_1 == rssi_3)  & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                            conclude.setText("你要找的beacon靠近第二個esp32，但離第一與第三的距離相等");
                        }else {
                            conclude.setText("你要找的beacon靠近第二個esp32");
                        }
                    }else if((rssi_3 > rssi_1) & (rssi_3 > rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                        if((rssi_1 == rssi_2)  & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                            conclude.setText("你要找的beacon靠近第三個esp32，但離第一與第二的距離相等");
                        }else {
                            conclude.setText("你要找的beacon靠近第三個esp32");
                        }
                    }else{
                        conclude.setText("資料有誤，或是未建構這項規則");
                    }
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

                intent.setClass(MainActivity.this,Map.class);

                intent.putExtras(bundle);
                startActivity(intent);

            }catch(Exception RSSI_not_found){
                Toast error = Toast.makeText(MainActivity.this,"資料有誤",Toast.LENGTH_SHORT);
                error.show();
            }
        }
    };

    private Spinner.OnItemSelectedListener spIDListener = new Spinner.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            //DatabaseReference esp32_no1_RSSI = database.getReference("esp32 no_0").child(spID.toString()).child("RSSI");

            //https://hjwang520.pixnet.net/blog/post/405000676-%5Bandroid-studio%5D%E4%B8%8B%E6%8B%89%E5%BC%8F%E9%81%B8%E5%96%AE%28spinner%29%E7%AD%86%E8%A8%98
            //下拉式選單參考，A是得到第幾個選項，B是得到文字
            String pos_A = String.valueOf(position);
            String pos_B = parent.getItemAtPosition(position).toString();

            //根據下拉式選單得到的文字，套用至esp32 no_0之後的那個child的位置

            //1.uuid(可能用不到)
            DatabaseReference esp32_no1_uuid = database.getReference("esp32 no_0").child(pos_B);
            //2.RSSI
            DatabaseReference esp32_no1_RSSI = database.getReference("esp32 no_0").child(pos_B).child("RSSI");
            DatabaseReference esp32_no2_RSSI = database.getReference("esp32 no_1").child(pos_B).child("RSSI");
            DatabaseReference esp32_no3_RSSI = database.getReference("esp32 no_2").child(pos_B).child("RSSI");
            //3.距離
            DatabaseReference esp32_no1_distance = database.getReference("esp32 no_0").child(pos_B).child("distance");
            DatabaseReference esp32_no2_distance = database.getReference("esp32 no_1").child(pos_B).child("distance");
            DatabaseReference esp32_no3_distance = database.getReference("esp32 no_2").child(pos_B).child("distance");

            //4.unix時間轉 時分秒
            DatabaseReference esp32_no1_unix = database.getReference("esp32 no_0").child(pos_B).child("time");
            DatabaseReference esp32_no2_unix = database.getReference("esp32 no_1").child(pos_B).child("time");
            DatabaseReference esp32_no3_unix = database.getReference("esp32 no_2").child(pos_B).child("time");

            //5.minor & major
            DatabaseReference esp32_no1_minor = database.getReference("esp32 no_0").child(pos_B).child("minor");
            DatabaseReference esp32_no1_major = database.getReference("esp32 no_0").child(pos_B).child("major");

            //DatabaseReference UUID = database.getReference("UUID");  //從firebase裡的"UUID"接收訊息
            //myRef.setValue("00000");  設定訊息為00000

            //顯示UUID
            try {
                String uuid = pos_B;
                tvuuid.setText("UUID : " + uuid);
            } catch (Exception RSSI_not_found) {
                tvuuid.setText("uuid錯誤，請再試一次");
            }
            /*
            esp32_no1_uuid.addValueEventListener(new ValueEventListener() {//讀取firebase裡的資料
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    try {
                        String uuid = dataSnapshot.getValue(String.class);
                        tvuuid.setText("UUID : " + uuid);
                    } catch (Exception RSSI_not_found) {
                        tvuuid.setText("uuid找不到，請再試一次");
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
*/
            //顯示RSSI(有三個)

            esp32_no1_RSSI.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    try {
                        Integer rssi1 = dataSnapshot.getValue(Integer.class);

                        invisible_rssi_1.setText(rssi1.toString());
                        //tvrssi_1.setText("RSSI: " + rssi1.toString());
                        tvrssi_1.setText(rssi1.toString());

                    } catch (Exception RSSI_not_found) {
                        tvrssi_1.setText("RSSI找不到，請再試一次");
                        invisible_rssi_1.setText("-150");
                    }}
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }});

            esp32_no2_RSSI.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot){
                    try {
                        Integer rssi2 = dataSnapshot.getValue(Integer.class);

                        invisible_rssi_2.setText(rssi2.toString());
                        //tvrssi_2.setText("RSSI: " + rssi2.toString());
                        tvrssi_2.setText(rssi2.toString());

                    } catch (Exception RSSI_not_found) {
                        tvrssi_2.setText("RSSI找不到，請再試一次");
                        invisible_rssi_2.setText("-150");
                    }}

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }});

            esp32_no3_RSSI.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    try {
                        Integer rssi3 = dataSnapshot.getValue(Integer.class);

                        invisible_rssi_3.setText(rssi3.toString());
                        //tvrssi_3.setText("RSSI: " + rssi3.toString());
                        tvrssi_3.setText(rssi3.toString());

                    } catch (Exception RSSI_not_found) {
                        tvrssi_3.setText("RSSI找不到，請再試一次");
                        invisible_rssi_3.setText("-150");
                    }}

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }});



            //顯示距離
            esp32_no1_distance.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    try {
                        double distance1 = dataSnapshot.getValue(Double.class);
                        distance_1.setText("距離: " + Double.toString(distance1));
                    } catch (Exception RSSI_not_found) {
                        distance_1.setText("距離找不到，請再試一次");
                    }}
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }});

            esp32_no2_distance.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    try {
                        double distance2 = dataSnapshot.getValue(Double.class);
                        distance_2.setText("距離: " + Double.toString(distance2));
                    } catch (Exception RSSI_not_found) {
                        distance_2.setText("距離找不到，請再試一次");
                    }}
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }});

            esp32_no3_distance.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    try {
                        double distance3 = dataSnapshot.getValue(Double.class);
                        distance_3.setText("距離: " + Double.toString(distance3));
                    } catch (Exception RSSI_not_found) {
                        distance_3.setText("距離找不到，請再試一次");
                    }}
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }});


            //unix時間轉(時分秒)，有三個

            esp32_no1_unix.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    try {
                        Integer time = dataSnapshot.getValue(Integer.class);
                        long time_switech = Long.valueOf(time)*1000;// its need to be in milisecond
                        Date day_month_year = new Date(time_switech);
                        String format = new SimpleDateFormat("MM月dd日, yyyy年 hh:mma").format(day_month_year);

                        tv_time_1.setText("組1偵測時間: \n" + format);
                    } catch (Exception time_not_found) {
                        tv_time_1.setText("組1時間找不到，請再試一次");
                    }}
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }});

            esp32_no2_unix.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    try {
                        Integer time = dataSnapshot.getValue(Integer.class);
                        long time_switech = Long.valueOf(time)*1000;// its need to be in milisecond
                        Date day_month_year = new Date(time_switech);
                        String format = new SimpleDateFormat("MM月dd日, yyyy年 hh:mma").format(day_month_year);

                        tv_time_2.setText("組2偵測時間: \n" + format);
                    } catch (Exception time_not_found) {
                        tv_time_2.setText("組2時間找不到，請再試一次");
                    }}
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }});

            esp32_no3_unix.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    try {
                        Integer time = dataSnapshot.getValue(Integer.class);
                        long time_switech = Long.valueOf(time)*1000;// its need to be in milisecond
                        Date day_month_year = new Date(time_switech);
                        String format = new SimpleDateFormat("MM月dd日, yyyy年 hh:mma").format(day_month_year);

                        tv_time_3.setText("組3偵測時間: \n" + format);

                    } catch (Exception time_not_found) {
                        tv_time_3.setText("組3時間找不到，請再試一次");
                    }}
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }});

            //major
            esp32_no1_major.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Integer major = dataSnapshot.getValue(Integer.class);
                        tvmajor.setText("Major : " + major.toString());
                    }catch (Exception RSSI_not_found) {
                        tvmajor.setText("Major找不到，\n請再試一次");
                    }}
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
                        tvminor.setText("Minor : " + minor.toString());
                    }catch (Exception RSSI_not_found) {
                        tvminor.setText("Minor找不到，\n請再試一次");
                    }}
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }});



        }
        @Override
        public void onNothingSelected(AdapterView<?>parent){
        }
    };



    /*
    myRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            String value = dataSnapshot.getValue(String.class);
            Log.d(TAG, "Value is: " + value);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    });
     */
}


//來源https://mnya.tw/cc/word/1495.html