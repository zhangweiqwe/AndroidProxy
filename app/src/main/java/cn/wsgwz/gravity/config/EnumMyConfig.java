package cn.wsgwz.gravity.config;

import java.util.ArrayList;
import java.util.List;

import cn.wsgwz.gravity.util.NativeUtils;

/**
 * Created by Jeremy Wang on 2016/12/9.
 */

public  enum EnumMyConfig{
    C_Q_1("重庆移动1号线", NativeUtils.getConfig("config1")),C_Q_1_S("重庆移动1号线在线视频", NativeUtils.getConfig("config1s")), C_Q_2("重庆移动2号线", NativeUtils.getConfig("config2")),
    C_Q_L_1("重庆联通1号线",NativeUtils.getConfig("configL1"));
    private String name;
    private String values;

    EnumMyConfig(String name, String values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }



    public static  final  List<EnumMyConfig> getMeConfig(){
        List<EnumMyConfig>  list = new ArrayList<>();
        list.add(EnumMyConfig.C_Q_1);
        list.add(EnumMyConfig.C_Q_1_S);
        list.add(EnumMyConfig.C_Q_2);
        list.add(EnumMyConfig.C_Q_L_1);
        return list;
    }
}