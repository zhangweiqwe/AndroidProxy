package cn.wsgwz.gravity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.DataOutputStream;
import java.io.File;

import cn.wsgwz.gravity.activity.DefinedShellActivity;
import cn.wsgwz.gravity.activity.HelperActivity;
import cn.wsgwz.gravity.adapter.MyFragmentPagerAdapter;
import cn.wsgwz.gravity.contentProvider.MyAppContentProvider;
import cn.wsgwz.gravity.fragment.GraspDataFragment;
import cn.wsgwz.gravity.fragment.MainFragment;
import cn.wsgwz.gravity.fragment.ExplainFragment;
import cn.wsgwz.gravity.fragment.log.LogContent;
import cn.wsgwz.gravity.fragment.log.LogFragment;
import cn.wsgwz.gravity.helper.MainActivityHelper;
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




    public static final  int REQUEST_WRITE_READ_EXTERNAL_CODE  = 3;
    public static final String[] REQUEST_WRITE_READ_EXTERNALPERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
    };

    public static final  int OVERLAY_PERMISSION_REQ_CODE = 5;
    public MainActivityHelper mainActivityHelper;
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
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
           // mainActivityHelper.setMarginStatusBar();
       // }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
        mainActivityHelper.setMarginStatusBar();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (settingHelper.getMainActivityStyle(this)){
            case 0:
                break;
            case 1:
            case 2:
                setTheme(R.style.AppThemeMianAcivity2);
                break;
        }

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
            //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //window.getAttributes().flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
        window.getAttributes().flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        setContentView(R.layout.activity_main);

        LinearLayout mainLL = (LinearLayout) findViewById(R.id.main_LL);
        switch (settingHelper.getMainActivityStyle(this)){
            case 0:
                break;
            case 1:
            case 2:
                ViewGroup.LayoutParams layoutParams =  mainLL.getLayoutParams();
                layoutParams.height = DensityUtil.dip2px(this,453);
                layoutParams.width = DensityUtil.dip2px(this,280);
                break;

        }


        mainActivityHelper = new MainActivityHelper(this);


        mainActivityHelper.initFile();
        mainActivityHelper.doOther();




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

                mainActivityHelper.setBackground(settingHelper,MainActivity.this);
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
                                boolean isInitSystem = new File(FileUtil.FIRST_INIT_JUME_PATH).exists();
                                if(isInitSystem){
                                    if(!MainFragment.isStartOrStopDoing){
                                        ShellUtil.maybeExecShell(true,MainActivity.this);
                                    }else {
                                        Toast.makeText(MainActivity.this,getString(R.string.busy_is_doing_some_thing),Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            case R.id.exec_stop:
                                boolean isInitSystem2 = new File(FileUtil.FIRST_INIT_JUME_PATH).exists();
                                if(isInitSystem2){
                                    if(!MainFragment.isStartOrStopDoing){
                                        ShellUtil.maybeExecShell(false,MainActivity.this);
                                    }else {
                                        Toast.makeText(MainActivity.this,getString(R.string.busy_is_doing_some_thing),Toast.LENGTH_SHORT).show();
                                    }
                                }

                                break;
                            case R.id.fllow_shell:
                                boolean b = !item.isChecked();
                                item.setChecked(b);
                                mainActivityHelper.getSharedPreferences().edit().putBoolean(SharedPreferenceMy.SHELL_IS_FLLOW_MENU,b).commit();
                                break;
                            case R.id.speedStatistics:
                               //LogUtil.printSS("---sss"+item.isChecked());
                                boolean c2 = !item.isChecked();
                                item.setChecked(c2);
                                //vt. 拿，取；采取；接受（礼物等）；买，花费；耗费（时间等）
                                /* 英 [ɪ'fekt]   美 [ɪ'fɛkt]   全球发音 跟读 口语练习
                                    n. 影响；效果；作用*/
                                mainActivityHelper.getSharedPreferences().edit().putBoolean(SharedPreferenceMy.SPEED_STATISTICS,c2).commit();
                                mainActivityHelper.setSuspensionState(c2);
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
                                mainActivityHelper.aboutDialogShow();
                                break;
                            case R.id.wallpaper:
                                selectWallpaper();
                                break;
                            case R.id.create_config:
                                mainActivityHelper.createConfig();
                                break;
                            case R.id.log_clear:
                                LogContent.clear(MainActivity.this);
                                break;
                            case R.id.log_share:
                                LogContent.share(MainActivity.this);
                                break;

                            case R.id.style_dialog:
                                settingHelper.setMainActivityStyle(MainActivity.this,2);
                                toolbar.getMenu().findItem(R.id.style_fullscreen).setChecked(false);
                                toolbar.getMenu().findItem(R.id.style_transparent_dialog).setChecked(false);
                                clearTopAndCutStyle();
                                break;
                            case R.id.style_transparent_dialog:
                                settingHelper.setMainActivityStyle(MainActivity.this,1);
                                toolbar.getMenu().findItem(R.id.style_dialog).setChecked(false);
                                toolbar.getMenu().findItem(R.id.style_fullscreen).setChecked(false);
                                clearTopAndCutStyle();
                                break;
                            case R.id.style_fullscreen:
                                settingHelper.setMainActivityStyle(MainActivity.this,0);
                                toolbar.getMenu().findItem(R.id.style_dialog).setChecked(false);
                                toolbar.getMenu().findItem(R.id.style_transparent_dialog).setChecked(false);
                                clearTopAndCutStyle();
                                break;
                            case R.id.helper:
                                startActivity(new Intent(MainActivity.this, HelperActivity.class));
                                break;

                        }
                        return false;
                    }
                });

        //overridePendingTransition(R.anim.main_start_animation, R.anim.main_exit_animation);
    }

    //清除栈顶activity，切换主题
    private void clearTopAndCutStyle(){
        Intent intent = new Intent(MainActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isFllowProxySercer = mainActivityHelper.getSharedPreferences().getBoolean(SharedPreferenceMy.SHELL_IS_FLLOW_MENU,true);
        boolean showSpeedStatistics = mainActivityHelper.getSharedPreferences().getBoolean(SharedPreferenceMy.SPEED_STATISTICS,true);
        menu.findItem(R.id.fllow_shell).setChecked(isFllowProxySercer);
        menu.findItem(R.id.speedStatistics).setChecked(showSpeedStatistics);
        menu.findItem(R.id.isCapture).setChecked(settingHelper.isCaptrue(this));

        switch (settingHelper.getMainActivityStyle(MainActivity.this)){
            case 0:
                menu.findItem(R.id.style_fullscreen).setChecked(true);
                menu.findItem(R.id.style_transparent_dialog).setChecked(false);
                menu.findItem(R.id.style_dialog).setChecked(false);
                break;
            case 1:
                menu.findItem(R.id.style_fullscreen).setChecked(false);
                menu.findItem(R.id.style_transparent_dialog).setChecked(true);
                menu.findItem(R.id.style_dialog).setChecked(false);
                break;
            case 2:
                menu.findItem(R.id.style_fullscreen).setChecked(false);
                menu.findItem(R.id.style_transparent_dialog).setChecked(false);
                menu.findItem(R.id.style_dialog).setChecked(true);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_WRITE_READ_EXTERNAL_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                }else {
                    finish();
                }
                break;
            case OVERLAY_PERMISSION_REQ_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    startService(mainActivityHelper.getIntentSpeedStatistics());
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


    private void addFragment(){
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        my_viewPager_V4 = (ViewPager) findViewById(R.id.my_viewPager_V4);
        fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        fragmentPagerAdapter.addFragment(new MainFragment(),"控制台");
        fragmentPagerAdapter.addFragment(new LogFragment(),"日志");
        //fragmentPagerAdapter.addFragment(new GraspDataFragment(),"抓包");
        //fragmentPagerAdapter.addFragment(new ExplainFragment(),"帮助");
        my_viewPager_V4.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(my_viewPager_V4);

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
            mainActivityHelper.getSharedPreferences().edit().putString(SharedPreferenceMy.WALLPAPER_PATH,uri.toString()).commit();
            mainActivityHelper.setBackground(settingHelper,MainActivity.this);
        }
        super.onActivityResult(requestCode, resultCode, data);
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



    public MyFragmentPagerAdapter getFragmentPagerAdapter() {
        return fragmentPagerAdapter;
    }
    public ViewPager getMy_viewPager() {
        return my_viewPager_V4;
    }
    public Toolbar getToolbar(){
        return toolbar;
    }


}
