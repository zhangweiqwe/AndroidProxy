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


/**
 * Created by Administrator on 2016/10/28.
 */

public class ShellHelper {
    public static final String DEFAULT_DNS = "129.29.29.29";
    private  static final ShellHelper shellParamsHelper  = new ShellHelper();

    private  Context context;
    private String startStr,stopStr;
    private String dns;
    private String proxy;
    private String port;
    private String uid;


    public static  final ShellHelper getInstance(){
        return shellParamsHelper;
    }




    public void init(Context context){
        ShellHelper.this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("main",Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("hasShell",false)){
            shellParamsHelper.startStr=sharedPreferences.getString("start.sh",null);
            shellParamsHelper.stopStr=sharedPreferences.getString("stop.sh",null);
        }else {
            sharedPreferences.edit().putBoolean("hasShell",true).commit();
            ShellHelper shellHelper = new ShellHelper();
            shellParamsHelper.startStr = shellHelper.getInitStartStr(context);
            shellParamsHelper.stopStr = shellHelper.getInitStopStr();
            sharedPreferences.edit().putString("start.sh", shellParamsHelper.startStr).commit();
            sharedPreferences.edit().putString("stop.sh", shellParamsHelper.stopStr).commit();
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
          uid =  context.getPackageManager().getApplicationInfo(context.getPackageName(),0).uid;
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
        String str ="iptables -t nat -F\n" +
                "iptables -t nat -A POSTROUTING -j MASQUERADE\n" +
                "iptables -t nat -I OUTPUT -p tcp --dport 80 -j DNAT --to-destination 127.0.0.1:"+ SocketServer.PORT+"\n" +
                "iptables -t nat -I OUTPUT -p tcp --dport 8080 -j DNAT --to-destination 127.0.0.1:"+SocketServer.PORT+"\n" +
                "iptables -t nat -I OUTPUT -m owner --uid-owner "+uid+" -p tcp -j ACCEPT\n" +
                "echo 启动脚本已执行！"
                ;
        String str2 = "#---------------全局代理设置--------------#\n" +
                "#全局代理UID\n" +
                "UID0=\""+uid+"\"\n" +
                "\n" +
                "#防跳规则设置(A/B)\n" +
                "MODE=\"A\"\n" +
                "\n" +
                "#全局/直连代理IP设置\n" +
                "IP=\"127.0.0.1\"\n" +
                "PORT=\""+SocketServer.PORT+"\"\n" +
                "\n" +
                "#是否支持HTTPS/是否用直连\n" +
                "MHTTPS=\"off\"\n" +
                "ZHILIAN=\"off\"\n" +
                "\n" +
                "#--------------定义本机设置---------------#\n" +
                "#半免，多个uid用空格间隔\n" +
                "UID1=\""+qqUid+"\"\n" +
                "\n" +
                "#不免，多个uid用空格间隔\n" +
                "UID2=\"\"\n" +
                "\n" +
                "#禁网，多个uid用空格间隔\n" +
                "UID3=\"1000\"\n" +
                "\n" +
                "#单放HTTPS，多个uid用空格间隔\n" +
                "UID4=\"\"\n" +
                "\n" +
                "#单放udp协议，多个uid用空格间隔\n" +
                "UID5=\"\"\n" +
                "\n" +
                "#定义DNS_IP，不填将使用默认DNS\n" +
                "DNSIP=\"\"\n" +
                "\n" +
                "#DNS放行，关闭后在线视频等无法使用\n" +
                "DNS=\"on\"\n" +
                "\n" +
                "#HTTPS放行，Samp全免版在设置里开关\n" +
                "HTTPS=\"off\"\n" +
                "\n" +
                "#UDP放行，解决部分APP不联网或网游问题\n" +
                "UDP=\"off\"\n" +
                "\n" +
                "#定义WIFI网卡，WIFI不联网的改为本机网卡\n" +
                "WIFIF=\"wlan0\"\n" +
                "\n" +
                "#指定tcp端口走回全局代理不经过模块处理\n" +
                "SDK=\"\"\n" +
                "\n" +
                "#--------------定义共享设置---------------#\n" +
                "#共享设备免设代理，若要手动设置请关闭\n" +
                "GXMM=\"on\"\n" +
                "\n" +
                "#共享设备DNS放行,直播或拖电脑须开启\n" +
                "#必须连同本机DNS放行一起开启才有效\n" +
                "GDNS=\"on\"\n" +
                "\n" +
                "#UDP放行,解决部分程序或网游联网问题\n" +
                "GXUDP=\"off\"\n" +
                "\n" +
                "#HTTPS放行,全局支持HTTPS的无需开启\n" +
                "GHTTPS=\"off\"\n" +
                "\n" +
                "#共享设备端口放行，多个端口用空格间隔\n" +
                "#TCP端口:\n" +
                "GTCP=\"\"\n" +
                "#UDP端口:\n" +
                "GUDP=\"\"\n" +
                "\n" +
                "#--------------其他选项设置---------------#\n" +
                "#自动放行快牙.闪传.茄子快传数据传输协议\n" +
                "ZDFX=\"on\"\n" +
                "\n" +
                "#QQ视频全免(可拨不能接)关可接但拨也不免\n" +
                "QQML=\"on\"\n" +
                "QUID=\"99999\"\n" +
                "\n" +
                "#新版虎牙.YY放行5002或23解决不能看直播\n" +
                "YUID=\"55555 66666\"\n" +
                "YUDP=\"23\"\n" +
                "\n" +
                "#--------------处理模块设置---------------#\n" +
                "#选(pdnsd/dnsp)免流解释DNS，不填则关闭\n" +
                "MDNS=\"\"\n" +
                "\n" +
                "#选(redsocks/u2nl)处理HTTPS，不填则关闭\n" +
                "CHTTPS=\"\"\n" +
                "\n" +
                "#设置u2nl代理IP和端口(red.在配置文件改)\n" +
                "PMIP=\"10.0.0.172\"\n" +
                "PMDK=\"80\"\n" +
                "\n" +
                "#设置dnsp模块的DNS解释域名\n" +
                "DNSP=\"http://dns.sturgeon.mopaas.com/nslookup.php\"\n" +
                "\n" +
                "#--------以下所有代码请勿修改-------#\n" +
                "By=\"Jume\"\n" +
                "#--------------启动处理模块---------------#\n" +
                "killall -9 0u2nl\n" +
                "killall -9 0dnsp\n" +
                "killall -9 0pdnsd\n" +
                "killall -9 0redsocks\n" +
                "if [[ $By == \"Jume\" ]] && [[ $CHTTPS == \"redsocks\" ]]\n" +
                "then\n" +
                "0redsocks -c /system/xbin/0redsocks.conf >/dev/null 2>&1 &\n" +
                "elif [[ $CHTTPS == \"u2nl\" ]]\n" +
                "then\n" +
                "0u2nl $PMIP $PMDK 1256 >/dev/null 2>&1 &\n" +
                "fi\n" +
                "if [[ $By == \"Jume\" ]] && [[ $MDNS == \"dnsp\" ]]\n" +
                "then\n" +
                "0dnsp -p 54321 -l 127.0.0.1 -h $IP -r $PORT -s $DNSP >/dev/null 2>&1 &\n" +
                "elif [[ $MDNS == \"pdnsd\" ]]\n" +
                "then\n" +
                "0pdnsd -c /system/xbin/0pdnsd.conf >/dev/null 2>&1 &\n" +
                "fi\n" +
                "#--------------设置本机规则---------------#\n" +
                "iptables -t nat -F OUTPUT\n" +
                "iptables -t nat -F PREROUTING\n" +
                "iptables -t nat -F POSTROUTING\n" +
                "iptables -t mangle -F OUTPUT\n" +
                "iptables -t mangle -F FORWARD\n" +
                "if [[ $UDP == \"on\" ]]\n" +
                "then\n" +
                "FXUDP=\"ACCEPT\"\n" +
                "else\n" +
                "FXUDP=\"DNAT --to-destination $IP:$PORT\"\n" +
                "fi\n" +
                "if [[ $MHTTPS == \"on\" ]]\n" +
                "then\n" +
                "HPORT=\"DNAT --to-destination 127.0.0.1:1256\"\n" +
                "else\n" +
                "HPORT=\"DNAT --to-destination $IP:$PORT\"\n" +
                "fi\n" +
                "if [[ $UDP == \"on\" ]] && [[ $MODE == \"A\" ]]\n" +
                "then\n" +
                "iptables -t nat -A OUTPUT -p 17 -j ACCEPT\n" +
                "fi\n" +
                "if [[ $MODE == \"A\" ]]\n" +
                "then\n" +
                "iptables -t nat -A OUTPUT -p 6 --dport 80 -j DNAT --to-destination $IP:$PORT\n" +
                "iptables -t nat -A OUTPUT -p 6 --dport 8080 -j DNAT --to-destination $IP:$PORT\n" +
                "iptables -t nat -A OUTPUT -p 6 ! --dport 80 -j $HPORT\n" +
                "iptables -t nat -A OUTPUT ! -p 6 -j DNAT --to-destination $IP:$PORT\n" +
                "elif [[ $MODE == \"B\" ]]\n" +
                "then\n" +
                "iptables -t nat -A OUTPUT -p 1 -j DNAT --to-destination $IP:$PORT\n" +
                "iptables -t nat -A OUTPUT -p 17 -j $FXUDP\n" +
                "iptables -t nat -A OUTPUT -p 6 --dport 80 -j DNAT --to-destination $IP:$PORT\n" +
                "iptables -t nat -A OUTPUT -p 6 --dport 8080 -j DNAT --to-destination $IP:$PORT\n" +
                "iptables -t nat -A OUTPUT -p 6 -j $HPORT\n" +
                "iptables -t nat -A OUTPUT -j DNAT --to-destination 127.0.0.1\n" +
                "fi\n" +
                "FX=\"31637 9876 2999 55283 6789 35415 67 68\"\n" +
                "for fx in $FX\n" +
                "do\n" +
                "if [[ $ZDFX == \"on\" ]] && [[ $By == \"Jume\" ]]\n" +
                "then\n" +
                "iptables -t nat -I PREROUTING -p 6 --dport $fx -j ACCEPT\n" +
                "fi\n" +
                "done\n" +
                "for quid in $QUID\n" +
                "do\n" +
                "if [[ $By == \"Jume\" ]] && [[ $QQML == \"on\" ]]\n" +
                "then\n" +
                "By=\"Jume\"\n" +
                "else\n" +
                "iptables -t nat -I OUTPUT -p 17 --dport 8000 -m owner --uid-owner $quid -j ACCEPT\n" +
                "iptables -t nat -I OUTPUT -p 17 --dport 16001 -m owner --uid-owner $quid -j ACCEPT\n" +
                "fi\n" +
                "done\n" +
                "if [[ $ZHILIAN == \"on\" ]]\n" +
                "then\n" +
                "GPORT=\"DNAT --to-destination $IP:$PORT\"\n" +
                "else\n" +
                "GPORT=\"REDIRECT --to-ports $PORT\"\n" +
                "fi\n" +
                "if [[ $MHTTPS == \"on\" ]]\n" +
                "then\n" +
                "GHPORT=\"REDIRECT --to-ports 1256\"\n" +
                "elif [[ $ZHILIAN == \"on\" ]]\n" +
                "then\n" +
                "GHPORT=\"DNAT --to-destination $IP:$PORT\"\n" +
                "else\n" +
                "GHPORT=\"REDIRECT --to-ports $PORT\"\n" +
                "fi\n" +
                "if [[ $HTTPS == \"on\" ]]\n" +
                "then\n" +
                "iptables -t nat -I OUTPUT -p 6 --dport 443 -j ACCEPT\n" +
                "fi\n" +
                "if [[ $DNSIP != \"\" ]]\n" +
                "then\n" +
                "DDNS=\"DNAT --to-destination $DNSIP:53\"\n" +
                "else\n" +
                "DDNS=\"ACCEPT\"\n" +
                "fi\n" +
                "if [[ $MDNS == \"dnsp\" ]]\n" +
                "then\n" +
                "DDNS=\"REDIRECT --to-ports 54321\"\n" +
                "elif [[ $MDNS == \"pdnsd\" ]]\n" +
                "then\n" +
                "DDNS=\"REDIRECT --to-ports 54321\"\n" +
                "fi\n" +
                "if [[ $DNS == \"on\" ]]\n" +
                "then\n" +
                "iptables -t nat -I OUTPUT -p 17 --dport 53 -j $DDNS\n" +
                "fi\n" +
                "for sdk in $SDK\n" +
                "do\n" +
                "if [ sdk != \"\"  ]\n" +
                "then\n" +
                "iptables -t nat -I OUTPUT -p 6 --dport $sdk -j DNAT --to $IP:$PORT\n" +
                "iptables -t nat -A PREROUTING -p 6 --dport $sdk -j $GPORT\n" +
                "fi\n" +
                "done\n" +
                "for yuid in $YUID\n" +
                "do\n" +
                "if [[ $yuid != \"\" ]]\n" +
                "then\n" +
                "iptables -t nat -I OUTPUT -p 17 --dport $YUDP -m owner --uid-owner $yuid -j ACCEPT\n" +
                "fi\n" +
                "done\n" +
                "#--------------设置应用规则---------------#\n" +
                "for uid0 in $UID0\n" +
                "do\n" +
                "if [ uid0 != \"\"  ]\n" +
                "then\n" +
                "iptables -t nat -I OUTPUT -p 6 -m owner --uid-owner $uid0 -j ACCEPT\n" +
                "fi\n" +
                "done\n" +
                "for bam in $UID1\n" +
                "do\n" +
                "if [ bam != \"\"  ]\n" +
                "then\n" +
                "iptables -t nat -I OUTPUT -m owner --uid-owner $bam -j ACCEPT\n" +
                "iptables -t nat -I OUTPUT -p 6 --dport 80 -m owner --uid-owner $bam -j DNAT --to-destination $IP:$PORT\n" +
                "iptables -t nat -I OUTPUT -p 6 --dport 8080 -m owner --uid-owner $bam -j DNAT --to-destination $IP:$PORT\n" +
                "fi\n" +
                "done\n" +
                "for bum in $UID2\n" +
                "do\n" +
                "if [ bum != \"\"  ]\n" +
                "then\n" +
                "iptables -t nat -I OUTPUT -m owner --uid-owner $bum -j ACCEPT\n" +
                "fi\n" +
                "done\n" +
                "for jiw in $UID3\n" +
                "do\n" +
                "if [ jiw != \"\"  ]\n" +
                "then\n" +
                "iptables -t mangle -A OUTPUT -m owner --uid-owner $jiw -j DROP\n" +
                "fi\n" +
                "done\n" +
                "for daf in $UID4\n" +
                "do\n" +
                "if [ daf != \"\"  ]\n" +
                "then\n" +
                "iptables -t nat -I OUTPUT -p 6 --dport 443 -m owner --uid-owner $daf -j ACCEPT\n" +
                "fi\n" +
                "done\n" +
                "for fudp in $UID5\n" +
                "do\n" +
                "if [ fudp != \"\"  ]\n" +
                "then\n" +
                "iptables -t nat -I OUTPUT -p 17 -m owner --uid-owner $fudp -j ACCEPT\n" +
                "fi\n" +
                "done\n" +
                "iptables -t mangle -A OUTPUT -m state --state INVALID -j DROP\n" +
                "iptables -t mangle -A FORWARD -m state --state INVALID -j DROP\n" +
                "iptables -A OUTPUT -p 6 ! --syn -m state --state NEW -j DROP\n" +
                "iptables -A FORWARD -p 6 ! --syn -m state --state NEW -j DROP\n" +
                "iptables -t mangle -I OUTPUT -s 192.168.0.0/16 -j ACCEPT\n" +
                "iptables -t nat -I OUTPUT -s 192.168.0.0/16 -j ACCEPT\n" +
                "iptables -t nat -I OUTPUT -d 127.0.0.1 -j ACCEPT\n" +
                "iptables -t nat -I OUTPUT -o lo -j ACCEPT\n" +
                "for wifif in $WIFIF\n" +
                "do\n" +
                "if [[ $wifif != \"\" ]]\n" +
                "then\n" +
                "iptables -t mangle -I OUTPUT -o $wifif -j ACCEPT\n" +
                "iptables -t nat -I OUTPUT -o $wifif -j ACCEPT\n" +
                "fi\n" +
                "done\n" +
                "#--------------设置共享规则---------------#\n" +
                "echo \"1\"  > /proc/sys/net/ipv4/ip_forward\n" +
                "if [[ $GXUDP == \"on\" ]]\n" +
                "then\n" +
                "GSUDP=\"ACCEPT\"\n" +
                "else\n" +
                "GSUDP=\"REDIRECT --to-ports $PORT\"\n" +
                "fi\n" +
                "if [[ $GXUDP == \"on\" ]] && [[ $MODE == \"A\" ]] && [[ $GXMM == \"on\" ]]\n" +
                "then\n" +
                "iptables -t nat -A PREROUTING -s 192.168.0.0/16 -p 17 -j ACCEPT\n" +
                "fi\n" +
                "if [[ $MODE == \"A\" ]] && [[ $GXMM == \"on\" ]] && [[ $By == \"Jume\" ]]\n" +
                "then\n" +
                "iptables -t nat -A PREROUTING -s 192.168.0.0/16 -p 6 --dport 80 -j $GPORT\n" +
                "iptables -t nat -A PREROUTING -s 192.168.0.0/16 -p 6 --dport 8080 -j $GPORT\n" +
                "iptables -t nat -A PREROUTING -s 192.168.0.0/16 -p 6 ! --dport 80 -j $GHPORT\n" +
                "#iptables -t nat -A PREROUTING -s 192.168.0.0/16 -p 6 ! --dport 8080 -j $GHPORT\n" +
                "iptables -t nat -A PREROUTING -s 192.168.0.0/16 ! -p 6 -j REDIRECT --to-ports $PORT\n" +
                "elif [[ $MODE == \"B\" ]] && [[ $GXMM == \"on\" ]] && [[ $By == \"Jume\" ]]\n" +
                "then\n" +
                "iptables -t nat -A PREROUTING -s 192.168.0.0/16 -p sctp -j REDIRECT --to-ports $PORT\n" +
                "iptables -t nat -A PREROUTING -s 192.168.0.0/16 -p 1 -j REDIRECT --to-ports $PORT\n" +
                "iptables -t nat -A PREROUTING -s 192.168.0.0/16 -p 17 -j $GSUDP\n" +
                "iptables -t nat -A PREROUTING -s 192.168.0.0/16 -p 6 --dport 80 -j $GPORT\n" +
                "iptables -t nat -A PREROUTING -s 192.168.0.0/16 -p 6 -j $GHPORT\n" +
                "iptables -t nat -A POSTROUTING -s 192.168.0.0/16 -j MASQUERADE\n" +
                "fi\n" +
                "iptables -t nat -I PREROUTING -d 1.2.3.4 -j DNAT --to-destination 123.125.96.11\n" +
                "if [[ $GDNS == \"on\" ]]\n" +
                "then\n" +
                "iptables -t nat -I PREROUTING -p 17 --dport 53 -j ACCEPT\n" +
                "fi\n" +
                "if [[ $GHTTPS == \"on\" ]]\n" +
                "then\n" +
                "iptables -t nat -I PREROUTING -p 6 --dport 443 -j ACCEPT\n" +
                "fi\n" +
                "for gtcp in $GTCP\n" +
                "do\n" +
                "if [[ $gtcp != \"\" ]]\n" +
                "then\n" +
                "iptables -t nat -I PREROUTING -p 6 --dport $gtcp -j ACCEPT\n" +
                "fi\n" +
                "done\n" +
                "for gudp in $GUDP\n" +
                "do\n" +
                "if [[ $gudp != \"\" ]]\n" +
                "then\n" +
                "iptables -t nat -I PREROUTING -p 17 --dport $gudp -j ACCEPT\n" +
                "fi\n" +
                "done\n" +
                "#---------------------完毕----------------------#";
        return str3;
    }
    public String getInitStopStr(){
        String str ="iptables -t nat -F\n" +
                "iptables -t nat -A POSTROUTING -j MASQUERADE\n" +
                "echo 关闭脚本已执行！";
        String str2 = "killall -9 0u2nl\n" +
                "killall -9 0dnsp\n" +
                "killall -9 0pdnsd\n" +
                "killall -9 0redsocks\n" +
                "iptables -t nat -F OUTPUT\n" +
                "iptables -t nat -F PREROUTING\n" +
                "iptables -t nat -F POSTROUTING\n" +
                "iptables -t mangle -F OUTPUT\n" +
                "iptables -t mangle -F FORWARD\n" +
                "echo \"0\"  > /proc/sys/net/ipv4/ip_forward\n" +
                "iptables -t nat -A POSTROUTING -j MASQUERADE";
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
