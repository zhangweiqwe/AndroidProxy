package cn.wsgwz.gravity.helper;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.activity.ConfigEditActivity;
import cn.wsgwz.gravity.service.SpeedStatisticsService;
import cn.wsgwz.gravity.util.DensityUtil;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.SharedPreferenceMy;

/**
 * Created by Jeremy Wang on 2017/1/19.
 */

public class MainActivityHelper {
    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;
    private Intent intentSpeedStatistics;
    public MainActivityHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        sharedPreferences = mainActivity.getSharedPreferences(SharedPreferenceMy.CONFIG, Context.MODE_PRIVATE);
        intentSpeedStatistics = new Intent(mainActivity,SpeedStatisticsService.class);
    }
    public void doOther(){
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                boolean showSpeedSuspension = sharedPreferences.getBoolean(SharedPreferenceMy.SPEED_STATISTICS,true);
                setSuspensionState(showSpeedSuspension);
                return false;
            }
        });
    }
    public void setSuspensionState(boolean state){
        if(state){
            if (false) {
                Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + mainActivity.getPackageName()));
                mainActivity.startActivityForResult(permissionIntent, MainActivity.OVERLAY_PERMISSION_REQ_CODE);
            } else {
                mainActivity.startService(intentSpeedStatistics);
            }
        }else {
            mainActivity.stopService(intentSpeedStatistics);
        }
    }
    public void initFile(){
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                    if(Build.VERSION.SDK_INT>=23){
                        if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                            //new PermissionHelper(mainActivity).requestPermissionsForMainActiivty();
                            ActivityCompat.requestPermissions(mainActivity,MainActivity.REQUEST_WRITE_READ_EXTERNALPERMISSION,MainActivity.REQUEST_WRITE_READ_EXTERNAL_CODE);
                        }else {

                        }
                    }else {
                    }
                return false;
            }
        });
    }

    public void setBackground(SettingHelper settingHelper,Context context){
        switch (settingHelper.getMainActivityStyle(context)){
            case 0:
                break;
            case 1:
                return;
            case 2:
                break;
        }
        String uriPath = sharedPreferences.getString(SharedPreferenceMy.WALLPAPER_PATH,null);
        if(uriPath!=null){
            ContentResolver contentResolver = mainActivity.getContentResolver();
            try {
                mainActivity.getWindow().setBackgroundDrawable(Drawable.createFromStream(contentResolver.openInputStream(Uri.parse(uriPath)),null) );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            try {
                mainActivity.getWindow().setBackgroundDrawable(Drawable.createFromStream(mainActivity.getAssets().open("3084.png"),null) );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    //创建新配置文件
    public void createConfig(){
        final File file = new File(FileUtil.APP_APTH_CONFIG);
        if(!file.exists()){
            file.mkdirs();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("新建配置文件");
        final EditText et = new EditText(mainActivity);
        et.setHint("输入文件名");
        builder.setView(et);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileNeme = et.getText().toString().trim();
                if(fileNeme==null||fileNeme.equals("")||fileNeme.length()>10){
                    Snackbar.make(mainActivity.getToolbar(),mainActivity.getString(R.string.new_file_name_illegality),Snackbar.LENGTH_SHORT).show();
                    return;
                }
                File newFile = new File(file.getAbsolutePath()+"/"+fileNeme+FileUtil.CONFIG_END_NAME);
                try {
                    newFile.createNewFile();
                    startEditConfig(newFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Dialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.payDialogStyleAnimation);
        dialog.show();
    }
    private void startEditConfig(File file){
        Intent intent = new Intent(mainActivity, ConfigEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConfigEditActivity.INTENT_BUNDLE_TYPE_FILE_KEY,file);
        bundle.putSerializable(ConfigEditActivity.INTENT_BUNDLE_TYPE_CREATE_NEW_KEY,true);
        intent.putExtras(bundle);
        mainActivity.startActivity(intent);
    }

    public void aboutDialogShow(){
        final String qq = "580466685";
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("关于");
        String aboutStr = "作者：我思故我在\r\n" +
                "版本："+FileUtil.VERSION_NUMBER+"\r\n"+
                "讨论群："+qq;


        SpannableString spannable = new SpannableString(aboutStr);
        spannable.setSpan(new AbsoluteSizeSpan(15,true), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String urlQQ = "mqqwpa://im/chat?chat_type=group&uin=580466685&version=1";
                try {
                    mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlQQ)));
                }catch (ActivityNotFoundException e){

                }
            }
        };
        spannable.setSpan(clickableSpan, spannable.length()-qq.length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//SPAN_EXCLUSIVE_EXCLUSIVE

        RelativeLayout relativeLayout = new RelativeLayout(mainActivity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(Build.VERSION.SDK_INT>21){
            layoutParams.setMargins(DensityUtil.dip2px(mainActivity,120),DensityUtil.dip2px(mainActivity,30),0,0);
        }else {
            layoutParams.setMargins(DensityUtil.dip2px(mainActivity,120),DensityUtil.dip2px(mainActivity,30),0,DensityUtil.dip2px(mainActivity,30));
        }
        layoutParams.setMarginStart(DensityUtil.dip2px(mainActivity,30));
        TextView textView = new TextView(mainActivity);

        textView.setLayoutParams(layoutParams);
        textView.setText(spannable);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setTextColor(Color.parseColor("#ffffff"));
        textView.setTextSize(16, TypedValue.COMPLEX_UNIT_SP);
        textView.setId(R.id.about_uid);
        relativeLayout.addView(textView);


        TextView wapmlTv = new TextView(mainActivity);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(Build.VERSION.SDK_INT>21){
            layoutParams2.setMargins(DensityUtil.dip2px(mainActivity,60),DensityUtil.dip2px(mainActivity,30),0,0);
        }else {
            layoutParams2.setMargins(DensityUtil.dip2px(mainActivity,60),DensityUtil.dip2px(mainActivity,30),0,DensityUtil.dip2px(mainActivity,30));
        }
        layoutParams2.addRule(RelativeLayout.BELOW,textView.getId());
        layoutParams2.setMarginStart(DensityUtil.dip2px(mainActivity,30));
        wapmlTv.setTextColor(mainActivity.getResources().getColor(R.color.colorAccent));
        wapmlTv.setClickable(true);
        wapmlTv.setText("名流网：www.wapml.cn\n");
       // wapmlTv.setTextSize(,TypedValue.COMPLEX_UNIT_SP);// complex  n.英 ['kɒmpleks] 复合体；综合设施
        wapmlTv.setLayoutParams(layoutParams2);
        wapmlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://wapml.cn"));
                it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                mainActivity.startActivity(it);
            }
        });


        relativeLayout.addView(wapmlTv);
        builder.setView(relativeLayout);
        //builder.setMessage(spannable);
        builder.setNeutralButton("错误反馈", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="+"857899299")));
                }catch (Exception e){
                    Toast.makeText(mainActivity,"打开qq失败，可能你没有安装qq",Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void setMarginStatusBar(){
     /*   *
         * 获取状态栏高度——方法
         **/
        int statusBarHeight1 = -1;
//获取status_bar_height资源的ID
        int resourceId = mainActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = mainActivity.getResources().getDimensionPixelSize(resourceId);
        }
        LinearLayout.LayoutParams params = ((LinearLayout.LayoutParams)(mainActivity.getToolbar().getLayoutParams()));
        params.setMargins(0,statusBarHeight1,0,0);
        mainActivity.getToolbar().setLayoutParams(params);

     /*   int statusBarHeight=0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusBarHeight = getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams)(toolbar.getLayoutParams()));
        params.setMargins(0,statusBarHeight,0,0);
        LogUtil.printSS(" sta"+statusBarHeight);
        toolbar.setLayoutParams(params);*/
    }
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public Intent getIntentSpeedStatistics() {
        return intentSpeedStatistics;
    }

    public void setIntentSpeedStatistics(Intent intentSpeedStatistics) {
        this.intentSpeedStatistics = intentSpeedStatistics;
    }
}
