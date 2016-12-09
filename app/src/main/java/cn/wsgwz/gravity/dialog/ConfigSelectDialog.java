package cn.wsgwz.gravity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pull.refreshview.XFooterView;
import com.example.pull.refreshview.XListView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.activity.ConfigEditActivity;
import cn.wsgwz.gravity.adapter.ConfigSelectAdapter;
import cn.wsgwz.gravity.config.EnumMyConfig;
import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;

/**
 * Created by Jeremy Wang on 2016/11/7.
 */

public class ConfigSelectDialog extends Dialog implements AdapterView.OnItemClickListener{
    private Context context;
    public ConfigSelectDialog(Context context) {
        super(context, R.style.payDialogStyle);
        this.context = context;
    }

    public ConfigSelectDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ConfigSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private TextView hint_TV,currentConfig_TV;
    private XListView list_view;
    private ConfigSelectAdapter configSelectAdapter;
    private List<File> fileList;

    private Intent intentServer;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(R.style.payDialogStyle);
        //getWindow().setWindowAnimations(R.style.AppTheme);
        setContentView(R.layout.dialog_config_select);
        initView();
    }
    private void initView(){
        intentServer = new Intent(getContext(), ProxyService.class);
        sharedPreferences  = getContext().getSharedPreferences(SharedPreferenceMy.MAIN_CONFIG,Context.MODE_PRIVATE);
        hint_TV = (TextView)findViewById(R.id.hint_TV);
        currentConfig_TV = (TextView) findViewById(R.id.currentConfig_TV);
        currentConfig_TV.setText(sharedPreferences.getString(SharedPreferenceMy.CURRENT_CONFIG_PATH,"(当前未选择模式)"));
        list_view = (XListView)findViewById(R.id.list_view);
        list_view.setPullLoadEnable(false);
        list_view.setPullRefreshEnable(false);
        fileList =  new ArrayList<>();
        configSelectAdapter = new ConfigSelectAdapter(getContext(),fileList);
        list_view.setOnItemClickListener(this);

        initListView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view,  int position, long id) {
        if(position==0){
            return;
        }
      final int   position1 = position - 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("重启生效");
        builder.setMessage("是否重启？");
        final File file = fileList.get(position1);
        if(file.exists()&&!file.getPath().contains("android_asset")){
            builder.setNegativeButton("编辑", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startEditConfig(fileList.get(position1));
                }
            });
            builder.setNeutralButton("删除", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(file.exists()){
                        fileList.remove(position1);
                        file.delete();
                        configSelectAdapter.notifyDataSetChanged();
                    }
                    Snackbar.make(view, getContext().getString(R.string.delate_file_succeed), Snackbar.LENGTH_SHORT).show();
                }
            });
        }

        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getContext().stopService(intentServer);
                getContext().startService(intentServer);
                sharedPreferences.edit().putString(SharedPreferenceMy.CURRENT_CONFIG_PATH,fileList.get(position1).getAbsolutePath()).commit();
                currentConfig_TV.setText(file.getAbsolutePath());
                //Snackbar.make(view, getContext().getString(R.string.restart_server_ok), Snackbar.LENGTH_SHORT).show();
                dialog.dismiss();
                ConfigSelectDialog.this.dismiss();
                ShellUtil.maybeExecShell(true,(MainActivity) context);
                if(onServerStateChangeListenner!=null){
                    onServerStateChangeListenner.onChange(true);
                }
            }
        });

        builder.show();
    }

    private void initListView(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1000:
                        list_view.setAdapter(configSelectAdapter);
                        //configSelectAdapter.notifyDataSetChanged();
                        //setListViewHeight(list_view);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(FileUtil.SD_APTH_QQ);
                if(file.exists()){
                    File[] files= file.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            if (name.endsWith(FileUtil.CONFIG_END_NAME)){
                                return true;
                            }
                            return false;
                        }
                    });
                    Collections.addAll(fileList,files);
                }

                File file1 = new File(FileUtil.SD_APTH_CONFIG);
                if(file1.exists()){
                   File[] files= file1.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            if (name.endsWith(FileUtil.CONFIG_END_NAME)){
                                return true;
                            }
                            return false;
                        }
                    });
                    Collections.addAll(fileList,files);
                }


                List<EnumMyConfig> listEnum = EnumMyConfig.getMeConfig();
                if(listEnum!=null){
                    for(int i=0;i<listEnum.size();i++){
                        Collections.addAll(fileList,new File(listEnum.get(i).getName()));
                    }
                }

                
                //addAssetsConfigs();
                handler.sendEmptyMessage(1000);

            }
        }).start();
    }
    //添加assets目录里的配置文件
    private void addAssetsConfigs(){
        try {
           String[] filesPath =  context.getAssets().list("config");
            for(int i=0;i<filesPath.length;i++){
                fileList.add(new File(FileUtil.ASSETS_CONFIG_PATH+filesPath[i]));
                //Log.d("sssssssss",""+filesPath[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
       /* if(file1.exists()){
            File[] files= file1.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".xml")){
                        return true;
                    }
                    return false;
                }
            });
            Collections.addAll(fileList,files);
        }*/
    }

    public    void setListViewHeight(ListView listView) {
        Adapter adapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            //android:minHeight="?android:attr/listPreferredItemHeight"

        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight +
                (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    //当服务状态发生改变
    public interface OnServerStateChangeListenner{
        void onChange(boolean isStart);
    }
    private OnServerStateChangeListenner onServerStateChangeListenner;
    public void setOnServerStateChangeListenner(OnServerStateChangeListenner onServerStateChangeListenner){
        this.onServerStateChangeListenner = onServerStateChangeListenner;
    }
    private void startEditConfig(File file){
        Intent intent = new Intent(getContext(), ConfigEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConfigEditActivity.INTENT_BUNDLE_TYPE_FILE_KEY,file);
        intent.putExtras(bundle);
        getContext().startActivity(intent);
    }

}
