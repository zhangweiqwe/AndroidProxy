package cn.wsgwz.gravity.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import cn.wsgwz.gravity.util.OnExecResultListenner;
import cn.wsgwz.gravity.util.ShellUtil;

/**
 * Created by Jeremy Wang on 2016/11/21.
 */

public class ApnDbHelper extends SQLiteOpenHelper {
    //private static final String DB_NAME = Environment.getExternalStorageDirectory()+"/"+"gravity/"+"telephony.db";
    private static final String DB_PATH = "/data/data/com.android.providers.telephony/databases/telephony.db";
    public static final String DB_PATH_REALSE = "/data/data/com.android.providers.telephony/databases";
    public static final String DB_NAME_REALSE = "telephony.db";
    private static final String DB_SHARED_PREFS_PATH = "/data/data/com.android.providers.telephony/shared_prefs";
    private static final String DB_SHARED_PREFS_PATH_1 = "/data/data/com.android.providers.telephony/shared_prefs/preferred-apn1.xml";
   // private static final String DB_NAME = "file:///android_asset/"+"telephony.db";
    private static final String DB_TABLE_CARRIERS = "carriers";
    private static final int DB_VERSION = 1;
    private static final String KEY__ID= "_id";

    private SQLiteDatabase sqLiteDatabase ;
    public ApnDbHelper(Context context) throws Exception{
        super(context, DB_PATH, null, DB_VERSION);
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(DB_PATH,null);
    }
    public ApnDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ApnDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Map<String,Object>  query(String apn_id){

        String query = "select * from "+DB_TABLE_CARRIERS+" where " +KEY__ID+" = "+"?;";
       // sqLiteDatabase.beginTransaction();
        Cursor cursor = sqLiteDatabase.rawQuery(query,new String[]{apn_id+""});
        boolean b = cursor.moveToFirst();
        if(!b){
            return null;
        }
            Map<String,Object> map = new HashMap<>();
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String numeric = cursor.getString(cursor.getColumnIndex("numeric"));
            String mcc = cursor.getString(cursor.getColumnIndex("mcc"));
            String mnc = cursor.getString(cursor.getColumnIndex("mnc"));
            String apn = cursor.getString(cursor.getColumnIndex("apn"));
            String user = cursor.getString(cursor.getColumnIndex("user"));
            String server = cursor.getString(cursor.getColumnIndex("server"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            String proxy = cursor.getString(cursor.getColumnIndex("proxy"));
            String port = cursor.getString(cursor.getColumnIndex("port"));
            String mmsproxy = cursor.getString(cursor.getColumnIndex("mmsproxy"));
            String mmsport = cursor.getString(cursor.getColumnIndex("mmsport"));
            String mmsc = cursor.getString(cursor.getColumnIndex("mmsc"));
            int authtype = cursor.getInt(cursor.getColumnIndex("authtype"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            int current = cursor.getInt(cursor.getColumnIndex("current"));
            String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
            String roaming_protocol = cursor.getString(cursor.getColumnIndex("roaming_protocol"));
            int carrier_enabled = cursor.getInt(cursor.getColumnIndex("carrier_enabled"));  //0 true
            int bearer = cursor.getInt(cursor.getColumnIndex("bearer"));
            String mvno_type = cursor.getString(cursor.getColumnIndex("mvno_type"));
            String mvno_match_data = cursor.getString(cursor.getColumnIndex("mvno_match_data"));
            int sub_id = cursor.getInt(cursor.getColumnIndex("sub_id"));
            int profile_id = cursor.getInt(cursor.getColumnIndex("profile_id"));
            int modem_cognitive = cursor.getInt(cursor.getColumnIndex("modem_cognitive")); //0 true
            int max_conns = cursor.getInt(cursor.getColumnIndex("max_conns"));
            int wait_time = cursor.getInt(cursor.getColumnIndex("wait_time"));
            int max_conns_time = cursor.getInt(cursor.getColumnIndex("max_conns_time"));
            int mtu = cursor.getInt(cursor.getColumnIndex("mtu"));

            map.put("_id",_id);
            map.put("apn",apn);
            map.put("proxy",proxy);
            map.put("port",port);

       // sqLiteDatabase.endTransaction();
        cursor.close();;
        sqLiteDatabase.close();

       // LogUtil.printSS(map.toString());
        return map;
    }

    public void update(Context context, final String apn_id, final String apn, final String proxy, final String port){

        ContentValues values = new ContentValues();
        values.put("apn",apn);
        values.put("proxy",proxy);
        values.put("port",port);
        //sqLiteDatabase.beginTransaction();
        sqLiteDatabase.update(DB_TABLE_CARRIERS,values,"_id=?",new String[]{apn_id});
        //sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();


    }
/*
    //获取当前的 apn_id
    public void getCurrentApnID(Context context, final OnIDChnageListenner onIDChnageListenner) {
        String shellStr = "mount -o remount rw "+DB_SHARED_PREFS_PATH+"\n chmod 0666 "+DB_SHARED_PREFS_PATH_1;
        ShellUtil.execShell(context, shellStr, new OnExecResultListenner() {
            @Override
            public void onSuccess(StringBuffer sb)  {
                if(onIDChnageListenner!=null){
                    FileInputStream fileInputStream  = null;
                    try {
                        fileInputStream = new FileInputStream(DB_SHARED_PREFS_PATH_1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    SAXReader saxReader = new SAXReader();
                    Document document = null;
                    try {
                        document = saxReader.read(fileInputStream);
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    Element elementRoot = document.getRootElement();
                    Element elementLong = elementRoot.element("long");
                    Attribute value = elementLong.attribute("value");
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onIDChnageListenner.succeed(value.getValue());
                }
            }

            @Override
            public void onError(StringBuffer sb) {
                if(onIDChnageListenner!=null){
                    onIDChnageListenner.error();
                }
            }
        });

    }*/

    //当查找到id
    public interface OnIDChnageListenner{
        void succeed(String value) ;
        void error();
    }
    private OnIDChnageListenner onIDChnageListenner;
    public void setOnIDChnageListenner(OnIDChnageListenner onIDChnageListenner){
        this.onIDChnageListenner = onIDChnageListenner;
    }

}
