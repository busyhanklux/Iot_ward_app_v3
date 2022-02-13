package com.example.iot_ward_app_v3;

import static java.lang.Math.abs;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Multi_deal_with extends AppCompatActivity {

    //畫板參考https://lowren.pixnet.net/blog/post/92267045
    int rule, door, select_number, room_choice, sup_adjust;
    int point_decide; //有幾個點的資料
    String select_room, beacon_name;

    TextView Messageeeeeeeeee;

    //你必須在這裡創立全域變數，這可能是最簡單的bundle方法，不然程式會誤認為 0 或 null
    Long check1, check2, check3;
    int rssi_1, rssi_2, rssi_3, rssi_sup;
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

    ArrayList<String> deal_array_des = new ArrayList<String>();
    ArrayList<Integer> deal_array_code = new ArrayList<Integer>();

    Button bt_back;
    private TextView rule_keep, door_keep, remind_text, remind_device_L, remind_device_R, remind_room_L, remind_room_R, dir;
    private RadioButton left_door, right_door;
    private RadioGroup select_door;
    private Button pre_display, display;
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
            sup_adjust = bundle.getInt("sup_adjust");

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

            File place_des = new File(getFilesDir(), "place_des.txt");   //位置的代碼

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
            int sup = room_choice + 1;

            long time_now = System.currentTimeMillis() / 1000; //現在時間

            FirebaseDatabase database_sw = FirebaseDatabase.getInstance();

            try {
                DatabaseReference beacon_tp_1 = database_sw.getReference("esp32 no_" + firebase_number_1);

                beacon_tp_1.child("100000").child("RSSI").setValue(-150);
                beacon_tp_1.child("100000").child("epochTime_temp").setValue(2000000000);
                beacon_tp_1.child("100000").child("time").setValue(0);

                DatabaseReference beacon_tp_2 = database_sw.getReference("esp32 no_" + firebase_number_2);

                beacon_tp_2.child("100000").child("RSSI").setValue(-150);
                beacon_tp_2.child("100000").child("epochTime_temp").setValue(2000000000);
                beacon_tp_2.child("100000").child("time").setValue(0);

                DatabaseReference beacon_tp_3 = database_sw.getReference("esp32 no_" + firebase_number_3);

                beacon_tp_3.child("100000").child("RSSI").setValue(-150);
                beacon_tp_3.child("100000").child("epochTime_temp").setValue(2000000000);
                beacon_tp_3.child("100000").child("time").setValue(0);

                DatabaseReference sup_tp = database_sw.getReference("esp32_sup" + sup);

                sup_tp.child("100000").child("RSSI").setValue(-150);
                sup_tp.child("100000").child("epochTime_temp").setValue(2000000000);
                sup_tp.child("100000").child("second").setValue(0);


            }catch (Exception exist) {

            }

            for (int i = 0; i < str_Dmultilist.length ; i++) { //實際用：str_Dmultilist.length  測試用：4

                //抓編號，因為有三個esp32，所以一圈要做三次
                //Toast txt = Toast.makeText(Multi_deal_with.this,str_Dmultilist[i]+"",Toast.LENGTH_SHORT);
                //txt.show();

                DatabaseReference beacon_time_check_1 = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(str_Dmultilist[i])).child("epochTime_temp");
                DatabaseReference beacon_time_check_2 = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(str_Dmultilist[i])).child("epochTime_temp");
                DatabaseReference beacon_time_check_3 = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(str_Dmultilist[i])).child("epochTime_temp");

                DatabaseReference beacon_time_check_second_1 = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(str_Dmultilist[i])).child("time");
                DatabaseReference beacon_time_check_second_2 = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(str_Dmultilist[i])).child("time");
                DatabaseReference beacon_time_check_second_3 = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(str_Dmultilist[i])).child("time");

                DatabaseReference beacon_RSSI_1 = database_sw.getReference("esp32 no_" + firebase_number_1).child(String.valueOf(str_Dmultilist[i])).child("RSSI");
                DatabaseReference beacon_RSSI_2 = database_sw.getReference("esp32 no_" + firebase_number_2).child(String.valueOf(str_Dmultilist[i])).child("RSSI");
                DatabaseReference beacon_RSSI_3 = database_sw.getReference("esp32 no_" + firebase_number_3).child(String.valueOf(str_Dmultilist[i])).child("RSSI");
                DatabaseReference beacon_RSSI_sup = database_sw.getReference("esp32_sup" + sup).child(String.valueOf(str_Dmultilist[i])).child("RSSI");

                int part_i = i;

                //第一次
                beacon_time_check_1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot B_time1) {

                        try {
                            long time1 = B_time1.getValue(Integer.class);

                            beacon_time_check_second_1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot B_second1) {

                                    long second_1 = B_second1.getValue(Integer.class);

                                    //Toast txt = Toast.makeText(Multi_deal_with.this, time1 + "", Toast.LENGTH_SHORT);
                                    //txt.show();
                                    //txt = Toast.makeText(Multi_deal_with.this, second_1+"", Toast.LENGTH_SHORT);
                                    //txt.show();

                                    //第一個esp32偵測他的時間差小於120秒，開始第二個
                                    if ((time_now - (time1 + second_1)) < 120) {
                                        deal_with_number_1.add(str_Dmultilist[part_i]);

                                        //txt = Toast.makeText(Multi_deal_with.this, deal_with_number_1 + "", Toast.LENGTH_SHORT);
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

                                                        //Toast txt = Toast.makeText(Multi_deal_with.this, time2 + "", Toast.LENGTH_SHORT);
                                                        //txt.show();
                                                        //txt = Toast.makeText(Multi_deal_with.this, second_2+"", Toast.LENGTH_SHORT);
                                                        //txt.show();

                                                        //第二個esp32偵測他的時間差小於120秒，開始第三個
                                                        if ((time_now - (time2 + second_2)) < 120) {
                                                            deal_with_number_2.add(str_Dmultilist[part_i]);

                                                            txt = Toast.makeText(Multi_deal_with.this, deal_with_number_2 + "", Toast.LENGTH_SHORT);
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

                                                                            Toast txt = Toast.makeText(Multi_deal_with.this, time3 + "", Toast.LENGTH_SHORT);
                                                                            //txt.show();
                                                                            //txt = Toast.makeText(Multi_deal_with.this, second_3+"", Toast.LENGTH_SHORT);
                                                                            //txt.show();

                                                                            //第三個esp32偵測他的時間差小於120秒，開始判斷
                                                                            if ((time_now - (time3 + second_3)) < 120) {
                                                                                deal_with_number_3.add(str_Dmultilist[part_i]);

                                                                                //txt = Toast.makeText(Multi_deal_with.this, deal_with_number_3 + "", Toast.LENGTH_SHORT);
                                                                                //txt.show();

                                                                                //RSSI判定(2/10)

                                                                                beacon_RSSI_1.addValueEventListener(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot RSSI_1) {

                                                                                        beacon_RSSI_2.addValueEventListener(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(@NonNull DataSnapshot RSSI_2) {

                                                                                                beacon_RSSI_3.addValueEventListener(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(@NonNull DataSnapshot RSSI_3) {

                                                                                                        rssi_1 = RSSI_1.getValue(Integer.class);
                                                                                                        rssi_2 = RSSI_2.getValue(Integer.class);
                                                                                                        rssi_3 = RSSI_3.getValue(Integer.class);

                                                                                                        point_decide++;

                                                                                                        if (sup_adjust == 1) {
                                                                                                            //Toast txt = Toast.makeText(Multi_deal_with.this, "有", Toast.LENGTH_SHORT);
                                                                                                            //txt.show();

                                                                                                            //txt = Toast.makeText(Multi_deal_with.this, part_i+"", Toast.LENGTH_SHORT);
                                                                                                            //txt.show();

                                                                                                            beacon_RSSI_sup.addValueEventListener(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(@NonNull DataSnapshot RSSI_sup) {

                                                                                                                    try {

                                                                                                                        rssi_sup = RSSI_sup.getValue(Integer.class);

                                                                                                                        //有就繼續，沒有就跳例外處理

                                                                                                                        int gap1_2 = abs(rssi_1) - abs(rssi_2); //12之距離
                                                                                                                        int gap1_3 = abs(rssi_1) - abs(rssi_3); //13之距離
                                                                                                                        int gap2_3 = abs(rssi_2) - abs(rssi_3); //23之距離

                                                                                                                        //特定條件下，啟用第二個三角形
                                                                                                                        int gapsup_1 = abs(rssi_sup) - abs(rssi_1);
                                                                                                                        int gapsup_3 = abs(rssi_sup) - abs(rssi_3);

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

                                                                                                                        String str_rule = String.valueOf(rule);
                                                                                                                        String old_rule = "";

                                                                                                                        Toast txt = Toast.makeText(Multi_deal_with.this, rule+ "規則", Toast.LENGTH_SHORT);
                                                                                                                        txt.show();

                                                                                                                        try {

                                                                                                                            FileWriter fw = new FileWriter(place_des,true);

                                                                                                                            fw.write(str_rule);
                                                                                                                            fw.write('\n');

                                                                                                                            fw.close();

                                                                                                                            /*
                                                                                                                            FileInputStream objI = new FileInputStream(place_des);
                                                                                                                            int b = objI.read();
                                                                                                                            while ((b = objI.read()) != 1)
                                                                                                                            {
                                                                                                                                old_rule = (char)b;
                                                                                                                            }

                                                                                                                            FileOutputStream objO = new FileOutputStream(place_des);
                                                                                                                            byte[] bArray = str_rule.getBytes();
                                                                                                                            objO.write(bArray);
                                                                                                                            objO.close();

                                                                                                                             */

                                                                                                                        } catch (IOException e) {
                                                                                                                            e.printStackTrace();

                                                                                                                        }

                                                                                                                        //Toast txt = Toast.makeText(Multi_deal_with.this, point_decide + "中道", Toast.LENGTH_SHORT);
                                                                                                                        //txt.show();

                                                                                                                        if (part_i == str_Dmultilist.length-1 ) //實際用：str_Dmultilist.length-1 測試用：3
                                                                                                                        {
                                                                                                                            Intent intent = new Intent();
                                                                                                                            Bundle bundle = new Bundle();

                                                                                                                            //point_decide--;

                                                                                                                            int P_D = deal_with_number_3.size();

                                                                                                                            //txt = Toast.makeText(Multi_deal_with.this, P_D + "中道尾" +  deal_array_code, Toast.LENGTH_SHORT);
                                                                                                                            //txt.show();

                                                                                                                            bundle.putInt("point_decide", P_D);

                                                                                                                            //打包，沒寫會出錯
                                                                                                                            intent.putExtras(bundle);

                                                                                                                            //跳轉到下一頁，處理資訊
                                                                                                                            intent.setClass(Multi_deal_with.this, Multi_mapdisplay.class);
                                                                                                                            startActivity(intent);
                                                                                                                            finish();
                                                                                                                        }

                                                                                                                    } catch (Exception sup_junk) {

                                                                                                                        int gap1_2 = abs(rssi_1) - abs(rssi_2); //12之距離
                                                                                                                        int gap1_3 = abs(rssi_1) - abs(rssi_3); //13之距離
                                                                                                                        int gap2_3 = abs(rssi_2) - abs(rssi_3); //23之距離

                                                                                                                        //因為找不到sup的東西，故本次為沒有第二個三角形
                                                                                                                        //int gapsup_1 = abs(rssi_sup) - abs(rssi_1);
                                                                                                                        //int gapsup_3 = abs(rssi_sup) - abs(rssi_3);

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

                                                                                                                        String str_rule = String.valueOf(rule);
                                                                                                                        String line = "";

                                                                                                                        Toast txt = Toast.makeText(Multi_deal_with.this, rule+ "規則", Toast.LENGTH_SHORT);
                                                                                                                        txt.show();

                                                                                                                        try {

                                                                                                                            FileWriter fw = new FileWriter(place_des,true);

                                                                                                                            fw.write(str_rule);
                                                                                                                            fw.write('\n');

                                                                                                                            fw.close();

                                                                                                                        } catch (IOException e) {
                                                                                                                            e.printStackTrace();

                                                                                                                        }

                                                                                                                        //Toast txt = Toast.makeText(Multi_deal_with.this, point_decide + "中道", Toast.LENGTH_SHORT);
                                                                                                                        //txt.show();

                                                                                                                        if (part_i == str_Dmultilist.length-1) //實際用：str_Dmultilist.length-1 測試用：3
                                                                                                                        {
                                                                                                                            Intent intent = new Intent();
                                                                                                                            Bundle bundle = new Bundle();

                                                                                                                            //point_decide--;

                                                                                                                            int P_D = deal_with_number_3.size();;

                                                                                                                            //txt = Toast.makeText(Multi_deal_with.this, P_D + "中道尾" +  deal_array_code, Toast.LENGTH_SHORT);
                                                                                                                            //txt.show();

                                                                                                                            bundle.putInt("point_decide", P_D);

                                                                                                                            //打包，沒寫會出錯
                                                                                                                            intent.putExtras(bundle);

                                                                                                                            //跳轉到下一頁，處理資訊
                                                                                                                            intent.setClass(Multi_deal_with.this, Multi_mapdisplay.class);
                                                                                                                            startActivity(intent);
                                                                                                                            finish();
                                                                                                                        }

                                                                                                                        //txt = Toast.makeText(Multi_deal_with.this, "並沒有", Toast.LENGTH_SHORT);
                                                                                                                        //txt.show();
                                                                                                                    }
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                                                }
                                                                                                            });

                                                                                                        } else {

                                                                                                            int gap1_2 = abs(rssi_1) - abs(rssi_2); //12之距離
                                                                                                            int gap1_3 = abs(rssi_1) - abs(rssi_3); //13之距離
                                                                                                            int gap2_3 = abs(rssi_2) - abs(rssi_3); //23之距離

                                                                                                            //因為找不到sup的東西，故本次為沒有第二個三角形
                                                                                                            //int gapsup_1 = abs(rssi_sup) - abs(rssi_1);
                                                                                                            //int gapsup_3 = abs(rssi_sup) - abs(rssi_3);

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

                                                                                                            deal_array_code.add(rule);
                                                                                                            deal_array_des.add(description);

                                                                                                            String str_rule = String.valueOf(rule);
                                                                                                            String line = "";

                                                                                                            Toast txt = Toast.makeText(Multi_deal_with.this, rule+ "規則", Toast.LENGTH_SHORT);
                                                                                                            txt.show();

                                                                                                            try {

                                                                                                                FileWriter fw = new FileWriter(place_des,true);

                                                                                                                fw.write(str_rule);
                                                                                                                fw.write('\n');

                                                                                                                fw.close();

                                                                                                            } catch (IOException e) {
                                                                                                                e.printStackTrace();

                                                                                                            }

                                                                                                            //Toast txt = Toast.makeText(Multi_deal_with.this, point_decide + "中道", Toast.LENGTH_SHORT);
                                                                                                            //txt.show();

                                                                                                            if (part_i == str_Dmultilist.length-1) //實際用：str_Dmultilist.length-1 測試用：3
                                                                                                            {
                                                                                                                Intent intent = new Intent();
                                                                                                                Bundle bundle = new Bundle();

                                                                                                                //point_decide--;

                                                                                                                int P_D = deal_with_number_3.size();

                                                                                                                //txt = Toast.makeText(Multi_deal_with.this, P_D + "中道尾" +  deal_array_code, Toast.LENGTH_SHORT);
                                                                                                                //txt.show();

                                                                                                                bundle.putInt("point_decide", P_D);

                                                                                                                //打包，沒寫會出錯
                                                                                                                intent.putExtras(bundle);

                                                                                                                //跳轉到下一頁，處理資訊
                                                                                                                intent.setClass(Multi_deal_with.this, Multi_mapdisplay.class);
                                                                                                                startActivity(intent);
                                                                                                                finish();
                                                                                                            }

                                                                                                            //txt = Toast.makeText(Multi_deal_with.this, "沒有", Toast.LENGTH_SHORT);
                                                                                                            //txt.show();
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
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } catch (Exception b404) {

                        }

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


        } catch (Exception e) {

            Toast txt = Toast.makeText(Multi_deal_with.this, "發生錯誤，請再試一次", Toast.LENGTH_SHORT);
            txt.show();

            Intent intent = new Intent();

            intent.setClass(Multi_deal_with.this, Multi_main.class);
            startActivity(intent);
            finish();
        }

    }
}