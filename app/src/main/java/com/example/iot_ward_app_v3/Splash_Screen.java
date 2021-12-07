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

    String s_time = "";
    int check_time_for_text_number;
    long time_now = System.currentTimeMillis() / 1000;

    TextView check_time_for_text;

    //把資料預載放這
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        check_time_for_text = findViewById(R.id.time_check);
        check_time_for_text_number = Integer.parseInt((String) check_time_for_text.getText());

        /*
        File file = new File(getFilesDir(), "environment.txt"); //文字檔
        if (!file.exists()) {
            try {
                file.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */

        //時間檔案，
        //如果Database的內容被改的時間等.大於這個text的時間，改動他，並將這個text的時間更新 => 檢測變動的參數變 1
        //如果Database的內容被改的時間小於這個text的時間，不理他
        //如果沒有text檔案，那就將text時間判定為0
        File time_F = new File(getFilesDir(), "times.txt");
        if (!time_F.exists()) {
            try {
                time_F.createNewFile();
                check_time_for_text.setText("1");
                s_time = s_time + time_now;
                Toast hint = Toast.makeText(Splash_Screen.this, s_time,Toast.LENGTH_SHORT);
                hint.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
        DatabaseReference firebase_time_check = database_environment_name.getReference("environment").child("change_time");
        firebase_time_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long change_time = dataSnapshot.getValue(long.class);
                //改動他
                if(change_time >= time_now){
                    Toast hint = Toast.makeText(Splash_Screen.this, "變動",Toast.LENGTH_SHORT);
                    hint.show();
                    s_time = s_time + time_now;
                    check_time_for_text.setText("1");

                }
                //不改他
                if(change_time < time_now){
                    Toast hint = Toast.makeText(Splash_Screen.this, "沒有變動",Toast.LENGTH_SHORT);
                    hint.show();
                    check_time_for_text.setText("2");

                }
            }
            @Override
            public void onCancelled(DatabaseError error) { }
            });

        Toast hint2 = Toast.makeText(Splash_Screen.this, check_time_for_text.getText() ,Toast.LENGTH_SHORT);
        hint2.show();

        if(check_time_for_text.getText() == "1")
        {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(time_F);
                //寫入
                fos.write(s_time.getBytes());
                //fos關閉(結尾時必要)
                fos.close();
                check_time_for_text.setText("2");

            } catch (IOException e) {
                e.printStackTrace();
                Toast hint = Toast.makeText(Splash_Screen.this, "錯誤",Toast.LENGTH_SHORT);
                hint.show();
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

        //檢查完畢後(全部為0)，傳送
        if(check_time_for_text.getText() == "2"){
            Intent splash_to_main = new Intent();
            splash_to_main.setClass(Splash_Screen.this,MainActivity.class);
            startActivity(splash_to_main);
            finish();
        }

    }
}