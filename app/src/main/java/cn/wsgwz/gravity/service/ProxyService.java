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
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            socketServer = new SocketServer(ProxyService.this);
            socketServer.start();
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
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





}
