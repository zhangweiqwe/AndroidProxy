package cn.wsgwz.gravity.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import org.dom4j.DocumentException;

import java.io.IOException;

import cn.wsgwz.gravity.config.Config;
import cn.wsgwz.gravity.core.SocketServer;
import cn.wsgwz.gravity.fragment.log.LogContent;
import cn.wsgwz.gravity.service.ProxyService;
import cn.wsgwz.gravity.util.LogUtil;
import cn.wsgwz.gravity.util.SharedPreferenceMy;


/**
 * Created by Administrator on 2016/10/28.
 */

public class ShellHelper {
    public static final String DEFAULT_DNS = "129.29.29.29";
    private  static final ShellHelper shellParamsHelper  = new ShellHelper();

    private String startStr,stopStr;
    private String dns;
    private String proxy;
    private String port;
    private String uid;


    public static  final ShellHelper getInstance(){
        return shellParamsHelper;
    }




    public static final void init(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferenceMy.MAIN,Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(SharedPreferenceMy.HAS_SHELL,false)){
            shellParamsHelper.startStr=sharedPreferences.getString(SharedPreferenceMy.START_SH,null);
            shellParamsHelper.stopStr=sharedPreferences.getString(SharedPreferenceMy.STOP_SH,null);
        }else {
            sharedPreferences.edit().putBoolean(SharedPreferenceMy.HAS_SHELL,true).commit();
            ShellHelper shellHelper = new ShellHelper();
            shellParamsHelper.startStr = shellHelper.getInitStartStr(context);
            shellParamsHelper.stopStr = shellHelper.getInitStopStr();
            sharedPreferences.edit().putString(SharedPreferenceMy.START_SH, shellParamsHelper.startStr).commit();
            sharedPreferences.edit().putString(SharedPreferenceMy.STOP_SH, shellParamsHelper.stopStr).commit();
        }

    }

    private  void setShellInfo(){
        String a0 = "UID0='";
        if(shellParamsHelper.startStr.contains(a0)){
            int index = shellParamsHelper.startStr.indexOf(a0)+a0.length();
            ShellHelper.this.setUid(shellParamsHelper.startStr.substring(index,shellParamsHelper.startStr.indexOf("'",index)));
        }

        String a1 = "DIP='";
        if(shellParamsHelper.startStr.contains(a1)){
            int index = shellParamsHelper.startStr.indexOf(a1)+a1.length();
            ShellHelper.this.setProxy(shellParamsHelper.startStr.substring(index,shellParamsHelper.startStr.indexOf("'",index)));
        }

        String a2 = "PORT='";
        if(shellParamsHelper.startStr.contains(a2)){
            int index = shellParamsHelper.startStr.indexOf(a2)+a2.length();
            ShellHelper.this.setPort(shellParamsHelper.startStr.substring(index,shellParamsHelper.startStr.indexOf("'",index))) ;
        }

        String a3 = "PDNP='";
        if(shellParamsHelper.startStr.contains(a3)){
            int index = shellParamsHelper.startStr.indexOf(a3)+a3.length();
            ShellHelper.this.setDns(shellParamsHelper.startStr.substring(index,shellParamsHelper.startStr.indexOf("'",index)));
        }
    }

    public String getStartStr() {
        setShellInfo();
        return startStr;
    }

    public void setStartStr(String startStr) {
        this.startStr = startStr;
    }

    public String getStopStr() {
        setShellInfo();
        return stopStr;
    }

    public void setStopStr(String stopStr) {
        this.stopStr = stopStr;
    }

    public String getInitStartStr(Context context){
        int uid = 8888;
        try {
          uid =  context.getPackageManager().getApplicationInfo("cn.wsgwz.gravity",0).uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int qqUid = 8888;
        try {
            qqUid =  context.getPackageManager().getApplicationInfo("com.tencent.mobileqq",0).uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String str3 ="#!/system/bin/sh\n" +
                "#-------------------------------------------#\n" +
                "#       终极防跳8.0_By Jume      #\n" +
                "#-------------------------------------------#\n" +
                "#-----------全局代理设置-----------#\n" +
                "echo \"#全局代理UID\n" +
                "UID0='"+uid+"'\n" +
                "\n" +
                "#全局/直连转发IP设置\n" +
                "DIP='127.0.0.1'\n" +
                "PORT='"+ SocketServer.PORT+"'\n" +
                "\n" +
                "#-----------定义本机设置-----------#\n" +
                "#半代理应用，多个uid用空格间隔\n" +
                "UID1=''\n" +
                "\n" +
                "#不代理应用，多个uid用空格间隔\n" +
                "UID2=''\n" +
                "\n" +
                "#禁联网应用，多个uid用空格间隔\n" +
                "UID3='1000'\n" +
                "\n" +
                "#单放UDP应用，多个uid用空格间隔\n" +
                "UID4=''\n" +
                "\n" +
                "#单放HTTPS应用，多个uid用空格间隔\n" +
                "UID5=''\n" +
                "\n" +
                "#定义DNS解释IP，不填将使用默认DNS\n" +
                "DNSIP=''\n" +
                "\n" +
                "#DNS放行，开启转发/共享电脑等需开启\n" +
                "FDNS='on'\n" +
                "\n" +
                "#HTTPS全放行，防部分代理HTTPS不联网\n" +
                "FHTTPS='off'\n" +
                "\n" +
                "#UDP全放行，防部分APP或网游联网问题\n" +
                "FXUDP='off'\n" +
                "\n" +
                "#自动放行WIFI,连放行网卡关闭则代理WIFI\n" +
                "ZFWIFI='off'\n" +
                "\n" +
                "#放行WIFI网卡名称,共享不经代理尝试留空\n" +
                "WIFIF='wlan0'\n" +
                "\n" +
                "#指定tcp端口走回全局代理不经过模块处理\n" +
                "TDK=''\n" +
                "\n" +
                "#本机TCP/UDP端口放行，多个用空格间隔\n" +
                "BTCP=''\n" +
                "BUDP=''\n" +
                "\n" +
                "#-----------定义共享设置-----------#\n" +
                "#共享免设代理，关后直接共享则不经代理\n" +
                "GXMM='on'\n" +
                "\n" +
                "#共享规则设置(A1热点/A2usb/A3特殊|B)\n" +
                "GXMOE='A1'\n" +
                "\n" +
                "#共享DNS放行,在线视频或共享电脑须开启\n" +
                "GDNS='on'\n" +
                "\n" +
                "#防止共享网络不经代理没问题不建议开启\n" +
                "FZGX='off'\n" +
                "\n" +
                "#UDP放行,解决部分程序或网游等联网问题\n" +
                "GFUDP='off'\n" +
                "\n" +
                "#HTTPS放行,全局支持HTTPS代理无需开启\n" +
                "GHTTPS='off'\n" +
                "\n" +
                "#共享TCP/UDP端口放行，多个用空格间隔\n" +
                "GTCP=''\n" +
                "GUDP=''\n" +
                "\n" +
                "#-----------其他选项设置-----------#\n" +
                "#执行脚本自动关开一遍网络防止QQ乱跳等\n" +
                "ZDKG='on'\n" +
                "\n" +
                "#QQ视频代理(可拨不能接)关能但拨也不代理\n" +
                "QQML='on'\n" +
                "QUID='99999'\n" +
                "\n" +
                "#设置脚本'Jume'资源库文件夹安装目录位置\n" +
                "JDIR='/system/xbin'\n" +
                "\n" +
                "#-----------转发处理设置-----------#\n" +
                "#(u2nl/Hu2nl/redsocks)tcp处理/on只转端口\n" +
                "CHTTPS='redsocks'\n" +
                "CHDK='1256'\n" +
                "\n" +
                "#设置(u2nl/Hu2nl/redsocks)模块转发服务器\n" +
                "UIP='10.0.0.172'\n" +
                "UDK='80'\n" +
                "\n" +
                "#(dnsp/tdnsp/pdnsd)dns解释/on只转端口\n" +
                "MDNS='pdnsd'\n" +
                "MDDK='54321'\n" +
                "\n" +
                "#设置pdnsd的DNS代理IP(多个IP可用','隔开)\n" +
                "PDNP='"+DEFAULT_DNS+"'\n" +
                "\n" +
                "#设置dnsp模块在线解释DNS时所使用的地址\n" +
                "DNSP='http://dns1.sturgeon.mopaas.com/nslookup.php'\n" +
                "\n" +
                "#--------以下内容切勿修改-------#\n" +
                "BY='Jume'\n" +
                "#-----------启动终极v8.0-----------#\n" +
                "\" > /data/Jume8.conf\n" +
                "chmod 777 /data/Jume8.conf\n" +
                "echo \"m='a'\" > /data/a.conf\n" +
                "chmod 777 /data/a.conf\n" +
                ". /data/Jume8.conf\n" +
                "$JDIR/Jume/Jume8\n" +
                "#------------------完毕------------------#\n" +
                "#以下为自定义脚本:\n" +
                "\n" +
                "#-------------------------------------------#";

        return str3;
    }
    public String getInitStopStr(){
        String str3 = "#!/system/bin/sh\n" +
                "#--------------------------\n" +
                "#关闭防跳自动关闭网络\n" +
                "GW='on'\n" +
                "#脚本'Jume'资料库存放目录位置\n" +
                "JDIR='/system/xbin'\n" +
                "#--------------------------\n" +
                "iptables -t nat -F OUTPUT\n" +
                "iptables -t mangle -F OUTPUT\n" +
                "iptables -t nat -F PREROUTING\n" +
                "echo \"m='b'\" > /data/a.conf\n" +
                "echo \"a='$GW'\" >> /data/a.conf\n" +
                "chmod 777 /data/a.conf\n" +
                "$JDIR/Jume/Jume8\n" +
                "#--------------------------\n" +
                "#以下为自定义脚本:\n" +
                "\n" +
                "#--------------------------";
        return str3;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
