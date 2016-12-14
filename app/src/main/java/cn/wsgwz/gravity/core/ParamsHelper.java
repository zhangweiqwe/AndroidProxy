package cn.wsgwz.gravity.core;


import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.wsgwz.gravity.config.Config;
import cn.wsgwz.gravity.config.Matching;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Administrator on 2016/11/1.
 */

public class ParamsHelper {

    protected String firstline,requestType,url,uri,httpVersion,host;
    private LinkedHashMap<String, String> linkedHashMap;
    private Config config;

    public  static final ParamsHelper read(InputStream  clientInputStream ,Config config) throws IOException {



        ParamsHelper paramsHelper=null;
        String line = ParamsHelper.readLine(clientInputStream).toString();
        StringTokenizer tokenizer;
        if(!checkFirstLine(line)){
             // LogUtil.printSS("line   bug (udp)"+line );
            return null;
        }else {
            paramsHelper = new ParamsHelper();
            paramsHelper.config = config;
            paramsHelper.firstline = line;
            tokenizer = new StringTokenizer(line);
            paramsHelper.requestType = tokenizer.nextToken();
            paramsHelper.url = tokenizer.nextToken();
            paramsHelper.httpVersion = tokenizer.nextToken();
            paramsHelper.linkedHashMap = new LinkedHashMap<>();
        }

        String key,value;
        while (line!=null){
            line = paramsHelper.readLine(clientInputStream).toString();
            if(line==null||line.trim().length()==0) break;
            try {
                tokenizer = new StringTokenizer(line);
                key = tokenizer.nextToken(":");
                value = line.replaceAll(key, "").replace(": ", "");
                paramsHelper.linkedHashMap.put(key, value);
            }catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
                //LogUtil.printSS(" ArrayIndexOutOfBoundsException    "+line);
            }

        }

                getUri(paramsHelper);
        return paramsHelper;
    }

    public    static  final    StringBuffer readLine(InputStream in) throws IOException {
        StringBuffer
            sb= new StringBuffer();
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
        //LogUtil.printSS("-------->"+sb.toString()+"<---");
        return sb;
    }
    public  static final boolean checkFirstLine(String firstline){
        if(firstline==null){
            return false;
        }
        if((firstline.startsWith("GET")||(firstline.startsWith("CONNECT")||(firstline.startsWith("POST"))))){
            return true;
        }
        return false;
    }
    private  static void getUri(ParamsHelper paramsHelper) {
        if(paramsHelper.url==null){
            return;
        }
        String key = getKeyIgnoreLowerCase("Host",paramsHelper.getLinkedHashMap());
        String value = paramsHelper.linkedHashMap.get(key);
        //如果http请请求体没找到host,尝试从url找
        if(value==null){
            value = getHost(paramsHelper.url);
        }
        paramsHelper.setHost(value);
        if(key!=null)
        {
                if(paramsHelper.url.contains(value)){
                    paramsHelper.uri = paramsHelper.url.substring(paramsHelper.url.indexOf(value)+value.length(),paramsHelper.url.length());
                }else {
                    paramsHelper.uri = paramsHelper.url;
                }
        }

    /*    if(header.containsKey("host"))
        {
            int temp = url.indexOf(header.get("host"));
            temp += header.get("host").length();

            if(temp < 0) {
                // prevent index out of bound, use entire url instead
                uri = url;
            } else {
                // get uri from part of the url
                uri = url.substring(temp);
            }
        }*/
    }

    public static final String  getKeyIgnoreLowerCase(String keyIgnoreLowerCase,LinkedHashMap<String, String> linkedHashMap){
        if(keyIgnoreLowerCase==null||linkedHashMap==null){
            return  null;
        }
        for(String key:linkedHashMap.keySet()){
            if(key.compareToIgnoreCase(keyIgnoreLowerCase)==0){
                return key;
            }
        }

        return null;
    }
    public String toString(){
    /*    StringBuffer sb = new StringBuffer();
        for(String key:linkedHashMap.keySet()){
            sb.append(key+": "+ParamsHelper.this.linkedHashMap.get(key)+"\r\n");
        }
*/
       // LogUtil.printSS("----->");
        StringBuffer sb = Matching.match(ParamsHelper.this, config);
       // LogUtil.printSS("<-----------------");


       /* LogUtil.printSS(""+sb.toString()+"<-------");
        LogUtil.printSS("<-------");*/



        //LogUtil.printSS("          ------>"+sb.toString()+"<-------");
        return sb.toString();
    }

    private  static String getHost(String url){
        if(url==null||url.trim().equals("")){
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

    public void setFirstline(String firstline) {
        this.firstline = firstline;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public LinkedHashMap<String, String> getLinkedHashMap() {
        return linkedHashMap;
    }

    public void setLinkedHashMap(LinkedHashMap<String, String> linkedHashMap) {
        this.linkedHashMap = linkedHashMap;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
