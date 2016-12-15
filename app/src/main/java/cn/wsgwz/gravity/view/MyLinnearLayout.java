package cn.wsgwz.gravity.view;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.ListView;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.MyScrollView2;

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
                if(myAnimatorUpdateListener!=null){
                    myAnimatorUpdateListener.pause();
                }
                y= ev.getY();
                break;
            case MotionEvent. ACTION_UP:

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // LogUtil.printSS("-----  ACTION_UP");
                        if(myAnimatorUpdateListener!=null){
                            if(myAnimatorUpdateListener.isPause()){
                                myAnimatorUpdateListener.play();
                            }
                        }
                        if (isNeedAnimation()) {
                            // Log.v("mlguitar", "will up and animation");
                            animation();
                        }

                    }
                },300);
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
            int yy = conentLL.getTop() - deltaY;

            // 移动布局
            conentLL.layout(conentLL.getLeft(), yy, conentLL.getRight(),
                    conentLL.getBottom() - deltaY);
        }
    }

    private MyLinnearLayout.MyAnimatorUpdateListener myAnimatorUpdateListener;
    // 开启动画移动
    public void animation() {
        // 原始代码
        // TranslateAnimation ta = new TranslateAnimation(0, 0, conentLL.getTop(),
        // normal.top);
        // 修复后的代码:
        //ObjectAnimator objectAnimator;
        //ObjectAnimator animator = ObjectAnimator.ofFloat(conentLL,"alpha",1,0,1);
        ObjectAnimator animator = ObjectAnimator.ofFloat(conentLL,"translationY",conentLL.getTop()- normalRect.top,0);
        //animator.setDuration(2000);
        animator.setDuration(350);
        //animator.setInterpolator(new MyInterpolator());
        myAnimatorUpdateListener = new MyLinnearLayout.MyAnimatorUpdateListener(animator);
        animator.addUpdateListener(myAnimatorUpdateListener);
        animator.start();
       /* TranslateAnimation ta = new TranslateAnimation(0, 0, conentLL.getTop()- normalRect.top, 0);
        ta.setInterpolator(new MyInterpolator());
        ta.setDuration(400);
        conentLL.startAnimation(ta);*/
        // 设置回到正常的布局位置
        conentLL.layout(normalRect.left, normalRect.top, normalRect.right, normalRect.bottom);
        normalRect.setEmpty();
        //myAnimatorUpdateListener = null;
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




    class MyAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private ObjectAnimator animator;
        /**
         * 暂停状态
         */
        private boolean isPause = false;
        /**
         * 是否已经暂停，如果一已经暂停，那么就不需要再次设置停止的一些事件和监听器了
         */
        private boolean isPaused = false;
        /**
         * 当前的动画的播放位置
         */
        private float fraction = 0.0f;
        /**
         * 当前动画的播放运行时间
         */
        private long mCurrentPlayTime = 0l;

        /**
         * 是否是暂停状态
         *
         * @return
         */
        public boolean isPause() {
            return isPause;
        }

        public MyAnimatorUpdateListener(ObjectAnimator animator) {
            this.animator = animator;
        }

        /**
         * 停止方法，只是设置标志位，剩余的工作会根据状态位置在onAnimationUpdate进行操作
         */
        public void pause() {
            isPause = true;
        }

        public void play() {
            isPause = false;
            isPaused = false;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            /**
             * 如果是暂停则将状态保持下来，并每个刷新动画的时间了；来设置当前时间，让动画
             * 在时间上处于暂停状态，同时要设置一个静止的时间加速器，来保证动画不会抖动
             */
            if (isPause) {
                if (!isPaused) {
                    mCurrentPlayTime = animation.getCurrentPlayTime();
                    fraction = animation.getAnimatedFraction();
                    animation.setInterpolator(new TimeInterpolator() {
                        @Override
                        public float getInterpolation(float input) {
                            return fraction;
                        }
                    });
                    isPaused = true;
                }
                //每隔动画播放的时间，我们都会将播放时间往回调整，以便重新播放的时候接着使用这个时间,同时也为了让整个动画不结束
                new CountDownTimer(ValueAnimator.getFrameDelay(), ValueAnimator.getFrameDelay()) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        animator.setCurrentPlayTime(mCurrentPlayTime);
                    }
                }.start();
            } else {
                //将时间拦截器恢复成线性的，如果您有自己的，也可以在这里进行恢复
                animation.setInterpolator(new MyLinnearLayout.MyInterpolator());
            }
        }

    }

    class MyInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float v) {
            return  v*v * v * ((3.0f + 1) * v - 3.0f);
        }
    }




}
