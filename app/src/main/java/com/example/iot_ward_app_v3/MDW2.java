package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

public class MDW2 extends AppCompatActivity {

    TextView des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdw2);


        des = findViewById(R.id.des);

        dialog();
    }

    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示訊息");  //設置標題
        builder.setIcon(R.drawable.logo4); //標題前面那個小圖示
        builder.setMessage("跳轉完成"); //提示訊息

        builder.setPositiveButton("確定",((dialog, which) -> {}));

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v -> {

            try {

                Intent intent = getIntent();
                Bundle bundle2 = intent.getExtras();

                int sup_adjust = bundle2.getInt("sup_adjust");
                int room_choice = bundle2.getInt("room_choice");

                Toast txt = Toast.makeText(MDW2.this,"sup_adjust："+sup_adjust+"room_choice："+ room_choice, Toast.LENGTH_SHORT);
                txt.show();

                //RSSI的資料

                File f_RSSI = new File("/data/data/com.example.iot_ward_app_v3/files/RSSI_inf.txt");
                FileReader fr_RSSI = new FileReader(f_RSSI);
                char [] fc_RSSI = new char [(int)f_RSSI.length()];
                fr_RSSI.read(fc_RSSI);
                fr_RSSI.close();

                String text = new String(fc_RSSI);
                String text_split[] = text.split(" |\n");
                String text_display = new String();

                //place_des的資料

                File f_place_des = new File("/data/data/com.example.iot_ward_app_v3/files/place_des.txt");
                FileReader fr_place_des = new FileReader(f_place_des);
                char [] fc_place_des = new char [(int)f_place_des.length()];
                fr_place_des.read(fc_place_des);
                fr_place_des.close();

                String text_place_des = new String(fc_place_des);
                String text_place_des_split[] = text_place_des.split(" |\n");
                String text_place_des_display = new String();
                int display_count = 0;

                File device_txt_list = new File(getFilesDir(), "device_list.txt");   //設備的數字代碼
                FileInputStream fis_Dlist = new FileInputStream(device_txt_list);

                File device_txt_name = new File(getFilesDir(), "device_name.txt");   //設備名稱
                FileInputStream fis_Dname = new FileInputStream(device_txt_name);

                byte[] D_name = new byte[100000];
                int len_Dname = fis_Dname.read(D_name);
                String str_Dname = new String(D_name , 0, len_Dname);
                String str_Dmultiname [] = str_Dname.split(" ");

                byte[] D_list = new byte[1024];
                int len_Dlist = fis_Dlist.read(D_list);
                String str_Dlist = new String(D_list , 0, len_Dlist);
                String str_Dmultilist [] = str_Dlist.split(" ");

                int rssi_1 = 0,rssi_2 = 0,rssi_3 = 0,rssi_sup = 0;

                String device_name;
                String device_chinese_name = "";
                String des_all = "";
                String place_speak = "";

                for (int i = 0; i < text_split.length; i++) {

                    if (sup_adjust == 0)
                    {

                        if((i % 4) == 0)
                        {
                            device_name = text_split[i];

                            for (int j = 0; j < str_Dmultilist.length; j++) {

                                if (device_name.equals(str_Dmultilist[j]))
                                {

                                    device_chinese_name = str_Dmultiname[j];
                                }

                            }

                            rssi_1 = Integer.parseInt(text_split[i+1]);
                            rssi_2 = Integer.parseInt(text_split[i+2]);
                            rssi_3 = Integer.parseInt(text_split[i+3]);

                            place_speak = text_place_des_split[display_count];

                            switch (place_speak)
                            {
                                case "1":
                                    place_speak = "門口前方牆角(第一個esp)";
                                    break;
                                case "11":
                                    place_speak = "門口前方牆角(第一個esp)，門口斜對牆角(第二個esp)或門口平行牆角(第三個esp)的距離相似";
                                    break;
                                case "2":
                                    place_speak = "門口斜對牆角(第二個esp)";
                                    break;
                                case "21":
                                    place_speak = "門口斜對牆角(第二個esp)，門口前方牆角(第一個esp)或門口平行牆角(第三個esp)的距離相似";
                                    break;
                                case "3":
                                    place_speak = "門口平行牆角(第三個esp)";
                                    break;
                                case "31":
                                    place_speak = "門口平行牆角(第三個esp)，門口前方牆角(第一個esp)或門口斜對牆角(第二個esp)的距離相似";
                                    break;
                                case "12":
                                    place_speak = "遠離門口前方牆角(第一個esp)，門口斜對牆角(第二個esp)或門口平行牆角(第三個esp)的距離相似";
                                    break;
                                case "22":
                                    place_speak = "遠離門口斜對牆角(第二個esp)，門口前方牆角(第一個esp)或門口平行牆角(第三個esp)的距離相似";
                                    break;
                                case "32":
                                    place_speak = "遠離門口平行牆角(第三個esp)，門口前方牆角(第一個esp)或門口斜對牆角(第二個esp)的距離相似";
                                    break;
                                case "661":
                                    place_speak = "可能在門口或空間中心";
                                    break;

                            }

                            display_count++;

                            des_all = des_all + (device_name+"號："+ device_chinese_name +"\n("+ rssi_1 + ") " + "("+ rssi_2 + ") " + "("+ rssi_3 + ") \n" + "位置：" + place_speak + "\n");


                        }

                        des.setText(des_all);

                        //des.setText(device_name+"號：("+ rssi_1 + ") " + "("+ rssi_2 + ") " + "("+ rssi_3 + ") \n");
                    }

                    if (sup_adjust == 1)
                    {

                        if((i % 5) == 0)
                        {
                            device_name = text_split[i];
                            rssi_1 = Integer.parseInt(text_split[i+1]);
                            rssi_2 = Integer.parseInt(text_split[i+2]);
                            rssi_3 = Integer.parseInt(text_split[i+3]);

                            place_speak = text_place_des_split[display_count];

                            switch (place_speak)
                            {
                                case "1":
                                    place_speak = "門口前方牆角(第一個esp)";
                                    break;
                                case "10":
                                    place_speak = "靠近門口，稍微接近門口前方牆角(第一個esp)";
                                    break;
                                case "11":
                                    place_speak = "門口前方牆角(第一個esp)，門口斜對牆角(第二個esp)或門口平行牆角(第三個esp)的距離相似";
                                    break;
                                case "2":
                                    place_speak = "門口斜對牆角(第二個esp)";
                                    break;
                                case "21":
                                    place_speak = "門口斜對牆角(第二個esp)，門口前方牆角(第一個esp)或門口平行牆角(第三個esp)的距離相似";
                                    break;
                                case "3":
                                    place_speak = "門口平行牆角(第三個esp)";
                                    break;
                                case "30":
                                    place_speak = "靠近門口，稍微接近門口平行牆角(第三個esp)";
                                    break;
                                case "31":
                                    place_speak = "門口平行牆角(第三個esp)，門口前方牆角(第一個esp)或門口斜對牆角(第二個esp)的距離相似";
                                    break;
                                case "12":
                                    place_speak = "遠離門口前方牆角(第一個esp)，門口斜對牆角(第二個esp)或門口平行牆角(第三個esp)的距離相似";
                                    break;
                                case "22":
                                    place_speak = "遠離門口斜對牆角(第二個esp)，門口前方牆角(第一個esp)或門口平行牆角(第三個esp)的距離相似";
                                    break;
                                case "29":
                                    place_speak = "可能靠近門口";
                                    break;
                                case "32":
                                    place_speak = "遠離門口平行牆角(第三個esp)，門口前方牆角(第一個esp)或門口斜對牆角(第二個esp)的距離相似";
                                    break;
                                case "66":
                                    place_speak = "可能位於該空間的中心";
                                    break;
                                case "660":
                                    place_speak = "可能靠近門口";
                                    break;
                                case "661":
                                    place_speak = "可能在門口或空間中心";
                                    break;

                            }

                            display_count++;

                            des_all = des_all + (device_name+"號："+ device_chinese_name +"\n("+ rssi_1 + ") " + "("+ rssi_2 + ") " + "("+ rssi_3 + ") \n" + "位置：" + place_speak + "\n\n");

                        }

                        des.setText(des_all);

                        //des.setText(device_name+"號：("+ rssi_1 + ") " + "("+ rssi_2 + ") " + "("+ rssi_3 + ") \n");
                    }

                }

            }catch (Exception e){

                e.printStackTrace();
            }

            dialog.dismiss();

        }));

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }
}