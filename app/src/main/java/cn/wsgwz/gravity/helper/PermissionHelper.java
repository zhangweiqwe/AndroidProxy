package cn.wsgwz.gravity.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;

import java.io.IOException;

import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Wsgwz on 2016/12/30.
 * mainactivity将会用到 主要用于在mainActivity请求之前获取到
 * android.permission.READ_EXTERNAL_STORAGE
 * android.permission.WRITE_EXTERNAL_STORAGE
 * 权限
 */

public class PermissionHelper {
    private Context context;


    public PermissionHelper(Context context) {
        this.context = context;
    }

    public void requestPermissionsForMainActiivty(){
        //PackageManager packageManager = context.getPackageManager();
        //PackageInfo packageInfo;
        Runtime runtime =Runtime.getRuntime();
        try {
            runtime.exec(" pm grant cn.wsgwz.gravity  android.permission.READ_EXTERNAL_STORAGE android.permission.WRITE_EXTERNAL_STORAGE\n"+
            "exit\n").waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
   /* private void permissionRequestDemo()  {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String[] persimmsions = packageInfo.requestedPermissions;
        PermissionInfo[] persimmsions2 = packageInfo.permissions;
        int[] persimmsions3 = packageInfo.requestedPermissionsFlags;


        for(int i=0;i<persimmsions.length;i++){
            LogUtil.printSS(i+"----->"+persimmsions[i]+"<------");
        }


        if(persimmsions2!=null){
            for(int i=0;i<persimmsions2.length;i++){
                LogUtil.printSS(i+"--------->"+persimmsions[i]+"<------");
            }
        }


        for(int i=0;i<persimmsions3.length;i++){
            LogUtil.printSS(i+"--------------->"+persimmsions[i]+"<------");
        }

    }*/
}
