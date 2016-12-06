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
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import org.dom4j.DocumentException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.SocketHandler;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.config.Config;
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
                    Toast.makeText(this,getResources().getText(R.string.config_not_fund),Toast.LENGTH_SHORT).show();
                }
            }else {
                InputStream in = getAssets().open("text.xml");
                config = ConfigXml.read(in);
            }
            //LogUtil.printSS("----ss"+3);
            LogContent.addItem("建议接入点:"+" apn:"+config.getApn_apn()+" 代理:"+config.getApn_proxy()+" 端口:"+config.getApn_port());

            //LogUtil.printSS("----ss"+4);
            //Config.DNS  = config.getDns();

          //  setApn(config.getApn_apn(),config.getApn_proxy(),config.getApn_port());

            LogContent.addItemAndNotify("服务开始");
            startProxy();


        } catch (Exception e) {
            Toast.makeText(this,getString(R.string.start_server_error)+e.getMessage().toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
            LogContent.addItemAndNotify(e.getMessage().toString());
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
        socketThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        executor.execute(new RequestHandler(server.accept(),config,context)); }
                    catch (IOException e) {
                        LogContent.addItemAndNotify(e.getMessage().toString());
                    }
                }
            }
        });
        socketThread.start();
        LogContent.addItemAndNotify("线程池已启动");
        isStart = true;
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

        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
                LogContent.addItemAndNotify(e.getMessage().toString());
            }
        }
        if (executor != null) {
            executor.shutdown();
        }
        if (socketThread != null && !socketThread.isInterrupted()) {
            socketThread.interrupt();
        }
        notificationManager.cancel(0);
        isStart = false;

        LogContent.addItemAndNotify("服务结束");
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
