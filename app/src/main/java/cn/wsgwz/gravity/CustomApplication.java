package cn.wsgwz.gravity;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import android.widget.Toast;

/**
 * Created by Jeremy Wang on 2016/10/26.
 */

public class CustomApplication extends Application {
    public static final String PACKAGE_NAME = "cn.wsgwz.gravity";
    private SettingHelper settingHelper = SettingHelper.getInstance();
    @Override
    public void onCreate() {
        super.onCreate();
        ObjTemp objTemp = getCurProcessName(this);
        if(objTemp.getCurrentProgressName().equals("cn.wsgwz.gravity")){
            ShellHelper.init(this);
            if(!objTemp.isStartProxyService()){
                settingHelper.setIsStart(this,false);
            }
        }
        //Toast.makeText(this,getCurProcessName(this)+"\n"+objTemp+"",Toast.LENGTH_SHORT).show();
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
    private  ObjTemp getCurProcessName(Context context) {
        ObjTemp objTemp = new ObjTemp();
        //List<Object> list = new ArrayList<>();
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                 objTemp.setCurrentProgressName((appProcess.processName));
            }else if(appProcess.processName.equals("cn.wsgwz.gravity:remoteProxy")){
                objTemp.setStartProxyService(true);
            }
        }
        return objTemp;
    }
    private final class ObjTemp{
        private boolean isStartProxyService;
        private String currentProgressName;

        public boolean isStartProxyService() {
            return isStartProxyService;
        }

        public void setStartProxyService(boolean startProxyService) {
            isStartProxyService = startProxyService;
        }

        public String getCurrentProgressName() {
            return currentProgressName;
        }

        public void setCurrentProgressName(String currentProgressName) {
            this.currentProgressName = currentProgressName;
        }

        @Override
        public String toString() {
            return "ObjTemp{" +
                    "isStartProxyService=" + isStartProxyService +
                    ", currentProgressName='" + currentProgressName + '\'' +
                    '}';
        }
    }
}
