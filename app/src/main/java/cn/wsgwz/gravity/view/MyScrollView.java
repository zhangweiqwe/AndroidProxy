package cn.wsgwz.gravity.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import cn.wsgwz.gravity.util.LogUtil;


public class MyScrollView extends ScrollView {

    // 拖动的距离 size = 4 的意思 只允许拖动屏幕的1/4
    private static final int size = 2;
    private View mRoot;
    private float y;
    private Rect normal = new Rect();;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            mRoot = getChildAt(0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (mRoot == null) {
            return super.onTouchEvent(ev);
        } else {
            commOnTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    public void commOnTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                y = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (isNeedAnimation()) {
                    // Log.v("mlguitar", "will up and animation");
                    animation();
                }
                break;
            case MotionEvent.ACTION_MOVE:

                final float preY = y;
                float nowY = ev.getY();
                /**
                 * size=4 表示 拖动的距离为屏幕的高度的1/4
                 */
                int deltaY = (int) (preY - nowY) / size;
                // 滚动
                // scrollBy(0, deltaY);

                y = nowY;
                // 当滚动到最上或者最下时就不会再滚动，这时移动布局
                if (isNeedMove()) {
                    if (normal.isEmpty()) {
                        // 保存正常的布局位置
                        normal.set(mRoot.getLeft(), mRoot.getTop(),
                                mRoot.getRight(), mRoot.getBottom());
                        return;
                    }
                    int yy = mRoot.getTop() - deltaY;

                    // 移动布局
                    mRoot.layout(mRoot.getLeft(), yy, mRoot.getRight(),
                            mRoot.getBottom() - deltaY);
                }
                break;
            default:
                break;
        }
    }

    // 开启动画移动
    public void animation() {
        // 原始代码
        // TranslateAnimation ta = new TranslateAnimation(0, 0, mRoot.getTop(),
        // normal.top);
        // 修复后的代码:
        TranslateAnimation ta = new TranslateAnimation(0, 0, mRoot.getTop()- normal.top, 0);
        ta.setDuration(350);
        mRoot.startAnimation(ta);
        // 设置回到正常的布局位置
        mRoot.layout(normal.left, normal.top, normal.right, normal.bottom);
        normal.setEmpty();
    }

    // 是否需要开启动画
    public boolean isNeedAnimation() {
        return !normal.isEmpty();
    }

    // 是否需要移动布局
    public boolean isNeedMove() {
        int offset = mRoot.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        if (scrollY == 0 || scrollY == offset) {
            return true;
        }
        return false;
    }
}
