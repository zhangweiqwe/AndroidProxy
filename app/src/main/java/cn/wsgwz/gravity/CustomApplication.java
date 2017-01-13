package cn.wsgwz.gravity;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import cn.wsgwz.gravity.helper.SettingHelper;
import cn.wsgwz.gravity.helper.ShellHelper;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.NativeUtils;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;
import android.app.ActivityManager;
import android.util.Log;

/**
 * Created by Jeremy Wang on 2016/10/26.
 */

public class CustomApplication extends Application {
    public static final String PACKAGE_NAME = "cn.wsgwz.gravity";
    private SettingHelper settingHelper = SettingHelper.getInstance();
    @Override
    public void onCreate() {
        super.onCreate();
        if(getCurProcessName(this).equals("cn.wsgwz.gravity")){
           // LogUtil.printSS("CustomApplication  a");
            ShellHelper.init(this);
            /*SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceMy.CONFIG, Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(SharedPreferenceMy.SERVICE_IS_START,false).commit();*/
            settingHelper.setIsStart(this,false);
        }
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
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }

        }
        return null;
    }
}
