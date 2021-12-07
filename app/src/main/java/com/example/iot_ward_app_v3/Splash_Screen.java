package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

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

    //把資料預載放這
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        File file = new File(getFilesDir(), "environment.txt"); //文字檔
        if (!file.exists()) {
            try {
                file.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //時間檔案，
        //如果Database的內容被改的時間等.大於這個text的時間，改動他，並將這個text的時間更新
        //如果Database的內容被改的時間小於這個text的時間，不理他
        //如果沒有text檔案，那就時間判定為0
        File time_F = new File(getFilesDir(), "times.txt");
        if (!time_F.exists()) {
            try {
                time_F.createNewFile();


                long time_now = System.currentTimeMillis() / 1000;
                s_time = s_time + time_now;
                Toast hint = Toast.makeText(Splash_Screen.this,  s_time,Toast.LENGTH_SHORT);
                hint.show();

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    //寫入
                    fos.write(Integer.parseInt(String.valueOf(time_now)));
                    //fos關閉(結尾時必要)
                    fos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();

        Dictionary environment = new Hashtable(); //字典，環境
        environment.put("1","大型空間");
        environment.put("2","樂得兒產房");

        //Toast hint = Toast.makeText(Splash_Screen.this,"歡迎",Toast.LENGTH_SHORT);
        //Toast hint = Toast.makeText(Splash_Screen.this, (String) environment.get("e1"),Toast.LENGTH_SHORT);
        //hint.show();

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

        Intent splash_to_main = new Intent();
        splash_to_main.setClass(Splash_Screen.this,MainActivity.class);
        startActivity(splash_to_main);
        finish();

    }
}