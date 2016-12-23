package cn.wsgwz.gravity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import cn.wsgwz.gravity.util.LogUtil;
/**
 * Created by Jeremy Wang on 2016/12/23.
 */

public class WaterFlowView extends View {
    private Paint  paint = new Paint();
    public WaterFlowView(Context context) {
        super(context);
    }

    public WaterFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WaterFlowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int x=0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    invalidate();
                    break;
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtil.printSS("onDetachedFromWindow()");
    }

    public void startOnDraw(){
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(x<1000){
                    x++;
                    handler.sendEmptyMessage(1000);
                }else {
                    timer.cancel();
                    LogUtil.printSS("     timer.cancel(); ");
                }
            }
        },200,200);
    }
    private double degreeToRad(double degree){
        if(x==1){
            LogUtil.printSS(Math.PI+"--");
        }
        return degree * (Math.PI/180);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
        float sinY = centerY+(float)(y*10);
        LogUtil.printSS(sinY+"----");
        canvas.drawPoint(x,sinY,paint);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LogUtil.printSS("  onFinishInflate()");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //LogUtil.printSS("  onTouchEvent()");
        return super.onTouchEvent(event);
    }
}
