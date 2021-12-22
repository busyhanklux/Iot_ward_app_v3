package com.example.iot_ward_app_v3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener; //OnClickListener的package
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView; //ImageView的package
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

public class change_page__environment extends AppCompatActivity {

    private Button BT_CPE_back;
    TextView Hint;

    String e_time = "";
    long environment_Time; //時間比較，減少傳輸浪費
    int environment_number; //確認有幾個環境，會影響迴圈次數

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_page__environment);

        File environment_txt_time  = new File(getFilesDir(), "environment_txt_time.txt");

        Hint = (TextView)findViewById(R.id.Hint);

        BT_CPE_back = findViewById(R.id.BT_CPE_back);
        BT_CPE_back.setOnClickListener(BT_CPE_back_L);

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

        FirebaseDatabase database_environment_name = FirebaseDatabase.getInstance();
        DatabaseReference firebase_number_check = database_environment_name.getReference("environment").child("number");
        firebase_number_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int number = dataSnapshot.getValue(int.class);
                environment_number = number;
                Hint.setText("目前資料庫含有 "+ number + " 個環境的設定");

            }
            @Override
            public void onCancelled(DatabaseError error) { }
        });

        Toast hint = Toast.makeText(change_page__environment.this, environment_number+"",Toast.LENGTH_SHORT);
        hint.show();

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
}
