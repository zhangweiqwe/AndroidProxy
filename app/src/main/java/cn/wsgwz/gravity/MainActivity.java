package cn.wsgwz.gravity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;


import cn.wsgwz.gravity.activity.ConfigEditActivity;
import cn.wsgwz.gravity.activity.DefinedShellActivity;
import cn.wsgwz.gravity.adapter.MyFragmentPagerAdapter;
import cn.wsgwz.gravity.contentProvider.MyAppContentProvider;
import cn.wsgwz.gravity.fragment.GraspDataFragment;
import cn.wsgwz.gravity.fragment.MainFragment;
import cn.wsgwz.gravity.fragment.ExplainFragment;
import cn.wsgwz.gravity.fragment.log.LogContent;
import cn.wsgwz.gravity.fragment.log.LogFragment;
import cn.wsgwz.gravity.helper.FirstUseInitHelper;
import cn.wsgwz.gravity.helper.PermissionHelper;
import cn.wsgwz.gravity.helper.SettingHelper;
import cn.wsgwz.gravity.helper.ShellHelper;
import cn.wsgwz.gravity.receiver.SMSBroadcastReceiver;
import cn.wsgwz.gravity.service.SpeedStatisticsService;
import cn.wsgwz.gravity.util.DensityUtil;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;

import static junit.framework.Assert.assertEquals;
/*
adb push D:\me\app\app-release.apk /sdcard/
adb shell
su
pm install -r /sdcard/app-release.apk
 */

public class MainActivity extends AppCompatActivity implements LogFragment.OnListFragmentInteractionListenner{
    //选择背景请求值
    public static final  int REQUEST_CODE_SELECT_WALLPAPER = 4;


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager my_viewPager_V4;
   // private   ScreenSlidePagerAdapter screenSlidePagerAdapter;
    private MyFragmentPagerAdapter fragmentPagerAdapter;

    private SettingHelper settingHelper = SettingHelper.getInstance();


    private SharedPreferences sharedPreferences;
    public static final  int REQUEST_WRITE_READ_EXTERNAL_CODE  = 3;
    public static final String[] REQUEST_WRITE_READ_EXTERNALPERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
    };

    public static final  int OVERLAY_PERMISSION_REQ_CODE = 5;
    //suspension
   // private Intent intentServer;


    /*
    feature
 英 ['fiːtʃə]   美 ['fitʃɚ]   全球发音 跟读 口语练习
n. 特色，特征；容貌；特写或专题节目
vi. 起重要作用
vt. 特写；以…为特色；由…主演
    overlay [əʊvə'leɪ]
vt. 在表面上铺一薄层，镀
n. 覆盖图；覆盖物
1.重制商务圈；
2.图片浏览增加手势操作；

decor
 英 ['deɪkɔː; 'de-]   美 [de'kɔr]   全球发音 跟读 口语练习
n. 装饰，布置
     */
   /* public void sendSMS(String phoneNumber,String message){
        //获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
        }
    }
*/
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //sendSMS("10086","流量");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            setMarginStatusBar();
        }
    }
    private Intent intentSpeedStatistics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences(SharedPreferenceMy.CONFIG,MODE_PRIVATE);
        intentSpeedStatistics = new Intent(this,SpeedStatisticsService.class);
        boolean showSpeedSuspension = sharedPreferences.getBoolean(SharedPreferenceMy.SPEED_STATISTICS,true);
        setSuspensionState(showSpeedSuspension);


       /* IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);
        //注册广播
        SMSBroadcastReceiver smsBroadcastReceiver = new SMSBroadcastReceiver();
        this.registerReceiver(smsBroadcastReceiver, intentFilter);*/


        /*SettingHelper settingHelper = SettingHelper.getInstance();
        settingHelper.setIsStart(this,false);
        settingHelper.isStart(this);*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

                setBackground();
                addFragment();
                setSupportActionBar(toolbar);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.apn_setting:
                                Intent intentApn = new Intent(Settings.ACTION_APN_SETTINGS);
                                startActivity(intentApn);
                                break;
                            case R.id.exec_start:
                                ShellUtil.maybeExecShell(true,MainActivity.this);
                                break;
                            case R.id.exec_stop:
                                ShellUtil.maybeExecShell(false,MainActivity.this);
                                break;
                            case R.id.fllow_shell:
                                boolean b = !item.isChecked();
                                item.setChecked(b);
                                sharedPreferences.edit().putBoolean(SharedPreferenceMy.SHELL_IS_FLLOW_MENU,b).commit();
                                break;
                            case R.id.speedStatistics:
                               //LogUtil.printSS("---sss"+item.isChecked());
                                boolean c2 = !item.isChecked();
                                item.setChecked(c2);
                                //vt. 拿，取；采取；接受（礼物等）；买，花费；耗费（时间等）
                                /* 英 [ɪ'fekt]   美 [ɪ'fɛkt]   全球发音 跟读 口语练习
                                    n. 影响；效果；作用*/
                                sharedPreferences.edit().putBoolean(SharedPreferenceMy.SPEED_STATISTICS,c2).commit();
                                setSuspensionState(c2);
                                break;
                            case R.id.isCapture:
                                boolean c1 = !item.isChecked();
                                settingHelper.setIsCapture(MainActivity.this,c1);
                                Snackbar.make(toolbar,getString(R.string.restart_service_take_effect),Snackbar.LENGTH_SHORT).show();
                                break;
                            case R.id.defined_shell:
                                startActivity(new Intent(MainActivity.this,DefinedShellActivity.class));
                                break;
                            case R.id.about_Appme:
                                aboutDialogShow();
                                break;
                            case R.id.wallpaper:
                                selectWallpaper();
                                break;
                            case R.id.create_config:
                                createConfig();
                                break;
                            case R.id.log_clear:
                                LogContent.clear(MainActivity.this);
                                break;
                            case R.id.log_share:
                                LogContent.share(MainActivity.this);
                                break;

                        }
                        return false;
                    }
                });

        //overridePendingTransition(R.anim.main_start_animation, R.anim.main_exit_animation);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isFllowProxySercer = sharedPreferences.getBoolean(SharedPreferenceMy.SHELL_IS_FLLOW_MENU,true);
        boolean showSpeedStatistics = sharedPreferences.getBoolean(SharedPreferenceMy.SPEED_STATISTICS,true);
        menu.findItem(R.id.fllow_shell).setChecked(isFllowProxySercer);
        menu.findItem(R.id.speedStatistics).setChecked(showSpeedStatistics);
        menu.findItem(R.id.isCapture).setChecked(settingHelper.isCaptrue(this));
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void onStart() {
        super.onStart();

     /* boolean b =  ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED;
        ActivityCompat.requestPermissions(this,REQUEST_WRITE_READ_EXTERNALPERMISSION,REQUEST_WRITE_READ_EXTERNAL_CODE);*/

        boolean isInitSystem = sharedPreferences.getBoolean(SharedPreferenceMy.IS_INIT_SYSTEM,false);
        if(!isInitSystem){
            new PermissionHelper(MainActivity.this).requestPermissionsForMainActiivty();
            if(Build.VERSION.SDK_INT>=23){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    new PermissionHelper(this).requestPermissionsForMainActiivty();
                    new FirstUseInitHelper(MainActivity.this,sharedPreferences).initFileToSdcard();
                    //ActivityCompat.requestPermissions(this,REQUEST_WRITE_READ_EXTERNALPERMISSION,REQUEST_WRITE_READ_EXTERNAL_CODE);
                }else {
                    new FirstUseInitHelper(MainActivity.this,sharedPreferences).initFileToSdcard();
                }
            }else {
                new FirstUseInitHelper(MainActivity.this,sharedPreferences).initFileToSdcard();
            }
        }
    }






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_WRITE_READ_EXTERNAL_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    new FirstUseInitHelper(MainActivity.this,sharedPreferences).initFileToSdcard();
                }else {
                    finish();
                }
                break;
            case OVERLAY_PERMISSION_REQ_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    startService(intentSpeedStatistics);
                }else {
                    //finish();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        if(menu!=null){
            if(fragmentPagerAdapter.getItem(0) instanceof MainFragment){
                menu.findItem(R.id.about_Appme).setVisible(true);
                menu.findItem(R.id.log_clear).setVisible(false);
                menu.findItem(R.id.log_share).setVisible(false);
            }
        }

        return super.onCreateOptionsMenu(menu);
    }
    public void setSuspensionState(boolean state){
        if(state){
            ConnectivityManager mConnectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext())) {
            if (false) {
                //Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_alert), Toast.LENGTH_LONG).show();
                Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(permissionIntent, OVERLAY_PERMISSION_REQ_CODE);
               // ActivityCompat.requestPermissions(this,new String[]{Settings.ACTION_MANAGE_OVERLAY_PERMISSION},REQUEST_WRITE_READ_EXTERNAL_CODE);
            } else {
                startService(intentSpeedStatistics);
            }
            //LogUtil.printSS("  "+showSpeedStatistics);
            //startService(intentSpeedStatistics);
        }else {
            stopService(intentSpeedStatistics);
        }
    }

    private void addFragment(){
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        my_viewPager_V4 = (ViewPager) findViewById(R.id.my_viewPager_V4);
        fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        fragmentPagerAdapter.addFragment(new MainFragment(),"控制台");
        fragmentPagerAdapter.addFragment(new LogFragment(),"日志");
        //fragmentPagerAdapter.addFragment(new GraspDataFragment(),"抓包");
        fragmentPagerAdapter.addFragment(new ExplainFragment(),"说明");
        my_viewPager_V4.setOffscreenPageLimit(1);
        my_viewPager_V4.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(my_viewPager_V4);
       // TabLayout tabLayout = null;
        //tabLayout.setupWithViewPager(my_viewPager);
        //my_viewPager_V4.setOffscreenPageLimit(1);

        my_viewPager_V4.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Menu menu = toolbar.getMenu();
                if(menu==null){
                    return;
                }
                switch (position){
                    case 0:
                        //  if(screenSlidePagerAdapter.getItem(position) instanceof MainFragment){
                        menu.findItem(R.id.about_Appme).setVisible(true);
                        menu.findItem(R.id.log_clear).setVisible(false);
                        menu.findItem(R.id.log_share).setVisible(false);
                        //  }
                        break;
                    case 1:
                        if(!MainFragment.isStartOrStopDoing){
                            menu.findItem(R.id.about_Appme).setVisible(false);
                        }
                        menu.findItem(R.id.log_clear).setVisible(true);
                        menu.findItem(R.id.log_share).setVisible(true);
                        // }
                        break;
                    case 2:
                        // if(screenSlidePagerAdapter.getItem(position) instanceof GraspDataFragment){
                        if(!MainFragment.isStartOrStopDoing){
                            menu.findItem(R.id.about_Appme).setVisible(false);
                        }
                        menu.findItem(R.id.log_clear).setVisible(false);
                        menu.findItem(R.id.log_share).setVisible(false);
                        // }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void toolbarAnimateMe(boolean isShow){
        if(toolbar==null){
            return;
        }
        if (isShow) {
            toolbar.animate()
                    .translationY(0)
                    .alpha(1)
                    .setDuration(300)
                    .setInterpolator(new DecelerateInterpolator());
        } else {
            toolbar.animate()
                    .translationY(-toolbar.getBottom())
                    .alpha(0)
                    .setDuration(300)
                    .setInterpolator(new DecelerateInterpolator());
        }
    }
    //暴露 viewpager中的 fragment
    public Object getItemFragment(int position,Class<?> fragmentClassName){
       android.support.v4.app.Fragment fragment =  fragmentPagerAdapter.getItem(position);
        if(fragment.getClass().equals(fragmentClassName)){
            return fragment;
        }
        return null;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intentHome = new Intent(Intent.ACTION_MAIN);
            intentHome.addCategory(Intent.CATEGORY_HOME);
            startActivity(intentHome);
            return true;
        }*/
        return super.onKeyDown(keyCode, event);
    }
    private void aboutDialogShow(){
        final String qq = "580466685";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlQQ)));
                }catch (ActivityNotFoundException e){

                }
            }
        };
        spannable.setSpan(clickableSpan, spannable.length()-qq.length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//SPAN_EXCLUSIVE_EXCLUSIVE

        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(DensityUtil.dip2px(this,120),DensityUtil.dip2px(this,30),0,0);
        layoutParams.setMarginStart(DensityUtil.dip2px(this,30));
        TextView textView = new TextView(this);
        textView.setLayoutParams(layoutParams);
        textView.setText(spannable);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setTextColor(Color.parseColor("#ffffff"));
        textView.setTextSize(16, TypedValue.COMPLEX_UNIT_SP);
        relativeLayout.addView(textView);

        builder.setView(relativeLayout);
        //builder.setMessage(spannable);
        builder.setNeutralButton("错误反馈", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startQQTalk("857899299");
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

    private void startQQTalk(String qq){
        String url="mqqwpa://im/chat?chat_type=wpa&uin="+qq;
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }catch (Exception e){
            Toast.makeText(MainActivity.this,"打开qq失败，可能你没有安装qq",Toast.LENGTH_LONG).show();
        }
    }


    //选择背景图片
    private void selectWallpaper(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        try {
            startActivityForResult(intent,REQUEST_CODE_SELECT_WALLPAPER);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_OK&&requestCode==REQUEST_CODE_SELECT_WALLPAPER){
            Uri uri = data.getData();
            sharedPreferences.edit().putString(SharedPreferenceMy.WALLPAPER_PATH,uri.toString()).commit();
            ContentResolver contentResolver = this.getContentResolver();
            try {
                MainActivity.this.getWindow().setBackgroundDrawable(Drawable.createFromStream(contentResolver.openInputStream(uri),null) );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void setBackground(){
        boolean isInitSdcard = sharedPreferences.getBoolean(SharedPreferenceMy.IS_INIT_SYSTEM,false);
         String uriPath = sharedPreferences.getString(SharedPreferenceMy.WALLPAPER_PATH,null);
        if(isInitSdcard){
            if(uriPath!=null){
                ContentResolver contentResolver = this.getContentResolver();
                try {
                    MainActivity.this.getWindow().setBackgroundDrawable(Drawable.createFromStream(contentResolver.openInputStream(Uri.parse(uriPath)),null) );
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }  if(uriPath==null) {
            try {
                MainActivity.this.getWindow().setBackgroundDrawable(Drawable.createFromStream(getAssets().open("2583.jpg"),null) );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    //创建新配置文件
    private void createConfig(){
        final File file = new File(FileUtil.APP_APTH_CONFIG);
        if(!file.exists()){
            file.mkdirs();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新建配置文件");
        final EditText et = new EditText(this);
        et.setHint("输入文件名");
        builder.setView(et);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileNeme = et.getText().toString().trim();
                if(fileNeme==null||fileNeme.equals("")||fileNeme.length()>10){
                    Snackbar.make(toolbar,getString(R.string.new_file_name_illegality),Snackbar.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, ConfigEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConfigEditActivity.INTENT_BUNDLE_TYPE_FILE_KEY,file);
        bundle.putSerializable(ConfigEditActivity.INTENT_BUNDLE_TYPE_CREATE_NEW_KEY,true);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //logfragmnet监听交互
    @Override
    public void onListFRagmentInteraction(String str) {

        final String backgroundHost = "11.22.33.44";
        if(str.contains(backgroundHost)){
            //TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if(networkInfo.getType()==ConnectivityManager.TYPE_WIFI){
                LogContent.addItemAndNotify(getString(R.string.not_allow_current_is_wifi));
                return;
            }else {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://"+backgroundHost);
                intent.setData(content_url);
                try{
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    private void setMarginStatusBar(){
     /*   *
         * 获取状态栏高度——方法1
         **/
        int statusBarHeight1 = -1;
//获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        LinearLayout.LayoutParams params = ((LinearLayout.LayoutParams)(toolbar.getLayoutParams()));
            params.setMargins(0,statusBarHeight1,0,0);
            toolbar.setLayoutParams(params);

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


    public MyFragmentPagerAdapter getFragmentPagerAdapter() {
        return fragmentPagerAdapter;
    }
    public ViewPager getMy_viewPager() {
        return my_viewPager_V4;
    }
    public Toolbar getToolbar(){
        return toolbar;
    }

    public Intent getIntentSpeedStatistics() {
        return intentSpeedStatistics;
    }

    public void setIntentSpeedStatistics(Intent intentSpeedStatistics) {
        this.intentSpeedStatistics = intentSpeedStatistics;
    }
}
