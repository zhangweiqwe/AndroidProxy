package cn.wsgwz.gravity.service;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import org.dom4j.DocumentException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.SocketHandler;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.config.Config;
import cn.wsgwz.gravity.config.EnumMyConfig;
import cn.wsgwz.gravity.config.xml.ConfigXml;
import cn.wsgwz.gravity.core.RequestHandler;
import cn.wsgwz.gravity.fragment.log.LogContent;
import cn.wsgwz.gravity.helper.ApnDbHelper;
import cn.wsgwz.gravity.helper.ShellHelper;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;


/**
 * Created by Administrator on 2016/10/24.
 */

public class ProxyService extends Service {

    public static final String BACKGROUND_HOST = "11.22.33.44";
    public static final int PORT = 12888;
    private SharedPreferences sharedPreferences;
    public static boolean isStart;

    protected ServerSocket server;
    protected ExecutorService executor;
    private Thread socketThread;

    private Notification.Builder builder;
    private NotificationManager notificationManager;
    private    Intent intentMain;

    private   ApnDbHelper apnDbHelper = null;;

    private Config config;

    private ShellHelper shellHelper = ShellHelper.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        LogContent.addItem(Build.BRAND+"  "+Build.MODEL+" "+Build.VERSION.RELEASE+"  "+"  API:"+Build.VERSION.SDK_INT);
        LogContent.addItem("当前版本: "+getResources().getString(R.string.app_name)+FileUtil.VERSION_NUMBER);


        //LogUtil.printSS("----ss"+1);
        showNotification();
       // LogUtil.printSS("----ss"+2);
        sharedPreferences = getSharedPreferences(SharedPreferenceMy.MAIN_CONFIG,MODE_PRIVATE);
        try {
            if(!LogUtil.IS_STREAM_DEBUG){
                String currentConfigPath = sharedPreferences.getString(SharedPreferenceMy.CURRENT_CONFIG_PATH,null);
                if(currentConfigPath==null){
                    return;
                }
                File file = new File(currentConfigPath);
                if(file.getPath().startsWith("/"+FileUtil.ASSETS_CONFIG_PATH)){
                    config = ConfigXml.read(getAssets().open( file.getAbsolutePath().replaceFirst("/","")));
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
                        Toast.makeText(this, getResources().getText(R.string.config_not_fund), Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
            }else {
                InputStream in = getAssets().open("text.xml");
                config = ConfigXml.read(in);
            }

            LogContent.addItemAndNotify("建议接入点:"+" apn:"+config.getApn_apn()+" 代理:"+config.getApn_proxy()+" 端口:"+config.getApn_port());
          //  setApn(config.getApn_apn(),config.getApn_proxy(),config.getApn_port());


            exec();
            startProxy();


        } catch (Exception e) {
            Toast.makeText(this,getString(R.string.start_server_error)+e.getMessage().toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
            LogContent.addItemAndNotify(e.getMessage().toString());
        }
    }

    private void exec(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("main",Context.MODE_PRIVATE);
        String startStr =  shellHelper.getStartStr().replace(ShellHelper.dns,config.getDns());
        sharedPreferences.edit().putString("start.sh",startStr).commit();
        shellHelper.setStartStr(startStr);
        fllowServer(true);
    }

    private void fllowServer(boolean isStart){
        boolean isExecShell = sharedPreferences.getBoolean(SharedPreferenceMy.SHELL_IS_FLLOW_MENU, true);
        if(isExecShell){
            ShellUtil.maybeExecShell(isStart,MainActivity.mainActivity);
        }
    }
    //启动proxy
    private void startProxy(){
        executor = Executors.newCachedThreadPool();
        final Context context = getApplicationContext();
        try { server = new ServerSocket(PORT); }
        catch (IOException e) {
            LogContent.addItemAndNotify(e.getMessage().toString());
        }

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1004:
                        LogContent.addItemAndNotify((String)msg.obj);
                        break;
                }
            }
        };
        socketThread = new Thread(new Runnable() {
            @Override
            public void run() {


                    try {
                        while (true) {
                        executor.execute(new RequestHandler(server.accept(),config,context));
                        }
                        }
                    catch (IOException e) {
                        e.printStackTrace();
                        Message msg = Message.obtain();
                        msg.what=1004;
                        msg.obj = e.getMessage().toString();
                        handler.sendMessage(msg);

                }

            }
        });
        socketThread.start();
        if((socketThread!=null&&socketThread.isAlive())&&executor!=null){
            LogContent.addItemAndNotify("服务开始");
            isStart = true;
        }
    }


    private void showNotification(){
        builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.diqiu);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.diqiu));
        intentMain = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intentMain,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setContentText(getResources().getString(R.string.app_name)+" "+FileUtil.VERSION_NUMBER+" "+"运行中");
        builder.setTicker(getResources().getString(R.string.app_name)+"  "+FileUtil.VERSION_NUMBER+"已运行");
        builder.setOngoing(true);
        Notification  notification = builder.build();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        if (socketThread != null && !socketThread.isInterrupted()) {
            socketThread.interrupt();
        }
        if (executor != null) {
            executor.shutdown();
        }
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
                LogContent.addItemAndNotify(e.getMessage().toString());
            }
        }

        notificationManager.cancel(0);
        isStart = false;
        LogContent.addItemAndNotify("服务结束");

        fllowServer(false);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void setApn(final String apn, final String proxy, final String port){

        String shellStr = "cd "+ApnDbHelper.DB_PATH_REALSE+"\n"+
                "mount -o remount rw /."+"\n"+
                "chmod 777 "+ApnDbHelper.DB_NAME_REALSE;
        ShellUtil.execShell(ProxyService.this, shellStr, new OnExecResultListenner() {
            @Override
            public void onSuccess(final StringBuffer sb) {
                try {
                    apnDbHelper = new ApnDbHelper(ProxyService.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    startProxy();
                    Toast.makeText(ProxyService.this,getString(R.string.auto_setting_apn_error)+"\n"+e.getMessage().toString()+"\n"+
                            "("+apn+"--"+proxy+"--"+port+")",Toast.LENGTH_SHORT).show();
                }

                if(apnDbHelper==null){
                    return;
                }
                apnDbHelper.getCurrentApnID(ProxyService.this, new ApnDbHelper.OnIDChnageListenner() {
                    @Override
                    public void succeed(String value)  {
                        apnDbHelper.update(ProxyService.this,value,apn,proxy,port);
                        startProxy();
                    }

                    @Override
                    public void error() {
                        Toast.makeText(ProxyService.this,getString(R.string.auto_setting_apn_error)+"\n"+sb.toString()+"\n"+
                                "("+apn+"--"+proxy+"--"+port+")",Toast.LENGTH_SHORT).show();
                        startProxy();
                    }
                });
            }

            @Override
            public void onError(StringBuffer sb) {
                Toast.makeText(ProxyService.this,getString(R.string.auto_setting_apn_error)+"\n"+sb.toString()+"\n"+
                        "("+apn+"--"+proxy+"--"+port+")",Toast.LENGTH_SHORT).show();
                startProxy();
            }
        });
    }
}
