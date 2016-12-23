package cn.wsgwz.gravity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2016/12/23.
 */

public class BackgroundSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

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
                            x=x+2;
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
    public BackgroundSurfaceView(Context context) {
        super(context);
        LogUtil.printSS("--1");
    }

    public BackgroundSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        LogUtil.printSS("--2");
    }

    public BackgroundSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LogUtil.printSS("--3");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BackgroundSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        double y = Math.sin(degreeToRad(x));
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        float sinY = centerY+(float)(y*400);
        //LogUtil.printSS(y+"<--");
        canvas.drawPoint(x+offsetX,sinY,paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.printSS("onDraw");
    }
    public static final int DIM = 1024;
    public static final int DM1 = 1024;


    private double RD(int i,int j){
        double a=0,b=0,c,d,n=0;
        while((c=a*a)+(d=b*b)<4&&n++<880)
        {b=2*a*b+j*8e-9-.645411;a=c-d+i*8e-9+.356888;}
        return 255*Math.pow((n-80)/800,3.);
    }
    private double GR(int i,int j){
        double a=0,b=0,c,d,n=0;
        while((c=a*a)+(d=b*b)<4&&n++<880)
        {b=2*a*b+j*8e-9-.645411;a=c-d+i*8e-9+.356888;}
        return 255*Math.pow((n-80)/800,.7);
    }
    private double BL(int i,int j){
        double a=0,b=0,c,d,n=0;
        while((c=a*a)+(d=b*b)<4&&n++<880)
        {b=2*a*b+j*8e-9-.645411;a=c-d+i*8e-9+.356888;}
        return 255*Math.pow((n-80)/800,.5);
    }

    private void main() throws IOException {
        for(int j=0;j<DIM;j++){
            for(int i=0;i<DIM;i++){
                pixelWrite(i,j);
            }
        }
    }
    private int[] color = new int[3];
    private void pixelWrite(int i, int j) throws IOException {
        color[0] = (int)RD(i,j)&255;
        color[1] = (int)GR(i,j)&255;
        color[2] = (int)BL(i,j)&255;


        LogUtil.printSS(color[0]+"---"+color[1]+"------"+color[2]);
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

       // WaterFlowSurfaceView.this.setZOrderOnTop(true);//设置画布  背景透明
        try {
            main();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
