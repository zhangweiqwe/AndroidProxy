package cn.wsgwz.gravity.fragment.log;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2016/12/2.
 */

public class LogContent {

    public static final List<String>  ITEMS = new ArrayList<>();
    /*static {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:MM:ss:SSS");
        ITEMS.add(simpleDateFormat.format(new Date())+"  ..."+"");
    }*/
    public static RecyclerView.Adapter adapter;
    public  static void addItem(String str){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:MM:ss:SSS");
        ITEMS.add(simpleDateFormat.format(new Date())+"  "+str);
    };
    public  static void addItemOnlyStr(String str){
        ITEMS.add(str);
    };
    public  static void addItemOnlyStrAndNotify(String str){
        ITEMS.add(str);
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }else {
            //LogUtil.printSS("adapter  == "+adapter);
        }
    }
    public static void initAdapter(RecyclerView.Adapter adapter){
        LogContent.adapter = adapter;
    }
    public  static void addItemAndNotify(String str){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:MM:ss:SSS");
        ITEMS.add(simpleDateFormat.format(new Date())+"  "+str);
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }else {
           //LogUtil.printSS("adapter  == "+adapter);
        }
    }

    //清除
    public static void clear(Context context){
        if(context==null){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("是否删除");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ITEMS.clear();
                if(adapter!=null){
                    adapter.notifyDataSetChanged();
                }else {
                    //LogUtil.printSS("adapter  == "+adapter);
                }
            }
        });
        builder.setNegativeButton("否",null);
        builder.show();

    }
    //分享
    public static void share(Context context){
        if(context==null){
            return;
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<ITEMS.size();i++){
            sb.append(ITEMS.get(i)+"\n");
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        context.startActivity(intent);
    }
    //清除
    public static void clearNotHint(){
                ITEMS.clear();
                if(adapter!=null){
                    adapter.notifyDataSetChanged();
                }else {
                    //LogUtil.printSS("adapter  == "+adapter);
                }
    }

}
