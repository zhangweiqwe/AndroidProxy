package cn.wsgwz.gravity.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeremy Wang on 2016/11/3.
 */

public class Config {
    private String version;
    private String apn;
    private String dns;

    private String http_proxy;
    private int http_port;
    private List<String> http_delate;
    private String http_first;

    private String https_proxy;
    private int https_port;
    private List<String> https_delate;
    private String https_first;

    private String author;
    private String explain;

    private boolean httpsSupport;

    private String configName;

    public Config(String version, String apn, String dns, String http_proxy, int http_port, List<String> http_delate, String http_first, String https_proxy, int https_port, List<String> https_delate, String https_first, String author, String explain, boolean httpsSupport, String configName) {
        this.version = version;
        this.apn = apn;
        this.dns = dns;
        this.http_proxy = http_proxy;
        this.http_port = http_port;
        this.http_delate = http_delate;
        this.http_first = http_first;
        this.https_proxy = https_proxy;
        this.https_port = https_port;
        this.https_delate = https_delate;
        this.https_first = https_first;
        this.author = author;
        this.explain = explain;
        this.httpsSupport = httpsSupport;
        this.configName = configName;
    }

    public String getVersion() {
        return version;
    }

    public String getApn() {
        return apn;
    }

    public String getDns() {
        return dns;
    }

    public String getHttp_proxy() {
        return http_proxy;
    }

    public int getHttp_port() {
        return http_port;
    }

    public List<String> getHttp_delate() {
        return http_delate;
    }

    public String getHttp_first() {
        return http_first;
    }

    public String getHttps_proxy() {
        return https_proxy;
    }

    public int getHttps_port() {
        return https_port;
    }

    public List<String> getHttps_delate() {
        return https_delate;
    }

    public String getHttps_first() {
        return https_first;
    }

    public String getAuthor() {
        return author;
    }

    public String getExplain() {
        return explain;
    }

    public boolean isHttpsSupport() {
        return httpsSupport;
    }

    public String getConfigName() {
        return configName;
    }

    @Override
    public String toString() {
        return "Config{" +
                "version='" + version + '\'' +
                ", apn='" + apn + '\'' +
                ", dns='" + dns + '\'' +
                ", http_proxy='" + http_proxy + '\'' +
                ", http_port=" + http_port +
                ", http_delate=" + http_delate +
                ", http_first='" + http_first + '\'' +
                ", https_proxy='" + https_proxy + '\'' +
                ", https_port=" + https_port +
                ", https_delate=" + https_delate +
                ", https_first='" + https_first + '\'' +
                ", author='" + author + '\'' +
                ", explain='" + explain + '\'' +
                ", httpsSupport=" + httpsSupport +
                ", configName='" + configName + '\'' +
                '}';
    }
}
