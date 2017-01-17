package cn.wsgwz.gravity.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.PSSParameterSpec;

import cn.wsgwz.gravity.config.Config;
import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Administrator on 2016/10/29.
 */

public class ClientToServerThread extends Thread {
    /**
     * 读取客户端发送过来的数据，发送给服务器端
     *
     * @param isIn
     * @param osOut
     */

    private InputStream clientInputStream;
    private OutputStream remoteOutputStream;
    private  Config config;
    private Boolean isCapture;
    public ClientToServerThread(InputStream clientInputStream, OutputStream remoteOutputStream, Config config,Boolean isCapture){
        this.clientInputStream = clientInputStream;
        this.remoteOutputStream = remoteOutputStream;
        this.config = config;
        this.isCapture = isCapture;
    }
    @Override
    public void run() {
        super.run();
        ParamsHelper paramsHelper = null;
        try {
            paramsHelper = ParamsHelper.read(clientInputStream,config,isCapture);
            if(paramsHelper==null){
                return;
            }
            byte[] bytes = paramsHelper.toString().getBytes();
            remoteOutputStream.write(bytes);
            remoteOutputStream.flush();
            if(paramsHelper.getRequestType().startsWith("POST")){
                RequestHandler.doPost(paramsHelper,remoteOutputStream,clientInputStream);
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }finally {
            if(clientInputStream!=null){
                try {
                    clientInputStream.close();
                } catch (IOException e) {
                }
            }
            if(remoteOutputStream!=null){
                try {
                    remoteOutputStream.close();
                } catch (IOException e) {
                }
            }

        }
    }
}
