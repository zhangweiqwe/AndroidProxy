package cn.wsgwz.gravity.demo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import java.util.Objects;
import java.util.TimerTask;

import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class MathDrawTimerTask extends TimerTask implements Handler.Callback {
    private static final Object OBJ_SYNC = new Object();
    private static final int HANDLER_MESSAGW_WHAT = 0x1000;//本次线程执行完成
    private SurfaceHolder surfaceHolder;

    private Handler handler = new Handler(this);

    private int drawCount = 0;

    public MathDrawTimerTask(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case HANDLER_MESSAGW_WHAT:
                drawCount++;
                LogUtil.printSS("TimerTask "+drawCount+"次线程执行完成");
                break;
        }
        return false;
    }
    int x=1;//橫
    int y=650;//纵
    @Override
    public void run() {
        synchronized (OBJ_SYNC){
            Canvas canvas = surfaceHolder.lockCanvas();
            //canvas.drawColor(Color.BLACK);//黑色画布
            Paint paint = new Paint();
            paint.setStrokeWidth(4);
            paint.setAntiAlias(true);
            paint.setColor(Color.GREEN);
            float y0 = (float) (Math.sin(Math.PI/180.0*x)*70)+400;
            canvas.drawPoint(x, y0,paint);//sin 对边/斜边
            x+=10;
            if(x>1080){
                x=1;
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
            //surfaceHolder.unlockCanvasAndPost(surfaceHolder.lockCanvas(null));
            //handler.sendEmptyMessage(HANDLER_MESSAGW_WHAT);
        }
    }
}
