package cn.wsgwz.gravity.util;

import android.util.Log;

/**
 * Created by Administrator on 2016/10/22.
 */

public class LogUtil {
    private static final boolean NEED_PRINT = true;
    private static final String LOG_TAG = "sssssssssssss    ";
    public static synchronized void printSS(String str){
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
