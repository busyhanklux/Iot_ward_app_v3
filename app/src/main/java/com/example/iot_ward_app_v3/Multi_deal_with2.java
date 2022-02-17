package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

public class Multi_deal_with2 extends AppCompatActivity {

    int rule, door, select_number, room_choice, sup_adjust;
    int point_decide,count = 0; //有幾個點的資料
    String select_room, beacon_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_deal_with2);

        Intent intent2 = this.getIntent();
        Bundle bundle2 = intent2.getExtras();

        //你選擇的房間
        room_choice = bundle2.getInt("room_choice");
        sup_adjust = bundle2.getInt("sup_adjust");

        //Toast txt = Toast.makeText(Multi_deal_with2.this,"room_choice："+room_choice+"，sup_adjust："+ sup_adjust, Toast.LENGTH_SHORT);
        //txt.show();

        try {

            Toast txt = Toast.makeText(Multi_deal_with2.this,"Part1", Toast.LENGTH_SHORT);
            txt.show();

            /*
            File deal_number = new File(getFilesDir(), "deal_number.txt");   //待處理的序號

            int length = (int) deal_number.length();
            byte[] buff = new byte[length];

            FileInputStream fileInput = new FileInputStream(deal_number);
            fileInput.read(buff);

            String result = new String(buff, "UTF-8");

            txt = Toast.makeText(Multi_deal_with2.this,result+"", Toast.LENGTH_SHORT);
            txt.show();

            fileInput.close();
            */

            /*FileReader fr = new FileReader("/data/data/com.example.iot_ward_app_v3/files/deal_number.txt");
            //將BufferedReader與FileReader做連結

            BufferedReader br = new BufferedReader(fr);

            String readData = "";

            String temp = br.readLine(); //readLine()讀取一整行
            while (temp!=null) {
                readData += temp;
                temp = br.readLine();
            }

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, readData, duration);
            toast.show();*/

            /*
            File f = new File("/data/data/com.example.iot_ward_app_v3/files/deal_number.txt");
            FileReader fr = new FileReader(f);
            char [] fc = new char [(int)f.length()];
            fr.read(fc);
            fr.close();

            String s = new String(fc);

            txt = Toast.makeText(Multi_deal_with2.this,s+"", Toast.LENGTH_SHORT);
            txt.show();

             */

        }catch (Exception e) {

            e.printStackTrace();

            Toast txt = Toast.makeText(Multi_deal_with2.this,"錯誤", Toast.LENGTH_SHORT);
            txt.show();


        }

    }
}