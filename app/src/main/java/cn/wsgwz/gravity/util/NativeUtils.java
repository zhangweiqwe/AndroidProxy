package cn.wsgwz.gravity.util;

import java.util.Map;

/**
 * Created by Administrator on 2016/10/23.
 */

public class NativeUtils {
    static {
        System.loadLibrary("native-lib");
    }
}
