package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

public class MDW2 extends AppCompatActivity {

    Context context = MDW2.this;
    TextView des;
    Button BT_icon,BT_icon2,BT_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdw2);


        des = findViewById(R.id.des);

        //圖例
        BT_icon = findViewById(R.id.BT_icon);
        BT_icon.setOnClickListener(BT_icon_L);
        //圖例
        BT_icon2 = findViewById(R.id.BT_icon2);
        BT_icon2.setOnClickListener(BT_icon2_L);
        //回首頁的按鈕
        BT_home = findViewById(R.id.BT_home);
        BT_home.setOnClickListener(BT_home_L);

        dialog();
    }

    public class DrawView extends View {
        public Canvas mCanvas;

        public DrawView(MDW2 context) {
            super(context);
        }

        //最後依照擺法，設定成直角
        public void onDraw(Canvas canvas) {
            super.onDraw(mCanvas);

            Paint p = new Paint();    // 創建畫筆
            Paint rect = new Paint(); //畫方形的畫筆
            Paint circle = new Paint();
            Paint rect_door = new Paint(); //畫門的

            // 三角形繪圖
            p.setColor(Color.parseColor("#FBD9D9"));
            p.setTextSize(100);
            rect.setColor(Color.parseColor("#33FFA6")); //綠色
            rect.setTextSize(100);
            rect_door.setColor(Color.parseColor("#00C1FF"));
            rect_door.setTextSize(100);

            Path Room = new Path(); //畫長方
            Room.moveTo(240, 190); /*左上*/ Room.lineTo(760, 190); //右上
            Room.lineTo(760, 710);/*右下*/ Room.lineTo(240, 710);//左下
            Room.close(); // 使這些點構成封閉的多邊形
            canvas.drawPath(Room, rect);

            //規則有：
            // 0、(-1、-2、-3)、(-230、-232、-233)、(-130、-131、-133)
            // (-120、-121、-122)、(11、1、21、2、31、3、12、22、32)、66

            //rule的數字：代碼，如果有負，其一不在，三者不在為0
            //第一：為第幾個附近
            //第二：(如果為負)兩位數一起看，第幾和第幾之間
            //第二：(如果為正)如果相似，個位為1(等近)或2(等遠)，十位為主角
            //第三：三位數，同二，再加上靠近第幾(中間為0)
            //關於door，只是因為門的位置，改變了三角形的方向

            Intent intent = getIntent();
            Bundle bundle2 = intent.getExtras();

            String door_choice = bundle2.getString("door_choice");
            int door = Integer.parseInt(door_choice);

            if (door == 1) {//左門
                Path Door = new Path(); //畫長方門
                Door.moveTo(250, 700); /*左上*/ Door.lineTo(400, 700); //右上
                Door.lineTo(400, 730);/*右下*/  Door.lineTo(250, 730);//左下
                Door.close(); // 使這些點構成封閉的多邊形
                canvas.drawPath(Door, rect_door);

                Path path = new Path();

                //(0,0)左上、等腰直角三角
                int door1_triangle_1_x = 250,door1_triangle_2_x = 750; //左右
                int door1_triangle_1_y = 200,door1_triangle_2_y = 700; //上下

                // moveTo：此點為多邊形的起點
                path.moveTo(door1_triangle_1_x, door1_triangle_1_y); //1號
                path.lineTo(door1_triangle_2_x, door1_triangle_1_y); //2號
                path.lineTo(door1_triangle_2_x, door1_triangle_2_y); //3號
                path.close(); // 使這些點構成封閉的多邊形
                canvas.drawPath(path, p);

                p.setColor(Color.WHITE);							// 設置白色
                //建議：圖例
                p.setTextSize(45);
                canvas.drawText("1.門前牆角" ,150,100,p);
                canvas.drawText("2.門斜牆角" ,675,100,p);
                canvas.drawText("3.門平行牆角",650,800,p);

                circle.setAntiAlias(true);  circle.setColor(Color.RED);

                try {

                    File f_place_des = new File("/data/data/com.example.iot_ward_app_v3/files/place_des.txt");
                    FileReader fr_place_des = new FileReader(f_place_des);
                    char [] fc_place_des = new char [(int)f_place_des.length()];
                    fr_place_des.read(fc_place_des);
                    fr_place_des.close();

                    String text_place_des = new String(fc_place_des);
                    String text_place_des_split[] = text_place_des.split(" |\n");


                    for (int i = 0; i < text_place_des_split.length; i++) {

                        int rule = Integer.parseInt(text_place_des_split[i]);

                        //根據規則有不同的動作，cx,cy為圓的圓心位置
                        if (rule == 1) { //第一個esp近
                            canvas.drawCircle(door1_triangle_1_x + 100, door1_triangle_1_y + 100, 20, circle); //(350,300)
                        }
                        if (rule == 2) { //第二個esp近
                            canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_1_y + 100, 20, circle); //(650,300)
                        }
                        if (rule == 3) { //第三個esp近
                            canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_2_y - 100, 20, circle); //(650,600)
                        }
                        if (rule == 11) { //靠近第一個esp，23等距
                            canvas.drawCircle(door1_triangle_1_x + 100, door1_triangle_1_y + 250, 20, circle); //(300,450)
                        }
                        if (rule == 12) { //遠離第一個esp，23等距
                            canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_1_y + 250, 20, circle); //(300,450)
                        }
                        if (rule == 21) { //靠近第二個esp，13等距
                            canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_1_y + 150, 20, circle); //(650,350)
                        }
                        if (rule == 22) { //遠離第二個esp，13等距
                            canvas.drawCircle(door1_triangle_1_x + 150, door1_triangle_2_y - 150, 20, circle); //(400,550)
                        }
                        if (rule == 31) { //靠近第三個esp，12等距
                            canvas.drawCircle(door1_triangle_1_x + 250, door1_triangle_2_y - 150, 20, circle); //(500,550)
                        }
                        if (rule == 32) { //遠離第三個esp，12等距
                            canvas.drawCircle(door1_triangle_1_x + 250, door1_triangle_1_y + 150, 20, circle); //(500,350)
                        }
                        if (rule == 66) { //中心
                            canvas.drawCircle(door1_triangle_1_x + 250, door1_triangle_1_y + 250, 20, circle); //(500,450)
                        }
                        if (rule == 660 || rule == 29) { //門口
                            canvas.drawCircle(door1_triangle_1_x + 100, door1_triangle_1_y + 400, 20, circle); //(350,600)
                        }
                        if (rule == 661) { //門口esp32未啟動或設置，你要找的設備可能在門口或空間中心
                            canvas.drawCircle(door1_triangle_1_x + 250, door1_triangle_1_y + 250, 20, circle); //(500,450)
                            canvas.drawCircle(door1_triangle_1_x + 100, door1_triangle_1_y + 400, 20, circle); //(350,600)
                        }
                        if (rule == 30) { //門口，接近第三
                            canvas.drawCircle(door1_triangle_2_x - 325, door1_triangle_1_y + 400, 20, circle); //(425,600)
                        }
                        if (rule == 10) { //門口，接近第一
                            canvas.drawCircle(door1_triangle_2_x - 350, door1_triangle_1_y + 225, 20, circle); //(600,425)
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            if (door == 2) {//右門
                Path Door = new Path(); //畫長方門
                Door.moveTo(750, 700); /*左上*/ Door.lineTo(600, 700); //右上
                Door.lineTo(600, 730);/*右下*/  Door.lineTo(750, 730);//左下
                Door.close(); // 使這些點構成封閉的多邊形
                canvas.drawPath(Door, rect_door);

                Path path = new Path();
                //(0,0)左上、等腰直角三角

                int door2_triangle_1_x = 750,door2_triangle_2_x = 250; //右左
                int door2_triangle_1_y = 200,door2_triangle_2_y = 700; //上下

                path.moveTo(door2_triangle_1_x, door2_triangle_1_y);// 此點為多邊形的起點
                path.lineTo(door2_triangle_2_x, door2_triangle_1_y);
                path.lineTo(door2_triangle_2_x, door2_triangle_2_y);
                path.close(); // 使這些點構成封閉的多邊形
                canvas.drawPath(path, p);

                p.setColor(Color.WHITE);							// 設置白色
                //建議：圖例
                p.setTextSize(45);
                canvas.drawText("1.門前牆角" ,675,100,p);
                canvas.drawText("2.門斜牆角" ,150,100,p);
                canvas.drawText("3.門平行牆角",125,800,p);

                try {

                    File f_place_des = new File("/data/data/com.example.iot_ward_app_v3/files/place_des.txt");
                    FileReader fr_place_des = new FileReader(f_place_des);
                    char [] fc_place_des = new char [(int)f_place_des.length()];
                    fr_place_des.read(fc_place_des);
                    fr_place_des.close();

                    String text_place_des = new String(fc_place_des);
                    String text_place_des_split[] = text_place_des.split(" |\n");

                    for (int i = 0; i < text_place_des_split.length; i++) {

                        int rule = Integer.parseInt(text_place_des_split[i]);

                        //根據規則有不同的動作
                        circle.setAntiAlias(true);  circle.setColor(Color.RED);
                        //cx,cy為圓的圓心位置
                        if (rule == 1) { //第一個esp近
                            canvas.drawCircle(door2_triangle_1_x - 100, door2_triangle_1_y + 100, 20, circle);
                        }
                        if (rule == 2) { //第二個esp近
                            canvas.drawCircle(door2_triangle_2_x + 100, door2_triangle_1_y + 100, 20, circle);
                        }
                        if (rule == 3) { //第三個esp近
                            canvas.drawCircle(door2_triangle_2_x + 100, door2_triangle_2_y - 100, 20, circle);
                        }
                        if (rule == 11) { //靠近第一個esp，23等距
                            canvas.drawCircle(door2_triangle_1_x - 100, door2_triangle_1_y + 250, 20, circle);
                        }
                        if (rule == 12) { //遠離第一個esp，23等距
                            canvas.drawCircle(door2_triangle_2_x + 100, door2_triangle_1_y + 250, 20, circle);
                        }
                        if (rule == 21) { //靠近第二個esp，13等距
                            canvas.drawCircle(door2_triangle_2_x + 100, door2_triangle_1_y + 150, 20, circle);
                        }
                        if (rule == 22) { //遠離第二個esp，13等距
                            canvas.drawCircle(door2_triangle_1_x - 150, door2_triangle_2_y - 150, 20, circle);
                        }
                        if (rule == 31) { //靠近第三個esp，12等距
                            canvas.drawCircle(door2_triangle_1_x - 250, door2_triangle_2_y - 150, 20, circle);
                        }
                        if (rule == 32) { //遠離第三個esp，12等距
                            canvas.drawCircle(door2_triangle_1_x - 250, door2_triangle_1_y + 150, 20, circle);
                        }
                        if (rule == -120) { //沒有3，12等遠
                            canvas.drawCircle(door2_triangle_1_x - 250, door2_triangle_1_y - 100, 20, circle);
                            canvas.drawCircle(door2_triangle_1_x - 250, door2_triangle_1_y + 100, 20, circle);
                        }
                        if (rule == 66) { //中心
                            canvas.drawCircle(door2_triangle_2_x + 250, door2_triangle_1_y + 250, 20, circle); //(500,450)
                        }
                        if (rule == 660 || rule == 29) { //門口
                            canvas.drawCircle(door2_triangle_1_x - 100, door2_triangle_1_y + 400, 20, circle); //(650,600)
                        }
                        if (rule == 661) { //門口esp32未啟動或設置，你要找的設備可能在門口或空間中心
                            canvas.drawCircle(door2_triangle_2_x + 250, door2_triangle_1_y + 250, 20, circle); //(500,450)
                            canvas.drawCircle(door2_triangle_1_x - 100, door2_triangle_1_y + 400, 20, circle); //(650,600)
                        }
                        if (rule == 30) {
                            canvas.drawCircle(door2_triangle_2_x + 325, door2_triangle_1_y + 400, 20, circle); //(575,600)
                        }
                        if (rule == 10) { //門口，接近第一
                            canvas.drawCircle(door2_triangle_2_x + 350, door2_triangle_1_y + 225, 20, circle); //(600,425)
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }}

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

                            for (int j = 0; j < str_Dmultilist.length; j++) {

                                if (device_name.equals(str_Dmultilist[j]))
                                {

                                    device_chinese_name = str_Dmultiname[j];
                                    des_all = des_all + (device_name+"號："+ device_chinese_name +"\n("+ rssi_1 + ") " + "("+ rssi_2 + ") " + "("+ rssi_3 + ") \n" + "位置：" + place_speak + "\n\n");
                                }

                            }

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

                            for (int j = 0; j < str_Dmultilist.length; j++) {

                                if (device_name.equals(str_Dmultilist[j]))
                                {

                                    device_chinese_name = str_Dmultiname[j];
                                    des_all = des_all + (device_name+"號："+ device_chinese_name +"\n("+ rssi_1 + ") " + "("+ rssi_2 + ") " + "("+ rssi_3 + ") \n" + "位置：" + place_speak + "\n\n");
                                }

                            }
                        }

                        des.setText(des_all);

                        //des.setText(device_name+"號：("+ rssi_1 + ") " + "("+ rssi_2 + ") " + "("+ rssi_3 + ") \n");

                    }

                }



                LinearLayout layout=(LinearLayout) findViewById(R.id.draw_pic);
                DrawView view_multi =new DrawView(this);
                view_multi.setMinimumHeight(600);
                view_multi.setMinimumWidth(300);
                view_multi.invalidate();
                layout.addView(view_multi);

            }catch (Exception e){

                e.printStackTrace();
            }

            dialog.dismiss();

        }));

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    private  View.OnClickListener BT_icon_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {

                Intent intent = getIntent();
                Bundle bundle2 = intent.getExtras();
                String door_choice = bundle2.getString("door_choice");
                int door = Integer.parseInt(door_choice);

                if (door == 1){
                    ImageView iv = new ImageView(context);
                    iv.setImageResource(R.drawable.icon_left);
                    AlertDialog.Builder icon_check = new AlertDialog.Builder(MDW2.this);
                    icon_check.setTitle("顏色、圖例顯示");
                    icon_check.setIcon(R.drawable.logo4);
                    icon_check.setView(iv);
                    icon_check.setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
                if (door == 2){
                    ImageView iv = new ImageView(context);
                    iv.setImageResource(R.drawable.icon_right);
                    AlertDialog.Builder icon_check = new AlertDialog.Builder(MDW2.this);
                    icon_check.setTitle("顏色、圖例顯示");
                    icon_check.setIcon(R.drawable.logo4);
                    icon_check.setView(iv);
                    icon_check.setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }

            }catch (Exception error){
                Toast test = Toast.makeText(MDW2.this,"123",Toast.LENGTH_SHORT);
                test.show();
            }
        }
    };

    private  View.OnClickListener BT_icon2_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {

                    ImageView iv = new ImageView(context);
                    AlertDialog.Builder icon_check = new AlertDialog.Builder(MDW2.this);
                    icon_check.setTitle("描述說明");
                    icon_check.setMessage("每個結果從上到下依序為：\n號碼、RSSI、相對位置");
                    icon_check.setIcon(R.drawable.logo4);
                    icon_check.setView(iv);
                    icon_check.setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

            }catch (Exception error){
                Toast test = Toast.makeText(MDW2.this,"123",Toast.LENGTH_SHORT);
                test.show();
            }
        }
    };

    //回到首頁重新查詢
    private  View.OnClickListener BT_home_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MDW2.this,Multi_main.class);
            startActivity(intent);
            finish();

        }};

}