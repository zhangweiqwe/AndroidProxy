package cn.wsgwz.gravity;

import android.app.Application;
import android.os.Environment;

import java.io.File;

import cn.wsgwz.gravity.helper.ShellHelper;
import cn.wsgwz.gravity.util.LogUtil;
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
        super.onTerminate();
        // 程序终止的时候执行
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }
}
