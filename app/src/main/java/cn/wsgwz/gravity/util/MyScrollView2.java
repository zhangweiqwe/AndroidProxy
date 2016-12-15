package cn.wsgwz.gravity.util;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ScrollView;

import java.util.Timer;
import java.util.TimerTask;


public class MyScrollView2 extends ScrollView {
    //把scrollview中的第一个子控件当做layout滑动的 布局
    private View rootView;
    //保存没有移动时rootView的layout状态
    private Rect normalRect = new Rect();

    private static final int SIZE = 3;
    private    float y=0;


    public MyScrollView2(Context context) {
        super(context);
    }

    public MyScrollView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
/*

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public OtherScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
*/

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        rootView = MyScrollView2.this.getChildAt(0);
    }



    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        //保存按下时的y轴
        if (rootView == null) {
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
             /*   if(myAnimatorUpdateListener!=null){
                    myAnimatorUpdateListener.pause();
                }*/
                move(ev);
                break;
            default:
                break;
        }
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
                normalRect.set(rootView.getLeft(), rootView.getTop(),
                        rootView.getRight(), rootView.getBottom());
                return ;
            }
            int yy = rootView.getTop() - deltaY;

            // 移动布局
            rootView.layout(rootView.getLeft(), yy, rootView.getRight(),
                    rootView.getBottom() - deltaY);
        }
    }

    private MyAnimatorUpdateListener myAnimatorUpdateListener;
    // 开启动画移动
    public void animation() {
        // 原始代码
        // TranslateAnimation ta = new TranslateAnimation(0, 0, rootView.getTop(),
        // normal.top);
        // 修复后的代码:
        //ObjectAnimator objectAnimator;
        //ObjectAnimator animator = ObjectAnimator.ofFloat(rootView,"alpha",1,0,1);
        ObjectAnimator animator = ObjectAnimator.ofFloat(rootView,"translationY",rootView.getTop()- normalRect.top,0);
        //animator.setDuration(2000);
        animator.setDuration(350);
        //animator.setInterpolator(new MyInterpolator());
        myAnimatorUpdateListener = new MyAnimatorUpdateListener(animator);
        animator.addUpdateListener(myAnimatorUpdateListener);
        animator.start();
       /* TranslateAnimation ta = new TranslateAnimation(0, 0, rootView.getTop()- normalRect.top, 0);
        ta.setInterpolator(new MyInterpolator());
        ta.setDuration(400);
        rootView.startAnimation(ta);*/
        // 设置回到正常的布局位置
        rootView.layout(normalRect.left, normalRect.top, normalRect.right, normalRect.bottom);
        normalRect.setEmpty();
        //myAnimatorUpdateListener = null;
    }

    // 是否需要开启动画
    public boolean isNeedAnimation() {
        return !normalRect.isEmpty();
    }

    // 是否需要移动布局
    public boolean isNeedMove() {
        int offset = rootView.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        if (scrollY == 0 || scrollY == offset) {
            return true;
        }
        return false;
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
                animation.setInterpolator(new MyInterpolator());
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
