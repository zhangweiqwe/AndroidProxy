package cn.wsgwz.gravity.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Administrator on 2016/10/29.
 */

public class ServerToClientThread extends Thread{
    /**
     * 将服务器端返回的数据转发给客户端
     *
     * @param
     * @param
     */
    private InputStream isOut;
    private OutputStream osIn;

    private String ad = "<a href=\"http://shimian.laimimi.cn/\"><img src=\"http://img02.taobaocdn.com/imgextra/i2/2133890962/TB21Ag_XVXXXXboXXXXXXXXXXXX_!!2133890962.gif\" width=\"250\" height=\"250\" border=\"0\"></a></div>";
    private  byte[] buffer = new byte[4096];
    public ServerToClientThread(InputStream isOut, OutputStream osIn) {
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
               /* String str = null;
                if (len > 0) {
                    str = new String(buffer,0,len);
                    osIn.write(str.getBytes("iso-8859-1"));
                    osIn.flush();
                }
                LogUtil.printSS("--->"+str+"<----");*/
            }
        } catch (Exception e) {// e.printStackTrace();
        }finally{
            if(isOut!=null){
                try {
                    isOut.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            if(osIn!=null){
                try {
                    osIn.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }




}
