package cn.wsgwz.gravity.core;

import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.spec.PSSParameterSpec;

import javax.crypto.spec.OAEPParameterSpec;
import javax.net.ssl.HttpsURLConnection;

import cn.wsgwz.gravity.config.Config;
import cn.wsgwz.gravity.config.ModuleEnum;
import cn.wsgwz.gravity.other.BackgroundHtml;
import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.LogUtil;


/**
 * Created by Administrator on 2016/11/1.
 */

public class RequestHandler implements Runnable{
    private InputStream clientInputStream;
    private InputStream remoteInputStream;
    protected OutputStream clientOutputStream,remoteOutputStream;
    protected Socket clientSocket,remoteSocket;



    private Config config;
    private boolean httpsSpport;;
    private Context context;



    public RequestHandler(Socket clientSocket, Config config, Context context){
        this.clientSocket = clientSocket;
        this.config = config;
        httpsSpport=config.isConnectSupport();
        this.context = context;
    }

    private ServerToClientThread serverToClientThread =null;
    private ClientToServerThread clientToServerThread =null;

    //connect
    private ServerToClientForConnectThread serverToClientForConnectThread = null;
    private ClientToServerForConnectThread clientToServerForConnectThread = null;

    private void writeHeader(ParamsHelper paramsHelper)throws IOException{
        remoteSocket = new Socket(config.getHttpHost(),config.getHttpPort());
        remoteInputStream = remoteSocket.getInputStream();
        remoteOutputStream = remoteSocket.getOutputStream();
        byte[] bytes = paramsHelper.toString().getBytes("iso-8859-1");
        remoteOutputStream.write(bytes);
        remoteOutputStream.flush();
    }

    private void getOrPostThread()throws InterruptedException{
        serverToClientThread = new ServerToClientThread(remoteInputStream,clientOutputStream);
        clientToServerThread = new ClientToServerThread(clientInputStream,remoteOutputStream,config);
        serverToClientThread.start();
        clientToServerThread.start();
        clientToServerThread.join();
        serverToClientThread.join();
    }
    public static void doPost(ParamsHelper paramsHelper,OutputStream remoteOutputStream,InputStream clientInputStream)throws IOException{
        String contentLenStr = paramsHelper.getLinkedHashMap().get(ParamsHelper.getKeyIgnoreLowerCase("Content-Length",paramsHelper.getLinkedHashMap()));
        if(contentLenStr==null){
            return;
        }
        int contentLength = Integer.parseInt(contentLenStr);
        if(contentLength!=0){
            for (int i = 0; i < contentLength; i++)
            {
                remoteOutputStream.write(clientInputStream.read());
            }
            byte[] bytes1 = "\r\n".getBytes("iso-8859-1");
            remoteOutputStream.write(bytes1);
            remoteOutputStream.flush();
        }
    }
    @Override
    public void run() {
        try {
            clientInputStream = clientSocket.getInputStream();
            final ParamsHelper paramsHelper = ParamsHelper.read(clientInputStream,config);
            if(paramsHelper==null){
                return;
            }
            clientOutputStream = clientSocket.getOutputStream();
            String requestType = paramsHelper.getRequestType();


            if(requestType.startsWith("GET")){
                String host = paramsHelper.getHost();
                if(host!=null){
                    if(host.trim().startsWith("11.22.33.44")){
                        clientOutputStream.write(BackgroundHtml.getBackgroundHtml(paramsHelper,config).getBytes());
                        return;
                    }
                }else {
                    return;
                }
                writeHeader(paramsHelper);
                getOrPostThread();
            }else if(requestType.startsWith("CONNECT")){
                if(!httpsSpport){
                    return;
                }
                writeHeader(paramsHelper);
                serverToClientForConnectThread = new ServerToClientForConnectThread(remoteInputStream,clientOutputStream);
                clientToServerForConnectThread = new ClientToServerForConnectThread(clientInputStream,remoteOutputStream);
                serverToClientForConnectThread.start();
                clientToServerForConnectThread.start();
                clientToServerForConnectThread.join();
                serverToClientForConnectThread.join();
            }else  if(requestType.startsWith("POST")){
                writeHeader(paramsHelper);
                doPost(paramsHelper,remoteOutputStream,clientInputStream);
                getOrPostThread();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
                try {
                    if(remoteOutputStream != null) remoteOutputStream.close();
                    if(remoteInputStream != null) remoteInputStream.close();
                    if(remoteSocket != null) remoteSocket.close();
                    if(clientOutputStream != null) clientOutputStream.close();
                    if(clientInputStream != null) clientInputStream.close();
                    if(clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}




