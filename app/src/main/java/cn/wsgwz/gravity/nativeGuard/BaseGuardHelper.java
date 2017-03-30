package cn.wsgwz.gravity.nativeGuard;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.wsgwz.gravity.util.FileUtil;

/**
 * Created by Administrator on 2017/3/30 0030.
 */

public abstract  class BaseGuardHelper {
    protected String getAbiFolder() {
        String abi;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abi = Build.SUPPORTED_ABIS[0];
        } else {
            //noinspection deprecation
            abi = Build.CPU_ABI;
        }
        String folder = null;
        if (abi.contains("armeabi-v7a")) {
            folder = "armeabi-v7a";
        } else if (abi.contains("x86_64")) {
            folder = "x86_64";
        } else if (abi.contains("x86")) {
            folder = "x86";
        } else if (abi.contains("armeabi")) {
            folder = "armeabi";
        }
        return folder;
    }
    protected abstract void start(Context context, NativeStatusListenner nativeStatusListenner);

    protected boolean cpAssetsToPath(String aseetsFilePath,String cpToPath){
        if(aseetsFilePath==null||cpToPath==null){
            return false;
        }

        InputStream inputStream = getClass().getResourceAsStream(aseetsFilePath);
        try {
            FileOutputStream fileOutputStream  = new FileOutputStream(cpToPath);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len=inputStream.read(buffer))!=-1){
                fileOutputStream.write(buffer,0,len);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    protected abstract void stop(Context context, NativeStatusListenner nativeStatusListenner);


}
