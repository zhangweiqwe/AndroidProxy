package cn.wsgwz.gravity.config;

import java.io.InputStream;
import cn.wsgwz.gravity.config.Config;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by Jeremy Wang on 2016/11/3.
 */

public class ConfigJson {
    public static final Config read(InputStream in) throws IOException, JSONException {
        Config config = null;

        JSONObject jsonObject = getConfigJSONObject(in);


        String delateSplitFlag = ",";

        String version = jsonObject.optString("version","1.0");
        String apn = jsonObject.optString("apn","cmwap");
        String dns = jsonObject.optString("dns","114.114.114.114");



        String http_proxy = jsonObject.optString("http_proxy","10.0.0.172:80");
        String[] httpAddressArr = http_proxy.contains(delateSplitFlag)?http_proxy.split(":"):null;
        String http_proxy_proxy = "10.0.0.172";
        int http_proxy_port = 80;
        if(httpAddressArr!=null&&httpAddressArr.length==2){
            http_proxy_proxy = httpAddressArr[0];
            try {
                http_proxy_port = Integer.parseInt(httpAddressArr[1]);
            } catch (NumberFormatException e) {
                // TODO: handle exception
            }
        }

        String http_delate = jsonObject.optString("http_delate",null);
        List<String>  http_delate_list = null;
        if(http_delate!=null){
            if(http_delate.contains(delateSplitFlag)){
                String[] needDelate = http_delate.split(delateSplitFlag);
                http_delate_list = new ArrayList<>();
                for(int i=0;i<needDelate.length;i++){
                    String  d= needDelate[i].trim();
                    if(!(d.length()==0)){
                        http_delate_list.add(d);
                    }
                }
            }else if(http_delate.trim().length()>0){
                http_delate_list = new ArrayList<>();
                http_delate_list.add(http_delate.trim());
            }

        }

        String http_first = jsonObject.optString("http_first","[M] [U] [V]\r\nHost: [h]\r\n");



        String https_proxy = jsonObject.optString("https_proxy",null);
        String[] httpsAddressArr = https_proxy.contains(delateSplitFlag)?https_proxy.split(":"):null;
        String https_proxy_proxy = "10.0.0.172";
        int https_proxy_port = 80;
        if(httpsAddressArr!=null&&httpsAddressArr.length==2){
            https_proxy_proxy = httpsAddressArr[0];
            try {
                https_proxy_port = Integer.parseInt(httpsAddressArr[1]);
            } catch (NumberFormatException e) {
                // TODO: handle exception
            }

        }


        String https_delate = jsonObject.optString("https_delate",null);
        List<String>  https_delate_list = null;
        if(https_delate!=null){
            if(https_delate.contains(delateSplitFlag)){
                String[] needDelate = https_delate.split(delateSplitFlag);
                https_delate_list = new ArrayList<>();
                for(int i=0;i<needDelate.length;i++){
                    String s = needDelate[i].trim();
                    if(!(s.length()==0)){
                        https_delate_list.add(s);
                    }
                }
            }else if(https_delate.trim().length()>0){
                https_delate_list = new ArrayList<>();
                https_delate_list.add(https_delate.trim());
            }

        }

        String https_first = jsonObject.optString("https_first","[M] [U] [V]\r\nHost: [h]\r\n");




        String author = jsonObject.optString("author","未知作者");
        String explain = jsonObject.optString("explain","无说明");
        String configName = jsonObject.optString("name","未知名称");



       
        boolean httpsSupport = jsonObject.optBoolean("https_support",true);


        config = new Config( version,  apn,  dns,  http_proxy_proxy,  http_proxy_port,  http_delate_list,  http_first,
                 https_proxy_proxy,  https_proxy_port, https_delate_list,  https_first,
                 author,  explain,  httpsSupport,configName) ;
        return config;
    }

    private  static final JSONObject getConfigJSONObject(InputStream in) throws IOException, JSONException{
        BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"));
        String line = null;
        StringBuilder sb = new StringBuilder();
        Map<String,String> otherMap = new HashMap<String, String>();
        while((line =br.readLine())!=null){
            if(line.startsWith("#")){

            }else if(line.startsWith("@")){
                String authorStr = "@作者";
                String explainStr = "@说明";
                String nameStr = "@名称";
                if(line.startsWith(authorStr)){
                    otherMap.put("author", line.replace(authorStr, "").trim());
                }else if(line.startsWith(explainStr)){
                    otherMap.put("explain", line.replace(explainStr, "").trim());
                }else if(line.startsWith(nameStr)){
                    otherMap.put("name", line.replace(nameStr, "").trim());
                }
            }else {
                sb.append(line+"\r\n");
            }
        }
        //System.out.println(sb.toString());

        if(sb.length()==0){
            return null;
        }else{
            JSONObject jsonObject = new JSONObject("{"+sb.toString()+"}");
            for (String key : otherMap.keySet()) {
                jsonObject.put(key, otherMap.get(key));
            }
            return jsonObject ;
        }

    }

}
