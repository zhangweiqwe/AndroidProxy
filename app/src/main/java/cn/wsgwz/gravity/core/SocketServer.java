package cn.wsgwz.gravity.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;

import org.dom4j.DocumentException;

import cn.wsgwz.gravity.config.Config;
import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.ShellUtil;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2016/12/20.
 */
public class SocketServer extends Thread{
    public static final short PORT = 12888;
    private Config config;
    private Context context;
    private ServerSocket serverSocket = new ServerSocket(PORT);;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public SocketServer(Context context) throws IOException, DocumentException {
        this.config = ShellUtil.getConfig(context,true);;
        this.context = context;
    }
    @Override
    public void run() {
        try {
            while (true) {
                executorService.execute(new RequestHandler(serverSocket.accept(),config,context));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void releasePort(){
        if(executorService!=null){
            executorService.shutdownNow();
        }
        if(serverSocket!=null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
