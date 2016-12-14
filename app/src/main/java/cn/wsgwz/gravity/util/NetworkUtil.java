package cn.wsgwz.gravity.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Jeremy Wang on 2016/12/14.
 */

public class NetworkUtil {
    private int SERVERPORT = 60880;

    private String ping = "ping -c 1 -w 0.5 ";
    private Context context;
    private  String locAddress;
    private Process process ;
    private int lastIpAddress;
    private Runtime runtime = Runtime.getRuntime();

    public NetworkUtil(Context context) {
        this.context = context;
    }




    private Handler handler = new Handler(){

        public void dispatchMessage(Message msg) {
            switch (msg.what) {

                case 222:// 服务器消息
                    break;
                case 333:// 扫描完毕消息
                   // LogUtil.printSS("扫描到主机："+((String)msg.obj).substring(6));
                    break;
                case 444://扫描失败
                    //LogUtil.printSS((String)msg.obj);
                    break;
            }
        }

    };


    /**
     * 扫描局域网内ip，找到对应服务器
     */
    public void scan(){

        locAddress = getLocAddrIndex();//获取本地ip前缀

        if(locAddress==null||locAddress.equals("")){
            LogUtil.printSS("扫描失败，请检查wifi网络");
            return ;
        }

        for ( int i = 0; i < 256; i++) {//创建256个线程分别去ping
            lastIpAddress = i ;

            new Thread(new Runnable() {

                public void run() {

                    String p = NetworkUtil.this.ping + locAddress + lastIpAddress ;

                    String current_ip = locAddress+ lastIpAddress;

                    try {
                        process = runtime.exec(p);

                        int result = process.waitFor();
                        if (result == 0) {
                            //LogUtil.printSS("-------连接成功"+"          =="+current_ip);
                            // 向服务器发送验证信息
                            String msg = sendMsg(current_ip,"scan"+getLocAddress()+" ( "+android.os.Build.MODEL+" ) ");
                            //String msg = sendMsg("192.168.1.141","scan"+getLocAddress()+" ( "+android.os.Build.MODEL+" ) ");

                            //如果验证通过...
                            if (msg != null){
                                if (msg.contains("OK")){
                                    LogUtil.printSS("服务器IP：" + msg.substring(8,msg.length()));
                                    Message.obtain(handler, 333, msg.substring(2,msg.length())).sendToTarget();//返回扫描完毕消息
                                }
                            }
                        } else {
                           // LogUtil.printSS("----失败连接"+"          =="+current_ip);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    } finally {
                        process.destroy();
                    }
                }
            }).start();

        }

    }

    private Socket socket;

    //向serversocket发送消息
    private String sendMsg(final String ip, String msg) {

     // final   Socket socket = null;

        try {
            socket = new Socket(ip, SERVERPORT);
            socket.setKeepAlive(true);
            //向服务器发送消息
            PrintWriter os = new PrintWriter(socket.getOutputStream());
            os.println(msg);
            os.flush();// 刷新输出流，使Server马上收到该字符串

           Thread thread =new Thread(new Runnable() {
               @Override
               public void run() {
                   //从服务器获取返回消息
                   BufferedReader br = null;
                   try {
                       LogUtil.printSS("-----1+"+ip);
                       br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
                       String  res = br.readLine();
                       LogUtil.printSS("server 返回信息：" + res);
                   } catch (IOException e) {
                       e.printStackTrace();
                       LogUtil.printSS("-----2"+e.getMessage().toString());
                   }
                  // Message.obtain(handler, 222, res).sendToTarget();//发送服务器返回消息
               }
           });
            thread.start();
            thread.join();

        } catch (Exception unknownHost) {
            //LogUtil.printSS("You are trying to connect to an unknown host!"+ip);
        } finally {
            // 4: Closing connection
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return null;
    }






    //获取本地ip地址
    private String getLocAddress(){

        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();

        //LogUtil.printSS("--"+intToIp(ipAddress));
        return intToIp(ipAddress);

    }
    private String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    //获取IP前缀
    private String getLocAddrIndex(){
        String str = getLocAddress();
        if(!str.equals("")){
            return str.substring(0,str.lastIndexOf(".")+1);
        }
        return null;
    }

}
