package cn.wsgwz.gravity.demo.demoSync;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class Thread2 implements Runnable {
    private int currentCount = 0;
    @Override
    public void run() {
        while (true){
            synchronized (DemoSynchronized.LOCK_OBJ){
                DemoSynchronized.syncLog(++currentCount);
            }
            DemoSynchronized.syncLogThread2(currentCount);
        }

    }
}
