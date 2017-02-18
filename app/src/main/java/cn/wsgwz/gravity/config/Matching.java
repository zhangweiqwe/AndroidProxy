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
            case "POST":
                switch (config.getVersion()){
                    case "1.0":
                        sb = matchHttp_2_0(paramsHelper,config);
                        break;
                }
                break;
            case "CONNECT":
                switch (config.getVersion()){
                    case "1.0":
                        sb = matchConnect_2_0(paramsHelper,config);
                        break;
                }
                break;
        }
        return sb;
    }

    public static final StringBuffer matchHttp_2_0(ParamsHelper paramsHelper,Config config){
        String httpFirstLine = ModuleEnum.match(config.getHttp_first(),paramsHelper);
        StringBuffer sb = new StringBuffer();
        Map<String,String>  hashMap = paramsHelper.getHashMap();
        List<String> delateHeaders = config.getHttp_delate();
        if(delateHeaders!=null){
            for(int i=0;i<delateHeaders.size();i++){
                hashMap.remove(delateHeaders.get(i));
            }
        }
        sb.append(httpFirstLine);
        for(String key:hashMap.keySet()){
            sb.append(key+": "+hashMap.get(key)+paramsHelper.endOfLine);
        }
        sb.append(paramsHelper.endOfLine);
        return sb;
    }

    public static final StringBuffer matchConnect_2_0(ParamsHelper paramsHelper,Config config){
        String connectFirstLine = ModuleEnum.match(config.getHttps_first(),paramsHelper);
        StringBuffer sb = new StringBuffer();


        Map<String,String>  hashMap = paramsHelper.getHashMap();
        List<String> delateHeaders = config.getHttps_delate();
        if(delateHeaders!=null){
            for(int i=0;i<delateHeaders.size();i++){
                hashMap.remove(delateHeaders.get(i));
            }
        }
        sb.append(connectFirstLine);
        for(String key:hashMap.keySet()){
            sb.append(key+": "+hashMap.get(key)+paramsHelper.endOfLine);
        }
        sb.append(paramsHelper.endOfLine);
        return sb;
    }
}
