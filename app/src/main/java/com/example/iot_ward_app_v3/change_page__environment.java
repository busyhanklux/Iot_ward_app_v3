package com.example.iot_ward_app_v3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class change_page__environment extends AppCompatActivity {

    private Button BT_CPE_back;
    TextView Hint,synchronize;
    Spinner sp_look_CPE_name,sp_add_CPE_name,sp_change_CPE_name,sp_delete_CPE_name;

    String e_time = "";
    String e_name = "";
    String e_door = "";
    String e_number = "";
    String e_list = "";
    String format = "";

    ArrayList E_room_name = new ArrayList();

    long environment_Time; //時間比較，減少傳輸浪費
    int environment_number; //確認有幾個環境，會影響迴圈次數
    int check_for_environment_text;

    int run_count;
    long time_now = System.currentTimeMillis() / 1000;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_page__environment);

        File environment_txt_time  = new File(getFilesDir(), "environment_time.txt");   //時間
        File environment_txt_door  = new File(getFilesDir(), "environment_door.txt");   //門口
        File environment_txt_name  = new File(getFilesDir(), "environment_name.txt");   //環境名稱
        File environment_txt_number= new File(getFilesDir(), "environment_number.txt"); //環境總數
        File environment_txt_list  = new File(getFilesDir(), "environment_list.txt");   //環境的數字代碼


        Hint = (TextView)findViewById(R.id.Hint);
        synchronize = (TextView)findViewById(R.id.synchronize);

        BT_CPE_back = findViewById(R.id.BT_CPE_back);
        BT_CPE_back.setOnClickListener(BT_CPE_back_L);

        sp_look_CPE_name   = (Spinner)findViewById(R.id.sp_look_CPE_name);
        sp_add_CPE_name    = (Spinner)findViewById(R.id.sp_add_CPE_name);
        sp_change_CPE_name = (Spinner)findViewById(R.id.sp_change_CPE_name);
        sp_delete_CPE_name = (Spinner)findViewById(R.id.sp_delete_CPE_name);

        //減少傳輸浪費
        try {
            //讀檔案
            FileInputStream fis = new FileInputStream(environment_txt_time);
            byte[] b = new byte[1024];
            int len = fis.read(b);

            //從文字檔獲取時間(環境)
            environment_Time = Long.parseLong(new String(b, 0, len));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //數量
        FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
        DatabaseReference firebase_number_check = database_environment_name.getReference("environment").child("number");
        firebase_number_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int number = dataSnapshot.getValue(int.class);
                environment_number = number;
                Hint.setText(number+"");

            }
            @Override
            public void onCancelled(DatabaseError error) { }
        });

        //抓取資料庫的資料
        DatabaseReference firebase_time_check = database_environment_name.getReference("environment").child("change_time");
        firebase_time_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot_E) {

                //E，從資料庫抓取的數值
                long E_change_time = dataSnapshot_E.getValue(long.class);

                //改動他
                e_time = e_time + time_now;

                //寫入
                try {
                    FileOutputStream ETT = null;
                    ETT = new FileOutputStream(environment_txt_time);
                    ETT.write(e_time.getBytes());
                    ETT.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast hint = Toast.makeText(change_page__environment.this, e_time+"",Toast.LENGTH_SHORT);
                //hint.show();
                check_for_environment_text = 1;

                //需要改動
                if (check_for_environment_text == 1) {
                    //Toast hint = Toast.makeText(Splash_Screen.this, "他需要被變動",Toast.LENGTH_SHORT);
                    //hint.show();

                    FileOutputStream fos = null;
                    try {
                        //寫入
                        fos = new FileOutputStream(environment_txt_time);
                        fos.write(e_time.getBytes());
                        fos.close();

                        //環境數量
                        FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
                        DatabaseReference environment_number_check = database_environment_name.getReference("environment").child("number");
                        environment_number_check.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot Data_E_number) {

                                int E_number = Data_E_number.getValue(int.class);

                                //小的是String，大的是int
                                e_number = e_number + E_number;

                                //寫入有幾個環境
                                try {
                                    FileOutputStream E_T_T = null;

                                    E_T_T = new FileOutputStream(environment_txt_number);
                                    E_T_T.write(e_number.getBytes());
                                    E_T_T.close();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast hint = Toast.makeText(change_page__environment.this, "E_number錯誤",Toast.LENGTH_SHORT);
                                    hint.show();
                                }

                                //處理編號list(1 2 3)
                                DatabaseReference environment_list_check = database_environment_name.getReference("environment").child("list");
                                environment_list_check.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot Data_E_list) {

                                        String E_list = Data_E_list.getValue(String.class);
                                        String []E_multilist = E_list.split(" ");

                                        //寫入有幾個環境
                                        try {
                                            FileOutputStream E_L = null;

                                            E_L = new FileOutputStream(environment_txt_list);
                                            E_L.write(E_list.getBytes());
                                            E_L.close();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Toast hint = Toast.makeText(change_page__environment.this, "E_list錯誤",Toast.LENGTH_SHORT);
                                            hint.show();
                                        }

                                        run_count = 0;

                                        ArrayList<String> room_name = new ArrayList<String>();
                                        room_name.add("選擇環境");

                                        for(int i = 0; i < E_multilist.length; i++){

                                            run_count++;

                                            //環境名稱
                                            DatabaseReference environment_name_check = database_environment_name.getReference("environment").child(String.valueOf(E_multilist[i]));

                                            environment_name_check.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot Data_E_name) {

                                                    String E_name = Data_E_name.getValue(String.class);
                                                    e_name = e_name + E_name + " ";

                                                    room_name.add(E_name); //(左)位置，(右)名稱

                                                    if (run_count == E_number)
                                                    {
                                                        E_room_name = room_name;
                                                        FileOutputStream E_N_N = null;
                                                        try {
                                                            E_N_N = new FileOutputStream(environment_txt_name);
                                                            E_N_N.write(e_name.getBytes());
                                                            E_N_N.close();

                                                            //spinner相關，你需要一個xml來調整大小(把android拿掉
                                                            //Spinner(sp_look_CPE_name_S)
                                                            ArrayAdapter<String> sp_look_CPE_name_S =
                                                                    new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,E_room_name);

                                                            sp_look_CPE_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                            sp_look_CPE_name.setAdapter(sp_look_CPE_name_S); //設定資料來源
                                                            sp_look_CPE_name.setOnItemSelectedListener(sp_look_CPE_name_L);

                                                            //Spinner(sp_add_CPE_name_S)
                                                            ArrayAdapter<String> sp_add_CPE_name_S =
                                                                    new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,E_room_name);

                                                            sp_add_CPE_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                            sp_add_CPE_name.setAdapter(sp_look_CPE_name_S); //設定資料來源

                                                            //Spinner(sp_change_CPE_name_S)
                                                            ArrayAdapter<String> sp_change_CPE_name_S =
                                                                    new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,E_room_name);

                                                            sp_change_CPE_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                            sp_change_CPE_name.setAdapter(sp_look_CPE_name_S); //設定資料來源

                                                            //Spinner(sp_delete_CPE_name_S)
                                                            ArrayAdapter<String> sp_delete_CPE_name_S =
                                                                    new ArrayAdapter<String>(change_page__environment.this,R.layout.spinner_value_choice_color,E_room_name);

                                                            sp_delete_CPE_name_S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                            sp_delete_CPE_name.setAdapter(sp_look_CPE_name_S); //設定資料來源

                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError error) { }
                                            });
                                        }

                                        //門的方向
                                        DatabaseReference environment_door_check = database_environment_name.getReference("environment").child("door");
                                        environment_door_check.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot Data_E_door) {

                                                String E_door = Data_E_door.getValue(String.class);
                                                e_door = e_door + E_door;

                                                if (run_count == E_number) {
                                                    FileOutputStream E_D_D = null; //Let them come(X)
                                                    try {
                                                        E_D_D = new FileOutputStream(environment_txt_door);
                                                        E_D_D.write(e_door.getBytes());
                                                        E_D_D.close();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                            }
                                        });
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError error) { }
                                });
                            }
                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                    check_for_environment_text = 2;

                    long time_now_fix = time_now*1000;
                    Date day_month_year = new Date(time_now_fix);
                    String format = new SimpleDateFormat("yyyy/MM/dd ahh:mm:ss").format(day_month_year);
                    synchronize.setText(format);

                }

                //全數完成
                //檢查完畢 或 不需要改動
                if (check_for_environment_text == 2) {

                }

            }
            @Override
            public void onCancelled(DatabaseError error) { }
        });

        //spinner相關，你需要一個xml來調整大小(把android拿掉
        //Spinner(sp_esp32_choice)
        //ArrayAdapter<String> adapternumber2 =
        //        new ArrayAdapter<String>(this,R.layout.spinner_value_choice_color,esp32_num);
        //adapternumber2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //sp_esp32_choice.setAdapter(adapternumber2);    //設定資料來源
        //sp_esp32_choice.setOnItemSelectedListener(sp_esp32_choice_Listener);
    }

    public View.OnClickListener BT_CPE_back_L = view ->
    {

        Intent intent = new Intent();
        intent.setClass(change_page__environment.this,adminster_page.class);
        startActivity(intent);
        finish();
    };

    Spinner.OnItemSelectedListener sp_look_CPE_name_L = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Toast hint = Toast.makeText(change_page__environment.this, parent.getItemAtPosition(position).toString()+"",Toast.LENGTH_SHORT);
            hint.show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }};
}
