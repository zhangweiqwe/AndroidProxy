package cn.wsgwz.gravity.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Administrator on 2016/10/29.
 */

public class ClientToServerForConnectThread extends Thread {
    /**
     * 读取客户端发送过来的数据，发送给服务器端
     *
     * @param isIn
     * @param osOut
     */

    private InputStream isIn;
    private OutputStream osOut;
    private  byte[] buffer = new byte[409600];
    public ClientToServerForConnectThread(InputStream isIn, OutputStream osOut){
        this.isIn = isIn;
        this.osOut = osOut;
    }
    @Override
    public void run() {
        super.run();
        try {
            int len;
            while ((len = isIn.read(buffer)) != -1) {
               if (len > 0) {
                    osOut.write(buffer, 0, len);
                    osOut.flush();
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        } finally{
            if(osOut!=null){
                try {
                    osOut.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            if(isIn!=null){
                try {
                    isIn.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }

        }


    }
}
