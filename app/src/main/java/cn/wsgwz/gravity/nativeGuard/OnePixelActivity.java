package cn.wsgwz.gravity.nativeGuard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import cn.wsgwz.gravity.service.ProxyService;

public class OnePixelActivity extends Activity {

    private static final String TAG  = OnePixelActivity.class.getSimpleName();
    //private ProxyServiceGuardHelper proxyServiceGuardHelper = ProxyServiceGuardHelper.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setTitle("Gravity 重启中！！");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.width = 1;
        params.height = 1;
        window.setAttributes(params);

        Intent intent = getIntent();
        String action = null;
        if(intent!=null){
            action = intent.getAction();
        }

      /*  startService(new Intent(this, ProxyService.class));
        finish();*/

      /*  proxyServiceGuardHelper.stop(this, new NativeStatusListenner() {
            @Override
            public void onChange(StatusEnum statusEnum, StringBuilder sbMessage) {
                Log.d("daemon---->stop Pixcel",""+statusEnum.toString()+(sbMessage==null?"null":sbMessage.toString()));
                proxyServiceGuardHelper.start(OnePixelActivity.this, new NativeStatusListenner() {
                    @Override
                    public void onChange(StatusEnum statusEnum, StringBuilder sbMessage) {
                        Log.d("daemon---->start Pixcel",""+statusEnum.toString()+(sbMessage==null?"null":sbMessage.toString()));
                        finish();
                    }
                });
            }
        });*/


        Log.d(TAG,"  onCreate"+action);
    }
}
