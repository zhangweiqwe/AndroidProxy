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

    public  static final ParamsHelper read(InputStream  clientInputStream ,Config config) throws IOException {
        ParamsHelper paramsHelper=null;
        String line = ParamsHelper.readLine(clientInputStream).toString();
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
        }

        String key, value = null;
        while ((line = paramsHelper.readLine(clientInputStream).toString()) != null) {
            if (line.trim().length() == 0) break;
            tokenizer = new StringTokenizer(line);
            key = tokenizer.nextToken(":");
            value = line.replaceAll(key, "").replace(": ", "");
            paramsHelper.hashMap.put(key, value);
        }
        getUri(paramsHelper);
        return paramsHelper;
    }

    private static final StringBuffer readLine(InputStream in) throws IOException {
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
    @Override
    public String toString(){
        StringBuffer sb = Matching.match(ParamsHelper.this, config);
        //if(!sb.toString().startsWith("CONNECT"))
        //LogUtil.printSS("--->"+sb+"<----------");
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
}
