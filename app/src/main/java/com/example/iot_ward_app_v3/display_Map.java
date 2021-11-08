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
    TextView door_number;
    Button BT_home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        BT_home = findViewById(R.id.BT_home);
        BT_home.setOnClickListener(BT_home_L);

        Intent intent2 = this.getIntent();
        Bundle bundle2 = intent2.getExtras();
        int rssi_1 = bundle2.getInt("rssi_1");
        int rssi_2 = bundle2.getInt("rssi_2");
        int rssi_3 = bundle2.getInt("rssi_3");

        door = bundle2.getInt("door2");
        door_number = (TextView)findViewById(R.id.door_number);
        door_number.setText(String.valueOf(door));

        rule = bundle2.getInt("rule2");
        Toast test = Toast.makeText(display_Map.this,door+""+rule,Toast.LENGTH_SHORT);
        test.show();

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

        public void onDraw(Canvas canvas) {
            super.onDraw(mCanvas);

            Paint p = new Paint();                            // 創建畫筆
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
            Room.moveTo(190, 90);//左上
            Room.lineTo(610, 90);//右上
            Room.lineTo(610, 510);//右下
            Room.lineTo(190, 510);//左下
            Room.close(); // 使這些點構成封閉的多邊形
            canvas.drawPath(Room, rect);

            if (door == 1) {//左門

                Path path = new Path();
                //(0,0)左上、等腰直角三角 或 直角三角
                // 此點為多邊形的起點
                path.moveTo(200, 100); //1號
                path.lineTo(600, 100); //2號
                path.lineTo(600, 500);//3號
                path.close(); // 使這些點構成封閉的多邊形
                canvas.drawPath(path, p);

                //根據規則有不同的動作
                //cx,cy為圓的圓心位置
                if (rule == -1) { //只有第一個esp
                    circle.setAntiAlias(true);
                    circle.setColor(Color.RED);
                    canvas.drawCircle(200-100, 100-50, 20, circle); //-100 -50
                }
                if (rule == -2) { //只有第二個esp
                    p.setAntiAlias(true);
                    p.setColor(Color.RED);
                    canvas.drawCircle(600+100, 100-100, 20, circle); //+100 -100
                }
                if (rule == -3) { //只有第三個esp
                    p.setAntiAlias(true);
                    p.setColor(Color.RED);
                    canvas.drawCircle(600+100, 500+50, 20, circle); //+100 +50
                }
                if (rule == 1) { //第一個esp近
                    circle.setAntiAlias(true);
                    circle.setColor(Color.RED);
                    canvas.drawCircle(200+100, 100+50, 20, circle); //+100 +50
                }
                if (rule == 2) { //第二個esp近
                    p.setAntiAlias(true);
                    p.setColor(Color.RED);
                    canvas.drawCircle(600-100, 100+100, 20, circle); //-100 +100
                }
                if (rule == 3) { //第三個esp近
                    p.setAntiAlias(true);
                    p.setColor(Color.RED);
                    canvas.drawCircle(600-100, 500-50, 20, circle); //-100 -50
                }
            }

            if (door == 2) {//右門

                Path path = new Path();
                //(0,0)左上、等腰直角三角 或 直角三角
                path.moveTo(600, 100);// 此點為多邊形的起點
                path.lineTo(200, 100);
                path.lineTo(200, 500);
                path.close(); // 使這些點構成封閉的多邊形
                canvas.drawPath(path, p);

                //根據規則有不同的動作
                //cx,cy為圓的圓心位置
                if (rule == -1) { //只有第一個esp
                    circle.setAntiAlias(true);
                    circle.setColor(Color.RED);
                    canvas.drawCircle(600+100, 100-50, 20, circle); //+100 -50
                }
                if (rule == -2) { //只有第二個esp
                    p.setAntiAlias(true);
                    p.setColor(Color.RED);
                    canvas.drawCircle(200-100, 100-100, 20, circle); //-100 -100
                }
                if (rule == -3) { //只有第三個esp
                    p.setAntiAlias(true);
                    p.setColor(Color.RED);
                    canvas.drawCircle(200-100, 500+50, 20, circle); //-100 +50
                }
                if (rule == 1) {
                    circle.setAntiAlias(true);
                    circle.setColor(Color.RED);
                    canvas.drawCircle(600-100, 100+50, 20, circle); //-100 +50
                }
                if (rule == 2) {
                    circle.setAntiAlias(true);
                    circle.setColor(Color.RED);
                    canvas.drawCircle(200+100, 100+100, 20, circle); //+100 +100
                }
                if (rule == 3) {
                    circle.setAntiAlias(true);
                    circle.setColor(Color.RED);
                    canvas.drawCircle(200+100, 500-50, 20, circle); //+100 -50
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
}
