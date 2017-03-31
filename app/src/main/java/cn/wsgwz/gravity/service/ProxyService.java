package cn.wsgwz.gravity.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;

import java.io.IOException;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.core.SocketServer;
import cn.wsgwz.gravity.helper.CapturePackageHelper;
import cn.wsgwz.gravity.helper.SettingHelper;
import cn.wsgwz.gravity.nativeGuard.NativeStatusListenner;
import cn.wsgwz.gravity.nativeGuard.ProxyServiceGuardHelper;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.ShellUtil;
import cn.wsgwz.gravity.util.aboutShell.Command;


/**
 * Created by Administrator on 2016/10/24.
 */
public class ProxyService extends Service  {
    private SettingHelper settingHelper = SettingHelper.getInstance();
    private SocketServer socketServer;
    public static final short NOTIFY_SERVER_ID = 123;
    private  NotificationManager notificationManager;
    private void showNotification(){
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.diqiu);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.diqiu));
        Intent intentMain = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intentMain,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setContentText(getResources().getString(R.string.app_name)+" "+FileUtil.VERSION_NUMBER+" "+"运行中");
        builder.setTicker(getResources().getString(R.string.app_name)+"  "+FileUtil.VERSION_NUMBER+"已运行");
        builder.setOngoing(true);
        Notification  notification = builder.build();
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_SERVER_ID,notification);


    }




    private CapturePackageHelper capturePackageHelper;
    private boolean isCapture = true;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("daemon---->java server ","onCreate()");
        try {
            isCapture = settingHelper.isCaptrue(this);
            if(isCapture){
                capturePackageHelper = CapturePackageHelper.getInstance();
                capturePackageHelper.show(this);
            }
            settingHelper.setIsStart(this,true);
            socketServer = new SocketServer(ProxyService.this,isCapture);
            if(socketServer.getConfig()==null){
                return;
            }
            socketServer.start();
            showNotification();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,getString(R.string.start_server_error)+e.getMessage().toString(),Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this,getString(R.string.start_server_error)+e.getMessage().toString(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("daemon---->java server ","onStartCommand action=");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("daemon---->java server ","onDestroy()");
        if(socketServer!=null){
            socketServer.interrupt();
            socketServer.releasePort();
        }
        if(notificationManager!=null){
            notificationManager.cancel(NOTIFY_SERVER_ID);
        }
        //LogUtil.printSS("   ProxyService  onDestroy");
        settingHelper.setIsStart(this,false);
        if(isCapture){
            capturePackageHelper.destroy();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





}
