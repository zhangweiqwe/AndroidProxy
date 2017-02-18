package cn.wsgwz.gravity.config;

import cn.wsgwz.gravity.core.ParamsHelper;

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
            str = str.replace("[M]",requestType);
        }
        
        String url = paramsHelper.getUrl();
        if(url!=null){
            str = str.replace("[U]",url);
        }

        String uri = paramsHelper.getUri();
        if(uri!=null){
            str = str.replace("[u]",uri);
        }

        String httpVersion  = paramsHelper.getHttpVersion();
       if(httpVersion!=null){
            str = str.replace("[V]",httpVersion);
        }
        String host = paramsHelper.getHost();
        if(host!=null){
            str = str.replace("[h]",host);
        }

          
        return str;
    }
}
