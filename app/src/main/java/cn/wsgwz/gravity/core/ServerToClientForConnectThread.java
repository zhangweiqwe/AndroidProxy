package cn.wsgwz.gravity.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Administrator on 2016/10/29.
 */

public class ServerToClientForConnectThread extends Thread{
    /**
     * 将服务器端返回的数据转发给客户端
     *
     * @param
     * @param
     */
    private InputStream isOut;
    private OutputStream osIn;
    private  byte[] buffer = new byte[409600];
    public ServerToClientForConnectThread(InputStream isOut, OutputStream osIn) {
        this.isOut = isOut;
        this.osIn = osIn;
    }

    @Override
    public void run() {
        super.run();
        try {
            int len;
            while ((len = isOut.read(buffer)) != -1) {
                if (len > 0) {
                    osIn.write(buffer, 0, len);
                    osIn.flush();
               }
            }
        } catch (Exception e) { e.printStackTrace();
        }finally{

            if(isOut!=null){
                try {
                    isOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(osIn!=null){
                try {
                    osIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
