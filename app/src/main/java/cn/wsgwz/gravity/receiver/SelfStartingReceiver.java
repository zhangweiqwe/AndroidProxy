package cn.wsgwz.gravity.receiver;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Administrator on 2016/10/29.
 */

public class SelfStartingReceiver extends BroadcastReceiver {
    private ConnectivityManager connectivityManager;

    public static final Uri CURRENT_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    public static final Uri APN_LIST_URI = Uri.parse("content://telephony/carriers");
    @Override
    public void onReceive(Context context, Intent intent) {
          /*  connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo mobileInfo  = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(mobileInfo.isConnected()){
                if(!ProxyService.serviceIsStart){
                //    context.startService(new Intent(context,ProxyService.class));
                }
            }
        }catch (Exception e){
            Toast.makeText(context,"没有获取用户的权限",Toast.LENGTH_SHORT).show();
        }*/
    }

   /* public String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {

                        LogUtil.printSS(inetAddress.getHostAddress().toString());
                        return inetAddress.getHostAddress().toString();
                    }

                }
            }
        }
        catch (SocketException ex)
        {
            LogUtil.printSS(ex.toString());
        }
        return null;
    }*/
}
