package cn.wsgwz.gravity.demo.demoSinCos;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import cn.wsgwz.gravity.R;

/**
 * Created by Administrator on 2017/3/20 0020.
 */

public class SinCosSurfaceview extends SurfaceView {
    private static final String TAG  = SinCosSurfaceview.class.getName();
    private SinCosSurfaceHolderCallBack sinCosSurfaceHolderCallBack;
    public SinCosSurfaceview(Context context) {
        super(context);
        init();
        Log.d(TAG,"SinCosSurfaceview 1");
    }

    public SinCosSurfaceview(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = this.getContext().obtainStyledAttributes(attrs, R.styleable.SinCosSurfaceview);
        init();
        Log.d(TAG,"SinCosSurfaceview 2"+"<-->"+typedArray.getInteger(R.styleable.SinCosSurfaceview_key1,0)+"--"+typedArray.getInteger(R.styleable.SinCosSurfaceview_key2,0));
        typedArray.recycle();
    }

    public SinCosSurfaceview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        Log.d(TAG,"SinCosSurfaceview 3");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SinCosSurfaceview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        Log.d(TAG,"SinCosSurfaceview 4");
    }

    private void init(){
        getHolder().addCallback((sinCosSurfaceHolderCallBack = new SinCosSurfaceHolderCallBack()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //Log.d(TAG,"onTouchEvent"+event.getAction());
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                sinCosSurfaceHolderCallBack.setTouchX0(event.getX());
                sinCosSurfaceHolderCallBack.setTouchY0(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                sinCosSurfaceHolderCallBack.setTouchX1(event.getX());
                sinCosSurfaceHolderCallBack.setTouchY1(event.getY());
                sinCosSurfaceHolderCallBack.refershPathQuad(getHolder());
                break;

        }
        return super.onTouchEvent(event);
    }
}
