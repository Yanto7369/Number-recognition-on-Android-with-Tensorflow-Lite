package com.example.myapplication;


/*
参考
http://andante.in/i/%E6%8F%8F%E7%94%BB/%E3%81%8A%E7%B5%B5%E3%81%8B%E3%81%8D%E3%82%A2%E3%83%97%E3%83%AA%E3%81%A7%E6%9B%B8%E3%81%84%E3%81%9F%E7%B5%B5%E3%82%92%E4%BF%9D%E5%AD%98%E3%81%99%E3%82%8B%E3%80%82/
 */

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public  class penView extends View {

    private   float oldx = 0f;
    private float oldy = 0f;
    private  Bitmap bmp = null;
    private  Canvas bmpCanvas;
    private Paint paint;
    private static int CanvasHeight;
    private static int CanvasWidth;
   // private Activity _context;

    private void Init(){
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
      // CanvasInit();
    }
    private void CanvasInit(){
        bmp = Bitmap.createBitmap(CanvasWidth,CanvasHeight, Bitmap.Config.ARGB_8888);
        bmpCanvas = new Canvas(bmp);
        bmpCanvas.drawColor(Color.BLACK);
    }
    public  penView(Context context) {
        super(context);
       // _context = (Activity)context;
    Init();
    }

    public penView(Context context,AttributeSet atr) {
        super(context,atr);
       // _context = (Activity)context;
        Init();
       // 線の太さ
    }
    //ビューに最初にサイズが割り当てられたときに呼び出され、ビューのサイズがなんらかの理由で変更されると再度呼び出されます
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        CanvasWidth=w;
        CanvasHeight=h;
        CanvasInit();
        //bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        //bamp = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888);
        //bmpCanvas = new Canvas(bmp);
    }
    private static final String TAG = "ClassificationDemo";
    //引数はdisplaylistcanvas
    public void onDraw(Canvas canvas) {
       // bmp = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888);
        canvas.drawBitmap(bmp, 0, 0, null);
      //  bamp=bmp;
       // bmpCanvas.drawBitmap(bmp, 0, 0, null);
    }
    public  Bitmap getBmp(){
       // clearDrawList();
        Bitmap temp=bmp;//clearDrawlistでリセットされるので一時退避させる。
        clearDrawList();
        Log.d(TAG,"getBMP");
        return temp;
    }


    public boolean onTouchEvent(MotionEvent e){
        switch(e.getAction()){
            case MotionEvent.ACTION_DOWN: //最初のポイント
                oldx = e.getX();
                oldy = e.getY();
                break;
            case MotionEvent.ACTION_MOVE: //途中のポイント
                bmpCanvas.drawLine(oldx, oldy, e.getX(), e.getY(), paint);
                oldx = e.getX();
                oldy = e.getY();
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    private void clearDrawList(){
       // bmpCanvas.drawColor(Color.WHITE);
       //bmp = Bitmap.createBitmap(600,600, Bitmap.Config.ARGB_8888);
        CanvasInit();
        invalidate();
    }

}