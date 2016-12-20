package cn.wsgwz.gravity.config.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import  java.util.Collections;

import cn.wsgwz.gravity.config.Config;
import cn.wsgwz.gravity.config.ModuleEnum;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2016/11/3.
 */

public class ConfigXml {
    private static final String SPLIT = ",";


    public static final Config read(InputStream in) throws DocumentException, FileNotFoundException {




        Config config = null;
        String dns = null;
        String version = null;
        String apn_apn = null;
        String apn_proxy = null;
        String apn_port = null;

         String httpHost = null;
         int httpPort = 80;
         String httpFirstLine = null;
         List<String> httpNeedDelateHeaders = null;


        SAXReader saxReader = new SAXReader();
       // FileInputStream filterInputStream = new FileInputStream(file);
        try {
           in= pretreatmentXML(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document document = saxReader.read(in);
        Element root = document.getRootElement();


        version = root.attribute("version").getValue();
        dns = root.attribute("dns").getValue();
        apn_apn = root.attribute("apn_apn").getValue();
        apn_proxy = root.attribute("apn_proxy").getValue();
        apn_port = root.attribute("apn_port").getValue();


        Element http = root.element("http");
        httpHost = http.attribute("host").getValue();
        try {
            httpPort = Integer.parseInt(http.attribute("port").getValue());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }


        httpFirstLine = http.elementTextTrim("first-line").trim();

       // httpNeedDelateHeaders = new ArrayList<>();
        httpNeedDelateHeaders =Collections.synchronizedList(new ArrayList<String>());
        String tempDelateStr = http.elementTextTrim("delate").trim();
        if(tempDelateStr!=null&&tempDelateStr.trim().length()>0){
            if(tempDelateStr.contains(SPLIT)){
                String[] arr = tempDelateStr.split(SPLIT);
                for(int i=0;i<arr.length;i++){
                    if(arr[i].trim().equals("")){
                        continue;
                    }
                    httpNeedDelateHeaders.add(arr[i].trim());
                }
            }else {
                httpNeedDelateHeaders.add(tempDelateStr.trim());
            }
        }


        boolean connectSupport = false;
        String connectHost = null;
        int connectPort = 443;
        String connectFirstLine = null;
        List<String> connectNeedDelateHeaders = null;
        Element connnect = root.element("https");
        connectHost = connnect.attribute("host").getValue();
        try {
            connectPort = Integer.parseInt(connnect.attribute("port").getValue());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }

        String  b =connnect.attribute("switch").getValue();
        switch (b){
            case "on":
                connectSupport=true;
                break;
            case "off":
                connectSupport=false;
                break;
        }

        connectFirstLine = connnect.elementTextTrim("first-line").trim();

        //connectNeedDelateHeaders = new ArrayList<>();
        connectNeedDelateHeaders = Collections.synchronizedList(new ArrayList<String>());
        String tempDelateConnectStr = connnect.elementTextTrim("delate").trim();
        if(tempDelateConnectStr!=null&&tempDelateConnectStr.trim().length()>0){
            if(tempDelateConnectStr.contains(SPLIT)){
                String[] arr = tempDelateConnectStr.split(SPLIT);
                for(int i=0;i<arr.length;i++){
                    if(arr[i].trim().equals("")){
                        continue;
                    }
                    connectNeedDelateHeaders.add(arr[i].trim());
                }
            }else {
                connectNeedDelateHeaders.add(tempDelateConnectStr.trim());
            }
        }


        config = new Config(version,apn_apn,apn_proxy,apn_port, httpHost,   httpPort,   httpFirstLine,   httpNeedDelateHeaders,connectSupport,
                connectHost,   connectPort,   connectFirstLine,   connectNeedDelateHeaders );
        config.setDns(dns);
        return config;
    }

    //处理xml中特殊字符
    private static ByteArrayInputStream pretreatmentXML(InputStream in) throws IOException {
        if(in==null){
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"));
        String line = br.readLine();
        StringBuffer sb = new StringBuffer();
        while (line!=null){
            if(line.contains("&")){
                line = line.replace("&","&amp;");
            }
            sb.append(line+"\r\n");
            line=br.readLine();
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sb.toString().getBytes("utf-8"));

        return byteArrayInputStream;
    }


}
