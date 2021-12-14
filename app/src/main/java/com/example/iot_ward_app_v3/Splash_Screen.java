package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.Hashtable;

public class Splash_Screen extends AppCompatActivity {

    String d_time = "";
    String e_time = "";

    int check_time_for_text;
    //1需要被更動，2是改完了

    long time_now = System.currentTimeMillis() / 1000;

    //把資料預載放這
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //時間檔案(有兩個)，
        //如果Database的內容被改的時間等.大於這個text的時間，改動他，並將這個text的時間更新 => 檢測變動的參數變 1
        //如果Database的內容被改的時間小於這個text的時間，不理他
        //如果沒有text檔案，那就將text時間判定為0

        //流程，先檢查 device_txt_time 和 environment_txt_time 是否存在

        File device_txt_time       = new File(getFilesDir(), "device_txt_time.txt");
        File environment_txt_time  = new File(getFilesDir(), "environment_txt_time.txt");

        //如果兩個檔案都存在，跑這段

        if(device_txt_time.exists() && environment_txt_time.exists()){

            FirebaseDatabase database_device_name = FirebaseDatabase.getInstance();
            DatabaseReference device_firebase_time_check = database_device_name.getReference("device").child("change_time");
            device_firebase_time_check.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot_D) {

                    //變數
                    long device_Time = 0;
                    try {
                        //讀檔案
                        FileInputStream fis = new FileInputStream(device_txt_time);
                        byte[] b = new byte[1024];
                        int len = fis.read(b);

                        //從文字檔獲取時間(設備)
                        device_Time = Long.parseLong(new String(b, 0, len));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast hint = Toast.makeText(Splash_Screen.this, device_Time+"",Toast.LENGTH_SHORT);
                    //hint.show();

                    //D，從資料庫抓取的數值
                    long D_change_time = dataSnapshot_D.getValue(long.class);

                    //改動他
                    if (D_change_time >= device_Time) {
                        e_time = e_time + time_now;
                        check_time_for_text = 1;
                    }

                    //不改他
                    if (D_change_time < device_Time) {
                        check_time_for_text = 2;
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) { }
            });

            FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
            DatabaseReference firebase_time_check = database_environment_name.getReference("environment").child("change_time");
            firebase_time_check.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot_E) {

                    //變數
                    long environment_Time = 0;
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
                    Toast hint = Toast.makeText(Splash_Screen.this, environment_Time+"",Toast.LENGTH_SHORT);
                    //hint.show();

                    //E，從資料庫抓取的數值
                    long E_change_time = dataSnapshot_E.getValue(long.class);

                    //改動他
                    if (E_change_time >= environment_Time) {
                        e_time = e_time + time_now;
                        check_time_for_text = 1;
                    }

                    //不改他
                    if (E_change_time < environment_Time) {
                        check_time_for_text = 2;
                    }

                    //需要改動
                    if (check_time_for_text == 1) {
                        //Toast hint = Toast.makeText(Splash_Screen.this, "他需要被變動",Toast.LENGTH_SHORT);
                        //hint.show();

                        FileOutputStream fos = null;
                        try {
                            //寫入
                            fos = new FileOutputStream(environment_txt_time);
                            fos.write(e_time.getBytes());
                            fos.close();

                            check_time_for_text = 2;

                        } catch (IOException e) {
                            e.printStackTrace();
                            //hint = Toast.makeText(Splash_Screen.this, "錯誤",Toast.LENGTH_SHORT);
                            //hint.show();
                        }
                    }

                    //檢查完畢 或 不需要改動
                    if (check_time_for_text == 2) {
                        //Toast hint = Toast.makeText(Splash_Screen.this, "檢查完畢",Toast.LENGTH_SHORT);
                        //hint.show();

                        hint = Toast.makeText(Splash_Screen.this, e_time,Toast.LENGTH_SHORT);
                        hint.show();

                        Intent splash_to_main = new Intent();
                        splash_to_main.setClass(Splash_Screen.this, MainActivity.class);
                        startActivity(splash_to_main);
                        finish();

                    }

                    //測試數字，如果他有變動就是1
                    //Toast hint2 = Toast.makeText(Splash_Screen.this, check_time_for_text.getText() ,Toast.LENGTH_SHORT);
                    //hint2.show();
                }
                @Override
                public void onCancelled(DatabaseError error) { }
            });

        }

        //如果 device_txt_time(存在) 但 environment_txt_time(不存在)，跑這段

        if(device_txt_time.exists() && !environment_txt_time.exists()){

            FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
            DatabaseReference firebase_time_check = database_environment_name.getReference("environment").child("change_time");
            firebase_time_check.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    long environment_Time = 0;
                    try {
                        FileInputStream fis = new FileInputStream(device_txt_time);
                        byte[] b = new byte[1024];
                        int len = fis.read(b);
                        environment_Time = Long.parseLong(new String(b, 0, len));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast hint = Toast.makeText(Splash_Screen.this, environment_Time+"",Toast.LENGTH_SHORT);
                    //hint.show();

                    long change_time = dataSnapshot.getValue(long.class);
                    //改動他
                    if (change_time >= environment_Time) {
                        e_time = e_time + time_now;
                        check_time_for_text = 1;
                    }

                    //不改他
                    if (change_time < environment_Time) {
                        check_time_for_text = 2;
                    }

                    //需要改動
                    if (check_time_for_text == 1) {
                        //Toast hint = Toast.makeText(Splash_Screen.this, "他需要被變動",Toast.LENGTH_SHORT);
                        //hint.show();

                        FileOutputStream fos = null;
                        try {
                            //寫入
                            fos = new FileOutputStream(device_txt_time);
                            fos.write(e_time.getBytes());
                            fos.close();

                            check_time_for_text = 2;

                        } catch (IOException e) {
                            e.printStackTrace();
                            //hint = Toast.makeText(Splash_Screen.this, "錯誤",Toast.LENGTH_SHORT);
                            //hint.show();
                        }
                    }

                    //檢查完畢 或 不需要改動
                    if (check_time_for_text == 2) {
                        //Toast hint = Toast.makeText(Splash_Screen.this, "檢查完畢",Toast.LENGTH_SHORT);
                        //hint.show();

                        hint = Toast.makeText(Splash_Screen.this, e_time,Toast.LENGTH_SHORT);
                        hint.show();

                        Intent splash_to_main = new Intent();
                        splash_to_main.setClass(Splash_Screen.this, MainActivity.class);
                        startActivity(splash_to_main);
                        finish();

                    }

                    //測試數字，如果他有變動就是1
                    //Toast hint2 = Toast.makeText(Splash_Screen.this, check_time_for_text.getText() ,Toast.LENGTH_SHORT);
                    //hint2.show();
                }
                @Override
                public void onCancelled(DatabaseError error) { }
            });
        }

        //如果 device_txt_time(不存在) 但 environment_txt_time(存在)，跑這段

        if( !device_txt_time.exists() && environment_txt_time.exists()){

            FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
            DatabaseReference firebase_time_check = database_environment_name.getReference("environment").child("change_time");
            firebase_time_check.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    long environment_Time = 0;
                    try {
                        FileInputStream fis = new FileInputStream(device_txt_time);
                        byte[] b = new byte[1024];
                        int len = fis.read(b);
                        environment_Time = Long.parseLong(new String(b, 0, len));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast hint = Toast.makeText(Splash_Screen.this, environment_Time+"",Toast.LENGTH_SHORT);
                    //hint.show();

                    long change_time = dataSnapshot.getValue(long.class);
                    //改動他
                    if (change_time >= environment_Time) {
                        e_time = e_time + time_now;
                        check_time_for_text = 1;
                    }

                    //不改他
                    if (change_time < environment_Time) {
                        check_time_for_text = 2;
                    }

                    //需要改動
                    if (check_time_for_text == 1) {
                        //Toast hint = Toast.makeText(Splash_Screen.this, "他需要被變動",Toast.LENGTH_SHORT);
                        //hint.show();

                        FileOutputStream fos = null;
                        try {
                            //寫入
                            fos = new FileOutputStream(device_txt_time);
                            fos.write(e_time.getBytes());
                            fos.close();

                            check_time_for_text = 2;

                        } catch (IOException e) {
                            e.printStackTrace();
                            //hint = Toast.makeText(Splash_Screen.this, "錯誤",Toast.LENGTH_SHORT);
                            //hint.show();
                        }
                    }

                    //檢查完畢 或 不需要改動
                    if (check_time_for_text == 2) {
                        //Toast hint = Toast.makeText(Splash_Screen.this, "檢查完畢",Toast.LENGTH_SHORT);
                        //hint.show();

                        hint = Toast.makeText(Splash_Screen.this, e_time,Toast.LENGTH_SHORT);
                        hint.show();

                        Intent splash_to_main = new Intent();
                        splash_to_main.setClass(Splash_Screen.this, MainActivity.class);
                        startActivity(splash_to_main);
                        finish();

                    }

                    //測試數字，如果他有變動就是1
                    //Toast hint2 = Toast.makeText(Splash_Screen.this, check_time_for_text.getText() ,Toast.LENGTH_SHORT);
                    //hint2.show();
                }
                @Override
                public void onCancelled(DatabaseError error) { }
            });
        }

        //如果兩個檔案都不存在，跑這段
        if (!device_txt_time.exists() && !environment_txt_time.exists()) {
            try {
                device_txt_time.createNewFile();
                environment_txt_time.createNewFile();

                check_time_for_text = 1;
                d_time = d_time + time_now;
                e_time = e_time + time_now;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //測試數字，這邊不會被void onDataChange改動，所以...
        //Toast hint2 = Toast.makeText(Splash_Screen.this, check_time_for_text.getText() ,Toast.LENGTH_SHORT);
        //hint2.show();

        //如果它存在了，這段不會跑
        if(check_time_for_text == 1)
        {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(device_txt_time);
                //寫入
                fos.write(d_time.getBytes());
                //fos關閉(結尾時必要)
                fos.close();

                fos = new FileOutputStream(environment_txt_time);
                //寫入
                fos.write(e_time.getBytes());
                //fos關閉(結尾時必要)
                fos.close();

                check_time_for_text = 2;

            } catch (IOException e) {
                e.printStackTrace();
                //Toast hint = Toast.makeText(Splash_Screen.this, "錯誤",Toast.LENGTH_SHORT);
                //hint.show();
            }
        }

        Dictionary environment = new Hashtable(); //字典，環境
        environment.put("1","大型空間");
        environment.put("2","樂得兒產房");

        //Toast hint = Toast.makeText(Splash_Screen.this,"歡迎",Toast.LENGTH_SHORT);
        //Toast hint = Toast.makeText(Splash_Screen.this, (String) environment.get("e1"),Toast.LENGTH_SHORT);
        //hint.show();

        /*
        //這邊可以考慮，如果資料的內容不同時，如何處理
        //讀取
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int len = fis.read(b);
            String str2 = new String(b, 0, len);

            str2 = "大型空間 樂得兒產房";

            FileOutputStream fos = new FileOutputStream(file);
            //寫入
            fos.write(str2.toString().getBytes());
            //fos關閉(結尾時必要)
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
         */

        //檢查完畢後(2)，傳送
        if(check_time_for_text == 2){
            Intent splash_to_main = new Intent();
            splash_to_main.setClass(Splash_Screen.this,MainActivity.class);
            startActivity(splash_to_main);
            finish();
        }

    }
}