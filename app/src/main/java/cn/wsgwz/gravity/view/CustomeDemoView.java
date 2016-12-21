package cn.wsgwz.gravity.view;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.*;
/**
 * Created by Jeremy Wang on 2016/12/21.
 */

public class CustomeDemoView extends View{
    public CustomeDemoView(Context context) {
        super(context);
    }

    public CustomeDemoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomeDemoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomeDemoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
