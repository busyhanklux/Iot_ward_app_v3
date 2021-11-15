package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class display_Map extends AppCompatActivity {

    int door,rule,door2;
    int rssi_1,rssi_2,rssi_3;
    Long check1,check2,check3;
    int select_number;
    String select_room;

    TextView door_number;
    Button BT_home,BT_back;
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

        Intent intent2 = this.getIntent();
        Bundle bundle2 = intent2.getExtras();

        rssi_1 = bundle2.getInt("rssi_1");
        rssi_2 = bundle2.getInt("rssi_2");
        rssi_3 = bundle2.getInt("rssi_3");
        check1 = bundle2.getLong("check_time1");
        check2 = bundle2.getLong("check_time2");
        check3 = bundle2.getLong("check_time3");

        Bundle bundle = new Bundle();

        bundle.putInt("rssi_1",rssi_1);
        bundle.putInt("rssi_2",rssi_2);
        bundle.putInt("rssi_3",rssi_3);
        bundle.putLong("check_time1",check1);
        bundle.putLong("check_time2",check2);
        bundle.putLong("check_time3",check3);

        select_number = bundle2.getInt("select_number");
        select_room = bundle2.getString("select_room");
        bundle.putInt("select_number",select_number);
        bundle.putString("select_room",select_room);

        Toast test = Toast.makeText(display_Map.this,select_number+"嘎啦嘎拉"+select_room+"嘎啦嘎拉"+check3,Toast.LENGTH_SHORT);
        test.show();
        //intent2.putExtras(bundle);

        door = bundle2.getInt("door2");
        door_number = (TextView)findViewById(R.id.door_number);
        door_number.setText(String.valueOf(door));

        rule = bundle2.getInt("rule2");
        //Toast test = Toast.makeText(display_Map.this,door+"_1145_"+rule,Toast.LENGTH_SHORT);
        //test.show();

        LinearLayout layout=(LinearLayout) findViewById(R.id.draw_pic);
        DrawView view=new DrawView(this);
        view.setMinimumHeight(500);
        view.setMinimumWidth(300);
        view.invalidate();
        layout.addView(view);

    }

    public class DrawView extends View {
        public Paint mPaint;
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

            //canvas.drawText("String.valueOf(door)", 50, 100, p);        // 寫文字

            // 三角形繪圖
            p.setColor(Color.parseColor("#FF8C00"));
            p.setTextSize(100);
            rect.setColor(Color.parseColor("#33FFA6"));
            rect.setTextSize(100);
            //canvas.drawText("三角形：",350,100,p);

            Path Room = new Path(); //畫長方
            Room.moveTo(190, 190); /*左上*/ Room.lineTo(610, 190); //右上
            Room.lineTo(610, 610);/*右下*/ Room.lineTo(190, 610);//左下
            Room.close(); // 使這些點構成封閉的多邊形
            canvas.drawPath(Room, rect);

            //規則有：
            // 0、(-1、-2、-3)、(-230、-232、-233)、(-130、-131、-133)
            // (-120、-121、-122)、(11、1、21、2、31、3)
            // (12、22、32)
            //rule的數字：代碼，如果有負，其一不在，三者不在為0
            //第一：為第幾個附近
            //第二：(如果為負)兩位數一起看，第幾和第幾之間
            //第二：(如果為正)如果相似，個位為1(等近)或2(等遠)，十位為主角
            //第三：三位數，同二，再加上靠近第幾(中間為0)
            //關於door1、door2，只是因為門的位置，改變了三角形的方向

            if (door == 1) {//左門
                Path path = new Path();

                //(0,0)左上、等腰直角三角 或 直角三角
                //door1_triangle_1_x => 左 x(200) , door1_triangle_2_x => 右 x(600)
                int door1_triangle_1_x = 200,door1_triangle_2_x = 600;
                //door1_triangle_1_y => 上 y(100) , door1_triangle_2_y => 下 y(500)
                int door1_triangle_1_y = 200,door1_triangle_2_y = 600;
                // moveTo：此點為多邊形的起點
                path.moveTo(door1_triangle_1_x, door1_triangle_1_y); //1號
                path.lineTo(door1_triangle_2_x, door1_triangle_1_y); //2號
                path.lineTo(door1_triangle_2_x, door1_triangle_2_y); //3號
                path.close(); // 使這些點構成封閉的多邊形
                canvas.drawPath(path, p);

                //根據規則有不同的動作，cx,cy為圓的圓心位置
                if (rule == -1) { //只有第一個esp
                    circle.setAntiAlias(true);  circle.setColor(Color.RED);
                    canvas.drawCircle(door1_triangle_1_x - 100, door1_triangle_1_y - 50, 20, circle); //-100 -50
                    //canvas.drawCircle(200 - 100, 100 - 50, 20, circle); //-100 -50
                }
                if (rule == -2) { //只有第二個esp
                    circle.setAntiAlias(true);  circle.setColor(Color.RED);
                    canvas.drawCircle(door1_triangle_2_x + 100, door1_triangle_1_y - 100, 20, circle); //+100 -100
                    //canvas.drawCircle(600 + 100, 100 - 100, 20, circle); //+100 -100
                }
                if (rule == -3) { //只有第三個esp
                    circle.setAntiAlias(true);  circle.setColor(Color.RED);
                    canvas.drawCircle(door1_triangle_2_x + 100, door1_triangle_2_y + 50, 20, circle); //+100 +50
                    //canvas.drawCircle(600 + 100, 500 + 50, 20, circle); //+100 +50
                }
                if (rule == 1) { //第一個esp近
                    circle.setAntiAlias(true);  circle.setColor(Color.RED);
                    canvas.drawCircle(door1_triangle_1_x + 100, door1_triangle_1_y + 50, 20, circle); //+100 +50
                    //canvas.drawCircle(200 + 100, 100 + 50, 20, circle); //+100 +50
                }
                if (rule == 2) { //第二個esp近
                    circle.setAntiAlias(true);  circle.setColor(Color.RED);
                    canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_1_y + 100, 20, circle); //-100 +100
                    //canvas.drawCircle(600 - 100, 100 + 100, 20, circle); //-100 +100
                }
                if (rule == 3) { //第三個esp近
                    circle.setAntiAlias(true);  circle.setColor(Color.RED);
                    canvas.drawCircle(door1_triangle_2_x - 100, door1_triangle_2_y - 50, 20, circle); //-100 -50
                    //canvas.drawCircle(600 - 100, 500-50, 20, circle); //-100 -50
                }
            }

            if (door == 2) {//右門

                Path path = new Path();
                //(0,0)左上、等腰直角三角 或 直角三角
                //door2_triangle_1_x => 右 x(600) , door2_triangle_2_x => 左 x(200)
                int door2_triangle_1_x = 600,door2_triangle_2_x = 200;
                //door2_triangle_1_y => 上 y(100) , door2_triangle_2_y => 下 y(500)
                int door2_triangle_1_y = 200,door2_triangle_2_y = 600;
                path.moveTo(door2_triangle_1_x, door2_triangle_1_y);// 此點為多邊形的起點
                path.lineTo(door2_triangle_2_x, door2_triangle_1_y);
                path.lineTo(door2_triangle_2_x, door2_triangle_2_y);
                path.close(); // 使這些點構成封閉的多邊形
                canvas.drawPath(path, p);

                //根據規則有不同的動作
                //cx,cy為圓的圓心位置
                if (rule == -1) { //只有第一個esp
                    circle.setAntiAlias(true);  circle.setColor(Color.RED);
                    canvas.drawCircle(door2_triangle_1_x +100, door2_triangle_1_y -50, 20, circle); //+100 -50
                    //canvas.drawCircle(600+100, 100-50, 20, circle); //+100 -50
                }
                if (rule == -2) { //只有第二個esp
                    circle.setAntiAlias(true);  circle.setColor(Color.RED);
                    canvas.drawCircle(door2_triangle_2_x -100, door2_triangle_1_y -100, 20, circle); //-100 -100
                    //canvas.drawCircle(200-100, 100-100, 20, circle); //-100 -100
                }
                if (rule == -3) { //只有第三個esp
                    circle.setAntiAlias(true);  circle.setColor(Color.RED);
                    canvas.drawCircle(door2_triangle_2_x -100, door2_triangle_2_y +50, 20, circle); //-100 +50
                    //canvas.drawCircle(200-100, 500+50, 20, circle); //-100 +50
                }
                if (rule == 1) {
                    circle.setAntiAlias(true);  circle.setColor(Color.RED);
                    canvas.drawCircle(door2_triangle_1_x -100, door2_triangle_1_y +50, 20, circle); //-100 +50
                    //canvas.drawCircle(600-100, 100+50, 20, circle); //-100 +50
                }
                if (rule == 2) {
                    circle.setAntiAlias(true);  circle.setColor(Color.RED);
                    canvas.drawCircle(door2_triangle_2_x +100, door2_triangle_1_y +100, 20, circle); //+100 +100
                    //canvas.drawCircle(200+100, 100+100, 20, circle); //+100 +100
                }
                if (rule == 3) {
                    circle.setAntiAlias(true);  circle.setColor(Color.RED);
                    canvas.drawCircle(door2_triangle_2_x +100, door2_triangle_2_y -50, 20, circle); //+100 -50
                    //canvas.drawCircle(200+100, 500-50, 20, circle); //+100 -50
                }
            }


        }
    }

    //回到首頁重新查詢
    private  View.OnClickListener BT_home_L = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(display_Map.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    //回到上頁重新選擇
    private  View.OnClickListener BT_back_L = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            //try {
                Intent intent2 = new Intent();

                Bundle bundle = new Bundle();
//                Bundle bundle2 = intent2.getExtras();


                bundle.putInt("rssi_1",rssi_1);
                bundle.putInt("rssi_2",rssi_2);
                bundle.putInt("rssi_3",rssi_3);
                bundle.putLong("check_time1",check1);
                bundle.putLong("check_time2",check2);
                bundle.putLong("check_time3",check3);

                Toast test = Toast.makeText(display_Map.this,check1+"T"+check2+"T"+check3,Toast.LENGTH_SHORT);
                //Toast test = Toast.makeText(display_Map.this,rssi_1+"T"+rssi_2+"T"+rssi_3,Toast.LENGTH_SHORT);
                //Toast test = Toast.makeText(display_Map.this,"沒問題",Toast.LENGTH_SHORT);
                test.show();

                //int select_number = bundle2.getInt("select_number");
                //String select_room = bundle2.getString("select_room");
                bundle.putInt("select_number",select_number);
                bundle.putString("select_room",select_room);

                intent2.putExtras(bundle);
                intent2.setClass(display_Map.this,Map.class);
                startActivity(intent2);
                finish();
                /*
            }catch (Exception error){
                Toast test = Toast.makeText(display_Map.this,"未知錯誤",Toast.LENGTH_SHORT);
                test.show();
            }*/

        }
    };
}
