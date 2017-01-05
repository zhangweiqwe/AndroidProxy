package cn.wsgwz.gravity.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import cn.wsgwz.gravity.R;

/**
 * Created by Jeremy Wang on 2016/12/15.
 */

public class MyLinnearLayout extends LinearLayout {

    private View conentLL;
    private RecyclerView recyclerView;


    private Rect normalRect = new Rect();

    private static final int SIZE = 3;
    private    float y=0;

    public MyLinnearLayout(Context context) {
        super(context);
    }

    public MyLinnearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinnearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyLinnearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        conentLL = findViewById(R.id.conentLL);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (conentLL == null) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()){
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                y= ev.getY();
                break;
            case MotionEvent. ACTION_UP:
                if(isNeedAnimation()){
                    animation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //LogUtil.printSS(ev.toString()+"  ");
             /*   if(myAnimatorUpdateListener!=null){
                    myAnimatorUpdateListener.pause();
                }*/
                move(ev);
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        //保存按下时的y轴
        return super.onTouchEvent(ev);
    }








    private void move(MotionEvent ev){
        //final float preY = y;
        float nowY = ev.getY();
        /**
         * size=4 表示 拖动的距离为屏幕的高度的1/4
         */
        int deltaY = (int) (y - nowY) / SIZE;
        // 滚动
        // scrollBy(0, deltaY);

        // y = nowY;
        y = nowY;
        // 当滚动到最上或者最下时就不会再滚动，这时移动布局
        if (isNeedMove()) {
            if (normalRect.isEmpty()) {
                // 保存正常的布局位置
                normalRect.set(conentLL.getLeft(), conentLL.getTop(),
                        conentLL.getRight(), conentLL.getBottom());
                return ;
            }
            int yy = (int) (conentLL.getTop() - (deltaY*2));
            int zz = (int) (conentLL.getBottom() - (deltaY*2));

            // 移动布局
            conentLL.layout(conentLL.getLeft(), yy, conentLL.getRight(),
                    zz);
        }
    }

    // 开启动画移动
    public void animation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(conentLL,"translationY",conentLL.getTop()- normalRect.top,0);
        animator.setDuration(450);
        animator.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float v) {
                return v*v * v * ((3.0f + 1) * v - 3.0f);
            }
        });
        animator.start();

        // 设置回到正常的布局位置
        conentLL.layout(normalRect.left, normalRect.top, normalRect.right, normalRect.bottom);
        normalRect.setEmpty();
    }

    // 是否需要开启动画
    public boolean isNeedAnimation() {
        return !normalRect.isEmpty();
    }

    // 是否需要移动布局
    public boolean isNeedMove() {
       /* int offset = conentLL.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        LogUtil.printSS(""+offset+"     "+scrollY);
        if (scrollY == 0 || scrollY == offset) {
            return true;
        }*/
        return true;
    }







}
