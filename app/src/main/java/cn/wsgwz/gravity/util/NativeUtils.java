package cn.wsgwz.gravity.util;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/23.
 */

public class NativeUtils {
    static {
        System.loadLibrary("native");
    }

    public static native final String getConfig(String typeStr);

    public static native final void serverToClient(Socket remoteSocket,Socket clientSocket);

    public static native final void fork();
    public static native final void testBackground();

    public static native final void demoMutual(String s);

    public static native String demoJni(String s);
}
