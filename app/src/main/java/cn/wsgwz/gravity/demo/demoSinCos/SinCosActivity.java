package cn.wsgwz.gravity.demo.demoSinCos;

import android.app.Activity;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.lang.reflect.Field;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.util.LogUtil;

public class SinCosActivity extends Activity {
    public static final boolean DMEO = true;
    private SinCosSurfaceview sinCosSurfaceview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sin_cos);
        sinCosSurfaceview = (SinCosSurfaceview) findViewById(R.id.sinCosSurfaceview);
        LogUtil.printSS("-->"+getStatusBarHeight());
    }

    private int getStatusBarHeight() {
        Class<?> c = null;

        Object obj = null;

        Field field = null;

        int x = 0, sbar = 0;

        try {

            c = Class.forName("com.android.internal.R$dimen");

            obj = c.newInstance();

            field = c.getField("status_bar_height");

            x = Integer.parseInt(field.get(obj).toString());

            sbar = getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {

            e1.printStackTrace();

        }

        return sbar;
    }
}
