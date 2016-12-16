package cn.wsgwz.gravity.helper;

import android.os.Bundle;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2016/12/16.
 */

public class Other2 {


    public static void onCreate2() {
        //super.onCreate(savedInstanceState);

        String path ="/data/data/" + "cn.wsgwz.gravity";
        String cmd1 =path + "/lib/native-lib.so";
        String cmd2 =path + "/test";
        String cmd3 ="chmod 777 " + cmd2;
        String cmd4 ="dd if=" + cmd1 + " of=" + cmd2;
        RootCommand(cmd4);              //拷贝lib/libtest.so到上一层目录,同时命名为test.
        RootCommand(cmd3);              //改变test的属性,让其变为可执行
        RootCommand(cmd2);              //执行test程序.
    }
    private static boolean RootCommand(String command) {
        Process process= null;
        String str = "dd id=/data/data/cn.wsgwz.gravity/lib/native-lib.so of=/data/data/cn.wsgwz.gravity/test";
        try {
            process = Runtime.getRuntime().exec("sh");  //获得shell.
            DataInputStream inputStream = new DataInputStream(process.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes("cd /data/data/" + "cn.wsgwz.gravity" +"\n");  //保证在command在自己的数据目录里执行,才有权限写文件到当前目录

            outputStream.writeBytes(command + " &\n"); //让程序在后台运行，前台马上返回
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            process.waitFor();

            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String s = new String(buffer);
            LogUtil.printSS("CMD Result:\n" + s);
        } catch(Exception e) {
            return false;
        }
        return true;
    }

}
