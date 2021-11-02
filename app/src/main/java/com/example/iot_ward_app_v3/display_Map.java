package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class display_Map extends AppCompatActivity {

    int door,rule,door2;
    TextView door_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        Intent intent2 = this.getIntent();
        Bundle bundle2 = intent2.getExtras();
        int rssi_1 = bundle2.getInt("rssi_1");
        int rssi_2 = bundle2.getInt("rssi_2");
        int rssi_3 = bundle2.getInt("rssi_3");

        door = bundle2.getInt("door2");
        door_number = (TextView)findViewById(R.id.door_number);
        door_number.setText(String.valueOf(door));

        rule = bundle2.getInt("rule");
        Toast test = Toast.makeText(display_Map.this,door2+""+rule,Toast.LENGTH_SHORT);
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
        private int rule;

        public DrawView(display_Map context) {
            super(context);
        }

        public void onDraw(Canvas canvas) {
            super.onDraw(mCanvas);

            Paint p = new Paint();                            // 創建畫筆
            Paint rect = new Paint(); //畫方形的畫筆

            //canvas.drawText(String.valueOf(door), 50, 100, p);        // 寫文字

            // 三角形繪圖
            p.setColor(Color.parseColor("#FF8C00"));
            p.setTextSize(100);
            rect.setColor(Color.parseColor("#33FFA6"));
            rect.setTextSize(100);
            //canvas.drawText("三角形：",350,100,p);

            Path Room = new Path(); //畫長方
            Room.moveTo(190, 40);
            Room.lineTo(610, 40);
            Room.lineTo(610, 460);
            Room.lineTo(190, 460);
            Room.close(); // 使這些點構成封閉的多邊形
            canvas.drawPath(Room, rect);

            if (door == 1) {//左門

                Path path = new Path();
                //(0,0)左上、等腰直角三角 或 直角三角
                path.moveTo(200, 50);// 此點為多邊形的起點
                path.lineTo(600, 50);
                path.lineTo(600, 450);
                path.close(); // 使這些點構成封閉的多邊形
                canvas.drawPath(path, p);

                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(500, 750, 20, p);
            }
            if (door == 2) {//右門

                Path path = new Path();
                //(0,0)左上、等腰直角三角 或 直角三角
                path.moveTo(600, 50);// 此點為多邊形的起點
                path.lineTo(200, 50);
                path.lineTo(200, 450);
                path.close(); // 使這些點構成封閉的多邊形
                canvas.drawPath(path, p);

                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(700, 1050, 20, p);
            }

            //根據規則有不同的動作
            //cx,cy為圓的圓心位置
            if (rule == 1) {
                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(300, 100, 20, p);
            }
            if (rule == 2) {
                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(500, 100, 20, p);
            }
            if (rule == 3) {
                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(500, 300, 20, p);
            }
        }
    }
}
