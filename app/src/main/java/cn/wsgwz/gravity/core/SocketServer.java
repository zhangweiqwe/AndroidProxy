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

/**
 * Created by Jeremy Wang on 2016/12/20.
 */
public class SocketServer implements Runnable{
    public static final short PORT = 12888;
    private Config config;
    private Context context;
    protected ServerSocket server = new ServerSocket(PORT);;
    private ExecutorService executor = Executors.newCachedThreadPool();;

    public SocketServer(Context context) throws IOException, DocumentException {
        this.config = ShellUtil.getConfig(context,true);;
        this.context = context;
    }
    @Override
    public void run() {
        try {
            while (true) {
                executor.execute(new RequestHandler(server.accept(),config,context));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(server!=null){
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(server!=null){
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
