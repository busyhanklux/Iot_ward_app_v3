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

public class display_Map extends AppCompatActivity {
    Context context = display_Map.this;

    int door,rule;
    int rssi_1,rssi_2,rssi_3,rssi_sup;
    Long check1,check2,check3;
    int select_number;
    String select_room,beacon_name,description;

    TextView door_number,text_third;
    Button BT_home,BT_back,BT_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        //回首頁的按鈕
        BT_home = findViewById(R.id.BT_home);
        BT_home.setOnClickListener(BT_home_L);
        //回上一頁的按鈕
        BT_back = findViewById(R.id.BT_back);
        BT_back.setOnClickListener(BT_back_L);
        //圖例
        BT_icon = findViewById(R.id.BT_icon);
        BT_icon.setOnClickListener(BT_icon_L);

        Intent intent2 = this.getIntent();
        Bundle bundle2 = intent2.getExtras();

        rssi_1 = bundle2.getInt("rssi_1");
        rssi_2 = bundle2.getInt("rssi_2");
        rssi_3 = bundle2.getInt("rssi_3");
        rssi_sup = bundle2.getInt("rssi_sup");
        check1 = bundle2.getLong("check_time1");
        check2 = bundle2.getLong("check_time2");
        check3 = bundle2.getLong("check_time3");

        Bundle bundle = new Bundle();

        bundle.putInt("rssi_1",rssi_1);
        bundle.putInt("rssi_2",rssi_2);
        bundle.putInt("rssi_3",rssi_3);
        bundle.putInt("rssi_sup",rssi_sup);
        bundle.putLong("check_time1",check1);
        bundle.putLong("check_time2",check2);
        bundle.putLong("check_time3",check3);

        select_number = bundle2.getInt("select_number");
        select_room = bundle2.getString("select_room");
        beacon_name = bundle2.getString("beacon_name");

        description = bundle2.getString("description");
        text_third = (TextView)findViewById(R.id.text_third);
        text_third.setText(String.valueOf(description));

        bundle.putInt("select_number",select_number);
        bundle.putString("select_room",select_room);
        bundle.putString("description",description);

        door = bundle2.getInt("door2");
        door_number = (TextView)findViewById(R.id.door_number);
        door_number.setText(String.valueOf(door));


        rule = bundle2.getInt("rule2");
        //rule = 10;

        LinearLayout layout=(LinearLayout) findViewById(R.id.draw_pic);
        DrawView view=new DrawView(this);
        view.setMinimumHeight(600);
        view.setMinimumWidth(300);
        view.invalidate();
        layout.addView(view);

    }

    public class DrawView extends View {
        public Canvas mCanvas;

        public DrawView(display_Map context) {
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
                p.setTextSize(40);
                canvas.drawText("1.門前牆角" ,150,100,p);
                canvas.drawText("2.門斜牆角" ,675,100,p);
                canvas.drawText("3.門平行牆角",650,800,p);

                circle.setAntiAlias(true);  circle.setColor(Color.RED);

                //根據規則有不同的動作，cx,cy為圓的圓心位置
                if (rule == -1) { //只有第一個esp被偵測
                    canvas.drawCircle(door1_triangle_1_x - 100, door1_triangle_1_y - 50, 20, circle); //(150,150)
                    canvas.drawCircle(door1_triangle_1_x + 100, door1_triangle_1_y + 50, 20, circle); //(350,250)
                }
                if (rule == -2) { //只有第二個esp被偵測
                    canvas.drawCircle(door1_triangle_2_x + 100, door1_triangle_1_y - 50, 20, circle); //(850,150)
                    canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_1_y + 50, 20, circle); //(650,250)
                }
                if (rule == -3) { //只有第三個esp被偵測
                    canvas.drawCircle(door1_triangle_2_x + 100, door1_triangle_2_y + 50, 20, circle); //(850,750)
                    canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_2_y - 50, 20, circle); //(650,650)
                }
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
                if (rule == -120) { //沒有3，12等遠
                    canvas.drawCircle(door1_triangle_1_x + 250, door1_triangle_1_y - 100, 20, circle); //(500,100)
                    canvas.drawCircle(door1_triangle_1_x + 250, door1_triangle_1_y + 100, 20, circle); //(500,300)
                }
                if (rule == -121) { //沒有3，1近
                    canvas.drawCircle(door1_triangle_1_x + 100, door1_triangle_1_y - 100, 20, circle); //(350,100)
                    canvas.drawCircle(door1_triangle_1_x + 100, door1_triangle_1_y + 100, 20, circle); //(350,300)
                }
                if (rule == -122) { //沒有3，2近
                    canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_1_y - 100, 20, circle); //(650,100)
                    canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_1_y + 100, 20, circle); //(650,300)
                }
                if (rule == -130) { //沒有2，13等遠
                    canvas.drawCircle(door1_triangle_1_x + 125, door1_triangle_2_y - 125, 20, circle); //(375,575)
                    canvas.drawCircle(door1_triangle_1_x - 125, door1_triangle_2_y + 125, 20, circle); //(125,825)
                }
                if (rule == -131) { //沒有2，1近
                    canvas.drawCircle(door1_triangle_1_x + 125, door1_triangle_2_y - 325, 20, circle); //(375,375)
                    canvas.drawCircle(door1_triangle_1_x - 125, door1_triangle_2_y - 175, 20, circle);  //(125,525)
                }
                if (rule == -133) { //沒有2，3近
                    canvas.drawCircle(door1_triangle_1_x + 325, door1_triangle_2_y - 125, 20, circle); //(575,575)
                    canvas.drawCircle(door1_triangle_1_x + 175, door1_triangle_2_y + 125, 20, circle); //(425,825)
                }
                if (rule == -230) { //沒有1，23等
                    canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_1_y + 250, 20, circle); //(650,450)
                    canvas.drawCircle(door1_triangle_2_x + 100, door1_triangle_1_y + 250, 20, circle); //(850,450)
                }
                if (rule == -232) { //沒有1，2近
                    canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_1_y + 100, 20, circle); //(650,300)
                    canvas.drawCircle(door1_triangle_2_x + 100, door1_triangle_1_y + 100, 20, circle); //(850,300)
                }
                if (rule == -233) { //沒有1，3近
                    canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_2_y - 100, 20, circle); //(650,600)
                    canvas.drawCircle(door1_triangle_2_x + 100, door1_triangle_2_y - 100, 20, circle); //(850,600)
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
                p.setTextSize(40);
                canvas.drawText("1.門前牆角" ,675,100,p);
                canvas.drawText("2.門斜牆角" ,150,100,p);
                canvas.drawText("3.門平行牆角",125,800,p);

                //根據規則有不同的動作
                circle.setAntiAlias(true);  circle.setColor(Color.RED);
                //cx,cy為圓的圓心位置
                if (rule == -1) { //只有第一個esp被偵測
                    canvas.drawCircle(door2_triangle_1_x + 100, door2_triangle_1_y - 50, 20, circle);
                    canvas.drawCircle(door2_triangle_1_x - 100, door2_triangle_1_y + 50, 20, circle);
                }
                if (rule == -2) { //只有第二個esp被偵測
                    canvas.drawCircle(door2_triangle_2_x - 100, door2_triangle_1_y - 50, 20, circle);
                    canvas.drawCircle(door2_triangle_2_x + 100, door2_triangle_1_y + 50, 20, circle);
                }
                if (rule == -3) { //只有第三個esp被偵測
                    canvas.drawCircle(door2_triangle_2_x - 100, door2_triangle_2_y + 50, 20, circle);
                    canvas.drawCircle(door2_triangle_2_x + 100, door2_triangle_2_y - 50, 20, circle);
                }
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
                if (rule == -121) { //沒有3，1近
                    canvas.drawCircle(door2_triangle_1_x - 100, door2_triangle_1_y - 100, 20, circle);
                    canvas.drawCircle(door2_triangle_1_x - 100, door2_triangle_1_y + 100, 20, circle);
                }
                if (rule == -122) { //沒有3，2近
                    canvas.drawCircle(door2_triangle_2_x + 100, door2_triangle_1_y - 100, 20, circle);
                    canvas.drawCircle(door2_triangle_2_x + 100, door2_triangle_1_y + 100, 20, circle);
                }
                if (rule == -130) { //沒有2，13等遠
                    canvas.drawCircle(door2_triangle_1_x - 125, door2_triangle_2_y - 125, 20, circle);
                    canvas.drawCircle(door2_triangle_1_x + 125, door2_triangle_2_y + 125, 20, circle);
                }
                if (rule == -131) { //沒有2，1近
                    canvas.drawCircle(door2_triangle_1_x - 125, door2_triangle_2_y - 325, 20, circle);
                    canvas.drawCircle(door2_triangle_1_x + 125, door2_triangle_2_y - 175, 20, circle);
                }
                if (rule == -133) { //沒有2，3近
                    canvas.drawCircle(door2_triangle_1_x - 325, door2_triangle_2_y - 125, 20, circle);
                    canvas.drawCircle(door2_triangle_1_x - 175, door2_triangle_2_y + 125, 20, circle);
                }
                if (rule == -230) { //沒有1，23等
                    canvas.drawCircle(door2_triangle_2_x - 100, door2_triangle_1_y + 250, 20, circle);
                    canvas.drawCircle(door2_triangle_2_x + 100, door2_triangle_1_y + 250, 20, circle);
                }
                if (rule == -232) { //沒有1，2近
                    canvas.drawCircle(door2_triangle_2_x - 100, door2_triangle_1_y + 100, 20, circle);
                    canvas.drawCircle(door2_triangle_2_x + 100, door2_triangle_1_y + 100, 20, circle);
                }
                if (rule == -233) { //沒有1，3近
                    canvas.drawCircle(door2_triangle_2_x - 100, door2_triangle_2_y - 100, 20, circle);
                    canvas.drawCircle(door2_triangle_2_x + 100, door2_triangle_2_y - 100, 20, circle);
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
                    canvas.drawCircle(door2_triangle_2_x + 350, door2_triangle_1_y + 400, 20, circle); //(500,450)
                }
                if (rule == 10) { //門口，接近第一
                    canvas.drawCircle(door2_triangle_2_x + 350, door2_triangle_1_y + 225, 20, circle); //(600,425)
                }
            }
        }}

    //回到首頁重新查詢
    private  View.OnClickListener BT_home_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(display_Map.this,MainActivity.class);
            startActivity(intent);
            finish();
        }};

    //回到上頁重新選擇
    private  View.OnClickListener BT_back_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent intent2 = new Intent();

                Bundle bundle = new Bundle();

                bundle.putInt("rssi_1",rssi_1);
                bundle.putInt("rssi_2",rssi_2);
                bundle.putInt("rssi_3",rssi_3);
                bundle.putInt("rssi_sup",rssi_sup);

                bundle.putLong("check_time1",check1);
                bundle.putLong("check_time2",check2);
                bundle.putLong("check_time3",check3);

                bundle.putInt("select_number",select_number);
                bundle.putString("select_room",select_room);
                bundle.putString("beacon_name",beacon_name);

                bundle.putInt("select_door",door);

                intent2.putExtras(bundle);
                intent2.setClass(display_Map.this,Map.class);
                startActivity(intent2);
                finish();

            }catch (Exception error){
                Toast test = Toast.makeText(display_Map.this,"未知錯誤",Toast.LENGTH_SHORT);
                test.show();
            }
        }
    };

    private  View.OnClickListener BT_icon_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (door == 1){
                    ImageView iv = new ImageView(context);
                    iv.setImageResource(R.drawable.icon_left);
                    AlertDialog.Builder icon_check = new AlertDialog.Builder(display_Map.this);
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
                    AlertDialog.Builder icon_check = new AlertDialog.Builder(display_Map.this);
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
                Toast test = Toast.makeText(display_Map.this,"123",Toast.LENGTH_SHORT);
                test.show();
            }
        }
    };
}
