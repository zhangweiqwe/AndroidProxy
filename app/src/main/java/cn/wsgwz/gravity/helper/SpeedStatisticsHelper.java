package cn.wsgwz.gravity.helper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2017/1/16.
 */

public class SpeedStatisticsHelper {
    private final static int NEED_REFRESH_TIME = 800;
    private static SpeedStatisticsHelper speedStatisticsHelper;
    private SpeedStatisticsHelper() {
    }
    public static final SpeedStatisticsHelper getInstance(){
        if(speedStatisticsHelper==null){
            speedStatisticsHelper = new SpeedStatisticsHelper();
        }
        return speedStatisticsHelper;
    }
   /* （1）schedule方法：“fixed-delay”；如果第一次执行时间被delay了，随后的执行时间按 照 上一次 实际执行完成的时间点 进行计算
    （2）scheduleAtFixedRate方法：“fixed-rate”；如果第一次执行时间被delay了，随后的执行时间按照 上一次开始的 时间点 进行计算，并且为了”catch up”会多次执行任务,TimerTask中的执行体需要考虑同步
   */
    private WindowManager windowManager;
    private Timer timer;
    private long allTx , allRx   ,lastAllTx,lastAllRx   ,speedTx,speedRx,  tempAllTx,tempAllRX;
    private View smallSuspendSpeedView;
    private TextView txAll_TV ,rxAll_TV ,tx_TV ,rx_TV;

    private SettingHelper settingHelper = SettingHelper.getInstance();
    public void show(final Context context){
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        PackageManager pm = context.getPackageManager();
        //2.遍历手机操作系统 获取所有的应用程序的uid
        ApplicationInfo appcationInfo = null;
        try {
            appcationInfo = pm.getApplicationInfo(context.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final int uid = appcationInfo.uid;    // 获得软件uid
        //proc/uid_stat/10086
        tempAllTx = TrafficStats.getUidTxBytes(uid);//发送的 上传的流量byte
        tempAllRX = TrafficStats.getUidRxBytes(uid);//下载的流量 byte

        /*lastAllTx = TrafficStats.getUidTxBytes(uid);
        lastAllRx = TrafficStats.getUidRxBytes(uid);*/


        //方法返回值 -1 代表的是应用程序没有产生流量 或者操作系统不支持流量统计
        final java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#.##");
       /*英 [sə'spend]   美 [sə'spɛnd]   全球发音 跟读 口语练习
        vt. 延缓，推迟；使暂停；使悬浮
        vi. 悬浮；禁赛*/
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        smallSuspendSpeedView = layoutInflater.inflate(R.layout.speed_statistics_small_view,null);
        final WindowManager.LayoutParams wLayoutParams = new WindowManager.LayoutParams();
        wLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        wLayoutParams.format = PixelFormat.RGBA_8888;
        wLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wLayoutParams.gravity = Gravity.START | Gravity.TOP;
        wLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wLayoutParams.x = (int) settingHelper.getSpeedSuspensionX(context);
        wLayoutParams.y = (int) settingHelper.getSpeedSuspensionY(context);



        txAll_TV = (TextView) smallSuspendSpeedView.findViewById(R.id.txAll_TV);
        rxAll_TV = (TextView) smallSuspendSpeedView.findViewById(R.id.rxAll_TV);
        tx_TV = (TextView) smallSuspendSpeedView.findViewById(R.id.tx_TV);
        rx_TV = (TextView) smallSuspendSpeedView.findViewById(R.id.rx_TV);

        final int kbUnit = 1024;
        final int mUnit = (int) Math.pow(kbUnit,2);
        final Handler speedRefreshHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                double _tA=1,_rA=1;
                long _tC=1,_rC=1;
                switch (msg.what){
                    case 1000:
                        double tA=((double) (allTx-tempAllTx))/mUnit,rA=((double) (allRx-tempAllRX))/mUnit;
                        long tC=(speedTx/kbUnit),rC=(speedRx/kbUnit);
                        if(!(_tA==tA)){txAll_TV.setText("↑"+decimalFormat.format(tA)+"m"); _tA=tA;}
                        if(!(_rA==rA)){rxAll_TV.setText("↓"+decimalFormat.format(rA)+"m"); _rA=rA;}

                        if(!(_tC==tC)){tx_TV.setText("↑"+decimalFormat.format(tC)+"kb/s"); _tC=tC;}
                        if(!(_rC==rC)){rx_TV.setText("↓"+decimalFormat.format(rC)+"kb/s");_rC=rC;}

                        break;
                }
                super.handleMessage(msg);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                allTx = TrafficStats.getUidTxBytes(uid);//发送的 上传的流量byte
                allRx = TrafficStats.getUidRxBytes(uid);//下载的流量 byte

                speedTx = allTx - lastAllTx;
                speedRx = allRx - lastAllRx;
                lastAllTx = allTx;
                lastAllRx = allRx;

                speedRefreshHandler.sendEmptyMessage(1000);
                //LogUtil.printSS("--> timer（1000）"+"----"+allTx+"--=="+allRx);
            }
        },NEED_REFRESH_TIME,NEED_REFRESH_TIME);
        class MyOnTounchListenner implements View.OnTouchListener{
            int lastX = 0, lastY = 0;
            int paramX = 0, paramY = 0;
            long exitTime=0;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = wLayoutParams.x;
                        paramY = wLayoutParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        wLayoutParams.x = paramX + dx;
                        wLayoutParams.y = paramY + dy;
                        // 更新悬浮窗位置
                        windowManager.updateViewLayout(smallSuspendSpeedView, wLayoutParams);
                        settingHelper.setSpeedSuspensionX(context,wLayoutParams.x);
                        settingHelper.setSpeedSuspensionY(context,wLayoutParams.y);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if ((System.currentTimeMillis() - exitTime) < 300) {
                           // createWindow(context, type);
                            //LogUtil.printSS("-----------------------<--------->");
                            setColor(context);
                            return true;
                        } else {
                            exitTime = System.currentTimeMillis();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        }

        smallSuspendSpeedView.setOnTouchListener(new MyOnTounchListenner());
        String  color = settingHelper.getSuspensionColor(context);
        int colorCode = 0;
        if(color==null||color.length()<7){
            //LogUtil.printSS("---->"+color+"<----");
           setColor(context);
        }else {
            colorCode = Color.parseColor(color);
            txAll_TV.setTextColor(colorCode);
            rxAll_TV.setTextColor(colorCode);

            tx_TV.setTextColor(colorCode);
            rx_TV.setTextColor(colorCode);
        }

        windowManager.addView(smallSuspendSpeedView,wLayoutParams);
    }

    public void destroy(){
        if(smallSuspendSpeedView!=null){windowManager.removeView(smallSuspendSpeedView);}
        if(timer!=null){timer.cancel();}
    }

    private final void setColor(Context context){
        String color = "#"+getRandColorCode();
        int colorCode = Color.parseColor(color);
        settingHelper.setSuspensionColor(context,color);
        txAll_TV.setTextColor(colorCode);
        rxAll_TV.setTextColor(colorCode);

        tx_TV.setTextColor(colorCode);
        rx_TV.setTextColor(colorCode);
    }
    private final String getRandColorCode(){
        String r,g,b;
        Random random = new Random();
        r = Integer.toHexString(random.nextInt(256)).toUpperCase();
        g = Integer.toHexString(random.nextInt(256)).toUpperCase();
        b = Integer.toHexString(random.nextInt(256)).toUpperCase();

        r = r.length()==1 ? "0" + r : r ;
        g = g.length()==1 ? "0" + g : g ;
        b = b.length()==1 ? "0" + b : b ;

        return r+g+b;
    }

}
