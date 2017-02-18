package cn.wsgwz.gravity.config;

/**
 * Created by Jeremy Wang on 2016/12/20.
 */

public enum EnumAssetsConfig {
    YD_CQ_TianDao("config/YD_CQ_TianDao_Gai.g.txt","天道改(重庆移动)"),
    YD_CaiXing("config/YD_CaiXing.g.txt","彩信(移动)"),
    LT_SCCQ_1("config/LT_SCCQ_1.g.txt","双host改(四川,重庆联通)");


    private String key;
    private String values;

    EnumAssetsConfig(String key, String values) {
        this.key = key;
        this.values = values;
    }

    public String getKey() {
        return key;
    }

    public String getValues() {
        return values;
    }
}
