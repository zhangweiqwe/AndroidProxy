package cn.wsgwz.gravity.util;

import android.util.Log;

/**
 * Created by Administrator on 2016/10/22.
 */

public class LogUtil {
    public static final boolean isMe = false;
    private static final boolean NEED_PRINT = true;
    public static final boolean IS_STREAM_DEBUG = false;
    private static final String LOG_TAG = "sssssssssssss    ";
    public static void printSS(String str){
        if(NEED_PRINT){
            Log.d(LOG_TAG,str);
        }
    }
    public synchronized static final void printSSs(String str){
        if(NEED_PRINT){
            Log.d(LOG_TAG,str);
        }
    }
}
