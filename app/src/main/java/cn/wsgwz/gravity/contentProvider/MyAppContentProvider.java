package cn.wsgwz.gravity.contentProvider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.URL;

import cn.wsgwz.gravity.CustomApplication;
import cn.wsgwz.gravity.adapter.MyFragmentPagerAdapter;
import cn.wsgwz.gravity.helper.SettingHelper;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2017/1/9.
 */

public final class MyAppContentProvider extends ContentProvider {;
    private static final int CODE_SETTING_CONFIG_PATH = 1;
    public static final String PATH_SETTING_CONFIG_PATH = "path_config_path";
    private static final int CODE_SETTING_IS_START = 2;
    public static final String PATH_SETTING_IS_START = "path_is_start";

    private static final int CODE_SETTING_SUSPENSION_COLOR = 4;
    public static final String PATH_SETTING_SUSPENSION_COLOR= "path_suspension_color";

    private static final int CODE_SETTING_SEED_X_LOCATION = 6;
    public static final String PATH_SETTING_SEED_X_LOCATION= "path_x_location";

    private static final int CODE_SETTING_SEED_Y_LOCATION = 7;
    public static final String PATH_SETTING_SEED_Y_LOCATION= "path_y_location";


    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        uriMatcher.addURI(CustomApplication.PACKAGE_NAME,PATH_SETTING_CONFIG_PATH,CODE_SETTING_CONFIG_PATH);
        uriMatcher.addURI(CustomApplication.PACKAGE_NAME,PATH_SETTING_IS_START,CODE_SETTING_IS_START);
        uriMatcher.addURI(CustomApplication.PACKAGE_NAME,PATH_SETTING_SUSPENSION_COLOR,CODE_SETTING_SUSPENSION_COLOR);
        uriMatcher.addURI(CustomApplication.PACKAGE_NAME,PATH_SETTING_SEED_X_LOCATION,CODE_SETTING_SEED_X_LOCATION);
        uriMatcher.addURI(CustomApplication.PACKAGE_NAME,PATH_SETTING_SEED_Y_LOCATION,CODE_SETTING_SEED_Y_LOCATION);
        //LogUtil.printSS("MyAppContentProvider static 块" );
    }
    private ContentResolver contentResolver;
    private DbHelper dbHelper;
    //构造器不能私有
    public MyAppContentProvider(){
        //LogUtil.printSS("  MyAppContentProvider()");
    }
    @Override
    public boolean onCreate() {

        //LogUtil.printSS("MyAppContentProvider  onCreate()");
        Context context = getContext();
        dbHelper = new DbHelper(context,null,null,0);
        //LogUtil.printSS(" 2");
        contentResolver = context.getContentResolver();
        //LogUtil.printSS(" onCreate ->"+dbHelper);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        Cursor cursor = null;
        switch (getType(uri)){

            case PATH_SETTING_CONFIG_PATH:
            case PATH_SETTING_IS_START:
            case PATH_SETTING_SUSPENSION_COLOR:
            case PATH_SETTING_SEED_X_LOCATION:
            case PATH_SETTING_SEED_Y_LOCATION:
                SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
            /*table:表名，不能为null
            columns:要查询的列名，可以是多个，可以为null，表示查询所有列
            selection:查询条件，比如id=? and name=? 可以为null
            selectionArgs:对查询条件赋值，一个问号对应一个值，按顺序 可以为null
            having:语法have，可以为null
            orderBy：语法，按xx排序，可以为null*/
                cursor = sqLiteDatabase.query(DbHelper.TABLE_NAME,strings,s,strings1,null,null,s1);
                cursor.setNotificationUri(contentResolver,uri);
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case CODE_SETTING_CONFIG_PATH:
                return PATH_SETTING_CONFIG_PATH;
            case CODE_SETTING_IS_START:
                return PATH_SETTING_IS_START;
            case CODE_SETTING_SUSPENSION_COLOR:
                return PATH_SETTING_SUSPENSION_COLOR;
            case CODE_SETTING_SEED_X_LOCATION:
                return PATH_SETTING_SEED_X_LOCATION;
            case CODE_SETTING_SEED_Y_LOCATION:
                return PATH_SETTING_SEED_Y_LOCATION;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri uri1 = null;
        switch (getType(uri)){

            case PATH_SETTING_CONFIG_PATH:
            case PATH_SETTING_IS_START:
            case PATH_SETTING_SUSPENSION_COLOR:
            case PATH_SETTING_SEED_X_LOCATION:
            case PATH_SETTING_SEED_Y_LOCATION:
                SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
                long l = sqLiteDatabase.insert(DbHelper.TABLE_NAME,DbHelper._ID,contentValues);
                uri1 = ContentUris.withAppendedId(uri,l);
                contentResolver.notifyChange(uri1,null);
                break;
        }
        return uri1;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int id =0;
        switch (getType(uri)){
            case PATH_SETTING_CONFIG_PATH:
            case PATH_SETTING_IS_START:
            case PATH_SETTING_SUSPENSION_COLOR:
            case PATH_SETTING_SEED_X_LOCATION:
            case PATH_SETTING_SEED_Y_LOCATION:
                SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
                id= sqLiteDatabase.delete(DbHelper.TABLE_NAME, s, strings);
                contentResolver.notifyChange(uri,null);
                break;
        }
        return id;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int id =0;
        switch (getType(uri)){

            case PATH_SETTING_CONFIG_PATH:
            case PATH_SETTING_IS_START:
            case PATH_SETTING_SUSPENSION_COLOR:
            case PATH_SETTING_SEED_X_LOCATION:
            case PATH_SETTING_SEED_Y_LOCATION:
                SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
                id= sqLiteDatabase.update(DbHelper.TABLE_NAME, contentValues, s,strings);
                contentResolver.notifyChange(uri,null);
                break;
        }
        return id;
    }
    public int getVersionCode(Context context) {
             try {
                     PackageManager manager = context.getPackageManager();
                     PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                     return info.versionCode;
                } catch (Exception e) {
                    e.printStackTrace();
                     return -1;
                }
         }
    public final   class DbHelper extends SQLiteOpenHelper{
        //public static final int VERSION = 1;
        public static final String DB_NAME = "appContentProvider.db";
        public static final String TABLE_NAME = "table_name";
        public static final String _ID = "_id";
        public static final String _CONFIG_PATH = "_config_path";
        public static final String _IS_START = "_is_start";
        public static final String _SUSPENSION_COLOR = "_suspension_color";
        public static final String _SPEED_VIEW_X_LOCATION = "_speed_view_x_location";
        public static final String _SPEED_VIEW_Y_LOCATION = "_speed_view_y_location";
        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, DB_NAME, factory,getVersionCode(context) );
        }

       /* public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, DB_NAME, factory, VERSION, errorHandler);
        }*/

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String sql = "create table "+TABLE_NAME+"("+_ID+" integer primary key autoincrement,"+ _CONFIG_PATH+" varchar(16),"+_IS_START+" integer,"+_SUSPENSION_COLOR+" varchar(16),"
                    +_SPEED_VIEW_X_LOCATION+" real,"+_SPEED_VIEW_Y_LOCATION+" real"+");";
            String sql2 = "insert into "+TABLE_NAME+"("+_CONFIG_PATH+","+_IS_START+","+_SPEED_VIEW_X_LOCATION+","+_SPEED_VIEW_Y_LOCATION+") "+"values("+"'未选择',1,100,314"+")";
            sqLiteDatabase.execSQL(sql);
            sqLiteDatabase.execSQL(sql2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("drop table if exists "+TABLE_NAME);
            //LogUtil.printSS("onUpgrade ->"+"drop table if exists "+TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
