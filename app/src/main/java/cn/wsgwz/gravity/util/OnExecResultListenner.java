package cn.wsgwz.gravity.util;

/**
 * Created by Jeremy Wang on 2016/10/26.
 */

public interface OnExecResultListenner{
    void onSuccess(StringBuffer sb);
    void onError(StringBuffer sb);
}

