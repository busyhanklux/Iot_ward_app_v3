package com.example.iot_ward_app_v3;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

public class Map extends AppCompatActivity {

    //畫板參考https://lowren.pixnet.net/blog/post/92267045
    Button bt_back;
    int rule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        try {

            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            int rssi_1 = bundle.getInt("rssi_1");
            int rssi_2 = bundle.getInt("rssi_2");
            int rssi_3 = bundle.getInt("rssi_3");

            //利用判斷規則決定原點的位置
            //版本1：套用規則一
            if((rssi_1 > rssi_2) & (rssi_1 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                Toast test = Toast.makeText(Map.this,"第一個esp32",Toast.LENGTH_SHORT);
                test.show();
                rule = 1;
            }else if((rssi_2 > rssi_1) & (rssi_2 > rssi_3) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                Toast test = Toast.makeText(Map.this,"第二個esp32",Toast.LENGTH_SHORT);
                test.show();
                rule = 2;
            }else if((rssi_3 > rssi_1) & (rssi_3 > rssi_2) & (rssi_1 > -140) & (rssi_2 > -140) & (rssi_3 > -140)){
                Toast test = Toast.makeText(Map.this,"第三個esp32",Toast.LENGTH_SHORT);
                test.show();
                rule = 3;
            }else{

            }

            LinearLayout layout=(LinearLayout) findViewById(R.id.draw_pic);
            final DrawView view=new DrawView(this);
            view.setMinimumHeight(500);
            view.setMinimumWidth(300);
            view.invalidate();
            layout.addView(view);

            //Toast test = Toast.makeText(Map.this,rssi_1+" "+rssi_2+" "+rssi_3,Toast.LENGTH_SHORT);
            //test.show();

        }catch (Exception intent_error){
            Intent intent = new Intent();
            intent.setClass(Map.this,MainActivity.class);
        }

        //button
        bt_back = (Button)findViewById(R.id.back);
        bt_back.setOnClickListener(bt_backListener);
    }
    //畫板繪製地圖
    public class DrawView extends View {

        public DrawView(Context context) {
            super(context);
        }
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint p = new Paint();							// 創建畫筆
            /*
            //測試用的畫圓
            p.setAntiAlias(true);							// 設置畫筆的鋸齒效果。 true是去除。
            p.setColor(Color.RED);							// 設置紅色
            p.setTextSize(80);								// 設置文字的大小。
            canvas.drawText("圓形：",50,100,p);		// 寫文字
            canvas.drawCircle(80,20,20,p);
            */

            // 三角形繪圖
            p.setColor(Color.parseColor("#FF8C00"));
            p.setTextSize(100);
            //canvas.drawText("三角形：",350,100,p);
            Path path = new Path();
            //(0,0)在左上
            //狹窄的直角三角(3,4,5)
            /*
            path.moveTo(200, 600);// 此點為多邊形的起點
            path.lineTo(1000, 1200);
            path.lineTo(200, 1200);
             */
            //寬敞的等腰三角
            path.moveTo(500, 600); //上1
            path.lineTo(900, 1200);//右下2
            path.lineTo(100, 1200);//左下3
            path.close(); // 使這些點構成封閉的多邊形
            canvas.drawPath(path, p);

            //根據規則有不同的動作
            //cx,cy為圓的圓心位置
            if (rule == 1){
                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(500,750,20,p);
            }
            if (rule == 2){
                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(700,1050,20,p);
            }
            if (rule == 3){
                p.setAntiAlias(true);
                p.setColor(Color.RED);
                canvas.drawCircle(300,1050,20,p);
            }
        }

    }


    private  View.OnClickListener bt_backListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Map.this,MainActivity.class);
            startActivity(intent);
        }
    };

}