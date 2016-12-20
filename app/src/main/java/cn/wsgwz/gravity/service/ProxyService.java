package cn.wsgwz.gravity.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.dom4j.DocumentException;

import java.io.IOException;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.core.SocketServer;
import cn.wsgwz.gravity.util.FileUtil;


/**
 * Created by Administrator on 2016/10/24.
 */

public class ProxyService extends Service {
    private SocketServer socketServer;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            socketServer = new SocketServer(ProxyService.this);
            socketServer.start();
            showNotification();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,getString(R.string.start_server_error)+e.getMessage().toString(),Toast.LENGTH_LONG).show();
        } catch (DocumentException e) {
            e.printStackTrace();
            Toast.makeText(this,getString(R.string.start_server_error)+e.getMessage().toString(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(socketServer!=null){
            socketServer.interrupt();
            socketServer.releasePort();
        }
        notificationManager.cancel(0);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }



}
