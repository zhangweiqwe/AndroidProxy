package cn.wsgwz.gravity.demo;

import android.R.color;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import cn.wsgwz.gravity.R;

public class BmpDrow extends SurfaceView implements SurfaceHolder.Callback {

    private Bitmap bmp;
    private BitmapFactory.Options options;
    private Resources res;
    private SurfaceHolder holder;
    private int bmp_w;
    private int bmp_h;
    private int height;
    private int width;
    private Canvas canvas;
    private Rect rect;

    private void init(){
        res=getResources();
        options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        bmp=BitmapFactory.decodeResource(res, R.drawable.release_icon,options);
        //x=bmp.get
        holder=getHolder();
        holder.addCallback(this);
        bmp_w=bmp.getWidth();
        bmp_h=bmp.getHeight();
    }
    public BmpDrow(Context context) {
        super(context);
        init();
        // TODO Auto-generated constructor stub
    }

    public BmpDrow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BmpDrow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BmpDrow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        height=this.getHeight();
        width=this.getWidth();
		/*
		 * view什么时候绘制完毕，什么时候才知道大小
		 */
        //mydrow(width/2,height/2);
        test_drow();

        Log.e("height", ""+height);
        Log.e("width",""+width);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }
    public void test_drow(){
        Log.e("test drow", "test drow");
        canvas=holder.lockCanvas();
        if(bmp==null)
            Log.e("bmp", "error");
        canvas.drawBitmap(bmp, 0, 0, null);
        if(canvas!=null){
            holder.unlockCanvasAndPost(canvas);
        }

    }
    public void mydrow(int x,int y){
		/*
		 * 在屏幕的(x,y)位置绘制bitmap;
		 * Rect(int left, int top, int right, int bottom)
		 */
        int temp=y+bmp_h;
        if(temp<=0)
            temp=0;
        rect=new Rect(x,y,x+bmp_w,temp);
        try{
            //canvas=holder.lockCanvas(rect);
            canvas=holder.lockCanvas();
            //加上这里就不会有那个一闪的一闪的
            if(canvas!=null){
                // canvas.drawBitmap(bmp, matrix, paint)
                //从left,top开始用paint  draw bmp;
                //lock 需要lock矩形区域吗？
                Log.e("canvas", "drow");
                //canvas.save();
                canvas.drawColor(Color.BLACK);
                //canvas.drawBitmap(bmp, 0, 0, null);

                //canvas.clipRect(rect);
                // canvas.drawColor(color.background_dark);
                canvas.drawBitmap(bmp, x-bmp.getWidth()/2, y-bmp.getHeight()/2, null);
                // canvas.restore();
            }
        }finally{
            if(canvas!=null){
                holder.unlockCanvasAndPost(canvas);
            }
        }
        //test_drow();
		/*
		 * 加上之后会出现奇怪的问题，源图像会覆盖掉
		 * 可以实现的方法是本地保存btimap
		 * 这里面有提交两次，好像不行
		 */
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
		 /*
		  * 重载获得x,y把星星或者小图片会知道指定的位置
		  * bmp-》创建canvas
		  * 屏幕上应该只有一个星星
		  */
        Log.e("touch", "doing");
        int action=event.getAction();

        int  x=(int) event.getX();
        int y=(int) event.getY();
        mydrow(x,y);

        return true;

    }

}