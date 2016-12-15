package cn.wsgwz.gravity.util;

import android.Manifest;
import android.app.Fragment;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelUuid;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.AtomicFile;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SerializablePermission;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Jeremy Wang on 2016/10/26.
 */

public class FileUtil {
    public static final String SD_APTH = Environment.getExternalStorageDirectory()+"/"+"Gravity";
    /*public static final String SD_APTH_CONFIG = Environment.getExternalStorageDirectory()+"/"+"gravity"+"/config";
    public static final String SD_APTH_PCAP = Environment.getExternalStorageDirectory()+"/"+"gravity"+"/pcap";*/
    public static final String SD_APTH_QQ = Environment.getExternalStorageDirectory()+"/"+"Tencent"+"/QQfile_recv";
    public static final String SD_APTH_CONFIG = Environment.getExternalStorageDirectory()+"/"+"Gravity"+"/config";
    public static final String VERSION_NUMBER = "2.34";
    public static final String CONFIG_END_NAME = ".xml";
    public static final String ASSETS_CONFIG_PATH = "config/";

   // public static final String SD_APTH_CONFIG = "/data/data/cn.wsgwz.gravity/cache"+"/"+"gravity"+"/config";
   // public static final String SD_APTH_PCAP = "/data/data/cn.wsgwz.gravity/cache"+"/"+"gravity"+"/pcap";
    public static final String SD_APTH_PCAP = Environment.getExternalStorageDirectory()+"/"+"Gravity"+"/pcap";
    public static  final String CONFIG_FILE_NAME = "config.zip";
    public static  final String ABC_FILE_NAME = "abc.zip";
    public static  final String JUME_FILE_NAME = "Jume.zip";




    //传入file返回字符串
    public static StringBuffer getString(File file) throws IOException {
        if(file==null||!file.exists()){
            return null;
        }

        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"UTF-8");
        BufferedReader br = new BufferedReader(inputStreamReader);
        String line = br.readLine();
        StringBuffer sb = new StringBuffer();
        while (line!=null){
            sb.append(line+"\r\n");
            line = br.readLine();
        }

        return sb;
    }
    //将字符串储存在指定file
    public static void saveStr(File file,String str) throws IOException {
        if(file==null||!file.exists()){
            return;
        }
        if(str==null){
            return;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes("utf-8"));
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len=byteArrayInputStream.read(buffer))!=-1){
            fileOutputStream.write(buffer,0,len);
            fileOutputStream.flush();
        }

        byteArrayInputStream.close();
        fileOutputStream.close();

    }

}
