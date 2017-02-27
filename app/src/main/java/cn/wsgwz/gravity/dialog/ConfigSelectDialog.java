package cn.wsgwz.gravity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.pull.refreshview.XListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.activity.ConfigEditActivity;
import cn.wsgwz.gravity.adapter.ConfigSelectAdapter;
import cn.wsgwz.gravity.adapter.MyFragmentPagerAdapter;
import cn.wsgwz.gravity.config.EnumAssetsConfig;
import cn.wsgwz.gravity.fragment.MainFragment;
import cn.wsgwz.gravity.helper.SettingHelper;
import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.SharedPreferenceMy;

/**
 * Created by Jeremy Wang on 2016/11/7.
 */

public class ConfigSelectDialog extends Dialog implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{
    private SettingHelper settingHelper = SettingHelper.getInstance();
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
    private List<Object> list;

    private Intent intentServer;
    private SharedPreferences sharedPreferences;
    private   File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(R.style.payDialogStyle);
        setContentView(R.layout.dialog_config_select);
        initView();
    }

    private void initView(){
        intentServer = new Intent(getContext(), ProxyService.class);
        sharedPreferences  = getContext().getSharedPreferences(SharedPreferenceMy.CONFIG,Context.MODE_PRIVATE);
        hint_TV = (TextView)findViewById(R.id.hint_TV);
        currentConfig_TV = (TextView) findViewById(R.id.currentConfig_TV);
        currentConfig_TV.setText(settingHelper.getConfigPath(context));
        //currentConfig_TV.setText(sharedPreferences.getString(SharedPreferenceMy.CURRENT_CONFIG_PATH,getContext().getString(R.string.not_select_config)));
        list_view = (XListView)findViewById(R.id.list_view);
        list_view.setPullLoadEnable(false);
        list_view.setPullRefreshEnable(false);
        list =  new ArrayList<>();
        configSelectAdapter = new ConfigSelectAdapter(getContext(),list);
        list_view.setOnItemClickListener(this);
        list_view.setOnItemLongClickListener(this);
        list_view.setAdapter(configSelectAdapter);
        initListView();

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        LogUtil.printSS("---"+position);
        if(position==0){
            return false;
        }
        position = position - 1;
        Object obj = list.get(position);

        if(obj instanceof File){
            file = (File) obj;
        }else {
            return false;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("分享");
        builder.setMessage("是否分享？");
        builder.setPositiveButton("分享", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(file==null||!file.exists()){
                    return ;
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra("subject", file.getName()); //
                intent.putExtra("body", "来自gravity的配置文件分享"); //正文
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("text/plain");

                try {
                    context.startActivity(intent);
                }catch (ActivityNotFoundException e){

                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();


        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view,  int position, long id) {
        if(position==0){
            return;
        }
      final int   position1 = position - 1;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("重启生效");
        builder.setMessage("是否重启？");
        Object obj = list.get(position1);

        if(obj instanceof File){
            file = (File) obj;
        }else if(obj instanceof EnumAssetsConfig){
            final EnumAssetsConfig enumAssetsConfig = (EnumAssetsConfig) obj;
            file = new File(enumAssetsConfig.getKey());
            builder.setNeutralButton("复制到列表", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    File tempFile = new File(FileUtil.APP_APTH_CONFIG+"/复制-"+enumAssetsConfig.getValues()+FileUtil.CONFIG_END_NAME);
                    if(tempFile.exists()){
                        return;
                    }
                    InputStream in = null;
                    try {
                        in = context.getAssets().open(enumAssetsConfig.getKey());
                        int len = 0;
                        byte[] buffer = new byte[1024];
                        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                        while ((len=in.read(buffer))!=-1){
                            fileOutputStream.write(buffer,0,len);
                            fileOutputStream.flush();
                        }
                        in.close();
                        fileOutputStream.close();
                        list.add(0,tempFile);
                        configSelectAdapter.notifyDataSetChanged();
                        Snackbar.make(view,getContext().getString(R.string.copy_file_foredit_succeed),Snackbar.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        if(file.exists()){
            builder.setNegativeButton("编辑", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startEditConfig(file);
                }
            });
            builder.setNeutralButton("删除", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(file.exists()){
                        list.remove(position1);
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
                //sharedPreferences.edit().putString(SharedPreferenceMy.CURRENT_CONFIG_PATH,file.getAbsolutePath()).commit();
                settingHelper.setConfigPath(context,file.getAbsolutePath());
                currentConfig_TV.setText(file.getAbsolutePath());
               /* ConfigSelectDialog.this.getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ConfigSelectDialog.this.dismiss();
                    }
                },200);*/
                if(context!=null){
                    if(context instanceof MainActivity){
                        MainActivity mainActivity = (MainActivity)context;
                        ViewPager viewPager =  mainActivity.getMy_viewPager();
                       MyFragmentPagerAdapter myFragmentPagerAdapter = mainActivity.getFragmentPagerAdapter();
                       Fragment fragment =  myFragmentPagerAdapter.getItem(viewPager.getCurrentItem());
                        if(fragment instanceof MainFragment){
                            if(onServerStateChangeListenner!=null){
                                onServerStateChangeListenner.onChange(ConfigSelectDialog.this);
                            }
                        }

                    }
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
                        configSelectAdapter.notifyDataSetChanged();
                        //configSelectAdapter.notifyDataSetChanged();
                        //setListViewHeight(list_view);
                        break;
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Looper.prepare();


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
                    if(files!=null){
                        Collections.addAll(list,files);
                        handler.sendEmptyMessage(1000);
                    }
                }

                File file1 = new File(FileUtil.APP_APTH_CONFIG);
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
                    if(files!=null){
                        Collections.addAll(list,files);
                        handler.sendEmptyMessage(1000);
                    }
                }


               /* List<EnumMyConfig> listEnum = EnumMyConfig.getMeConfig();
                if(listEnum!=null){
                    for(int i=0;i<listEnum.size();i++){
                        Collections.addAll(list,new File(listEnum.get(i).getName()));
                    }
                }*/


                    Class clz = EnumAssetsConfig.class;
                    for (Object obj: clz.getEnumConstants()) {
                        list.add((EnumAssetsConfig)obj);
                        handler.sendEmptyMessage(1000);
                    }
                  /*  list.add(EnumAssetsConfig.ChongQing_YiDong_1);
                    list.add(EnumAssetsConfig.ChongQing_YiDong_1_S);
                    list.add(EnumAssetsConfig.ChongQing_YiDong_2);
                    list.add(EnumAssetsConfig.ChongQing_LianTong_1);
                    list.add(EnumAssetsConfig.SiChuan_YiDong_1);*/

                handler.sendEmptyMessage(1000);
                //Looper.loop();

            }
        }).start();
    }


    //当服务状态发生改变
    public interface OnServerStateChangeListenner{
        void onChange(ConfigSelectDialog configSelectDialog);
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
