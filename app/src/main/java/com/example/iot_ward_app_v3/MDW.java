package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;

public class MDW extends AppCompatActivity {

    TextView check;
    Button BT_Map_Open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdw);

        dialog();

        check = findViewById(R.id.check);
        BT_Map_Open = findViewById(R.id.BT_Map_Open);

        BT_Map_Open.setOnClickListener(BT_Map_Open_L);

    }

    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示訊息");  //設置標題
        builder.setIcon(R.mipmap.ic_launcher_round); //標題前面那個小圖示
        builder.setMessage("搜尋完成"); //提示訊息

        builder.setPositiveButton("確定",((dialog, which) -> {}));

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v -> {

            try {
                File f = new File("/data/data/com.example.iot_ward_app_v3/files/deal_number.txt");
                FileReader fr = new FileReader(f);
                char [] fc = new char [(int)f.length()];
                fr.read(fc);
                fr.close();

                String text = new String(fc);
                String text_split[] = text.split(" ");

                String text_display = new String();

                for (int i = 1; i < text_split.length; i++) {

                    text_display = text_display + "\n" + text_split[i] + "號";
                }

                //Toast txt = Toast.makeText(MDW.this,"以下是存在的號碼："+text_display+"", Toast.LENGTH_SHORT);
                //txt.show();

                //存號碼
                check.setText(text_display + "\n\n" + "按下「開啟地圖」，觀看它們在地圖上的位置\n\n");

                if (text_split.length == 1)
                {

                    text_display = "\n沒有符合條件的號碼";
                    check.setText(text_display + "\n\n" + "請回上頁搜尋其他環境，或是等待一段時間再搜尋\n\n");
                    BT_Map_Open.setText("回上頁重新搜尋");

                }

                dialog.dismiss();


            }catch (Exception e) {

            }

        }));

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


    }

    private View.OnClickListener BT_Map_Open_L = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            if (BT_Map_Open.getText().equals("回上頁重新搜尋"))
            {
                Intent intent = new Intent();

                intent.setClass(MDW.this, Multi_main.class);
                startActivity(intent);
                finish();

            }else{

            }
        }
    };

}

