package cn.wsgwz.gravity.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Jeremy Wang on 2016/12/15.
 */

public  class MyViewUtil {
    //将view转换成bitmap
    public static Bitmap getViewBitmap(View v){
        v.setDrawingCacheEnabled(true);
        v.measure(0,0);
        // v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.layout(0, 0, v.getMeasuredWidth(),v.getMeasuredHeight());
        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
        bitmap = Bitmap.createBitmap(bitmap);
        return bitmap;
    }
    /**
     * 缩放图片
     */
    public  static Bitmap bitmapScale(Bitmap bitmap,float x, float y) {
        // 因为要将图片放大，所以要根据放大的尺寸重新创建Bitmap，主要用于确定画布大小
        int x1 = (int) (bitmap.getWidth() * x);
        int y1 =  (int) (bitmap.getHeight() * y);
        if(x1==0||y1==0){
            return null;
        }
        Bitmap afterBitmap = Bitmap.createBitmap(
                x1,y1
                , bitmap.getConfig());
        Canvas canvas = new Canvas(afterBitmap);
        // 初始化Matrix对象
        Matrix matrix = new Matrix();
        // 根据传入的参数设置缩放比例
        matrix.setScale(x, y);
        Paint paint = new Paint();
        // 根据缩放比例，把图片draw到Canvas上
        canvas.drawBitmap(bitmap, matrix,paint);
        return afterBitmap;
    }

}
