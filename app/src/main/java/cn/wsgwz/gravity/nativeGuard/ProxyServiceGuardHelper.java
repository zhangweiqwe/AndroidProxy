package cn.wsgwz.gravity.nativeGuard;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.ShellUtil;
import cn.wsgwz.gravity.util.aboutShell.Command;

/**
 * Created by Administrator on 2017/3/30 0030.
 */

public class ProxyServiceGuardHelper extends BaseGuardHelper {


    private static final String START_GUARD =
            "./data/data/cn.wsgwz.gravity/watchdog_local_server_socket.so  cn.wsgwz.gravity/cn.wsgwz.gravity.nativeGuard.OnePixelActivity cn.wsgwz.gravity.native.ation.START /data/data/cn.wsgwz.gravity/nativeLockFile 8";

    private static final ProxyServiceGuardHelper proxyServiceGuardHelper = new ProxyServiceGuardHelper();
    public static final ProxyServiceGuardHelper getInstance(){
            return proxyServiceGuardHelper;
    }
    private static final String TAG = ProxyServiceGuardHelper.class.getSimpleName();
    @Override
    public void start(Context context, final NativeStatusListenner nativeStatusListenner) {
        if(context==null||nativeStatusListenner==null){
            return;
        }
        int appVersionCode = 0;
        try {
            appVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File("/data/data/cn.wsgwz.gravity/"+"watchdog_local_server_socket.so");
        if(!file.exists()){
            boolean b1 = cpAssetsToPath("/assets/"+getAbiFolder()+"/"+"watchdog_local_server_socket.so",
                    FileUtil.CACHE_DIR+"/"+"watchdog_local_server_socket.so");
            Log.d(TAG,b1 +""+appVersionCode);

            if(!b1){
                if (nativeStatusListenner!=null){
                    nativeStatusListenner.onChange(NativeStatusListenner.StatusEnum.START_ERROR,new StringBuilder("!b1"));
                }
                return;
            }
            String execStr  = "#! /system/bin/sh\n"+
                    "mount -o remount ,rw /"+"\n"+
                    /*"cp -f "+  FileUtil.CACHE_DIR+"/"+"watchdog_local_server_socket.so"+" "+"/data/data"+"\n"+
                    "cd /data/data/"+"\n"+*/
                    /*"cd /data/data/cn.wsgwz.gravity/"+"\n"+*/
                    "chmod 777 /data/data/cn.wsgwz.gravity/watchdog_local_server_socket.so";
            ShellUtil.execShell(context, execStr, new OnExecResultListenner() {
                @Override
                public void onSuccess(StringBuffer sb) {
                    if (nativeStatusListenner!=null){
                        Command command = new Command(START_GUARD);
                        command.execute();
                        int result = command.getExitCode();
                        if (result == 0) {
                            nativeStatusListenner.onChange(NativeStatusListenner.StatusEnum.START_OK,null);
                        }else {
                            nativeStatusListenner.onChange(NativeStatusListenner.StatusEnum.START_ERROR,new StringBuilder(result+""));
                        }
                    }
                }

                @Override
                public void onError(StringBuffer sb) {
                    if (nativeStatusListenner!=null){
                        nativeStatusListenner.onChange(NativeStatusListenner.StatusEnum.START_ERROR,new StringBuilder().append(sb).append("---> onError"));
                    }
                }
            });
        }else {
            if (nativeStatusListenner!=null){

                Command command = new Command(START_GUARD);
                command.execute();
                int result = command.getExitCode();
                if (result == 0) {
                    nativeStatusListenner.onChange(NativeStatusListenner.StatusEnum.START_OK,null);
                }else {
                    nativeStatusListenner.onChange(NativeStatusListenner.StatusEnum.START_ERROR,null);
                }
            }
        }

    }

    @Override
    public void stop(Context context, NativeStatusListenner nativeStatusListenner) {
        if(context==null||nativeStatusListenner==null){
            return;
        }
        if (nativeStatusListenner!=null){

            Command command = new Command("killall watchdog_local_server_socket.so");//busybox pkill -SIGINT watchdog_local_server_socket.so
            command.execute();
            int result = command.getExitCode();
            if (result == 0) {
                nativeStatusListenner.onChange(NativeStatusListenner.StatusEnum.STOP_OK,null);
            }else {
                nativeStatusListenner.onChange(NativeStatusListenner.StatusEnum.STOP_ERROR,new StringBuilder().append(result));
            }
        }

    }
}
