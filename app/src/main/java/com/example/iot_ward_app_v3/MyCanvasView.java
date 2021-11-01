
package com.example.iot_ward_app_v3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class MyCanvasView extends View {

    Context context;
    private Paint   mBitmapPaint;
    private Bitmap mBitmap,background;
    private Canvas  mCanvas;
    //畫筆
    private Path mPath;

    private Paint circlePaint,mPaint;
    private Path circlePath;
    //暫存使用者手指的X,Y座標
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;


    public MyCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        //畫點選畫面時顯示的圈圈
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        //繪製線條
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        //background = BitmapFactory.decodeResource(getResources(),R.drawable.aaaaa);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //初始化空畫布
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //取得上一個動作所畫過的內容
        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        //依據移動路徑畫線
        canvas.drawPath( mPath,  mPaint);
        //畫圓圈圈
        canvas.drawPath( circlePath,  circlePaint);
    }

    //覆寫:偵測使用者觸碰螢幕的事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(),y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                break;
        }
        invalidate();
        return true;

    }
    //觸碰到螢幕時，取得手指的X,Y座標；並順便設定為線的起點
    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    //在螢幕上滑動時，不斷刷新移動路徑
    private void touch_move(float x, float y) {
        //取得目前位置與前一點位置的X距離
        float dx = Math.abs(x - mX);
        //取得目前位置與前一點位置的Y距離
        float dy = Math.abs(y - mY);
        //判斷此兩點距離是否有大於預設的最小值，有才把他畫進去
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            //畫貝爾茲曲線
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            //更新上一點X座標
            mX = x;
            //更新上一點Y座標
            mY = y;

            //消滅上一點時的小圈圈位置
            circlePath.reset();
            //更新小圈圈的位置
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }
    //當使用者放開時，把位置設為終點
    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        mCanvas.drawPath(mPath,  mPaint);
        mPath.reset();
    }
    //清除所有畫筆內容
    public void clear(){
        setDrawingCacheEnabled(false);
        onSizeChanged(getWidth(),getHeight(),getWidth(),getHeight());
        invalidate();
        setDrawingCacheEnabled(true);

    }
    //設置接口從外部設置畫筆顏色
    public void setColor(int color){
        mPaint.setColor(color);
    }
    //設置接口從外部設置背景圖片
    public void setBackground(Bitmap bitmap){
        //BitmapFactory.decodeResource(getResources(),R.drawable.aaaaa);
        background = bitmap;
        invalidate();
    }
}

