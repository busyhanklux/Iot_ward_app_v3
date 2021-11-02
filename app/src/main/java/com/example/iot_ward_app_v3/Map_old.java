package com.example.iot_ward_app_v3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Map_old extends AppCompatActivity {

    //畫板參考https://lowren.pixnet.net/blog/post/92267045
    Button bt_back;
    int rule,door;
    private TextView test1;
    private RadioButton left_door,right_door;
    private RadioGroup  select_door;
    private Button pre_display;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        test1 = (TextView) findViewById(R.id.choice_place);

        //單選按鈕
        left_door  = (RadioButton)findViewById(R.id.left_door);
        right_door = (RadioButton)findViewById(R.id.right_door);
        select_door = (RadioGroup)findViewById(R.id.select_door);

        //設定 RadioGroup
        select_door.setOnCheckedChangeListener(select_door_L);

        //設定按鈕
        pre_display = (Button)findViewById(R.id.check_door);
        pre_display.setOnClickListener(pre_display_L);

        try {

            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            int rssi_1 = bundle.getInt("rssi_1");
            int rssi_2 = bundle.getInt("rssi_2");
            int rssi_3 = bundle.getInt("rssi_3");

            //利用判斷規則決定原點的位置
            //版本1：套用規則一
            //規則一：純粹的比rssi哪個為最小，它就是最靠近的
            //規則二：延伸規則一，套用但出現兩者rssi相同之情形
            if((rssi_1 > rssi_2) & (rssi_1 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                Toast test = Toast.makeText(Map_old.this,"第一個esp32",Toast.LENGTH_SHORT);
                test.show();
                //test1.setText("1");
                rule = 1;
            }else if((rssi_2 > rssi_1) & (rssi_2 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                Toast test = Toast.makeText(Map_old.this,"第二個esp32",Toast.LENGTH_SHORT);
                test.show();
                //test1.setText("2");
                rule = 2;
            }else if((rssi_3 > rssi_1) & (rssi_3 > rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                Toast test = Toast.makeText(Map_old.this,"第三個esp32",Toast.LENGTH_SHORT);
                test.show();
                //test1.setText("3");
                rule = 3;
            }else{
                //test1.setText("沒有這東西");
            }

            LinearLayout layout=(LinearLayout) findViewById(R.id.draw_pic);
            DrawView view=new DrawView(Map_old.this);
            view.setMinimumHeight(500);
            view.setMinimumWidth(300);
            view.invalidate();
            layout.addView(view);

            Toast test2 = Toast.makeText(Map_old.this,rssi_1+" "+rssi_2+" "+rssi_3,Toast.LENGTH_SHORT);
            test2.show();

        }catch (Exception intent_error){
            Intent intent = new Intent();
            intent.setClass(Map_old.this,MainActivity.class);
        }

        //button
        bt_back = (Button)findViewById(R.id.back);
        bt_back.setOnClickListener(bt_backListener);
    }

    private OnCheckedChangeListener select_door_L = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.left_door){ //左側門
                door = 1;
            }
            if (checkedId == R.id.right_door){ //右側門
                door = 2;
            }
        }
    };

    //畫板繪製地圖
    public class DrawView extends View {
        public Paint mPaint;
        public Canvas mCanvas;

        public DrawView(Context context) {
            super(context);
        }

        public void onDraw(Canvas canvas) {
            super.onDraw(mCanvas);

            Paint p = new Paint();							// 創建畫筆
            Paint rect = new Paint(); //畫方形的畫筆

            canvas.drawText(String.valueOf(door),50,100,p);		// 寫文字

            // 三角形繪圖
            p.setColor(Color.parseColor("#FF8C00"));
            p.setTextSize(100);
            rect.setColor(Color.parseColor("#33FFA6"));
            rect.setTextSize(100);
            //canvas.drawText("三角形：",350,100,p);

            Path Room = new Path(); //畫長方
            Room.moveTo(190,40);
            Room.lineTo(610, 40);
            Room.lineTo(610, 460);
            Room.lineTo(190, 460);
            Room.close(); // 使這些點構成封閉的多邊形
            canvas.drawPath(Room, rect);


            if (door == 1){//左門

                Path path = new Path();
                //(0,0)左上、等腰直角三角 或 直角三角
                path.moveTo(200, 50);// 此點為多邊形的起點
                path.lineTo(600, 50);
                path.lineTo(600, 450);
                path.close(); // 使這些點構成封閉的多邊形
                canvas.drawPath(path, p);

                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(500,750,20,p);
            }
            if (door == 2){//右門

                Path path = new Path();
                //(0,0)左上、等腰直角三角 或 直角三角
                path.moveTo(600, 50);// 此點為多邊形的起點
                path.lineTo(200, 50);
                path.lineTo(200, 450);
                path.close(); // 使這些點構成封閉的多邊形
                canvas.drawPath(path, p);

                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(700,1050,20,p);
            }

            //根據規則有不同的動作
            //cx,cy為圓的圓心位置
            if (rule == 1){
                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(300,100,20,p);
            }
            if (rule == 2){
                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(500,100,20,p);
            }
            if (rule == 3){
                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(500,300,20,p);
            }}}

    public View.OnClickListener pre_display_L = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast test3 = Toast.makeText(Map_old.this,String.valueOf(door),Toast.LENGTH_SHORT);
            test3.show();

            LinearLayout layout = findViewById(R.id.draw_pic);
            DrawView view=new DrawView(Map_old.this);
            view.setMinimumHeight(500);
            view.setMinimumWidth(300);
            view.invalidate();
            layout.addView(view);
        }
    };

    private  View.OnClickListener bt_backListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Map_old.this,MainActivity.class);
            startActivity(intent);
        }
    };

}