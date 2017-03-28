package cn.wsgwz.gravity.demo;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.wsgwz.gravity.R;

public class Main4Activity extends Activity {


    private MathGLSurfaceView myMathGLSurfaceView;
    private MathGLSurfaceViewRender mathGLSurfaceViewRender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        myMathGLSurfaceView = (MathGLSurfaceView) findViewById(R.id.myMathGLSurfaceView);
        mathGLSurfaceViewRender = new MathGLSurfaceViewRender();
        myMathGLSurfaceView.setRenderer(mathGLSurfaceViewRender);


    }

    @Override
    protected void onResume() {
        super.onResume();
        myMathGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myMathGLSurfaceView.onPause();
    }
}
