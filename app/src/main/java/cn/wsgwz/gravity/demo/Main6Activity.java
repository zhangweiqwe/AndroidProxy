package cn.wsgwz.gravity.demo;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.demo.demoSync.DemoSynchronized;

public class Main6Activity extends Activity implements Runnable{
    private static final String TAG = Main6Activity.class.getSimpleName();
    public static final boolean DEMO = false;

    private static final int MSG_WHAT_OX10 =0x10;

    private Handler handler;
    private Thread handlerDemoThread;

    private int handlerMessageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);



        handlerDemoThread = new Thread(this);
        Log.d(TAG,handlerDemoThread.getName()+"---"+handlerDemoThread.getId());
        handlerDemoThread.start();

        //DemoSynchronized.getInstance().begin();

    }

    @Override
    public void run() {
        Looper.prepare();
        Log.d(TAG,"Looper.myLooper() "+Looper.myLooper());
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_WHAT_OX10:
                        handlerMessageCount++;
                        Log.d(TAG,"handlerMessageCount "+handlerMessageCount);
                        Toast.makeText(Main6Activity.this,handlerMessageCount+"--",Toast.LENGTH_SHORT).show();
                        break;
                }
                super.handleMessage(msg);
            }
        };
        sendMsg();

        Looper.loop();


    }

    private void sendMsg(){
        int z= 0;
        while (z<5){
            z++;
            try {
                Thread.sleep(1000);
                handler.sendEmptyMessage(MSG_WHAT_OX10);

                Log.d(TAG,"handlerMessageCount  sendMsg"+handlerMessageCount);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
