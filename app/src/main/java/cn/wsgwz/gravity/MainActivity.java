package cn.wsgwz.gravity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;


import cn.wsgwz.gravity.activity.ConfigEditActivity;
import cn.wsgwz.gravity.activity.DefinedShellActivity;
import cn.wsgwz.gravity.fragment.GraspDataFragment;
import cn.wsgwz.gravity.fragment.MainFragment;
import cn.wsgwz.gravity.fragment.ExplainFragment;
import cn.wsgwz.gravity.fragment.log.LogContent;
import cn.wsgwz.gravity.fragment.log.LogFragment;
import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;
import cn.wsgwz.gravity.util.UnzipFromAssets;
import cn.wsgwz.gravity.view.slidingTabLayout.ScreenSlidePagerAdapter;
import cn.wsgwz.gravity.view.slidingTabLayout.SlidingTabLayout;
import cn.wsgwz.gravity.view.slidingTabLayout.ViewPager;
import cn.wsgwz.gravity.util.LogUtil;
import static junit.framework.Assert.assertEquals;


public class MainActivity extends AppCompatActivity implements LogFragment.OnListFragmentInteractionListenner{
    //选择背景请求值
    public static final  int REQUEST_CODE_SELECT_WALLPAPER = 4;


    private  Toolbar toolbar;
    private RelativeLayout main_RL;
    private RelativeLayout activity_main;
    private SlidingTabLayout slidingTabLayout;
    private  ViewPager my_viewPager;
    private   ScreenSlidePagerAdapter screenSlidePagerAdapter;



    private SharedPreferences sharedPreferences;
    public static final  int REQUEST_WRITE_READ_EXTERNAL_CODE  = 3;
    public static final String[] REQUEST_WRITE_READ_EXTERNALPERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS

    };
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


    private void setMarginDector(){
        /**
         * 获取状态栏高度——方法1
         * */
        int statusBarHeight1 = -1;
//获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams)(toolbar.getLayoutParams()));
            params.setMargins(0,statusBarHeight1,0,0);
            toolbar.setLayoutParams(params);
    }


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
        initView();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            setMarginDector();
        }
        main_RL.postDelayed(new Runnable() {
            @Override
            public void run() {
                setBackground();
            }
        },200);

        //overridePendingTransition(R.anim.main_start_animation, R.anim.main_exit_animation);

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isFllowProxySercer = sharedPreferences.getBoolean(SharedPreferenceMy.SHELL_IS_FLLOW_MENU,true);
        menu.findItem(R.id.fllow_shell).setChecked(isFllowProxySercer);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void onStart() {
        super.onStart();
        boolean isInitSystem = sharedPreferences.getBoolean(SharedPreferenceMy.IS_INIT_SYSTEM,false);
        if(!isInitSystem){
            if(Build.VERSION.SDK_INT>=23){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,REQUEST_WRITE_READ_EXTERNALPERMISSION,REQUEST_WRITE_READ_EXTERNAL_CODE);
                }else {
                    initFileToSdcard();
                }
            }else {
                initFileToSdcard();
            }
        }
    }

    private void initSystemFile(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.start_init_app_util));
        final Dialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.payDialogStyleAnimation);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        String drectoryName = getResources().getString(R.string.app_name);
        final String str =
                "mount -o remount ,rw /"+"\n"+
                        "mkdir /system/xbin/"+drectoryName+"\n"+
                        "mkdir /system/xbin/Jume"+"\n"+
                        "cp "+FileUtil.SD_APTH_CONFIG+"/"+FileUtil.ABC_FILE_NAME+" "+"/system/xbin/"+drectoryName+"\n"+
                        "cd /system/xbin/"+drectoryName+"\n"+
                        "unzip -o "  +FileUtil.ABC_FILE_NAME  +"\n"+
                        "chmod -R 777  /system/xbin/"+drectoryName+"\n"+
                        "cd ..\n"+
                        "cp "+FileUtil.SD_APTH_CONFIG+"/"+FileUtil.JUME_FILE_NAME+" "+"/system/xbin/Jume"+"\n"+
                        "cd /system/xbin/Jume"+"\n"+
                        "unzip -o "  +FileUtil.JUME_FILE_NAME  +"\n"+
                        "chmod -R 777  /system/xbin/Jume";


                main_RL.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ShellUtil.execShell(MainActivity.this, str, new OnExecResultListenner() {
                            @Override
                            public void onSuccess(StringBuffer sb) {
                                sharedPreferences.edit().putBoolean(SharedPreferenceMy.IS_INIT_SYSTEM,true).commit();
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this,getString(R.string.init_app_util_success),Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(StringBuffer sb) {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this,getString(R.string.init_app_util_error),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                },2000);


    }

    private void initFileToSdcard(){
        try {
            UnzipFromAssets.unZip( this,  FileUtil.CONFIG_FILE_NAME,  FileUtil.SD_APTH_CONFIG,  true);
            UnzipFromAssets.toSdcard( this,  FileUtil.ABC_FILE_NAME, FileUtil.SD_APTH_CONFIG,  true);
            UnzipFromAssets.toSdcard( this,  FileUtil.JUME_FILE_NAME, FileUtil.SD_APTH_CONFIG,  true);
            sharedPreferences.edit().putBoolean(SharedPreferenceMy.IS_INIT_SYSTEM,true).commit();
            final Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case 1000:
                            initSystemFile();
                            break;
                        case 1001:
                            LogContent.addItemAndNotify(getString(R.string.get_root_permission_error));
                            break;
                    }
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process process = null;
                    DataOutputStream dataOutputStream = null;
                    BufferedReader errorBr = null;
                    try {
                        process = Runtime.getRuntime().exec("su");
                        dataOutputStream = new DataOutputStream(process.getOutputStream());
                        dataOutputStream.writeBytes("exit\n");
                        dataOutputStream.flush();
                        process.waitFor();
                    //process.waitFor();
                        errorBr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line = null;
                    while ((line=errorBr.readLine())!=null){
                        if(line.equals("[-] Unallowed user")){
                            handler.sendEmptyMessage(1001);
                            return;
                        }
                    }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        process.destroy();
                        if(dataOutputStream!=null){
                            try {
                                dataOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if(errorBr!=null){
                            try {
                                errorBr.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    handler.sendEmptyMessage(1000);
                }
            }).start();

        } catch (IOException e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_WRITE_READ_EXTERNAL_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    initFileToSdcard();
                }else {
                    finish();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        if(menu!=null){
            if(screenSlidePagerAdapter.getItem(0) instanceof MainFragment){
                menu.findItem(R.id.about_Appme).setVisible(true);
                menu.findItem(R.id.log_clear).setVisible(false);
                menu.findItem(R.id.log_share).setVisible(false);
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    public Toolbar getToolbar(){
        return toolbar;
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setLogo(R.mipmap.diqiu);//设置app logo
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        //toolbar.setSubtitle("gravity"+" "+FileUtil.VERSION_NUMBER);
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
        sharedPreferences = getSharedPreferences(SharedPreferenceMy.CONFIG,MODE_PRIVATE);
        main_RL = (RelativeLayout) findViewById(R.id.main_RL);
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        slidingTabLayout = (SlidingTabLayout)findViewById(R.id.slidingTabLayout);
        my_viewPager = (ViewPager) findViewById(R.id.my_viewPager);
        screenSlidePagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager(),this);
        addFragment();
     /*   int uid = 0;
        try {
           uid =  (getPackageManager().getApplicationInfo(getPackageName(), 0)).uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Spannable spannable = new SpannableString(getString(R.string.app_name)+" uid:"+uid);
        spannable.setSpan(new AbsoluteSizeSpan(23,true), 0, getString(R.string.app_name).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new AbsoluteSizeSpan(16,true), getString(R.string.app_name).length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/

    }




    private void addFragment(){
        screenSlidePagerAdapter.addTab("控制台", MainFragment.class);
        screenSlidePagerAdapter.addTab("日志", LogFragment.class);
        screenSlidePagerAdapter.addTab("抓包", GraspDataFragment.class);
        //screenSlidePagerAdapter.addTab("配置", ConfigFragment.class);
        screenSlidePagerAdapter.addTab("说明", ExplainFragment.class);
        my_viewPager.setAdapter(screenSlidePagerAdapter);
        my_viewPager.setOffscreenPageLimit(screenSlidePagerAdapter.getCount());
        slidingTabLayout.setViewPager(my_viewPager);
        my_viewPager.setOffscreenPageLimit(1);

        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
       Fragment fragment =  screenSlidePagerAdapter.getItem(position);
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
        final String qq = "857899299";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("关于");
        String aboutStr = "作者：我思故我在\r\n" +
                "版本："+FileUtil.VERSION_NUMBER+"\r\n"+
                "联系方式：QQ"+qq;

        Spannable spannable = new SpannableString(aboutStr);
        spannable.setSpan(new AbsoluteSizeSpan(15,true), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }
        };
        spannable.setSpan(clickableSpan, spannable.length()-qq.length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setMessage(spannable);
        builder.setNeutralButton("联系", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startQQTalk(qq);
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
                activity_main.setBackground(Drawable.createFromStream(contentResolver.openInputStream(uri),null) );
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
                    activity_main.setBackground(Drawable.createFromStream(contentResolver.openInputStream(Uri.parse(uriPath)),null) );
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }  if(uriPath==null) {
            try {
                activity_main.setBackground(Drawable.createFromStream(getAssets().open("bg.jpg"),null) );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    //创建新配置文件
    private void createConfig(){
        final File file = new File(FileUtil.SD_APTH_CONFIG);
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
                    Snackbar.make(main_RL,getString(R.string.new_file_name_illegality),Snackbar.LENGTH_SHORT).show();
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


    public ScreenSlidePagerAdapter getScreenSlidePagerAdapter() {
        return screenSlidePagerAdapter;
    }
    public ViewPager getMy_viewPager() {
        return my_viewPager;
    }



}
