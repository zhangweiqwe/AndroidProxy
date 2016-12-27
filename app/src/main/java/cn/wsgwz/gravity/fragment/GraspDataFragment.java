package cn.wsgwz.gravity.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.gesture.GestureUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pull.refreshview.XListView;
import com.example.pull.refreshview.XScrollView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import cn.wsgwz.gravity.MainActivity;
import cn.wsgwz.gravity.R;
import cn.wsgwz.gravity.activity.GraspDataFilePreviewActivity;
import cn.wsgwz.gravity.adapter.GraspDataAdapter;
import cn.wsgwz.gravity.util.FileUtil;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.SharedPreferenceMy;
import cn.wsgwz.gravity.util.ShellUtil;

public class GraspDataFragment extends Fragment implements View.OnClickListener ,AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{
    private static boolean isCaptureDoing;
    private Button start_graspData;
    private ListView list_view;

    private GraspDataAdapter adapter;
    private List<File> list;

      /*  String str = "cd /system/xbin/gravity\n"+
            "./tcpdump -i any -p -s 0 -w "+ FileUtil.SD_APTH+"/"+fileName;*/
 /* String str = "busybox pkill -SIGINT tcpdump";*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grasp_data,container,false);
        initView(view);
        //LogUtil.printSS("  GraspDataFragment ");
        return view;
    }
    private void stopCapture(){
        start_graspData.setText("开始抓包");
        String str = "busybox pkill -SIGINT tcpdump";
        ShellUtil.execShell(getActivity(), str, new OnExecResultListenner() {
            @Override
            public void onSuccess(StringBuffer sb) {
                isCaptureDoing = false;
                Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.already_stop_capture), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(StringBuffer sb) {
                isCaptureDoing = false;
            }
        });
    }
    private void startCapture(){
        start_graspData.setText("停止抓包");
        if(!isCaptureDoing) {
            isCaptureDoing = true;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = simpleDateFormat.format(new Date())+".pcap";
        File directoryRoot;
            directoryRoot = new File(FileUtil.SD_APTH_PCAP);
        if(!directoryRoot.exists()){
            directoryRoot.mkdirs();
        }
       /* File file = new File(directoryRoot,fileName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        String str = null;

        String path;
            path=FileUtil.SD_APTH_PCAP;
        str= "mount -o remount,rw /system"+"\n"+
                "cd /system/xbin/"+getResources().getString(R.string.app_name)+"\n"+
                "./tcpdump -i any -p -s 0 -w "+path+"/"+fileName+"\n"+
        "chmod 777 "+path+"/"+fileName;
        ShellUtil.execShell(getActivity(), str, new OnExecResultListenner() {
            @Override
            public void onSuccess(StringBuffer sb) {
                list.clear();
                list.addAll(getFiles());
                adapter.notifyDataSetChanged();
                setListViewHeight(list_view);
            }

            @Override
            public void onError(StringBuffer sb) {
            }
        });
    }


    @Override
    public void onClick(View v) {
        //LogUtil.printSS("       -----isInitSdcard"+isInitSdcard+"            isInitSystem"+isInitSystem);
        switch (v.getId()){
            case R.id.start_graspData:

                        if(!isCaptureDoing){
                            Intent intentMain = new Intent(Intent.ACTION_MAIN);
                            intentMain.addCategory(Intent.CATEGORY_HOME);
                            getActivity().startActivity(intentMain);
                            Toast.makeText(getActivity(),getString(R.string.doing_capture),Toast.LENGTH_LONG).show();
                            startCapture();
                        }else {
                            stopCapture();
                        }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intentPreView = new Intent(getActivity(),GraspDataFilePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GraspDataFilePreviewActivity.BUNDLE_KRY_FILE,list.get(position));
        intentPreView.putExtras(bundle);
        startActivity(intentPreView);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        final File file = list.get(position);
        if(file==null||!file.exists()){
            return true;
        }
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage("删除它");
        builder.setMessage("文件路径："+file.getAbsolutePath());
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(file.exists()){
                    list.remove(position);
                    file.delete();
                    adapter.notifyDataSetChanged();
                    setListViewHeight(list_view);
                }

            }
        });
        builder.setNeutralButton("全部删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int i=0;i<list.size();i++){
                    if(list.get(i).exists()){
                        list.get(i).delete();
                    }
                }
                list.clear();
                adapter.notifyDataSetChanged();
                setListViewHeight(list_view);
            }
        });
        Dialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.payDialogStyleAnimation);
        dialog.show();
        return false;
    }

    private void initView(View view){
        start_graspData = (Button) view.findViewById(R.id.start_graspData);
       // stop_graspData = (Button) view.findViewById(R.id.stop_graspData);
        start_graspData.setOnClickListener(this);
        if(isCaptureDoing){start_graspData.setText("停止抓包");}
       // stop_graspData.setOnClickListener(this);

        list_view  = (ListView) view.findViewById(R.id.list_view);
        list = new ArrayList<>();
        list.addAll(getFiles());
        adapter = new GraspDataAdapter(list,getActivity());
        list_view.setAdapter(adapter);
        setListViewHeight(list_view);
        list_view.setOnItemClickListener(this);
        list_view.setOnItemLongClickListener(this);
    }



    private  List<File> getFiles(){
        List<File> tempList = new ArrayList<>();
        File directoryTemp;
            directoryTemp = new File(FileUtil.SD_APTH_PCAP);

        if(!directoryTemp.exists()){
            return tempList;
        }else {
            File[] files = directoryTemp.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if(name.endsWith(".pcap")){
                        return true;
                    }
                    return false;
                }
            });
            if(files!=null){
                Collections.addAll(tempList,files);
            }
        }
        return tempList;
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
  /*  String str = "cd /system/xbin/gravity\n"+
            "./tcpdump -i any -p -s 0 -w "+ FileUtil.SD_APTH+"/"+fileName;*/
 /* String str = "busybox pkill -SIGINT tcpdump";*/
}
