package cn.wsgwz.gravity.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.wsgwz.gravity.config.xml.ConfigXml;
import cn.wsgwz.gravity.core.ParamsHelper;
import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by Jeremy Wang on 2016/11/3.
 */

public enum  ModuleEnum {
    ;//METHOD("[method]"),   TAB("[tab]"),URI( "[uri]"),URL("[url]"),VERSION("[version]"),HOST("[host]"),R("\\r"),N("\\n"),T("\\t");
    private String values;
    ModuleEnum(String values){
        this.values = values;
    }
    public String getValues() {
        return values;
    }
    public static final String match(String str, ParamsHelper paramsHelper){
        if(str==null){
            return null;
        }
        String requestType = paramsHelper.getRequestType();
        if(requestType!=null){
            str = str.replace("[method]",requestType);
        }
        String uri = paramsHelper.getUri();
        if(uri!=null){
            str = str.replace("[uri]",uri);
        }
        String url = paramsHelper.getUrl();
        if(url!=null){
            str = str.replace("[url]",url);
        }
        String httpVersion  = paramsHelper.getHttpVersion();
       if(httpVersion!=null){
            str = str.replace("[version]",httpVersion);
        }
        String host = paramsHelper.getHost();
        if(host!=null){
            str = str.replace("[host]",host);
        }

        str = str.replace(" ","")
       .replace("[tab]"," ")
       .replace("\\r","\r")
       .replace("\\n","\n")
       .replace("\\t","\t");
          
        return str;
    }
}
