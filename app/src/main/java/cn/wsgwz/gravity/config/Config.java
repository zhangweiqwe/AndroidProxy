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
    private String apn_apn;
    private String apn_proxy;
    private String apn_port;


    private String httpHost;
    private int httpPort;
    private String httpFirstLine;
    private List<String> httpNeedDelateHeaders;


    private boolean connectSupport;
    private String connectHost;
    private int connectPort;
    private String connectFirstLine;
    private List<String> connectNeedDelateHeaders;

    public Config(String version, String apn_apn, String apn_proxy, String apn_port, String httpHost, int httpPort, String httpFirstLine, List<String> httpNeedDelateHeaders, boolean connectSupport, String connectHost, int connectPort, String connectFirstLine, List<String> connectNeedDelateHeaders) {
        this.version = version;
        this.apn_apn = apn_apn;
        this.apn_proxy = apn_proxy;
        this.apn_port = apn_port;
        this.httpHost = httpHost;
        this.httpPort = httpPort;
        this.httpFirstLine = httpFirstLine;
        this.httpNeedDelateHeaders = httpNeedDelateHeaders;
        this.connectSupport = connectSupport;
        this.connectHost = connectHost;
        this.connectPort = connectPort;
        this.connectFirstLine = connectFirstLine;
        this.connectNeedDelateHeaders = connectNeedDelateHeaders;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApn_apn() {
        return apn_apn;
    }

    public void setApn_apn(String apn_apn) {
        this.apn_apn = apn_apn;
    }

    public String getApn_proxy() {
        return apn_proxy;
    }

    public void setApn_proxy(String apn_proxy) {
        this.apn_proxy = apn_proxy;
    }

    public String getApn_port() {
        return apn_port;
    }

    public void setApn_port(String apn_port) {
        this.apn_port = apn_port;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public void setHttpHost(String httpHost) {
        this.httpHost = httpHost;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public String getHttpFirstLine() {
        return httpFirstLine;
    }

    public void setHttpFirstLine(String httpFirstLine) {
        this.httpFirstLine = httpFirstLine;
    }

    public List<String> getHttpNeedDelateHeaders() {
        return httpNeedDelateHeaders;
    }

    public void setHttpNeedDelateHeaders(List<String> httpNeedDelateHeaders) {
        this.httpNeedDelateHeaders = httpNeedDelateHeaders;
    }

    public boolean isConnectSupport() {
        return connectSupport;
    }

    public void setConnectSupport(boolean connectSupport) {
        this.connectSupport = connectSupport;
    }

    public String getConnectHost() {
        return connectHost;
    }

    public void setConnectHost(String connectHost) {
        this.connectHost = connectHost;
    }

    public int getConnectPort() {
        return connectPort;
    }

    public void setConnectPort(int connectPort) {
        this.connectPort = connectPort;
    }

    public String getConnectFirstLine() {
        return connectFirstLine;
    }

    public void setConnectFirstLine(String connectFirstLine) {
        this.connectFirstLine = connectFirstLine;
    }

    public List<String> getConnectNeedDelateHeaders() {
        return connectNeedDelateHeaders;
    }

    public void setConnectNeedDelateHeaders(List<String> connectNeedDelateHeaders) {
        this.connectNeedDelateHeaders = connectNeedDelateHeaders;
    }
}
