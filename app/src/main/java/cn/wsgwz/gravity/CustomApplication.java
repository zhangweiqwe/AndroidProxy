package cn.wsgwz.gravity;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import cn.wsgwz.gravity.helper.ShellHelper;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.NativeUtils;
import cn.wsgwz.gravity.util.ShellUtil;

/**
 * Created by Jeremy Wang on 2016/10/26.
 */

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ShellHelper.init(this);
    }

    @Override
    public void onTerminate() {
        //NativeUtils.fork();
        super.onTerminate();
        // 程序终止的时候执行
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }

    int i=0;
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        LogUtil.printSS("S         onTrimMemory ");

    }

}
