package cn.wsgwz.gravity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileInputStream;
import java.text.Format;
import java.util.Timer;
import java.util.TimerTask;

import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2016/12/23.
 */

public class WaterFlowSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Timer timer;
    private Paint paint = new Paint();
    private TimerTask timerTask = new TimerTask() {
        private Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1000:
                        //Log.d("ssssssssss","---->"+x);
                        if(x<getWidth()-86){
                            x=x+1;
                        }else {
                            timer.cancel();
                            //timer.cancel();
                            timerTask.cancel();
                            LogUtil.printSS("     timer.cancel(); auto"+getWidth());
                        }
                        canvas = surfaceHolder.lockCanvas();
                        draw();
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        break;
                }
            }
        };
        @Override
        public void run() {
           // LogUtil.printSS("--->"+x);
                handler.sendEmptyMessage(1000);
        }
    };
    private int x=0;
    public WaterFlowSurfaceView(Context context) {
        super(context);
        LogUtil.printSS("--1");
    }

    public WaterFlowSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        LogUtil.printSS("--2");
    }

    public WaterFlowSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LogUtil.printSS("--3");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WaterFlowSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LogUtil.printSS("--4");
    }





    public void startOnDraw(){
        timer = new Timer();
        timer.schedule(timerTask,20,20);
    }
    private double degreeToRad(double degree){
        return degree * (Math.PI/180);
    }

    private void draw(){
        paint.setColor(Color.parseColor("#FF6347"));
        int width = getWidth();
        int height = getHeight();
        //LogUtil.printSS("  --"+width+" ---"+height);
        int centerY = height/2;


        //x轴
        canvas.drawLine(0,centerY,width,centerY,paint);
        int offsetX = 86;
        //y轴
        canvas.drawLine(offsetX,0,offsetX,height,paint);

        //x轴y轴标示
        paint.setTextSize(58);
        int distanceAxis=86;
        canvas.drawText("x",width-distanceAxis,centerY+distanceAxis,paint);
        canvas.drawText("y",offsetX+distanceAxis,distanceAxis,paint);

        //正弦曲线
        double y = Math.cos(degreeToRad(x));
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        float sinY = centerY+(float)(y*20);
        //LogUtil.printSS(y+"<--");
        canvas.drawPoint(x+offsetX,sinY,paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.printSS("onDraw");
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

       // WaterFlowSurfaceView.this.setZOrderOnTop(true);//设置画布  背景透明

        startOnDraw();
        LogUtil.printSS("surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        LogUtil.printSS(" surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        LogUtil.printSS("  surfaceDestroyed");
        if(timer!=null){
            timer.cancel();
        }
        if(timerTask!=null){
            timerTask.cancel();
        }

    }
}
