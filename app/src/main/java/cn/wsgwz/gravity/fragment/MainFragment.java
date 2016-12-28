package cn.wsgwz.gravity.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DecimalFormat;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.pull.refreshview.XListView;
import com.example.pull.refreshview.XScrollView;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.activity.ConfigEditActivity;
import cn.wsgwz.gravity.config.Config;
import cn.wsgwz.gravity.dialog.ConfigSelectDialog;
import cn.wsgwz.gravity.fragment.log.LogContent;
import cn.wsgwz.gravity.helper.ShellHelper;
import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.$InterfaceTest;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.MyScrollView2;
import cn.wsgwz.gravity.util.NativeUtils;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;
import cn.wsgwz.gravity.view.MyScrollView;
import cn.wsgwz.gravity.view.WaterFlowSurfaceView;
import cn.wsgwz.gravity.view.WaterFlowView;
import cn.wsgwz.gravity.view.WaterWaveView;
import cn.wsgwz.photospreview.PhotosPreviewActivity;


public class MainFragment extends Fragment implements View.OnClickListener,ShellUtil.IsProgressListenner,GestureDetector.OnGestureListener{

    public static final int NOTIFY_SERVER_ID = 123564;

    private GestureDetector detector;
    private MyScrollView2 myScrollView;

    private Switch service_Switch;


    private Button select_Bn,explain_Bn;

    private SharedPreferences sharedPreferences;

    public static boolean isStartOrStopDoing;
    private Intent intentServer;
    private NotificationManager notificationManager;

    @$InterfaceTest("MainFragment")
    private String ar;
    @$InterfaceTest(getTestBoolean = true)
    private boolean b;
    private void fllowServer(boolean isStart){
        boolean isExecShell = sharedPreferences.getBoolean(SharedPreferenceMy.SHELL_IS_FLLOW_MENU, true);
        if(isExecShell){
            ShellUtil.maybeExecShell(isStart,(MainActivity) getActivity());
        }

    }

    //private   WaterWaveView waterWaveView;
    //private WaterFlowView waterFlowView;
    //private WaterFlowSurfaceView waterFlowSurfaceView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_main,container,false);
        initView(view);
        //LogUtil.printSS(ar+b);
      /*  waterWaveView = (WaterWaveView) view.findViewById(R.id.waterWaveView);
        waterWaveView.setmWaterLevel(0.9f);
        waterWaveView.startWave();*/

       // waterFlowView = (WaterFlowView)view.findViewById(R.id.waterFlowView);
       // waterFlowView.startOnDraw();

        //waterFlowSurfaceView = (WaterFlowSurfaceView) view.findViewById(R.id.waterFlowSurfaceView);

        return view;
    }

    @Override
    public void onDestroy() {
        //waterWaveView.stopWave();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();


        boolean serviceIsStart =  sharedPreferences.getBoolean(SharedPreferenceMy.SERVICE_IS_START,false);
        service_Switch.setChecked(serviceIsStart);
    }

    private void initView(final View view){


    /*    ViewGroup bannerContainer = (ViewGroup) view.findViewById(R.id.bannerContainer);

        // appId : 在 http://e.qq.com/dev/ 能看到的app唯一字符串
        // posId : 在 http://e.qq.com/dev/ 生成的数字串，并非 appid 或者 appkey
        BannerView banner = new BannerView(getActivity(), ADSize.BANNER, "1104624414", "6080206366159087");
        //设置广告轮播时间，为0或30~120之间的数字，单位为s,0标识不自动轮播
        banner.setRefresh(30);
        banner.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(int arg0) {
                Log.i("AD_DEMO", "BannerNoAD，eCode=" + arg0);
            }

            @Override
            public void onADReceiv() {
                Log.i("AD_DEMO", "ONBannerReceive");
            }
        });
        bannerContainer.addView(banner);
        *//* 发起广告请求，收到广告数据后会展示数据   *//*
        banner.loadAD();*/





        sharedPreferences = getActivity().getSharedPreferences(SharedPreferenceMy.CONFIG, Context.MODE_PRIVATE);


        detector = new GestureDetector(getActivity(),this);
        myScrollView = (MyScrollView2) view.findViewById(R.id.myScrollView);
        /*myScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return detector.onTouchEvent(motionEvent);
            }
        });*/
        intentServer = new Intent(getActivity(), ProxyService.class);

        service_Switch = (Switch) view.findViewById(R.id.service_Switch);
        /*boolean isStart = sharedPreferences.getBoolean(SharedPreferenceMy.SERVICE_IS_START,false);
        service_Switch.setChecked(isStart);*/
        boolean serviceIsStart =  sharedPreferences.getBoolean(SharedPreferenceMy.SERVICE_IS_START,false);
        service_Switch.setChecked(serviceIsStart);
        service_Switch.setOnCheckedChangeListener(onCheckedChangeListener);


        select_Bn = (Button) view.findViewById(R.id.select_Bn);
        explain_Bn = (Button) view.findViewById(R.id.explain_Bn);
        select_Bn.setOnClickListener(this);








        explain_Bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PhotosPreviewActivity.class));
            }
        });

        ShellUtil.setIsProgressListenner(this);

    }
    private void showNotification(){
        Notification.Builder builder = new Notification.Builder(getActivity());
        builder.setSmallIcon(R.mipmap.diqiu);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.diqiu));
        Intent intentMain = new Intent(getActivity(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),0,intentMain,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setContentText(getResources().getString(R.string.app_name)+" "+FileUtil.VERSION_NUMBER+" "+"运行中");
        builder.setTicker(getResources().getString(R.string.app_name)+"  "+FileUtil.VERSION_NUMBER+"已运行");
        builder.setOngoing(true);
        Notification  notification = builder.build();
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_SERVER_ID,notification);
    }

    private void flowStatistics(){
        int numM = 1048576;
        //1.获取一个包管理器。
        PackageManager pm = getActivity().getPackageManager();
//2.遍历手机操作系统 获取所有的应用程序的uid
        ApplicationInfo appcationInfo = null;
        try {
            appcationInfo = pm.getApplicationInfo(getActivity().getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int uid = appcationInfo.uid;    // 获得软件uid
        //proc/uid_stat/10086
        long alltTx = TrafficStats.getUidTxBytes(uid);//发送的 上传的流量byte
        long allRx = TrafficStats.getUidRxBytes(uid);//下载的流量 byte
        
        //方法返回值 -1 代表的是应用程序没有产生流量 或者操作系统不支持流量统计
        java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#.###");
        //查找是否上一次存在使用记录，则减去上一次使用的流量
        long lastTx = sharedPreferences.getLong(SharedPreferenceMy.LAST_UID_TXBYTES,0);
        long lastRx = sharedPreferences.getLong(SharedPreferenceMy.LAST_UID_RXBYTES,0);

        long lastAllTx = sharedPreferences.getLong(SharedPreferenceMy.LAST_ALL_UID_TXBYTES,0);
        long lastAllRx = sharedPreferences.getLong(SharedPreferenceMy.LAST_ALL_UID_RXBYTES,0);


        long thisTx = alltTx-lastAllTx;
        long thisRx = allRx-lastAllRx;



        LogContent.addItem("本次\t上传：\r"+decimalFormat.format( (double)thisTx/numM)+"\t下载：\r"+decimalFormat.format((double)thisRx/numM));
        LogContent.addItem("上次\t上传：\r"+decimalFormat.format((double)lastTx/numM)+"\t下载：\r"+decimalFormat.format((double)lastRx/numM));
        LogContent.addItemAndNotify("总共\t上传：\r"+decimalFormat.format((double)alltTx/numM)+"\t下载：\r"+decimalFormat.format((double)allRx/numM));
        //LogUtil.printSS(alltTx+"------"+allRx+"---"+decimalFormat.format(alltTx/1048576f)+"--------"+decimalFormat.format(allRx/1048576f));

        lastTx = thisTx;
        lastRx = thisRx;
        lastAllTx+=lastTx;
        lastAllRx+=lastRx;
        sharedPreferences.edit().putLong(SharedPreferenceMy.LAST_ALL_UID_TXBYTES,lastAllTx).putLong(SharedPreferenceMy.LAST_ALL_UID_RXBYTES,lastAllRx)
                .putLong(SharedPreferenceMy.LAST_UID_TXBYTES,lastTx).putLong(SharedPreferenceMy.LAST_UID_RXBYTES,lastRx).commit();



       // TrafficStats.getMobileTxBytes();//获取手机3g/2g网络上传的总流量
      //  TrafficStats.getMobileRxBytes();//手机2g/3g下载的总流量

       // TrafficStats.getTotalTxBytes();//手机全部网络接口 包括wifi，3g、2g上传的总流量
        //TrafficStats.getTotalRxBytes();//手机全部网络接口 包括wifi，3g、2g下载的总流量</applicationinfo>
    }
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if( sharedPreferences.getString(SharedPreferenceMy.CURRENT_CONFIG_PATH,null)!=null){
                if(b){
                    sharedPreferences.edit().putBoolean(SharedPreferenceMy.SERVICE_IS_START,true).commit();
                    getActivity().startService(intentServer);
                    fllowServer(true);
                    showNotification();
                } else {
                    sharedPreferences.edit().putBoolean(SharedPreferenceMy.SERVICE_IS_START,false).commit();
                    getActivity().stopService(intentServer);
                    fllowServer(false);
                    try {
                        if(notificationManager!=null){
                            notificationManager.cancel(NOTIFY_SERVER_ID);
                        }
                    }catch (Exception e){
                        LogContent.addItemAndNotify(e.getMessage().toString());
                    }
                    flowStatistics();
                }
            }else {
                Snackbar.make(service_Switch,getString(R.string.please_select_config), Snackbar.LENGTH_SHORT).show();
                if(b){
                    service_Switch.setChecked(false);
                }
                select_Bn.setClickable(false);
                service_Switch.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onClick(select_Bn);
                        select_Bn.setClickable(true);
                    }
                },800);
            }

        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_Bn:
                    final ConfigSelectDialog configSelectDialog = new ConfigSelectDialog(getActivity());
                    configSelectDialog.setOnServerStateChangeListenner(new ConfigSelectDialog.OnServerStateChangeListenner() {
                        @Override
                        public void onChange(boolean isStart) {
                            service_Switch.setOnCheckedChangeListener(null);
                                getActivity().stopService(intentServer);
                                sharedPreferences.edit().putBoolean(SharedPreferenceMy.SERVICE_IS_START,true).commit();
                                getActivity().startService(intentServer);
                            service_Switch.setChecked(true);
                                fllowServer(true);
                            service_Switch.setOnCheckedChangeListener(onCheckedChangeListener);

                        }
                    });
                    configSelectDialog.show();
                break;

        }
    }

    @Override
    public void doingSomeThing(final IsProgressEnum isProgressEnum) {
        isStartOrStopDoing = true;
        service_Switch.setEnabled(false);
    }
    @Override
    public void finallyThat() {
        isStartOrStopDoing = false;
        service_Switch.setEnabled(true);
    }




    @Override
    public boolean onDown(MotionEvent motionEvent) {
        //在按下动作时被调用
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        //在按住时被调用
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        //在抬起时被调用
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //在滚动时调用
      //  LogUtil.printSS("  "+motionEvent.getX()+"--"+motionEvent.getY()+"           "+motionEvent1.getX()+"--"+motionEvent1.getY()+"     "+v+"  "+v1);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //在长按时被调用
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //在抛掷动作时被调用
        //velocityX表示横向的移动
        return false;
    }



}
