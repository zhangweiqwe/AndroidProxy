package cn.wsgwz.gravity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;



import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


import cn.wsgwz.gravity.activity.ConfigEditActivity;
import cn.wsgwz.gravity.activity.DefinedShellActivity;
import cn.wsgwz.gravity.dialog.ConfigSelectDialog;
import cn.wsgwz.gravity.dialog.TVOffAnimation;
import cn.wsgwz.gravity.fragment.GraspDataFragment;
import cn.wsgwz.gravity.fragment.MainFragment;
import cn.wsgwz.gravity.fragment.log.LogContent;
import cn.wsgwz.gravity.fragment.log.LogFragment;
import cn.wsgwz.gravity.helper.ApnDbHelper;
import cn.wsgwz.gravity.helper.ShellHelper;
import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.NativeUtils;
import cn.wsgwz.gravity.util.NetworkUtil;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;
import cn.wsgwz.gravity.util.UnzipFromAssets;
import cn.wsgwz.gravity.view.slidingTabLayout.ScreenSlidePagerAdapter;
import cn.wsgwz.gravity.view.slidingTabLayout.SlidingTabLayout;
import cn.wsgwz.gravity.view.slidingTabLayout.ViewPager;

import static junit.framework.Assert.assertEquals;


public class MainActivity extends AppCompatActivity implements LogFragment.OnListFragmentInteractionListenner{
    //选择背景请求值
    public static final  int REQUEST_CODE_SELECT_WALLPAPER = 4;
    public static  MainActivity mainActivity ;


    private Toolbar toolbar;
    private RelativeLayout main_RL;
    private RelativeLayout activity_main;
    private SlidingTabLayout slidingTabLayout;
    private  ViewPager my_viewPager;
    private   ScreenSlidePagerAdapter screenSlidePagerAdapter;
    private ShellHelper shellHelper = ShellHelper.getInstance();


    private SharedPreferences sharedPreferences;
    public static final  int REQUEST_WRITE_READ_EXTERNAL_CODE  = 3;
    public static final String[] REQUEST_WRITE_READ_EXTERNALPERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS

    };


    //界面绘制完成
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        mainActivity = this;
        if(Build.VERSION.SDK_INT >= 21) {
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            int toolbarHeight = toolbar.getHeight();
            /* RelativeLayout.LayoutParams  params = ((RelativeLayout.LayoutParams)main_RL.getLayoutParams());
        params.setMargins(0,statusBarHeight+toolbarHeight,0,0);
        main_RL.setLayoutParams(params);*/

            RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams)(toolbar.getLayoutParams()));
            params.setMargins(0,statusBarHeight,0,0);
            toolbar.setLayoutParams(params);
        }

    }
    /*
    feature
 英 ['fiːtʃə]   美 ['fitʃɚ]   全球发音 跟读 口语练习
n. 特色，特征；容貌；特写或专题节目
vi. 起重要作用
vt. 特写；以…为特色；由…主演
    overlay [əʊvə'leɪ]
vt. 在表面上铺一薄层，镀
n. 覆盖图；覆盖物

decor
 英 ['deɪkɔː; 'de-]   美 [de'kɔr]   全球发音 跟读 口语练习
n. 装饰，布置
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);
        initView();
        setBackground();
        overridePendingTransition(R.anim.main_start_animation, R.anim.main_exit_animation);


       // LogUtil.printSS("Java--"+str);

        //startActivity(new Intent(this,Main2Activity.class));

        //initJume();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isFllowProxySercer = sharedPreferences.getBoolean(SharedPreferenceMy.SHELL_IS_FLLOW_MENU,true);
        menu.findItem(R.id.fllow_shell).setChecked(isFllowProxySercer);
        return super.onPrepareOptionsMenu(menu);
    }

    private void demoSocket(){
     /*   NetworkUtil networkUtil = new NetworkUtil(this);
            networkUtil.scan();
            if(true){
                Toast.makeText(MainActivity.this,"客户端",Toast.LENGTH_LONG).show();
                return;
        }*/
        Toast.makeText(MainActivity.this,"服务器",Toast.LENGTH_LONG).show();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1000:
                        Toast.makeText(MainActivity.this,"收到消息"+msg.obj,Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                ServerSocket serverSocket = new ServerSocket(60880);
                while(true){
                    final Socket socket = serverSocket.accept();
                    socket.setKeepAlive(true);
                    InputStream in = socket.getInputStream();
                    final OutputStream out = socket.getOutputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"));
                    String line=br.readLine();
                    while(line!=null){
                        LogUtil.printSS("--"+line+"-");
                        Thread thread =new Thread(new Runnable() {
                            @Override
                            public void run() {
                                PrintWriter pw = new PrintWriter(out);
                                    pw.write("Hello World\n");
                                    pw.flush();

                                // Message.obtain(handler, 222, res).sendToTarget();//发送服务器返回消息
                            }
                        });
                        thread.start();
                        thread.join();

                        Message msg = Message.obtain();
                        msg.obj = line;
                        msg.what = 1000;
                        handler.sendMessage(msg);
                        line = br.readLine();
                    }
                }
                }catch (IOException e){
                    LogUtil.printSS("IOException "+e.getMessage().toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    protected void onStart() {
        //demoSocket();

        super.onStart();
        boolean isInitSdcard = sharedPreferences.getBoolean(SharedPreferenceMy.IS_INIT_SDCARD,false);
        if(!isInitSdcard){
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



    private void initFileToSdcard(){
        try {
            UnzipFromAssets.unZip( this,  FileUtil.CONFIG_FILE_NAME,  FileUtil.SD_APTH_CONFIG,  true);
            UnzipFromAssets.toSdcard( this,  FileUtil.ABC_FILE_NAME, FileUtil.SD_APTH_CONFIG,  true);
            UnzipFromAssets.toSdcard( this,  FileUtil.JUME_FILE_NAME, FileUtil.SD_APTH_CONFIG,  true);
            sharedPreferences.edit().putBoolean(SharedPreferenceMy.IS_INIT_SDCARD,true).commit();

            initJume();
            Toast.makeText(this,getString(R.string.init_sdcard_file_ok),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
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
                    Toast.makeText(this,getString(R.string.init_sdcard_file_request_permisson_error),Toast.LENGTH_SHORT).show();
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
                        boolean isInitSdcard = sharedPreferences.getBoolean(SharedPreferenceMy.IS_INIT_SDCARD,false);
                        if(isInitSdcard){
                            selectWallpaper();
                        }else {
                            Toast.makeText(MainActivity.this,getString(R.string.init_sdcard_hint),Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.create_config:
                        boolean isInitSdcard2 = sharedPreferences.getBoolean(SharedPreferenceMy.IS_INIT_SDCARD,false);
                        if(isInitSdcard2){
                            createConfig();
                        }else {
                            Toast.makeText(MainActivity.this,getString(R.string.init_sdcard_hint),Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.log_clear:
                        LogContent.clear(MainActivity.this);
                        break;
                    case R.id.log_share:
                        LogContent.share(MainActivity.this);
                        break;

                }
               // overridePendingTransition(R.anim.dialog_pay_start_animation_set,R.anim.dialog_pay_exit_animation_set);
                return false;
            }
        });
        sharedPreferences = getSharedPreferences(SharedPreferenceMy.MAIN_CONFIG,MODE_PRIVATE);
        main_RL = (RelativeLayout) findViewById(R.id.main_RL);
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        slidingTabLayout = (SlidingTabLayout)findViewById(R.id.slidingTabLayout);
        my_viewPager = (ViewPager) findViewById(R.id.my_viewPager);
        screenSlidePagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager(),this);
        screenSlidePagerAdapter.addTab("控制台", MainFragment.class);
        screenSlidePagerAdapter.addTab("日志", LogFragment.class);
        screenSlidePagerAdapter.addTab("抓包", GraspDataFragment.class);
        //screenSlidePagerAdapter.addTab("配置", ConfigFragment.class);
       // screenSlidePagerAdapter.addTab("说明", ExplainFragment.class);
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
                        //if(screenSlidePagerAdapter.getItem(position) instanceof LogFragment){
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
        int uid = 0;
        try {
           uid =  (getPackageManager().getApplicationInfo(getPackageName(), 0)).uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Spannable spannable = new SpannableString("Gravitation"+" uid:"+uid);
        spannable.setSpan(new AbsoluteSizeSpan(23,true), 0, "Gravitation".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new AbsoluteSizeSpan(16,true), "Gravitation".length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

       // getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#22000000")));




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


    public void say(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
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
        dialog.getWindow().setWindowAnimations(R.style.payDialogStyleAnimation);
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
            //LogUtil.printSS(uri.toString());
            sharedPreferences.edit().putString(SharedPreferenceMy.WALLPAPER_PATH,uri.toString()).commit();
            ContentResolver contentResolver = this.getContentResolver();
            try {
                activity_main.setBackground(Drawable.createFromStream(contentResolver.openInputStream(uri),null) );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // can post image
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void setBackground(){
        boolean isInitSdcard = sharedPreferences.getBoolean(SharedPreferenceMy.IS_INIT_SDCARD,false);
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
                activity_main.setBackground(Drawable.createFromStream(getAssets().open("bg.jpeg"),null) );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

       /* if (activity_main.getBackground()==null){
            // activity_main.setBackgroundBa(R.mipmap.bg);
        }*/
    }

    private void initJume(){
       /* String str =
                "mount -o remount,rw /system"+"\n"+
                        "cd /system/xbin"+"\n"+
                        "mkdir Jume"+"\n"+
                        "cd /system"+"\n"+
                        "cp "+FileUtil.SD_APTH_CONFIG+"/"+FileUtil.JUME_FILE_NAME+" "+"system/xbin/Jume"+"\n"+
                        "cd Jume"+"\n"+
                        "unzip  "  +FileUtil.JUME_FILE_NAME  +" -o -d "+"/system/xbin/Jume"+"\n"+
                        "chmod -R 777  /system/xbin/Jume";*/

        String str =
                "mount -o remount,rw /system"+"\n"+
                        "mkdir /system/xbin/Jume"+"\n"+
                        "cp "+FileUtil.SD_APTH_CONFIG+"/"+FileUtil.JUME_FILE_NAME+" "+"system/xbin/Jume"+"\n"+
                        "cd /system/xbin/Jume"+"\n"+
                        "unzip  "  +FileUtil.JUME_FILE_NAME  +"\n"+
                        "chmod -R 777  /system/xbin/Jume";

       // String str = "cp "+FileUtil.SD_APTH_CONFIG+"/"+FileUtil.JUME_FILE_NAME+" "+"system/xbin/Jume"+"\n";
        ShellUtil.execShell(this, str, new OnExecResultListenner() {
            @Override
            public void onSuccess(StringBuffer sb) {
                //LogUtil.printSS("ssssssss  OK"+sb.toString());
            }

            @Override
            public void onError(StringBuffer sb) {
                //LogUtil.printSS("ssssssss  error"+sb.toString());
            }
        });
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
        //dialog.getWindow().setAllowEnterTransitionOverlap(true);
        //dialog.getWindow().setAllowReturnTransitionOverlap(true);
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
        if(str.contains(ProxyService.BACKGROUND_HOST)){
            //TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            switch (networkInfo.getType()){
                case ConnectivityManager.TYPE_WIFI:
                    //Toast.makeText(this,getString(R.string.not_allow_current_is_wifi),Toast.LENGTH_SHORT).show();
                    LogContent.addItemAndNotify(getString(R.string.not_allow_current_is_wifi));
                    return;
            }
            startBackgroundUseBrowsable();
        }
    }

    //打开后台
    private void startBackgroundUseBrowsable(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("http://"+ProxyService.BACKGROUND_HOST);
        intent.setData(content_url);
        try{
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public ScreenSlidePagerAdapter getScreenSlidePagerAdapter() {
        return screenSlidePagerAdapter;
    }

    public ViewPager getMy_viewPager() {
        return my_viewPager;
    }



}
