package cn.wsgwz.gravity.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import cn.wsgwz.gravity.CustomApplication;
import cn.wsgwz.gravity.contentProvider.MyAppContentProvider;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2017/1/9.
 */

public class SettingHelper {
    private static final Uri settingUriConfigPath = Uri.parse("content://"+ CustomApplication.PACKAGE_NAME+"/"+MyAppContentProvider.PATH_SETTING_CONFIG_PATH);
    private static final Uri settingUriIsStart = Uri.parse("content://"+ CustomApplication.PACKAGE_NAME+"/"+MyAppContentProvider.PATH_SETTING_IS_START);
    private SettingHelper(){}
    private static final SettingHelper settingHelper = new SettingHelper();
    public static final SettingHelper getInstance(){
        return settingHelper;
    }
    public void setConfigPath( Context context,String configPath){
        ContentResolver contentResolver = context.getContentResolver();
        /*if(getConfigPath(context)!=null){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MyAppContentProvider.DbHelper._CONFIG_PATH,configPath);
            contentResolver.update(settingUriConfigPath,contentValues,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
        }else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MyAppContentProvider.DbHelper._CONFIG_PATH,configPath);
            contentResolver.insert(settingUriConfigPath,contentValues);
        }*/
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyAppContentProvider.DbHelper._CONFIG_PATH,configPath);
        contentResolver.update(settingUriConfigPath,contentValues,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }
    public String getConfigPath(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        String configPath = null;
        Cursor cursor = contentResolver.query(settingUriConfigPath,null,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"}, null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            configPath  = cursor.getString(cursor.getColumnIndex(MyAppContentProvider.DbHelper._CONFIG_PATH));
        }
        return configPath;
    }


    public void setIsStart( Context context,boolean isStart){
        int i = isStart?0:1;
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyAppContentProvider.DbHelper._IS_START,i);
        contentResolver.update(settingUriIsStart,contentValues,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
       /* if(isStart(context)!=true){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MyAppContentProvider.DbHelper._IS_START,i);
            contentResolver.update(settingUriIsStart,contentValues,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
        }else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MyAppContentProvider.DbHelper._IS_START,i);
            contentResolver.insert(settingUriIsStart,contentValues);
        }*/
    }
    public boolean isStart(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        int i = 0;
        Cursor cursor = contentResolver.query(settingUriIsStart,null,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"},
                null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            i  = cursor.getInt(cursor.getColumnIndex(MyAppContentProvider.DbHelper._IS_START));
        }
        return i==0?true:false;
    }
}
