package cn.wsgwz.gravity.receiver;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import cn.wsgwz.gravity.helper.SettingHelper;
import cn.wsgwz.gravity.helper.SpeedStatisticsHelper;
import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.service.SpeedStatisticsService;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;

/**
 * Created by Administrator on 2016/10/29.
 */

public class SelfStartingReceiver extends BroadcastReceiver {
    private SpeedStatisticsHelper speedStatisticsHelper = SpeedStatisticsHelper.getInstance();
    private SettingHelper settingHelper = SettingHelper.getInstance();
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo netInfo;
    private Intent intentSpeedStatistics;
    private SharedPreferences sharedPreferences;
    private ConnectivityManager connectivityManager;
    public static final Uri CURRENT_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    public static final Uri APN_LIST_URI = Uri.parse("content://telephony/carriers");

    @Override
    public void onReceive(Context context, Intent intent) {
        intentSpeedStatistics = new Intent(context,SpeedStatisticsService.class);
        sharedPreferences =  context.getSharedPreferences(SharedPreferenceMy.CONFIG,Context.MODE_PRIVATE);
        String action = intent.getAction();
        if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            boolean showSpeedStatistics = sharedPreferences.getBoolean(SharedPreferenceMy.SPEED_STATISTICS,true);
            //LogUtil.printSS("---->"+showSpeedStatistics);
            mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = mConnectivityManager.getActiveNetworkInfo();
            //if(netInfo != null && netInfo.isAvailable()) {
            if(netInfo != null && netInfo.isAvailable()) {

                /////////////网络连接
                String name = netInfo.getTypeName();

                if(netInfo.getType()==ConnectivityManager.TYPE_WIFI){
                    /////WiFi网络
                }else if(netInfo.getType()==ConnectivityManager.TYPE_ETHERNET){
                    /////有线网络
                }else if(netInfo.getType()==ConnectivityManager.TYPE_MOBILE){
                    /////////3g网络
                }
            } else {
                ////////网络断开

            }
        }



      /*  String str = "am startservice -n cn.wsgwz.gravity/cn.wsgwz.gravity.service.ProxyService";
        ShellUtil.execShell(context, str, null);*/
        //LogUtil.printSS("ssssssssssssss");
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
