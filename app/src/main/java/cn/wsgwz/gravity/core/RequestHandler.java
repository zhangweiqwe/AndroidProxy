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



    public RequestHandler(Socket clientSocket, Config config){
        this.clientSocket = clientSocket;
        this.config = config;
        httpsSpport=config.isConnectSupport();
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
        byte[] bytes = paramsHelper.toString().getBytes();
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
    public static final void doPost(ParamsHelper paramsHelper,OutputStream remoteOutputStream,InputStream clientInputStream)throws IOException{
        String contentLenStr = paramsHelper.getHashMap().get("Content-Length");
        if(contentLenStr==null){
            contentLenStr = paramsHelper.getHashMap().get("content-length");
            if(contentLenStr==null){
                return;
            }
        }
        int contentLength = Integer.parseInt(contentLenStr);
        if(contentLength!=0){
            for (int i = 0; i < contentLength; i++)
            {
                remoteOutputStream.write(clientInputStream.read());
            }
            byte[] bytes = paramsHelper.endOfLine.getBytes();
            remoteOutputStream.write(bytes);
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


            if(requestType.startsWith(paramsHelper.GET)){
                String host = paramsHelper.getHost();
                if(host!=null){
                    if(host.trim().startsWith("11.22.33.44")){
                        clientOutputStream.write(BackgroundHtml.getBackgroundHtml(paramsHelper,config).getBytes());
                        clientOutputStream.flush();
                        return;
                    }
                }else {
                    return;
                }
                writeHeader(paramsHelper);
                getOrPostThread();
            }else if(requestType.startsWith(paramsHelper.CONNECT)){
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
            }else  if(requestType.startsWith(paramsHelper.POST)){
                writeHeader(paramsHelper);
                doPost(paramsHelper,remoteOutputStream,clientInputStream);
                getOrPostThread();
            }

        } catch (IOException e) {
            //e.printStackTrace();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }finally {
            if(remoteOutputStream!=null){
                try {
                    remoteOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
                    if(remoteInputStream != null){
                        try {
                            remoteInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(remoteSocket != null) {
                        try {
                            remoteSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    };
                    if(clientOutputStream != null){
                        try {
                            clientOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    };
                    if(clientInputStream != null){
                        try {
                            clientInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    };
                    if(clientSocket != null){
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    };

        }
    }

}




