package cn.wsgwz.gravity.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import cn.wsgwz.gravity.CustomApplication;
import cn.wsgwz.gravity.contentProvider.MyAppContentProvider;
import cn.wsgwz.gravity.util.FloatBytesConvertUtil;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2017/1/9.
 */

public class SettingHelper {
    private static final Uri settingUriConfigPath = Uri.parse("content://"+ CustomApplication.PACKAGE_NAME+"/"+MyAppContentProvider.PATH_SETTING_CONFIG_PATH);
    private static final Uri settingUriIsStart = Uri.parse("content://"+ CustomApplication.PACKAGE_NAME+"/"+MyAppContentProvider.PATH_SETTING_IS_START);
    private static final Uri settingUriSuspensioColor = Uri.parse("content://"+ CustomApplication.PACKAGE_NAME+"/"+MyAppContentProvider.PATH_SETTING_SUSPENSION_COLOR);
    private static final Uri settingUriSpeedSuspensionX = Uri.parse("content://"+ CustomApplication.PACKAGE_NAME+"/"+MyAppContentProvider.PATH_SETTING_SEED_X_LOCATION);
    private static final Uri settingUriSpeedSuspensionY = Uri.parse("content://"+ CustomApplication.PACKAGE_NAME+"/"+MyAppContentProvider.PATH_SETTING_SEED_Y_LOCATION);
    private static final Uri settingUriIsCapture = Uri.parse("content://"+ CustomApplication.PACKAGE_NAME+"/"+MyAppContentProvider.PATH_SETTING_IS_CAPTURE);
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

    public void setIsCapture( Context context,boolean isCapture){
        int i = isCapture?0:1;
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyAppContentProvider.DbHelper._IS_CAPTURE,i);
        contentResolver.update(settingUriIsCapture,contentValues,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }
    public boolean isCaptrue(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        int i = 0;
        Cursor cursor = contentResolver.query(settingUriIsCapture,null,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"},
                null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            i  = cursor.getInt(cursor.getColumnIndex(MyAppContentProvider.DbHelper._IS_CAPTURE));
        }
        return i==0?true:false;
    }



    public void setSuspensionColor( Context context,String color){
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyAppContentProvider.DbHelper._SUSPENSION_COLOR,color);
        contentResolver.update(settingUriSuspensioColor,contentValues,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }
    public String getSuspensionColor(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        String color = null;
        Cursor cursor = contentResolver.query(settingUriSuspensioColor,null,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"}, null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            color  = cursor.getString(cursor.getColumnIndex(MyAppContentProvider.DbHelper._SUSPENSION_COLOR));
        }
        return color;
    }

    public void setSpeedSuspensionX( Context context,float f){
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyAppContentProvider.DbHelper._SPEED_VIEW_X_LOCATION,f);
        contentResolver.update(settingUriSpeedSuspensionX,contentValues,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }
    public float getSpeedSuspensionX(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        float x = 0;
        Cursor cursor = contentResolver.query(settingUriSpeedSuspensionX,null,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"}, null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            x  = cursor.getFloat(cursor.getColumnIndex(MyAppContentProvider.DbHelper._SPEED_VIEW_X_LOCATION));
        }
        return x;
    }

    public void setSpeedSuspensionY( Context context,float f){
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyAppContentProvider.DbHelper._SPEED_VIEW_Y_LOCATION,f);
        contentResolver.update(settingUriSpeedSuspensionY,contentValues,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }
    public float getSpeedSuspensionY(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        float x = 0;
        Cursor cursor = contentResolver.query(settingUriSpeedSuspensionY,null,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"}, null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            x  = cursor.getFloat(cursor.getColumnIndex(MyAppContentProvider.DbHelper._SPEED_VIEW_Y_LOCATION));
        }
        return x;
    }

}
