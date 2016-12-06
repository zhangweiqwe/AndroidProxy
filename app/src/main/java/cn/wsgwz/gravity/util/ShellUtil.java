package cn.wsgwz.gravity.util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.fragment.MainFragment;
import cn.wsgwz.gravity.fragment.log.LogContent;
import cn.wsgwz.gravity.helper.ShellHelper;
import cn.wsgwz.gravity.service.ProxyService;

public class ShellUtil {

    //是否显示toast
    private static final boolean showToast = false;
    public static void execShell(final Context context, final String shellStr,final OnExecResultListenner onExecResultListenner){
        if(context==null||shellStr==null){
            return;
        }
        if(!checkRoot()){
            Toast.makeText(context,context.getString(R.string.root_permission_error),Toast.LENGTH_SHORT).show();
            if (onExecResultListenner!=null){
                onExecResultListenner.onError(new StringBuffer().append(context.getString(R.string.root_permission_error)));
            }
            return;
        }
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                StringBuffer sb = null;
                switch (msg.what){
                    case 1000:
                        sb= ((StringBuffer)msg.obj);
                        if (onExecResultListenner!=null){
                            onExecResultListenner.onSuccess(sb);
                        }
                        if(context!=null&&showToast){
                            Toast.makeText(context,"OK："+sb, Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 1001:
                        sb = ((StringBuffer)msg.obj);
                        if (onExecResultListenner!=null){
                            onExecResultListenner.onError(sb);
                        }
                        if(context!=null&&showToast){
                            Toast.makeText(context,"异常："+sb, Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
                super.handleMessage(msg);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sb =new StringBuffer();
                String str1 = "#!/system/bin/sh\n"+shellStr+"\n";
                Process process = null;

                InputStream localInputStream =null;
                DataOutputStream localDataOutputStream =null;
                try {
                    process = Runtime.getRuntime().exec("su");
                    localInputStream = process.getErrorStream();
                    localDataOutputStream = new DataOutputStream(process.getOutputStream());
                    localDataOutputStream.writeUTF(str1);
                    localDataOutputStream.writeBytes("exit\n");
                    localDataOutputStream.flush();
                    process.waitFor();
                    if (process.exitValue() != 0) {

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    LogContent.addItemAndNotify(e.getMessage().toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogContent.addItemAndNotify(e.getMessage().toString());
                }
                Message msg = new Message();
                msg.obj = sb;
                if (process.exitValue() == 0) {
                    msg.what =1000;
                }else {
                    msg.what = 1001;
                }
                handler.sendMessage(msg);



            }
        }).start();
    }

    private static boolean checkRoot(){

        boolean b = true;
            try {
                Runtime.getRuntime().exec("su");
            }catch (IOException e) {
                e.printStackTrace();
                b=false;
                LogContent.addItemAndNotify(e.getMessage().toString());
            }
        return b;

    }

    public static boolean isStartOrStopDoing;
    //是否是脚本更随服务
    public static void  maybeExecShell(final boolean state, final MainActivity activity){
        ShellHelper shellHelper = ShellHelper.getInstance();


        Toolbar toolbar = activity.getToolbar();
        final Menu menu = toolbar.getMenu();
        final View actionView = LayoutInflater.from(activity).inflate(R.layout.toolbar_actionview_progress,null);
        menu.findItem(R.id.about_Appme).setVisible(true).setActionView(actionView);
        isStartOrStopDoing=true;

        String str = null;
        if(state){
            str = shellHelper.getStartStr();
        }else {
            str = shellHelper.getStopStr();
        }

        ShellUtil.execShell(activity, shellHelper.getStartStr(), new OnExecResultListenner() {
            @Override
            public void onSuccess(StringBuffer sb) {
                isStartOrStopDoing=false;

                if(!(activity.getScreenSlidePagerAdapter().getItem(activity.getMy_viewPager().getCurrentItem()) instanceof MainFragment)){
                    menu.findItem(R.id.about_Appme).setVisible(false).setActionView(null);
                }else {
                    menu.findItem(R.id.about_Appme).setVisible(true).setActionView(null);
                }

                if (state) {
                    //Toast.makeText(activity, activity.getString(R.string.exec_start_ok), Toast.LENGTH_SHORT).show();
                    LogContent.addItem("脚本信息:"+" uid:"+ShellHelper.getUid()+" proxyIp:"+ShellHelper.getProxy()+" port:"+ShellHelper.getPort()+
                            " dns:"+ShellHelper.getDns());
                    LogContent.addItem(activity.getString(R.string.exec_start_ok));
                    LogContent.addItemAndNotify("应用后台: "+ ProxyService.BACKGROUND_HOST+" (移动网络情况下)点击进入");
                } else {
                    //Toast.makeText(activity, activity.getString(R.string.exec_stop_ok), Toast.LENGTH_SHORT).show();
                    LogContent.addItemAndNotify(activity.getString(R.string.exec_stop_ok));
                }

                //  dialog.dismiss();
            }

            @Override
            public void onError(StringBuffer sb) {
                isStartOrStopDoing=false;
                if(!(activity.getScreenSlidePagerAdapter().getItem(activity.getMy_viewPager().getCurrentItem()) instanceof MainFragment)){
                    menu.findItem(R.id.about_Appme).setVisible(false).setActionView(null);
                }else {
                    menu.findItem(R.id.about_Appme).setVisible(true).setActionView(null);
                }


                if (state) {
                    //Toast.makeText(activity, activity.getString(R.string.exec_start_error), Toast.LENGTH_SHORT).show();
                    LogContent.addItemAndNotify(activity.getString(R.string.exec_start_error));
                } else {
                    //Toast.makeText(activity, activity.getString(R.string.exec_stop_error), Toast.LENGTH_SHORT).show();
                    LogContent.addItemAndNotify(activity.getString(R.string.exec_stop_error));
                }
                // dialog.dismiss();
            }
        });
    }

}
