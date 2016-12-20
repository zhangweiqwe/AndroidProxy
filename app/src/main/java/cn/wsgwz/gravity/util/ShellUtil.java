package cn.wsgwz.gravity.util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import org.dom4j.DocumentException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.config.Config;
import cn.wsgwz.gravity.config.EnumMyConfig;
import cn.wsgwz.gravity.config.xml.ConfigXml;
import cn.wsgwz.gravity.fragment.IsProgressEnum;
import cn.wsgwz.gravity.fragment.MainFragment;
import cn.wsgwz.gravity.fragment.log.LogContent;
import cn.wsgwz.gravity.helper.ApnDbHelper;
import cn.wsgwz.gravity.helper.DemoGetInstance;
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

                    case 1004:
                        LogContent.addItemAndNotify((String)msg.obj);
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
                        //LogUtil.printSS("---!=0");
                    }else {
                        //LogUtil.printSS("---=0");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what=1004;
                    msg.obj = e.getMessage().toString();
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what=1004;
                    msg.obj = e.getMessage().toString();
                    handler.sendMessage(msg);
                }


                if (process.exitValue() == 0) {
                    Message msg = new Message();
                    msg.obj = sb;
                    msg.what =1000;
                    handler.sendMessage(msg);
                }else {
                    Message msg = new Message();
                    msg.obj = sb;
                    msg.what = 1001;
                    handler.sendMessage(msg);
                }





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
    private static void configInitShell(ShellHelper shellHelper,Config config,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("main",Context.MODE_PRIVATE);
        String startStr =  shellHelper.getStartStr().replace(shellHelper.getDns(),config.getDns());
        sharedPreferences.edit().putString("start.sh",startStr).commit();
        shellHelper.setStartStr(startStr);
    }
    //public static boolean isStartOrStopDoing;
    //是否是脚本更随服务
    public static void  maybeExecShell(final boolean state, final MainActivity activity){


        final ShellHelper shellHelper = ShellHelper.getInstance();
        String str = null;
        if(state){
            try {
                configInitShell(shellHelper,getConfig(activity,false),activity);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            str = shellHelper.getStartStr();
            LogContent.addItem("脚本信息:"+" uid:"+shellHelper.getUid()+" proxyIp:"+shellHelper.getProxy()+" port:"+shellHelper.getPort()+ " dns:"+shellHelper.getDns());
            if(isProgressListenner!=null){
                isProgressListenner.doingSomeThing(IsProgressEnum.START);
            }
        }else {
            str = shellHelper.getStopStr();
            if(isProgressListenner!=null){
                isProgressListenner.doingSomeThing(IsProgressEnum.STOP);
            }
        }

        Toolbar toolbar = activity.getToolbar();


        final Menu menu = toolbar.getMenu();
        View actionView = LayoutInflater.from(activity).inflate(R.layout.toolbar_actionview_progress,null);
        menu.findItem(R.id.about_Appme).setVisible(true).setActionView(actionView);



        ShellUtil.execShell(activity, str, new OnExecResultListenner() {
            @Override
            public void onSuccess(StringBuffer sb) {
                if(isProgressListenner!=null){
                    isProgressListenner.finallyThat();
                }

                if(!(activity.getScreenSlidePagerAdapter().getItem(activity.getMy_viewPager().getCurrentItem()) instanceof MainFragment)){
                    menu.findItem(R.id.about_Appme).setVisible(false).setActionView(null);
                }else {
                    menu.findItem(R.id.about_Appme).setVisible(true).setActionView(null);
                }

                if (state) {
                    //Toast.makeText(activity, activity.getString(R.string.exec_start_ok), Toast.LENGTH_SHORT).show();

                    LogContent.addItem(activity.getString(R.string.exec_start_ok));
                    LogContent.addItemAndNotify("应用后台: "+ "11.22.33.44"+" (移动网络情况下)点击进入");
                } else {
                    //Toast.makeText(activity, activity.getString(R.string.exec_stop_ok), Toast.LENGTH_SHORT).show();
                    LogContent.addItemAndNotify(activity.getString(R.string.exec_stop_ok));
                }

                //  dialog.dismiss();
            }

            @Override
            public void onError(StringBuffer sb) {
                if(isProgressListenner!=null){
                    isProgressListenner.finallyThat();
                }
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
    //当正在执行开启或关闭
    public static interface IsProgressListenner{
        void doingSomeThing(IsProgressEnum isProgressEnum);
        void finallyThat();
    }
    private static IsProgressListenner isProgressListenner;
    public static void setIsProgressListenner(IsProgressListenner isProgressListenner){
        ShellUtil.isProgressListenner = isProgressListenner;
    }

    public static final Config getConfig(Context context,boolean isRemote) throws IOException, DocumentException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferenceMy.MAIN_CONFIG,Context.MODE_PRIVATE);
        Config config = null;
        if(true){
            String currentConfigPath = sharedPreferences.getString(SharedPreferenceMy.CURRENT_CONFIG_PATH,null);
            if(!isRemote){
                LogContent.addItemAndNotify(currentConfigPath);
                LogContent.addItemAndNotify("建议接入点:"+" apn:"+config.getApn_apn()+" 代理:"+config.getApn_proxy()+" 端口:"+config.getApn_port());
            }
            if(currentConfigPath==null){
                return null;
            }
            File file = new File(currentConfigPath);
            if(file.getPath().startsWith("/"+FileUtil.ASSETS_CONFIG_PATH)){
                config = ConfigXml.read(context.getAssets().open( file.getAbsolutePath().replaceFirst("/","")));
            }else if(file.exists()){
                FileInputStream fileInputStream = new FileInputStream(file);
                config = ConfigXml.read(fileInputStream);
            }else {
                List<EnumMyConfig> listEnum = EnumMyConfig.getMeConfig();
                boolean b = false;
                if(listEnum!=null){
                    for(int i=0;i<listEnum.size();i++){
                        String pathName = file.getAbsolutePath();
                        if(pathName.contains("/")){
                            pathName = pathName.replace("/","");
                        }
                        if(listEnum.get(i).getName().equals(pathName)){
                            String values = listEnum.get(i).getValues();
                            config = ConfigXml.read(new ByteArrayInputStream(values.getBytes("utf-8")));
                            b = true;
                        };
                    }

                }

                if(!b){
                    Toast.makeText(context, context.getResources().getText(R.string.config_not_fund), Toast.LENGTH_SHORT).show();
                    return null;
                }

            }
        }else {
            InputStream in = context.getAssets().open("text.xml");
            config = ConfigXml.read(in);
        }
        return  config;
    }

}
