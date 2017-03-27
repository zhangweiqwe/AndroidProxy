package cn.wsgwz.gravity.demo.demoSinCos;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/3/20 0020.
 */

public class SinCosSurfaceHolderCallBack implements SurfaceHolder.Callback {
    private static final String TAG  = SinCosSurfaceHolderCallBack.class.getName();

    private Timer timer;
    private TimerTask timerTask;
    private Handler handler;
    private static final int HANDLER_WHAT_REFRESH = 0x12;

    private static final int TIMER_INTERVAl_TIME = 500;


    private static final int AUTO_INCREMENT_X = 10;
    private static final int Y_ADD = 1920/2;
    private static final int MUTIPLICATION_Y = 110;


    private Path path;
    private Paint paint;
    private float x0= 0;
    private float y0 = 0;

    private float touchX0 = 0;
    private float touchX1 = 0;
    private float touchY0 = 0;
    private float touchY1 = 0;
    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        Log.d(TAG,"surfaceCreated");
            paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(12f);

        path = new Path();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case HANDLER_WHAT_REFRESH:
                        refershPoint(holder);
                        break;
                }
            }
        };
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(HANDLER_WHAT_REFRESH);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,TIMER_INTERVAl_TIME,TIMER_INTERVAl_TIME);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Log.d(TAG,"surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG,"surfaceDestroyed");
        if(timer!=null){timer.cancel();};
    }

    private void refershPoint(SurfaceHolder holder){
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.BLACK);
        x0+=AUTO_INCREMENT_X;
        y0 = (float) ((Math.sin(Math.PI/180*x0)*MUTIPLICATION_Y)+Y_ADD);
        canvas.drawPoint(x0,y0,paint);
        canvas.drawPath(path,paint);
        holder.unlockCanvasAndPost(canvas);
    }

    public void refershPathQuad(SurfaceHolder holder){
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.BLACK);
        path.quadTo(touchX0,touchY0,touchX1,touchY1);
        canvas.drawPath(path,paint);
        holder.unlockCanvasAndPost(canvas);
    }


    public void setTouchY0(float touchY0) {
        this.touchY0 = touchY0;
    }

    public void setTouchY1(float touchY1) {
        this.touchY1 = touchY1;
    }

    public void setTouchX1(float touchX1) {
        this.touchX1 = touchX1;
    }

    public void setTouchX0(float touchX0) {
        this.touchX0 = touchX0;
    }

}
