package cn.wsgwz.gravity.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.explain.zoomimageview.sample.ViewPagerSampleActivity;
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
import cn.wsgwz.gravity.dialog.ConfigSelectDialog;
import cn.wsgwz.gravity.helper.ShellHelper;
import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;


public class MainFragment extends Fragment implements View.OnClickListener,ShellUtil.IsProgressListenner{
    private Switch service_Switch;
    private Intent intentServer;

    private Button select_Bn,explain_Bn;

    private SharedPreferences sharedPreferences;

    public static boolean isStartOrStopDoing;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_main,container,false);
        initView(view);
        return view;
    }
    private void initView(View view){
        service_Switch = (Switch) view.findViewById(R.id.service_Switch);
        service_Switch.setChecked(ProxyService.isStart);
        service_Switch.setOnClickListener(this);


        select_Bn = (Button) view.findViewById(R.id.select_Bn);
        explain_Bn = (Button) view.findViewById(R.id.explain_Bn);
        select_Bn.setOnClickListener(this);

        intentServer = new Intent(getActivity().getApplication(), ProxyService.class);

        sharedPreferences = getActivity().getSharedPreferences(SharedPreferenceMy.MAIN_CONFIG, Context.MODE_PRIVATE);



        explain_Bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ViewPagerSampleActivity.class));
            }
        });

        ShellUtil.setIsProgressListenner(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.service_Switch:
                boolean isExecShell = getActivity().getSharedPreferences(SharedPreferenceMy.MAIN_CONFIG,Context.MODE_PRIVATE).getBoolean(SharedPreferenceMy.SHELL_IS_FLLOW_MENU, true);
                if( sharedPreferences.getString(SharedPreferenceMy.CURRENT_CONFIG_PATH,null)!=null){
                    if(service_Switch.isChecked()){
                        getActivity().startService(intentServer);
                        if(isExecShell){
                            ShellUtil.maybeExecShell(true,(MainActivity) getActivity());
                        }
                    } else {
                        getActivity().stopService(intentServer);
                        if(isExecShell){
                            ShellUtil.maybeExecShell(false,(MainActivity) getActivity());
                        }
                    }
                }else {
                    Snackbar.make(service_Switch,getString(R.string.please_select_config), Snackbar.LENGTH_SHORT).show();
                    service_Switch.setChecked(false);

                    select_Bn.setClickable(false);
                    service_Switch.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onClick(select_Bn);
                            select_Bn.setClickable(true);
                        }
                    },800);
                }

                break;
            case R.id.select_Bn:
                boolean isInitSdcard = sharedPreferences.getBoolean(SharedPreferenceMy.IS_INIT_SDCARD,false);
                if(isInitSdcard){
                    ConfigSelectDialog configSelectDialog = new ConfigSelectDialog(getActivity());
                    configSelectDialog.setOnServerStateChangeListenner(new ConfigSelectDialog.OnServerStateChangeListenner() {
                        @Override
                        public void onChange(boolean isStart) {
                            service_Switch.setChecked(isStart);
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
        final boolean tempBool = service_Switch.isChecked();
        service_Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service_Switch.setChecked(tempBool);
                Toast.makeText(getActivity(),"正在执行: "+(isProgressEnum==null?"":isProgressEnum.getValues()),Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void finallyThat() {
        isStartOrStopDoing = false;
        service_Switch.setOnClickListener(this);
    }

}
