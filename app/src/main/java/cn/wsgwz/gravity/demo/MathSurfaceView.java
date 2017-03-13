package cn.wsgwz.gravity.demo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;

import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Administrator on 2017/3/9 0009.
 */

public class MathSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    public static final boolean DEMO = false;

    private MathDrawTimerTask mathDrawTimerTask;
    private Timer timer;
    private static final long INTERVAL_TIME = 800;
    public MathSurfaceView(Context context) {
        super(context);
        LogUtil.printSS("MathSurfaceView 0");
    }

    public MathSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);
        LogUtil.printSS("MathSurfaceView 1");
    }

    public MathSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LogUtil.printSS("MathSurfaceView 2");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MathSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LogUtil.printSS("MathSurfaceView 3");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.printSS("surfaceCreated");
        timer = new Timer();
        mathDrawTimerTask = new MathDrawTimerTask(holder);
        timer.schedule(mathDrawTimerTask,INTERVAL_TIME,INTERVAL_TIME);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.printSS("surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.printSS("surfaceDestroyed");
        timer.cancel();
    }
}
