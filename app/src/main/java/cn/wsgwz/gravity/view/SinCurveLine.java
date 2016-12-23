package cn.wsgwz.gravity.view;


import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import cn.wsgwz.gravity.R;


/**
 * 绘制正弦曲线
 *
 */
public class SinCurveLine extends SurfaceView implements SurfaceHolder.Callback{

    private static final String TAG = "SinCurveLine";
    Paint paint;
    int x = 0;
    SurfaceHolder surfaceHolder;
    Canvas canvas;

    private Timer timer;

    public SinCurveLine(Context context) {
        this(context, null);
        Log.d(TAG, "SinCurveLine");
    }

    public SinCurveLine(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.mySurfaceView);
        typedArray.getFloat(R.styleable.mySurfaceView_key1,30);
    }

    public SinCurveLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    private void init(){
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * 角度转换成弧度
     * @param degree
     * @return
     */
    private double degreeToRad(double degree){
        return degree * Math.PI/180;
    }




    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG,"surfaceCreated");
        init();
        final int width = getWidth();
        int height = getHeight();
        final int centerY = height/2;
        canvas = surfaceHolder.lockCanvas();
        //canvas.drawColor(Color.WHITE);
        paint.setTextSize(30);
        canvas.drawText("X", 5, 25, paint);
        canvas.drawText("Y",5,centerY+25,paint);
        canvas.drawLine(0, centerY, width, centerY, paint);//在屏幕中心绘制x轴
        canvas.drawLine(0, 0, 0, height, paint);//绘制Y轴

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1000:
                        paint.setColor(Color.BLUE);
                        paint.setStrokeWidth(5);
                        double r = degreeToRad(x);//角度转换成弧度
                        int y = (int) (centerY+150 - Math.sin(r)*200); //sin 对边/斜边 cos 邻边/斜边
                        canvas.drawPoint(x,y,paint);
                        x++;
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        canvas = surfaceHolder.lockCanvas();
                        break;
                }
            }
        } ;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(x<width){
                    handler.sendEmptyMessage(1000);
                }else {
                    timer.cancel();
                }
            }
        },50,50);



               /* while (x < width) {
                    paint.setColor(Color.BLUE);
                    paint.setStrokeWidth(5);
                    double rad = degreeToRad(x);//角度转换成弧度
                    int y = (int) (centerY - Math.sin(rad)*100);
                    canvas.drawPoint(x,y,paint);
                    x++;
            *//*canvas.drawLine(centerX,centerY,x,y,paint);
            preX = x;
            preY = y;
            * 打开注释，运行代码，会看到意外图形
            *//*

                }*/





    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
