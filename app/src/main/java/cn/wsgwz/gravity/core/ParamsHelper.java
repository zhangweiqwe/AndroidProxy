package cn.wsgwz.gravity.core;


import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.*;

import cn.wsgwz.gravity.config.Config;
import cn.wsgwz.gravity.config.Matching;
import cn.wsgwz.gravity.util.LogUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
/**
 * Created by Administrator on 2016/11/1.
 */

public class ParamsHelper {
    private String firstline,requestType,url,uri,httpVersion,host;
    public String POST ="POST",GET="GET",CONNECT="CONNECT",endOfLine="\r\n";
    private  Map<String, String> hashMap;
    private Config config;

    private StringBuffer sbOriginal;
    private boolean isCapture;
    private InputStream clientInputStream;

    public  static final ParamsHelper read(InputStream  clientInputStream ,Config config,Boolean isCapture) throws IOException {
        ParamsHelper paramsHelper = null;



        String line = ParamsHelper.readLine(clientInputStream,paramsHelper).toString();
        StringTokenizer tokenizer;
        if(!checkFirstLine(line)){
            return null;
        }else {
            paramsHelper = new ParamsHelper();
            paramsHelper.config = config;
            paramsHelper.firstline = line;
            tokenizer = new StringTokenizer(line);
            paramsHelper.requestType = tokenizer.nextToken();
            paramsHelper.url = tokenizer.nextToken();
            paramsHelper.httpVersion = tokenizer.nextToken();
            paramsHelper.hashMap =  Collections.synchronizedMap(new HashMap<String, String>());
            paramsHelper.clientInputStream = clientInputStream;
            if(isCapture){
                paramsHelper.sbOriginal = new StringBuffer();
                paramsHelper.isCapture = isCapture;
                paramsHelper.sbOriginal.append(paramsHelper.firstline+paramsHelper.endOfLine);
            }
        }

        String key, value = null;
        while ((line = paramsHelper.readLine(clientInputStream,paramsHelper).toString()) != null) {
            if (line.trim().length() == 0) break;
            tokenizer = new StringTokenizer(line);
            key = tokenizer.nextToken(":");
            value = line.replaceAll(key, "").replace(": ", "");
            paramsHelper.hashMap.put(key, value);
        }
        getUri(paramsHelper);
        return paramsHelper;
    }

    private static final StringBuffer readLine(InputStream in,ParamsHelper paramsHelper) throws IOException {
        StringBuffer sb= new StringBuffer();
            int c;
            loop:      while (true){
                switch ((c=in.read())){
                    case -1:
                        break loop;
                    case '\n':
                        sb.append((char)c);
                        break loop;
                    case '\r':
                        int c2 = in.read();
                        if((c2!='\n')&&c2!=-1){
                            sb.append((char)c);
                            sb.append((char)c2);
                            break ;
                        }else {
                            break loop;
                        }
                    default:
                        sb.append((char) c);
                        break ;
                }
            }
        if(paramsHelper!=null&&paramsHelper.isCapture&&sb.length()>0){
            paramsHelper.sbOriginal.append(sb+paramsHelper.endOfLine);
        }
        return sb;
    }
    public  static final boolean checkFirstLine(String firstline){
        if(firstline==null||firstline.trim().length()<1){
            return false;
        }
        if((firstline.startsWith("GET")||(firstline.startsWith("CONNECT")||(firstline.startsWith("POST"))))){
            return true;
        }
        return false;
    }
    private  static final void getUri(ParamsHelper paramsHelper) {
        if(paramsHelper.url==null){
            return;
        }
        String value = paramsHelper.hashMap.get("Host");
        //如果http请请求体没找到host,尝试从url找，和尝试小写host
        if(value==null){
            value = paramsHelper.hashMap.get("host");
            if(value==null){
                value = getHost(paramsHelper.url);
            }
        }
        paramsHelper.host = value;
        if(value!=null)
        {
                if(paramsHelper.url.contains(value)){
                    paramsHelper.uri = paramsHelper.url.substring(paramsHelper.url.indexOf(value)+value.length(),paramsHelper.url.length());
                }else {
                    paramsHelper.uri = paramsHelper.url;
                }
        }
    }

    private static final StringBuffer getPostLine(ParamsHelper paramsHelper,InputStream clientInputStream)throws IOException{
        StringBuffer sb = null;
        String contentLenStr = paramsHelper.getHashMap().get("Content-Length");
        if(contentLenStr==null){
            contentLenStr = paramsHelper.getHashMap().get("content-length");
            if(contentLenStr==null){
                return null;
            }
        }
        sb = new StringBuffer();
        int contentLength = Integer.parseInt(contentLenStr);
        if(contentLength!=0){
            for (int i = 0; i < contentLength; i++)
            {
                sb.append((char)clientInputStream.read());
            }
        }
        return sb;
    }
    @Override
    public String toString(){
        StringBuffer sb = Matching.match(ParamsHelper.this, config);


        if(ParamsHelper.this.getRequestType().startsWith("POST")){
            try {
              StringBuffer postSb  = getPostLine(ParamsHelper.this,ParamsHelper.this.clientInputStream);
                if(postSb!=null){
                    sb.append(postSb);
                }
                if(ParamsHelper.this.isCapture){
                    sbOriginal.append(postSb);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(ParamsHelper.this.isCapture){
            if(onRequestBeginningListenner!=null){
                onRequestBeginningListenner.requestBegin(sbOriginal,sb);
            }
        }
        return sb.toString();
    }

    private  static String getHost(String url){
        if(url==null){
            return "";
        }
        String host = "";
        Pattern p =  Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
        Matcher matcher = p.matcher(url);
        if(matcher.find()){
            host = matcher.group();
        }
        return host;
    }

    public String getFirstline() {
        return firstline;
    }
    public String getRequestType() {
        return requestType;
    }
    public String getUrl() {
        return url;
    }
    public String getUri() {
        return uri;
    }
    public String getHttpVersion() {
        return httpVersion;
    }
    public Map<String, String> getHashMap() {
        return hashMap;
    }
    public Config getConfig() {
        return config;
    }
    public String getHost() {
        return host;
    }

    //当数据开始请求时调用的接口()
    public static interface OnRequestBeginningListenner{
        void requestBegin(StringBuffer sb_Original,StringBuffer sb_Changed);
    }
    private static OnRequestBeginningListenner onRequestBeginningListenner;
    public static void setOnRequestBeginningListenner(OnRequestBeginningListenner onRequestBeginningListenner){
        ParamsHelper.onRequestBeginningListenner = onRequestBeginningListenner;
    }
}
