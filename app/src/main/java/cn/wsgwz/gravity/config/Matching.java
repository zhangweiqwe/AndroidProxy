package cn.wsgwz.gravity.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import cn.wsgwz.gravity.core.ParamsHelper;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2016/11/3.
 */

public class Matching {
    public static final StringBuffer match(ParamsHelper paramsHelper,Config config){
        StringBuffer sb=null;
        switch (paramsHelper.getRequestType()){
            case "GET":
                switch (config.getVersion()){
                    case "2.0":
                        sb = matchHttp_2_0(paramsHelper,config);
                        break;
                }
                break;
            case "CONNECT":
                switch (config.getVersion()){
                    case "2.0":
                        sb = matchConnect_2_0(paramsHelper,config);
                        break;
                }
                break;
            case "POST":
                switch (config.getVersion()){
                    case "2.0":
                        sb = matchHttp_2_0(paramsHelper,config);
                        break;
                }
                break;
        }
        return sb;
    }

    public static final StringBuffer matchHttp_2_0(ParamsHelper paramsHelper,Config config){
        String httpFirstLine = ModuleEnum.match(config.getHttpFirstLine(),paramsHelper);
        StringBuffer sb = new StringBuffer();


        Map<String,String>  linkedHashMap = paramsHelper.getHashMap();
        List<String> delateHeaders = config.getHttpNeedDelateHeaders();
        if(delateHeaders!=null){
            for(int i=0;i<delateHeaders.size();i++){
                linkedHashMap.remove(ParamsHelper.getKeyIgnoreLowerCase(delateHeaders.get(i),linkedHashMap));
            }
        }
        sb.append(httpFirstLine);
        for(String key:linkedHashMap.keySet()){
            sb.append(key+": "+linkedHashMap.get(key)+"\r\n");
        }
        sb.append("\r\n");
        return sb;
    }

    public static final StringBuffer matchConnect_2_0(ParamsHelper paramsHelper,Config config){
        String connectFirstLine = ModuleEnum.match(config.getConnectFirstLine(),paramsHelper);
        StringBuffer sb = new StringBuffer();


        Map<String,String>  linkedHashMap = paramsHelper.getHashMap();
        List<String> delateHeaders = config.getConnectNeedDelateHeaders();
        if(delateHeaders!=null){
            for(int i=0;i<delateHeaders.size();i++){
                linkedHashMap.remove(ParamsHelper.getKeyIgnoreLowerCase(delateHeaders.get(i),linkedHashMap));
            }
        }
        sb.append(connectFirstLine);
        for(String key:linkedHashMap.keySet()){
            sb.append(key+": "+linkedHashMap.get(key)+"\r\n");
        }
        sb.append("\r\n");
        return sb;
    }
}
