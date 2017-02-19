package cn.wsgwz.gravity.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.helper.SettingHelper;
import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;

/**
 * Created by Administrator on 2017/2/19.
 */

public class SwitchWidgetProvider extends AppWidgetProvider {

    private static SettingHelper settingHelper = SettingHelper.getInstance();
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //LogUtil.printSS("onDeleted");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //LogUtil.printSS("onEnabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager , int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //LogUtil.printSS("onUpdate");
        Intent intent = new Intent(context,SwitchWidgetProvider.class).setAction("cn.wsgwz.gravity.startOrStopService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent , PendingIntent.FLAG_UPDATE_CURRENT);


        ComponentName componentName = new ComponentName(context,SwitchWidgetProvider.class);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.switch_widget_provider_view);


        remoteViews.setOnClickPendingIntent(R.id.switch_RL, pendingIntent);


        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }

    //private static boolean isFirstOnReceive = true;
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //LogUtil.printSS("onReceive");
       /* if(isFirstOnReceive){
            isFirstOnReceive=false;
        }else {

        }*/

        startServiceAndRefreshState(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        //LogUtil.printSS("onAppWidgetOptionsChanged");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //LogUtil.printSS("onDisabled");
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        //LogUtil.printSS("onRestored");
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        //LogUtil.printSS("peekService");
        return super.peekService(myContext, service);
    }




    public static final void refreshState(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context,SwitchWidgetProvider.class);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.switch_widget_provider_view);
        if(settingHelper.isStart(context)){
            remoteViews.setTextViewText(R.id.switch_TV,"开启");
        }else {
            remoteViews.setTextViewText(R.id.switch_TV,"关闭");
        }

       /* Intent intent = new Intent(context,SwitchWidgetProvider.class).setAction("cn.wsgwz.gravity.startOrStopService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent , PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.switch_TV, pendingIntent);*/
        appWidgetManager.updateAppWidget(componentName, remoteViews);

    }


    private void startServiceAndRefreshState(Context context){
        Intent intentServer = new Intent(context, ProxyService.class);
        String configPath = settingHelper.getConfigPath(context);
        if(configPath !=null&&!configPath.equals("未选择")){
            if(!settingHelper.isStart(context)){
                context.startService(intentServer);
                fllowServer(true,context);
            } else {
                context.stopService(intentServer);
                fllowServer(false,context);
            }
            refreshState(context);
        }else {
            Toast.makeText(context,context.getString(R.string.please_select_config),Toast.LENGTH_SHORT).show();
        }
    }
    private static void fllowServer(boolean isStart,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferenceMy.CONFIG, Context.MODE_PRIVATE);
        boolean isExecShell = sharedPreferences.getBoolean(SharedPreferenceMy.SHELL_IS_FLLOW_MENU, true);
        if(isExecShell){
            ShellUtil.maybeExecShell(isStart,context);
        }

    }
}
