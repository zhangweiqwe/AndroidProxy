package cn.wsgwz.gravity.other;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import cn.wsgwz.gravity.config.Config;
import cn.wsgwz.gravity.core.ParamsHelper;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2016/11/18.
 */

public class BackgroundHtml {
    public static final String getBackgroundHtml(ParamsHelper paramsHelper, Config config)  {
        String requestStr = paramsHelper.toString();
        if(requestStr==null){
            return "<!DOCTYPE HTML>\n" +
                    "<html>\n" +
                    "<body>\n" +
                    "\n" +
                   "error"
                    +
                    "\n" +
                    "</body>\n" +
                    "</html>";
        }
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        String flag = "<br />";
        String appendStr = "欢迎使用"+flag+
                "Gravity 后台(地址:11.22.33.44)"+flag+flag;
        sb.append(appendStr);
        try {
            br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestStr.getBytes())));
            String  line = null;
            while ((line = br.readLine())!=null){
                sb.append(line+flag);
            }

            sb.append(flag+flag+flag);

            String httpsRequestStr = "CONNECT 11.22.33.44:443 HTTP/1.1\r\n" +
                    "Host: 11.22.33.44\r\n" +
                    "Proxy-Connection: keep-alive\r\n" +
                   // "User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; MZ-MX4 Pro Build/LMY48W) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/45.0.2454.94 Mobile Safari/537.36\r\n" +
                    "\r\n";

            br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(  ParamsHelper.read(new ByteArrayInputStream(httpsRequestStr.getBytes()),config).toString().getBytes()   )));
            String  line2 = null;
            while ((line2 = br.readLine())!=null){
                sb.append(line2+flag);
            }
            sb.append("  <a href=\"cn.wsgwz://cn.wsgwz.gravity/\">Gravitation</a><br/>");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        String str = "<!DOCTYPE HTML>\n" +
                "<html>\n" +
                "<head>\n" +
                "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />搜索\n" +
                " </head>"+
                "<body>\n" +
                "\n" +
               sb.toString()
                +
                "\n" +
                "</body>\n" +
                "</html>";

        return str;
    }
}
