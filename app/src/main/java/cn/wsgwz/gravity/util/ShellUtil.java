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
import android.support.design.widget.Snackbar;
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



    public static final void execShell(final Context context, final String shellStr, final OnExecResultListenner onExecResultListenner) {
        if (context == null || shellStr == null) {
            return;
        }
        if (!isRoot()) {
            if (onExecResultListenner != null) {
                onExecResultListenner.onError(new StringBuffer().append(context.getString(R.string.donot_have_root_permission_)));
            }
            return;
        }
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                StringBuffer sb = null;
                switch (msg.what) {
                    case 1000:
                        sb = ((StringBuffer) msg.obj);
                        if (onExecResultListenner != null) {
                            onExecResultListenner.onSuccess(sb);
                        }
                        break;
                    case 1001:
                        sb = ((StringBuffer) msg.obj);
                        if (onExecResultListenner != null) {
                            onExecResultListenner.onError(sb);
                        }
                        break;
                }
                super.handleMessage(msg);
            }
        };


        new Thread(new Runnable() {
            @Override
            public void run() {

                StringBuffer sb = new StringBuffer();
                String str1 = "#!/system/bin/sh\n" + shellStr + "\n";
                Process process = null;

                DataOutputStream localDataOutputStream = null;
                BufferedReader errorBr = null;
                try {
                    process = Runtime.getRuntime().exec("su");
                    localDataOutputStream = new DataOutputStream(process.getOutputStream());
                    localDataOutputStream.writeUTF(str1);
                    localDataOutputStream.writeBytes("exit\n");
                    localDataOutputStream.flush();
                    process.waitFor();

                   /* errorBr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line = null;
                    while ((line=errorBr.readLine())!=null){
                        if(line.equals("[-] Unallowed user")){
                            Message msg = Message.obtain();
                            msg.what = 1001;
                            msg.obj = line;
                            handler.sendMessage(msg);
                            return;
                        }
                    }*/

                    if (process.exitValue() == 0) {
                        Message msg = new Message();
                        msg.obj = new StringBuffer().append("0");
                        msg.what = 1000;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.obj =  new StringBuffer().append("!0");
                        msg.what = 1001;
                        handler.sendMessage(msg);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 1001;
                    msg.obj = new StringBuffer().append(e.getMessage().toString());
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 1001;
                    msg.obj = new StringBuffer().append(e.getMessage().toString()) ;
                    handler.sendMessage(msg);
                } finally {
                    process.destroy();
                    if(localDataOutputStream!=null){
                        try {
                            localDataOutputStream.close();
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


            }
        }).start();
    }



    private static final boolean isRoot(){
        boolean bool = false;
        try{
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())){
                bool = false;
            } else {
                bool = true;
            }
        } catch (Exception e) {

        }
        return bool;
    }



    private static void configInitShell(ShellHelper shellHelper,Config config,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferenceMy.MAIN,Context.MODE_PRIVATE);
        String startStr =  shellHelper.getStartStr().replace(shellHelper.getDns(),config.getDns());
        sharedPreferences.edit().putString(SharedPreferenceMy.START_SH,startStr).commit();
        shellHelper.setStartStr(startStr);
    }
    //public static boolean isStartOrStopDoing;
    //是否是脚本更随服务
    public static void  maybeExecShell(final boolean state, final MainActivity activity){


        final ShellHelper shellHelper = ShellHelper.getInstance();
        String str = null;
        Config config;
        if(state){
            try {
                config = getConfig(activity,false);
                if(config==null){
                    Toast.makeText(activity,activity.getString(R.string.config_not_fund),Toast.LENGTH_SHORT).show();
                    return;
                }
                configInitShell(shellHelper,config,activity);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            str = shellHelper.getStartStr();
            LogContent.addItem("脚本信息：\t"+"\rDNS：\r"+shellHelper.getDns());
            //LogContent.addItem("脚本信息:"+" uid:"+shellHelper.getUid()+" proxyIp:"+shellHelper.getProxy()+" port:"+shellHelper.getPort()+ " dns:"+shellHelper.getDns());
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

                if(!(activity.getFragmentPagerAdapter().getItem(activity.getMy_viewPager().getCurrentItem()) instanceof MainFragment)){
                    menu.findItem(R.id.about_Appme).setVisible(false).setActionView(null);
                }else {
                    menu.findItem(R.id.about_Appme).setVisible(true).setActionView(null);
                }

                if (state) {
                    //Toast.makeText(activity, activity.getString(R.string.exec_start_ok), Toast.LENGTH_SHORT).show();

                    LogContent.addItem(activity.getString(R.string.exec_start_ok));
                    LogContent.addItemAndNotify("应用后台：\t"+ "http://11.22.33.44"+"\r(点击进入)");
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
                if(!(activity.getFragmentPagerAdapter().getItem(activity.getMy_viewPager().getCurrentItem()) instanceof MainFragment)){
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
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferenceMy.CONFIG,Context.MODE_PRIVATE);
        Config config = null;
        if(true){
            String currentConfigPath = sharedPreferences.getString(SharedPreferenceMy.CURRENT_CONFIG_PATH,null);
            /*if(!isRemote){
                LogContent.addItemAndNotify(currentConfigPath);
            }*/
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

        if(!isRemote){
            if(config!=null){
                //LogContent.addItemAndNotify("建议接入点：\t"+"\rAPN：\r"+config.getApn_apn()+"\t代理：\r"+config.getApn_proxy()+"\t端口：\r"+config.getApn_port());
                LogContent.addItemAndNotify("建议接入点：\t"+"\rAPN：\r"+config.getApn_apn());
            }

        }
        return  config;
    }



}
