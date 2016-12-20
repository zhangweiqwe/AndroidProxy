package cn.wsgwz.gravity.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
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
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.MyScrollView2;
import cn.wsgwz.gravity.util.NativeUtils;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;
import cn.wsgwz.gravity.view.MyScrollView;
import cn.wsgwz.photospreview.PhotosPreviewActivity;


public class MainFragment extends Fragment implements View.OnClickListener,ShellUtil.IsProgressListenner,GestureDetector.OnGestureListener{

    private GestureDetector detector;
    private MyScrollView2 myScrollView;

    private Switch service_Switch;


    private Button select_Bn,explain_Bn;

    private SharedPreferences sharedPreferences;

    public static boolean isStartOrStopDoing;
    private Intent intentServer;

    private void fllowServer(boolean isStart){

        boolean isExecShell = sharedPreferences.getBoolean(SharedPreferenceMy.SHELL_IS_FLLOW_MENU, true);
        if(isExecShell){
            ShellUtil.maybeExecShell(isStart,(MainActivity) getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_main,container,false);
        initView(view);

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();


        boolean serviceIsStart =  sharedPreferences.getBoolean(SharedPreferenceMy.SERVICE_IS_START,false);
        service_Switch.setChecked(serviceIsStart);
    }

    private void initView(final View view){
        sharedPreferences = getActivity().getSharedPreferences(SharedPreferenceMy.MAIN_CONFIG, Context.MODE_PRIVATE);


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


    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if( sharedPreferences.getString(SharedPreferenceMy.CURRENT_CONFIG_PATH,null)!=null){
                if(b){
                    sharedPreferences.edit().putBoolean(SharedPreferenceMy.SERVICE_IS_START,true).commit();
                    LogContent.addItem(Build.BRAND+"  "+Build.MODEL+" "+Build.VERSION.RELEASE+"  "+"  API:"+Build.VERSION.SDK_INT);
                    LogContent.addItemAndNotify("当前版本: "+getResources().getString(R.string.app_name)+FileUtil.VERSION_NUMBER);
                    getActivity().startService(intentServer);
                    fllowServer(true);
                } else {
                    sharedPreferences.edit().putBoolean(SharedPreferenceMy.SERVICE_IS_START,false).commit();
                    getActivity().stopService(intentServer);
                    fllowServer(false);
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
                boolean isInitSdcard = sharedPreferences.getBoolean(SharedPreferenceMy.IS_INIT_SDCARD,false);
                if(isInitSdcard){
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
                }else {
                    Toast.makeText(getActivity(),getString(R.string.init_sdcard_hint),Toast.LENGTH_SHORT).show();
                }
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
