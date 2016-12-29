package cn.wsgwz.gravity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.wsgwz.gravity.fragment.log.LogContent;
import cn.wsgwz.gravity.util.FileUtil;
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
    private RelativeLayout main_RL;

    public FirstUseInitHelper(MainActivity mainActivity, SharedPreferences sharedPreferences, RelativeLayout main_RL) {
        this.mainActivity = mainActivity;
        this.sharedPreferences = sharedPreferences;
        this.main_RL = main_RL;
    }

    public void initFileToSdcard(){
        try {
            UnzipFromAssets.unZip( mainActivity,  FileUtil.CONFIG_FILE_NAME,  FileUtil.APP_APTH_CONFIG,  true);
            //UnzipFromAssets.unZip( mainActivity,  "xunlei.zip", FileUtil.APP_APTH_CONFIG+"/xunlei",  true);
           // UnzipFromAssets.unZip( mainActivity,  "xunlei.zip", "/sdcard/Gravity/xunlei",  true);
           // UnzipFromAssets.unZip( mainActivity,  "dc.zip", FileUtil.APP_APTH_CONFIG+"/dc",  true);
            UnzipFromAssets.unZip( mainActivity,  FileUtil.ABC_FILE_NAME, FileUtil.APP_APTH_CONFIG+"/Gravity",  true);
            UnzipFromAssets.unZip( mainActivity,  FileUtil.JUME_FILE_NAME, FileUtil.APP_APTH_CONFIG+"/Jume",  true);
           /* try {
                ZipUtils.upZipFile(new File("file:///android_asset/abc.zip"),FileUtil.SD_APTH);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ZipUtils.upZipFile(new File("file:///android_asset/Jume.zip"),FileUtil.SD_APTH);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            sharedPreferences.edit().putBoolean(SharedPreferenceMy.IS_INIT_SYSTEM,true).commit();
            final Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case 1000:
                            initSystemFile();
                            break;
                        case 1001:
                            LogContent.addItemAndNotify(mainActivity.getString(R.string.get_root_permission_error));
                            break;
                    }
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process process = null;
                    DataOutputStream dataOutputStream = null;
                    BufferedReader errorBr = null;
                    try {
                        process = Runtime.getRuntime().exec("su");
                        dataOutputStream = new DataOutputStream(process.getOutputStream());
                        dataOutputStream.writeBytes("exit\n");
                        dataOutputStream.flush();
                        process.waitFor();
                        //process.waitFor();
                        errorBr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        String line = null;
                        while ((line=errorBr.readLine())!=null){
                            if(line.equals("[-] Unallowed user")){
                                handler.sendEmptyMessage(1001);
                                return;
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        process.destroy();
                        if(dataOutputStream!=null){
                            try {
                                dataOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if(errorBr!=null){
                            try {
                                errorBr.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    handler.sendEmptyMessage(1000);
                }
            }).start();

        } catch (IOException e) {
        }
    }
    private void initSystemFile(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mainActivity);
        builder.setMessage(mainActivity.getString(R.string.start_init_app_util));
        final Dialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.payDialogStyleAnimation);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        String drectoryName = mainActivity.getResources().getString(R.string.app_name);
        /*final String str =
                "mount -o remount ,rw /"+"\n"+
                        "mkdir /system/xbin/"+drectoryName+"\n"+
                        "mkdir /system/xbin/Jume"+"\n"+
                        "cp "+FileUtil.SD_APTH_CONFIG+"/"+FileUtil.ABC_FILE_NAME+" "+"/system/xbin/"+drectoryName+"\n"+
                        "cd /system/xbin/"+drectoryName+"\n"+
                        "unzip -o "  +FileUtil.ABC_FILE_NAME  +"\n"+
                        "chmod -R 777  /system/xbin/"+drectoryName+"\n"+
                        "cd ..\n"+
                        "cp "+FileUtil.SD_APTH_CONFIG+"/"+FileUtil.JUME_FILE_NAME+" "+"/system/xbin/Jume"+"\n"+
                        "cd /system/xbin/Jume"+"\n"+
                        "unzip -o "  +FileUtil.JUME_FILE_NAME  +"\n"+
                        "chmod -R 777  /system/xbin/Jume";*/
        final String str =
                "mount -o remount ,rw /"+"\n"+
                        "cp -r "+ FileUtil.APP_APTH_CONFIG+"/Gravity"+" "+"/system/xbin/"+drectoryName+"\n"+
                        "cd /system/xbin/"+drectoryName+"\n"+
                        //"unzip -o "  +FileUtil.ABC_FILE_NAME  +"\n"+
                        "chmod -R 777  /system/xbin/"+drectoryName+"\n"+
                        "cd ..\n"+
                        "cp -r "+ FileUtil.APP_APTH_CONFIG+"/Jume"+" "+"/system/xbin/Jume"+"\n"+
                        "cd /system/xbin/Jume"+"\n"+
                        "chmod -R 777  /system/xbin/Jume";


        main_RL.postDelayed(new Runnable() {
            @Override
            public void run() {
                ShellUtil.execShell(mainActivity, str, new OnExecResultListenner() {
                    @Override
                    public void onSuccess(StringBuffer sb) {
                        sharedPreferences.edit().putBoolean(SharedPreferenceMy.IS_INIT_SYSTEM,true).commit();
                        dialog.dismiss();
                        Toast.makeText(mainActivity,mainActivity.getString(R.string.init_app_util_success),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(StringBuffer sb) {
                        dialog.dismiss();
                        Toast.makeText(mainActivity,mainActivity.getString(R.string.init_app_util_error),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        },2000);


    }

}
