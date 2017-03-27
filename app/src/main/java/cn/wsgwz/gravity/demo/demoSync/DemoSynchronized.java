package cn.wsgwz.gravity.demo.demoSync;

import android.util.Log;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class DemoSynchronized {
    private static final String TAG  = DemoSynchronized.class.getSimpleName();
    public static final Object LOCK_OBJ = new Object();

    private Thread thread1;
    private Thread thread2;


    private DemoSynchronized(){}
    private static final DemoSynchronized demoSynchronized = new DemoSynchronized();
    public static final synchronized DemoSynchronized  getInstance(){
        return demoSynchronized;
    }

    public static final void syncLog(int currentCount){
        Log.d("DemoSynchronized","-->"+Thread.currentThread().getId()+"<------>"+Thread.currentThread().getName()+"<--"+"---->"+currentCount+"<-");
    }
    public static final void syncLogThread1(int currentCount){
        Log.d("DemoSynchronized","-->11"+"<---->"+currentCount+"<-");
    }
    public static final void syncLogThread2(int currentCount){
        Log.d("DemoSynchronized","-->22"+"<---->"+currentCount+"<-");
    }

    public void begin(){
        thread1 = new Thread(new Thread1());
        thread2 = new Thread(new Thread2());
        thread1.start();
        thread2.start();
    }
}
