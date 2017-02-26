package cn.wsgwz.gravity.helper;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.support.design.widget.TabLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.http.conn.scheme.HostNameResolver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.fragment.log.LogContent;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;
import cn.wsgwz.gravity.util.UnzipFromAssets;
import cn.wsgwz.gravity.util.ZipUtils;

/**
 * Created by Jeremy Wang on 2016/12/27.
 */

public class FirstUseInitHelper {
    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;
    public FirstUseInitHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.sharedPreferences = mainActivity.mainActivityHelper.getSharedPreferences();
    }

    public void initFileToSdcard(){
        try {
            UnzipFromAssets.unZip( mainActivity,  FileUtil.CONFIG_FILE_NAME,  FileUtil.APP_APTH_CONFIG,  true);
            //UnzipFromAssets.unZip( mainActivity,  FileUtil.ABC_FILE_NAME, FileUtil.APP_APTH_CONFIG+"/Gravity",  true);
            UnzipFromAssets.unZip( mainActivity,  FileUtil.JUME_FILE_NAME, FileUtil.APP_APTH_CONFIG+"/Jume",  true);



        } catch (IOException e) {
        }
    }
    public void initFileToSystem(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mainActivity);
        builder.setMessage(mainActivity.getString(R.string.start_init_app_util));
        final Dialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.payDialogStyleAnimation);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        String drectoryName = mainActivity.getResources().getString(R.string.app_name);
        final String str =
                "#! /system/bin/sh\n"+
                "mount -o remount ,rw /"+"\n"+
                      /*  "cp -r "+ FileUtil.APP_APTH_CONFIG+"/Gravity"+" "+"/system/xbin/"+drectoryName+"\n"+
                        "cd /system/xbin/"+drectoryName+"\n"+
                        //"unzip -o "  +FileUtil.ABC_FILE_NAME  +"\n"+
                        "chmod -R 777  /system/xbin/"+drectoryName+"\n"+
                        "cd ..\n"+*/
                        "cp -r "+ FileUtil.APP_APTH_CONFIG+"/Jume"+" "+"/system/xbin/Jume"+"\n"+
                        "cd /system/xbin/Jume"+"\n"+
                        "chmod -R 777  /system/xbin/Jume";



                ShellUtil.execShell(mainActivity, str, new OnExecResultListenner() {
                    @Override
                    public void onSuccess(StringBuffer sb) {
                        dialog.dismiss();
                        Toast.makeText(mainActivity,mainActivity.getString(R.string.init_app_util_success)+sb.toString(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(StringBuffer sb) {
                        dialog.dismiss();
                        Toast.makeText(mainActivity,mainActivity.getString(R.string.init_app_util_error)+
                                sb.toString(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
