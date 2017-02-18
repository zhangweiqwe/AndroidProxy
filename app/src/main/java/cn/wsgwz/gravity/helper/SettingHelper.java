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
    private static final Uri settingUriMainActivityStyle = Uri.parse("content://"+ CustomApplication.PACKAGE_NAME+"/"+MyAppContentProvider.PATH_MAINACTIVITY_STYLE);
    private SettingHelper(){}
    private static final SettingHelper settingHelper = new SettingHelper();
    public static final SettingHelper getInstance(){
        return settingHelper;
    }


    private void setBoolean(Context context,boolean b,String key,Uri uri){
        int i = b?0:1;
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(key,i);
        contentResolver.update(uri,contentValues,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }
    private boolean getBoolean(Context context,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        int i = 0;
        Cursor cursor = contentResolver.query(uri,null,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"},
                null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            i  = cursor.getInt(cursor.getColumnIndex(key));
        }
        return i==0?true:false;
    }

    private void setString(Context context,String s,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(key,s);
        contentResolver.update(uri,contentValues,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }
    private String getString(Context context,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        String configPath = null;
        Cursor cursor = contentResolver.query(uri,null,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"}, null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            configPath  = cursor.getString(cursor.getColumnIndex(key));
        }
        return configPath;
    }
    private void setFloat(Context context,float f,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(key,f);
        contentResolver.update(uri,contentValues,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }

    private float getFloat(Context context,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        float x = 0;
        Cursor cursor = contentResolver.query(uri,null,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"}, null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            x  = cursor.getFloat(cursor.getColumnIndex(key));
        }
        return x;
    }



    private void setInt(Context context,int i,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(key,i);
        contentResolver.update(uri,contentValues,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }
    private int getInt(Context context,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        int i = 0;
        Cursor cursor = contentResolver.query(uri,null,MyAppContentProvider.DbHelper._ID+"=?",new String[]{"1"},
                null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            i  = cursor.getInt(cursor.getColumnIndex(key));
        }
        return i;
    }


    public void setConfigPath( Context context,String configPath){
        setString(context,configPath,MyAppContentProvider.DbHelper._CONFIG_PATH,settingUriConfigPath);
    }
    public String getConfigPath(Context context){
        return getString(context,MyAppContentProvider.DbHelper._CONFIG_PATH,settingUriConfigPath);
    }


    public void setIsStart( Context context,boolean isStart){
        setBoolean( context, isStart,MyAppContentProvider.DbHelper._IS_START,settingUriIsStart);
    }

    public boolean isStart(Context context){
        return getBoolean(context,MyAppContentProvider.DbHelper._IS_START,settingUriIsStart);
    }

    public void setIsCapture( Context context,boolean isCapture){
        setBoolean( context, isCapture,MyAppContentProvider.DbHelper._IS_CAPTURE,settingUriIsCapture);
    }
    public boolean isCaptrue(Context context){
        return getBoolean(context,MyAppContentProvider.DbHelper._IS_CAPTURE,settingUriIsCapture);
    }


    public void setSuspensionColor( Context context,String color){
        setString(context,color,MyAppContentProvider.DbHelper._SUSPENSION_COLOR,settingUriSuspensioColor);
    }
    public String getSuspensionColor(Context context){
        return getString(context,MyAppContentProvider.DbHelper._SUSPENSION_COLOR,settingUriSuspensioColor);
    }



    public void setSpeedSuspensionX( Context context,float f){
        setFloat(context,f,MyAppContentProvider.DbHelper._SPEED_VIEW_X_LOCATION,settingUriSpeedSuspensionX);
    }
    public float getSpeedSuspensionX(Context context){
        return getFloat(context,MyAppContentProvider.DbHelper._SPEED_VIEW_X_LOCATION,settingUriSpeedSuspensionX);
    }

    public void setSpeedSuspensionY( Context context,float f){
        setFloat(context,f,MyAppContentProvider.DbHelper._SPEED_VIEW_Y_LOCATION,settingUriSpeedSuspensionY);
    }
    public float getSpeedSuspensionY(Context context){
        return getFloat(context,MyAppContentProvider.DbHelper._SPEED_VIEW_Y_LOCATION,settingUriSpeedSuspensionY);
    }
    public void setMainActivityStyle(Context context,int i){
        setInt(context,i,MyAppContentProvider.DbHelper._STYLE_MAINACTIVITY,settingUriMainActivityStyle);
    }
    public int getMainActivityStyle(Context context){
        return  getInt(context,MyAppContentProvider.DbHelper._STYLE_MAINACTIVITY,settingUriMainActivityStyle);
    }


}
