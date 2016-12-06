package cn.wsgwz.gravity.helper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2016/10/27.
 */

public class GraspDataFileHelper {
    public static final String DEFAULT_ENDDING = "iso8859-1";
    public static final int BUFFER_LEN = 2048;
    public static GraspDataFileHelper graspDataFileHelper = null;
    public static final GraspDataFileHelper getInstance(){
        if(graspDataFileHelper ==null){
            graspDataFileHelper = new GraspDataFileHelper();
        }
        return graspDataFileHelper;
    }
    public void getString(final File file, final OnReponseListenner onReponseListenner){
        if(file==null||!file.exists()){
            return ;
        }
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (onReponseListenner==null){
                    return;
                }
                switch (msg.what){
                    case 1000:
                        onReponseListenner.begin((long)(msg.obj));
                        break;
                    case 1001:
                        Bundle bundle = msg.getData();
                        long length = bundle.getLong("length");
                        StringBuffer sb = (StringBuffer)bundle.getSerializable("sb");
                        onReponseListenner.progress(length,sb);
                      //  LogUtil.printSS(sb.toString());
                        break;
                    case 1002:
                        Bundle bundle2 = msg.getData();
                        StringBuffer sb2 = (StringBuffer)bundle2.getSerializable("sb");
                        onReponseListenner.success(sb2);
                        break;
                    case 1004:
                        onReponseListenner.error((String)(msg.obj));
                        break;

                }


            }
        };
        final StringBuffer sb =  new StringBuffer();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Message msg = Message.obtain();
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    msg.obj = file.length();
                    msg.what = 1000;
                    handler.sendMessage(msg);
                    int len = 0;
                    long currentLength = 0l;
                    byte[] buffer = new byte[BUFFER_LEN];
                    while ((len=fileInputStream.read(buffer))!=-1){
                        sb.append(new String(buffer,0,len));
                        currentLength+=len;
                        msg = Message.obtain();
                        msg.what =1001;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("sb",sb);
                        bundle.putLong("length",currentLength);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                    msg = Message.obtain();
                    msg.what =1002;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sb",sb);
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    msg = Message.obtain();
                    msg.what =1004;
                    msg.obj = e.toString();
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    msg = Message.obtain();
                    msg.what =1004;
                    msg.obj = e.toString();
                    handler.sendMessage(msg);
                }
                Looper.loop();
            }
        }).start();


    }

    public interface  OnReponseListenner{
        void begin(long length);
        void progress(long length,StringBuffer sb);
        void success(StringBuffer sb);
        void error(String errorStr);
    }
    private OnReponseListenner onReponseListenner;
    public void setOnReponseListenner(OnReponseListenner onReponseListenner){
        this.onReponseListenner = onReponseListenner;
    }
}
