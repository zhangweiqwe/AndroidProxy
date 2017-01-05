package cn.wsgwz.gravity.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ScrollView;


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
                    y= ev.getY();
                break;
            case MotionEvent. ACTION_UP:
                if(isNeedAnimation()){
                    animation();
                }
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
        float deltaY = (y - nowY) / SIZE;


        y = nowY;
        // 当滚动到最上或者最下时就不会再滚动，这时移动布局
        if (isNeedMove()) {
            if (normalRect.isEmpty()) {
                // 保存正常的布局位置
                normalRect.set(rootView.getLeft(), rootView.getTop(),
                        rootView.getRight(), rootView.getBottom());
                return ;
            }
            int yy = (int) (rootView.getTop() - (deltaY*2));
            int zz = (int) (rootView.getBottom() - (deltaY*2));

            // 移动布局
            rootView.layout(rootView.getLeft(), yy, rootView.getRight(),
                    zz);
        }
    }

    private  ObjectAnimator animator;
    // 开启动画移动
    public void animation() {

        animator = ObjectAnimator.ofFloat(rootView,"translationY",rootView.getTop()- normalRect.top,0);
        animator.setDuration(450);
        animator.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float v) {
                return v*v * v * ((3.0f + 1) * v - 3.0f);
            }
        });
        animator.start();
        // 设置回到正常的布局位置
        rootView.layout(normalRect.left, normalRect.top, normalRect.right, normalRect.bottom);
        normalRect.setEmpty();
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




}

    //v*v * v * ((3.0f + 1) * v - 3.0f)


