package cn.wsgwz.gravity.helper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.adapter.CapturePackageELVAdapter;
import cn.wsgwz.gravity.core.ParamsHelper;
import cn.wsgwz.gravity.util.DensityUtil;

/**
 * Created by Jeremy Wang on 2017/1/16.
 */

public class CapturePackageHelper implements ParamsHelper.OnRequestBeginningListenner,View.OnClickListener,ExpandableListView.OnItemLongClickListener{
    private ClipboardManager cm ;
    private final static int NEED_REFRESH_TIME = 800;
    private static CapturePackageHelper capturePackageHelper;
    private CapturePackageHelper() {
    }
    public static final CapturePackageHelper getInstance(){
        if(capturePackageHelper==null){
            capturePackageHelper = new CapturePackageHelper();
        }
        return capturePackageHelper;
    }
    //状态标识1.显示处理之前的 2.显示处理之后的 3.同时显示
    enum Enum{
        ORIGINAL,CHANGED,MEANWHILE;
    }
    private Enum anEnum = Enum.ORIGINAL;

   /* （1）schedule方法：“fixed-delay”；如果第一次执行时间被delay了，随后的执行时间按 照 上一次 实际执行完成的时间点 进行计算
    （2）scheduleAtFixedRate方法：“fixed-rate”；如果第一次执行时间被delay了，随后的执行时间按照 上一次开始的 时间点 进行计算，并且为了”catch up”会多次执行任务,TimerTask中的执行体需要考虑同步
   */
    private WindowManager windowManager;
    private View capturePackageExplainView;
    private SettingHelper settingHelper = SettingHelper.getInstance();

    private ExpandableListView expandableListView_Original,expandableListView_Changed;
    private CapturePackageELVAdapter capturePackageELVAdapter_Original,capturePackageELVAdapter_Changed;
    private List<StringBuffer> list_Original,list_Changed;

    private short styleBnState;
    private Button styleBn,clearBn,dragBn;
    public void show(final Context context){
        cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

       /*英 [sə'spend]   美 [sə'spɛnd]   全球发音 跟读 口语练习
        vt. 延缓，推迟；使暂停；使悬浮
        vi. 悬浮；禁赛*/
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        capturePackageExplainView = layoutInflater.inflate(R.layout.capture_package_view,null);
        expandableListView_Original = (ExpandableListView) capturePackageExplainView.findViewById(R.id.expandableListView_Original);
        expandableListView_Changed = (ExpandableListView) capturePackageExplainView.findViewById(R.id.expandableListView_Changed);

        styleBn = (Button) capturePackageExplainView.findViewById(R.id.styleBn);
        clearBn = (Button) capturePackageExplainView.findViewById(R.id.clearBn);
        dragBn = (Button) capturePackageExplainView.findViewById(R.id.dragBn);
        styleBn.setOnClickListener(this);
        clearBn.setOnClickListener(this);


        list_Original = new ArrayList<>();
        list_Changed = new ArrayList<>();
        capturePackageELVAdapter_Original = new CapturePackageELVAdapter(context,list_Original);
        capturePackageELVAdapter_Changed = new CapturePackageELVAdapter(context,list_Changed);

        expandableListView_Original.setGroupIndicator(null);
        expandableListView_Changed.setGroupIndicator(null);
        expandableListView_Original.setAdapter(capturePackageELVAdapter_Original);
        expandableListView_Changed.setAdapter(capturePackageELVAdapter_Changed);

        expandableListView_Original.setOnItemLongClickListener(this);
        expandableListView_Changed.setOnItemLongClickListener(this);

        ParamsHelper.setOnRequestBeginningListenner(this);


        final WindowManager.LayoutParams wLayoutParams = new WindowManager.LayoutParams();
        wLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        wLayoutParams.format = PixelFormat.RGBA_8888;
        wLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wLayoutParams.gravity = Gravity.START | Gravity.TOP;
        wLayoutParams.width = DensityUtil.dip2px(context, (float) (310*0.618));//191.58 0.168
        wLayoutParams.height = DensityUtil.dip2px(context,310);
        wLayoutParams.x = (int) settingHelper.getSpeedSuspensionX(context);
        wLayoutParams.y = (int) settingHelper.getSpeedSuspensionY(context);


        
      
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
                        windowManager.updateViewLayout(capturePackageExplainView, wLayoutParams);
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

        dragBn.setOnTouchListener(new MyOnTounchListenner());
        //capturePackageExplainView.setOnTouchListener(new MyOnTounchListenner());
        String  color = settingHelper.getSuspensionColor(context);
        int colorCode = 0;
        if(color==null||color.length()<7){
            //LogUtil.printSS("---->"+color+"<----");
           setColor(context);
        }else {
            colorCode = Color.parseColor(color);
        }

        windowManager.addView(capturePackageExplainView,wLayoutParams);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.styleBn:
                styleBnState++;
                switch (styleBnState%3){
                    case 0:
                        anEnum = Enum.ORIGINAL;
                        styleBn.setText("before");
                        break;
                    case 1:
                        anEnum = Enum.CHANGED;
                        styleBn.setText("after");
                        break;
                    case 2:
                        anEnum = Enum.MEANWHILE;
                        styleBn.setText("all");
                        break;
                }
                if(styleBnState==Short.MAX_VALUE){
                    styleBnState = 0;
                }
                break;
            case R.id.clearBn:
                list_Original.clear();
                list_Changed.clear();
                capturePackageELVAdapter_Original.notifyDataSetChanged();
                capturePackageELVAdapter_Changed.notifyDataSetChanged();
                break;
        }
    }

    private StringBuffer sb_Original,sb_Changed;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1000:
                    list_Original.add(sb_Original);
                    list_Changed.add(sb_Changed);
                    switch (anEnum){
                        case ORIGINAL:
                            expandableListView_Original.setVisibility(View.VISIBLE);
                            expandableListView_Changed.setVisibility(View.GONE);
                            capturePackageELVAdapter_Original.notifyDataSetChanged();
                            break;
                        case CHANGED:
                            expandableListView_Original.setVisibility(View.GONE);
                            expandableListView_Changed.setVisibility(View.VISIBLE);
                            capturePackageELVAdapter_Changed.notifyDataSetChanged();
                            break;
                        case MEANWHILE:
                            expandableListView_Original.setVisibility(View.VISIBLE);
                            expandableListView_Changed.setVisibility(View.VISIBLE);
                            capturePackageELVAdapter_Original.notifyDataSetChanged();
                            capturePackageELVAdapter_Changed.notifyDataSetChanged();
                            break;
                    }

                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void requestBegin(StringBuffer sb_Original, StringBuffer sb_Changed) {
        this.sb_Original = sb_Original;
        this.sb_Changed = sb_Changed;
        handler.sendEmptyMessage(1000);
    }

    public void destroy(){
        if(capturePackageExplainView!=null){windowManager.removeView(capturePackageExplainView);}
    }

    private final void setColor(Context context){
        String color = "#"+getRandColorCode();
        int colorCode = Color.parseColor(color);
        settingHelper.setSuspensionColor(context,color);
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

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        String str = ((TextView)view).getText().toString();
        ClipData myClip;
        myClip = ClipData.newPlainText(str.length()>10?str.substring(0,10):str, str);
        cm.setPrimaryClip(myClip);
        //Snackbar.make(view,view.getContext().getResources().getString(R.string.already_copy),Snackbar.LENGTH_SHORT).show();
        Toast.makeText(view.getContext(),view.getContext().getResources().getString(R.string.already_copy),Toast.LENGTH_SHORT).show();
        return false;
    }
}
